package simulizer;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.antlr.v4.runtime.ParserRuleContext;
import org.fxmisc.richtext.StyleSpansBuilder;

import simulizer.assembler.representation.Instruction;
import simulizer.parser.SimpBaseListener;
import simulizer.parser.SimpParser;

public class SyntaxHighlighter extends SimpBaseListener {
	private StyleSpansBuilder<Collection<String>> spansBuilder;
	private int lastTokenEnd = 0;

	public SyntaxHighlighter(StyleSpansBuilder<Collection<String>> spansBuilder) {
		this.spansBuilder = spansBuilder;
	}

	@Override
	public void enterInstruction(SimpParser.InstructionContext ctx) {
		String styleClass = "";
		
		try {
			// See if it exists
			Instruction.fromString(ctx.getText());
			// If it does, then set the style class to recognised
			styleClass = "recognised";
		} catch (NoSuchElementException e) {
			styleClass = "unrecognised";
		}
		
		addStyle(styleClass + "-instruction", ctx);
	}

	@Override
	public void enterComment(SimpParser.CommentContext ctx) {
		addStyle("comment", ctx);
	}

	@Override
	public void enterRegister(SimpParser.RegisterContext ctx) {
		addStyle("register", ctx);
	}

	@Override
	public void enterInteger(SimpParser.IntegerContext ctx) {
		addStyle("constant", ctx);
	}

	@Override
	public void enterString(SimpParser.StringContext ctx) {
		addStyle("constant", ctx);
	}

	@Override
	public void enterLabelID(SimpParser.LabelIDContext ctx) {
		addStyle("label", ctx);
	}

	@Override
	public void enterDataDirective(SimpParser.DataDirectiveContext ctx) {
		int start = ctx.getStart().getStartIndex();
		int length = ctx.getChild(0).getText().length();
		addStyle("data-directive", start, start + length);
	};
	
	@Override
	public void enterTextDirective(SimpParser.TextDirectiveContext ctx) {
		int start = ctx.getStart().getStartIndex();
		int length = ctx.getChild(0).getText().length();
		addStyle("text-directive", start, start + length);
	};

	@Override
	public void enterDirective(SimpParser.DirectiveContext ctx) {
		int start = ctx.getStart().getStartIndex();
		int length = ctx.getChild(0).getText().length();
		addStyle("directiveid", start, start + length);
	}
	
	@Override
	public void enterBaseAddress(SimpParser.BaseAddressContext ctx) {
		System.out.println(ctx.getChildCount());
		int start = ctx.getStart().getStartIndex();
		addStyle("base-bracket", start, start);
	}
	
	@Override
	public void exitBaseAddress(SimpParser.BaseAddressContext ctx) {
		System.out.println(ctx.getChildCount());
		
		if (ctx.getChildCount() == 3) {
			int stop = ctx.getStop().getStopIndex();
			addStyle("base-bracket", stop, stop);
		}
	}
	
	private void addStyle(String styleClass, ParserRuleContext ctx) {
		addStyle(styleClass, ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
	}

	private void addStyle(String styleClass, int start, int stop) {
		int spacing = start - lastTokenEnd;
		if (spacing > 0)
			spansBuilder.add(Collections.emptyList(), spacing);

		int styleSize = stop - start + 1;
		spansBuilder.add(Collections.singleton(styleClass), styleSize);

		lastTokenEnd = stop + 1;
	}

}
