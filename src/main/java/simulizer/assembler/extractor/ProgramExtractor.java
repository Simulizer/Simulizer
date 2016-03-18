package simulizer.assembler.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import simulizer.assembler.extractor.problem.Problem;
import simulizer.assembler.extractor.problem.ProblemLogger;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Statement;
import simulizer.assembler.representation.Variable;
import simulizer.assembler.representation.operand.Operand;
import simulizer.assembler.representation.operand.OperandFormat;
import simulizer.assembler.representation.operand.StringOperand;
import simulizer.parser.SimpBaseListener;
import simulizer.parser.SimpParser;
import simulizer.utils.StringUtils;

/**
 * extract the required information from a parse tree of a Simp program.
 * the data is gathered into publicly accessible fields.
 * @author mbway
 */
public class ProgramExtractor extends SimpBaseListener {

    private enum State {
        OUTSIDE,
        DATA_SEGMENT,
        TEXT_SEGMENT
    }

    private State currentState;
    final ProblemLogger log;

    public final Map<String, Integer> textSegmentLabels;
    public final List<Statement> textSegment;

    public final Map<String, Integer> dataSegmentLabels;
    public final List<Variable> dataSegment;

    /**
     * store annotations relating to statements in the text segment
     */
    public final Map<Integer, String> annotations;

    /**
     * keep track of labels that are waiting to be assigned to a line
     */
    public final List<String> outstandingLabels;

	/**
     * keep track of annotations which are yet to be bound to a statement.
	 * The method for doing this is as follows
	 * eg
	 *
	 *				  # @{ }@ outside .text, binds to initAnnotation (run before program starts)
	 * .text
	 *                # @{ }@ error, in the text segment but nothing to bind to
	 *  firstLabel:   # @{ }@ binds to instruction 1 (enter (instruction 1) with outstanding annotations and labels)
	 * 	add s0 s1 s1  # @{ }@ binds to instruction 1 (enter (instruction 2) with outstanding annotations)
	 * 	add s0 s1 s1  # @{ }@ binds to instruction 2 (enter (myLabel) with no outstanding labels)
	 * 	              # @{ }@ binds to instruction 2 (enter (myLabel) with no outstanding labels)
	 * 	myLabel:	  # @{ }@ binds to instruction 3 (enter (myOtherLabel) with outstanding labels, not pushed)
	 * 	              #                                then (instruction 3) with outstanding annotations and labels)
	 * 	myOtherLabel: # @{ }@ binds to instruction 3 (enter (instruction 3) with outstanding annotations and labels)
	 * 	add s0 s1 s1  # @{ }@ binds to instruction 3 (exit text segment with outstanding annotations)
	 * 	# end
	 *
	 */
	public final List<String> outstandingAnnotations;
	/**
     * annotation code which comes before either the data or text segment is run
     * before the program starts execution. This code can be used to setup the
     * environment to use throughout the program, for example loading the
     * appropriate visualisations.
	 */
	public String initAnnotationCode;


    public ProgramExtractor(ProblemLogger log) {
        currentState = State.OUTSIDE;
        this.log = log;

        textSegmentLabels = new HashMap<>();
        textSegment = new ArrayList<>();
        dataSegmentLabels = new HashMap<>();
        dataSegment = new ArrayList<>();

        annotations = new HashMap<>();

        outstandingLabels = new ArrayList<>();
		outstandingAnnotations = new ArrayList<>();
		initAnnotationCode = "";
    }


	private int startLine(ParserRuleContext ctx) {
		return ctx.getStart().getLine() - 1; // Antlr uses 1-based, simulizer uses 0-based
	}


    /**
     * when accessing a sub-rule from a grammar rule, the result may be null
     * or it may be non-null but with an exception showing the encountered error
     * @param ctx the context to check
     * @return whether there was a good parse for the grammar rule
     */
    private boolean goodMatch(ParserRuleContext ctx) {
        return (ctx != null) && (ctx.exception == null);
    }

    /**
     * whether a terminal node matches correctly
     * @param n the terminal node to test validity
     * @return whether the terminal node parsed correctly
     */
    private boolean goodMatch(TerminalNode n) {
        return n != null && !(n instanceof ErrorNode);
    }



    @Override
    public void visitErrorNode(ErrorNode node) {
        if(node.getSymbol().getCharPositionInLine() != -1) {
            int line = node.getSymbol().getLine();
            int rangeStart = node.getSymbol().getStartIndex();
            int rangeEnd = node.getSymbol().getStopIndex();

            log.logProblem("Error node: \"" + node.getText() + "\"", line, rangeStart, rangeEnd);
        } else {
            log.logProblem("Error node with no position information: \"" + node.getText() + "\"", Problem.NO_LINE_NUM);
        }
    }

    @Override
    public void enterDataSegment(SimpParser.DataSegmentContext ctx) {
        currentState = State.DATA_SEGMENT;
		if(!outstandingLabels.isEmpty()) {
			log.logProblem("the following labels cross this segment boundary: " +
					StringUtils.joinList(outstandingLabels), ctx);
		}
    }
    @Override
    public void enterTextSegment(SimpParser.TextSegmentContext ctx) {
        currentState = State.TEXT_SEGMENT;
		if(!outstandingLabels.isEmpty()) {
			log.logProblem("the following labels cross this segment boundary: " +
					StringUtils.joinList(outstandingLabels), ctx);
		}
    }

	@Override public void exitTextSegment(SimpParser.TextSegmentContext ctx) {
		pushAnnotations();
	}

	/**
     * check the operands of a .text or .data segment. They are allowed either
     * no operands, or a single address (which must be a positive integer)
     * @param ctx the operand list to check
     * @return whether the operand list meets the criteria
     */
    private boolean segmentDirectiveOperandsGood(SimpParser.DirectiveOperandListContext ctx) {
        if(ctx == null) {
            // no arguments is fine
            return true;
        } else {
            OperandExtractor ext = new OperandExtractor(log);
            List<Operand> operands = ext.extractDirectiveOperands(ctx);

            if(operands.size() != 1) {
                return false;
            } else {
                Operand op = operands.get(0);
                // should be an address, cannot be a label so must be an integer
                // integers aren't matched by the address rule, so check for
                // positive integer
                if(op.getType() != Operand.Type.Integer || op.asIntegerOp().value <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void enterDataDirective(SimpParser.DataDirectiveContext ctx) {
        // ignored other than to log errors

        if(!goodMatch(ctx)) {
            log.logParseError("data directive", ctx);
            return;
        }

        if(!segmentDirectiveOperandsGood(ctx.directiveOperandList())) {
            log.logProblem("invalid operand(s) to .data directive. format: .data ADDRESS?", ctx);
        }
    }

    @Override
    public void enterTextDirective(SimpParser.TextDirectiveContext ctx) {
        // ignored other than to log errors

        if(!goodMatch(ctx)) {
            log.logParseError("text directive", ctx);
            return;
        }

        if(!segmentDirectiveOperandsGood(ctx.directiveOperandList())) {
            log.logProblem("invalid operand(s) to .text directive. format: .text ADDRESS?", ctx);
        }
    }

    @Override
    public void exitProgram(SimpParser.ProgramContext ctx) {
        if(!textSegmentLabels.containsKey("main")) {
            log.logProblem("The program has no 'main' label", Problem.NO_LINE_NUM);
        }
        if(!outstandingLabels.isEmpty()) {
			log.logProblem("These labels could not be assigned to addresses" +
					"because the end of the program was reached: " +
					StringUtils.joinList(outstandingLabels), Problem.NO_LINE_NUM);
        }
    }

    @Override
    public void enterLabel(SimpParser.LabelContext ctx) {

        if(!goodMatch(ctx)) {
            log.logParseError("label", ctx);
            return;
        }
        if(!goodMatch(ctx.labelID())) {
            log.logParseError("labelID", ctx.labelID());
            return;
        }

        String labelName = ctx.labelID().getText();

        if(textSegmentLabels.containsKey(labelName) || dataSegmentLabels.containsKey(labelName)) {
            log.logProblem("the label name: \"" + labelName + "\" is taken", ctx);
        } else if(currentState != State.TEXT_SEGMENT && labelName.equals("main")) {
            log.logProblem("The 'main' label must be inside the .text segment", ctx);
        }


		if(!outstandingAnnotations.isEmpty()) {
			// see outstandingAnnotations documentation for logic behind this
			if(outstandingLabels.isEmpty()) {
				pushAnnotations();
			}
		}
        outstandingLabels.add(labelName);
    }

    @Override
    public void enterDirective(SimpParser.DirectiveContext ctx) {
        OperandExtractor ext = new OperandExtractor(log);
        List<Operand> operands = ext.extractDirectiveOperands(ctx.directiveOperandList());

        String directive = ctx.DIRECTIVE_ID().getText();


        if(currentState == State.DATA_SEGMENT) {
            switch(directive) {
                case ".globl":
                    // ignored other than to log errors
                    if(operands.isEmpty() || !operands.stream().allMatch(op ->
                        op.getType() == Operand.Type.Address &&
                        op.asAddressOp().labelOnly())) {
                        log.logProblem("invalid operand(s) to .globl directive. format: .globl LABEL(, LABEL)*", ctx);
                    }
                    break;
                case ".align":
                    // ignored other than to log errors
                    if(operands.size() != 1 || operands.get(0).getType() != Operand.Type.Integer) {
                        log.logProblem("invalid operand(s) to .align directive. format: .align INT", ctx);
                    }
                    break;
                case ".ascii":
                    if(operands.size() != 1 || !operands.stream().allMatch(op -> op.getType() == Operand.Type.String)) {
                        log.logProblem("invalid operand(s) to .ascii directive. format: .ascii STRING", ctx.directiveOperandList());
                    } else {
                        Operand op = operands.get(0); // only one argument permitted
                        pushVariable(new Variable(
                            Variable.Type.ASCII, op.asStringOp().value.length(), Optional.of(op), startLine(ctx)));
                    }
                    break;
                case ".asciiz":
                    if(operands.size() != 1 || !operands.stream().allMatch(op -> op.getType() == Operand.Type.String)) {
                        log.logProblem("invalid operand(s) to .asciiz directive. format: .asciiz STRING", ctx.directiveOperandList());
                    } else {
                        Operand op = operands.get(0); // only one argument permitted
                        op = new StringOperand(op.asStringOp().value + '\0'); // add the null terminator

                        pushVariable(new Variable(
                            Variable.Type.ASCIIZ, op.asStringOp().value.length(), Optional.of(op), startLine(ctx)));
                    }
                    break;
                case ".byte":
                    if(operands.isEmpty() || !operands.stream().allMatch(op -> op.getType() == Operand.Type.Integer)) {
                        log.logProblem("invalid operand(s) to .byte directive. format: .byte INT(, INT)*", ctx.directiveOperandList());
                    } else {
                        for(Operand op : operands) {
                            pushVariable(new Variable(
                                Variable.Type.Byte, 1, Optional.of(op), startLine(ctx)));
                        }
                    }
                    break;
                case ".half":
                    if(operands.isEmpty() || !operands.stream().allMatch(op -> op.getType() == Operand.Type.Integer)) {
                        log.logProblem("invalid operand(s) to .half directive. format: .half INT(, INT)*", ctx.directiveOperandList());
                    } else {
                        for(Operand op : operands) {
                            pushVariable(new Variable(
                                Variable.Type.Half, 2, Optional.of(op), startLine(ctx)));
                        }
                    }
                    break;
                case ".word":
                    if(operands.isEmpty() || !operands.stream().allMatch(op -> op.getType() == Operand.Type.Integer)) {
                        log.logProblem("invalid operand(s) to .word directive. format: .word INT(, INT)*", ctx.directiveOperandList());
                    } else {
                        for(Operand op : operands) {
                            pushVariable(new Variable(
                                Variable.Type.Word, 4, Optional.of(op), startLine(ctx)));
                        }
                    }
                    break;

                case ".space":
                    if(operands.size() != 1 || operands.get(0).getType() != Operand.Type.Integer) {
                        log.logProblem("invalid operand(s) to .space directive. format: .space INT", ctx.directiveOperandList());
                    } else {
                        Operand op = operands.get(0);
                        pushVariable(new Variable(
                            Variable.Type.Space, op.asIntegerOp().value, Optional.empty(), startLine(ctx)));
                    }
                    break;
                default:
                    log.logParseError("Directive", ctx);
                    break;
            }
        } else if(currentState == State.TEXT_SEGMENT) {
            // noinspection StatementWithEmptyBody (intellij)
            if(directive.equals(".globl")) {
                // ignore this directive
            } else {
                log.logProblem("only .globl directives should be placed inside the .text segment", ctx);
            }
        } else {
            log.logProblem("Assembler directives should only be placed inside " +
                           "either the .data or .text segment", ctx);
        }
    }


    @Override
    public void enterStatement(SimpParser.StatementContext ctx) {
        if(currentState == State.TEXT_SEGMENT) {

            String instructionName = ctx.instruction().IDENTIFIER().getText();

            Instruction instruction;
            try {
                instruction = Instruction.fromString(instructionName);
            } catch(NoSuchElementException e) {
                log.logProblem("Unknown instruction: \"" + instructionName + "\"", ctx.instruction());
                return;
            }

            OperandExtractor ext = new OperandExtractor(log);
            SimpParser.StatementOperandListContext ops = ctx.statementOperandList();
            List<Operand> operands;
            if(ops != null) {
                operands = ext.extractStatementOperands(ops);
            } else {
                operands = new ArrayList<>();
            }

            int requiredNum = instruction.getOperandFormat().getNumArgs();
            if(operands.size() != requiredNum) {
                log.logProblem("Wrong number of operands for " +
                    instructionName + " instruction (" + requiredNum + " required)", ctx.statementOperandList());
            } else {
                OperandFormat.OperandType arg1 = operands.size() > 0 ?
                    operands.get(0).getOperandFormatType() : null;

                OperandFormat.OperandType arg2 = operands.size() > 1 ?
                    operands.get(1).getOperandFormatType() : null;

                OperandFormat.OperandType arg3 = operands.size() > 2 ?
                    operands.get(2).getOperandFormatType() : null;

                if(!instruction.getOperandFormat().valid(arg1, arg2, arg3)) {
                    log.logProblem("Operands invalid for " + instructionName +
                        " instruction. Correct format: " + instruction.getOperandFormat().toString(),
                        ctx.statementOperandList());
                }
            }

            pushStatement(new Statement(instruction, operands, startLine(ctx)));

        } else {
            log.logProblem("Statements should only be placed inside the .text segment", ctx);
        }
    }

    @Override
    public void enterComment(SimpParser.CommentContext ctx) {
		String text = ctx.getText();

		String startMark = "@{";
		String endMark   = "}@";

		int aStart = text.indexOf(startMark);
		if(aStart != -1) {
			if(currentState == State.DATA_SEGMENT) {
				log.logProblem("Annotations must be placed inside the text segment, or before any segment", ctx);
				return;
			}

			while(aStart != -1) {
				int aEnd = text.indexOf(endMark, aStart);

				if(aEnd == -1) {
					log.logProblem("Annotation not closed", ctx);
					return;
				}
				outstandingAnnotations.add(text.substring(aStart+startMark.length(), aEnd));

				aStart = text.indexOf(startMark, aEnd);
			}


			if(currentState == State.OUTSIDE) {
				pushAnnotations();
			}
			// annotation before first statement or label
			else if(textSegment.isEmpty() && outstandingLabels.isEmpty()) {
				log.logProblem("Annotations must be placed after a statement or label", ctx);
			}
		}

    }

	public void pushAnnotations() {
		if(!outstandingAnnotations.isEmpty()) {
			String annotationCode = String.join("\n", outstandingAnnotations);
			if(currentState == State.OUTSIDE) {
				initAnnotationCode += annotationCode + '\n';
				outstandingAnnotations.clear();
			} else {
				if (textSegment.isEmpty()) {
					// could happen if text segment is: "label: # @{}@" with no instructions
					log.logProblem("annotation could not be bound to an instruction: \"" + annotationCode + "\"", Problem.NO_LINE_NUM);
					return;
				}
				int index = textSegment.size() - 1;
				if (annotations.containsKey(index)) {
					String code = annotations.get(index);
					annotationCode = code + "\n" + annotationCode;
				}
				annotations.put(index, annotationCode);
				outstandingAnnotations.clear();
			}
		}
	}

    public void pushStatement(Statement s) {
		if(outstandingLabels.isEmpty()) {
			// instruction # @{}@
			// this_instruction

			// bind annotations to the last instruction
			pushAnnotations();
			textSegment.add(s);
		} else {
			// instruction
			// label: # @{}@
			// this_instruction

			// bind annotations to this instruction
			textSegment.add(s);
			pushAnnotations();
		}

		// attach outstanding labels to this instruction
		if(!outstandingLabels.isEmpty()) {
			int index = textSegment.size() - 1;
			for (String labelName : outstandingLabels) {
				textSegmentLabels.put(labelName, index);
			}
			outstandingLabels.clear();
		}
    }

    public void pushVariable(Variable v) {
        dataSegment.add(v);
		if(!outstandingLabels.isEmpty()) {
			int index = dataSegment.size() - 1;
			for (String labelName : outstandingLabels) {
				dataSegmentLabels.put(labelName, index);
			}
			outstandingLabels.clear();
		}
    }
}
