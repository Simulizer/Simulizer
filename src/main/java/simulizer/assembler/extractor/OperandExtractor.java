package simulizer.assembler.extractor;

import simulizer.assembler.extractor.problem.ProblemLogger;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.operand.*;
import simulizer.parser.SmallMipsParser;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OperandExtractor {
    public List<Operand> operands;
    ProblemLogger log;

    public OperandExtractor(ProblemLogger log) {
        operands = new ArrayList<>();
        this.log = log;
    }

    public void extractDirectiveOperands(SmallMipsParser.DirectiveOperandListContext ctx) {

        List<SmallMipsParser.DirectiveOperandContext> operandContexts = ctx.directiveOperand();

        for(SmallMipsParser.DirectiveOperandContext opCtx : operandContexts) {
            if(opCtx.integer() != null) {
                operands.add(new IntegerOperand(extract(opCtx.integer())));
            } else if(opCtx.string() != null) {
                operands.add(new StringOperand(extract(opCtx.string())));
            } else if(opCtx.address() != null) {
                operands.add(extract(opCtx.address()));
            } else {
                log.logProblem("invalid directive operand", opCtx);
            }
        }
    }

    public void extractStatementOperands(SmallMipsParser.OperandListContext ctx) {

        List<SmallMipsParser.OperandContext> operandContexts = ctx.operand();

        for(SmallMipsParser.OperandContext opCtx : operandContexts) {
            if(opCtx.register() != null) {
                operands.add(new RegisterOperand(extract(opCtx.register())));
            } else if(opCtx.integer() != null) {
                operands.add(new IntegerOperand(extract(opCtx.integer())));
            } else if(opCtx.address() != null) {
                operands.add(extract(opCtx.address()));
            } else {
                log.logProblem("invalid instruction operand", opCtx);
            }
        }

    }


    public int extract(SmallMipsParser.IntegerContext ctx) {
        int abs;
        if(ctx.decInt() != null) {
            abs = Integer.parseInt(ctx.decInt().DEC_INT().getText());
        } else if(ctx.hexInt() != null) {
            abs = Integer.decode(ctx.hexInt().HEX_INT().getText()); // uses the 0x to determine hex
        } else {
            throw new IllegalArgumentException();
        }

        if(ctx.SIGN() == null || ctx.SIGN().toString().equals("+")) { // positive
            return abs;
        } else { // negative
            return -abs;
        }
    }

    public int extract(SmallMipsParser.UnsignedIntegerContext ctx) {
        int abs;
        if(ctx.decInt() != null) {
            abs = Integer.parseInt(ctx.decInt().DEC_INT().getText());
        } else if(ctx.hexInt() != null) {
            abs = Integer.decode(ctx.hexInt().HEX_INT().getText()); // uses the 0x to determine hex
        } else {
            throw new IllegalArgumentException();
        }

        return abs;
    }

    public String extract(SmallMipsParser.StringContext ctx) {
        // TODO: unescape strings
        String s = ctx.toString();
        return s.substring(1, s.length() - 2); // strip " from both ends
    }

    public AddressOperand extract(SmallMipsParser.AddressContext ctx) {
        AddressOperand a = new AddressOperand();

        if(ctx.baseAddress() != null) {
            a.register = Optional.of(extract(ctx.baseAddress().register()));
        }

        if(ctx.labelID() != null) {
            a.labelName = Optional.of(ctx.labelID().IDENTIFIER().toString());
        }

        if(ctx.SIGN() != null && ctx.unsignedInteger() != null) {
            int x = extract(ctx.unsignedInteger());
            if(ctx.SIGN().toString().equals("-")) { // negative otherwise positive
                x = -x;
            }
            a.constant = Optional.of(x);
        }

        return a;
    }

    public Register extract(SmallMipsParser.RegisterContext ctx) {
        SmallMipsParser.RegisterIDContext id = ctx.registerID();

        if(id.IDENTIFIER() != null) {
            // refer by name
            try {
                return Register.fromString(id.IDENTIFIER().getText());
            } catch (NoSuchElementException e) {
                log.logProblem("no such register: " + ctx.getText(), ctx);
            }
        } else if(id.unsignedInteger() != null) {
            // refer by number
            try {
                return Register.fromID(extract(id.unsignedInteger()));
            } catch (NoSuchElementException e) {
                log.logProblem("no such register: " + ctx.getText(), ctx);
            }
        }

        return null;
    }
}
