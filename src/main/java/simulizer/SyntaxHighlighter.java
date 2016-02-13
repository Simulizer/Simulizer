package simulizer;

import java.util.Collection;
import java.util.Collections;

import org.antlr.v4.runtime.ParserRuleContext;
import org.fxmisc.richtext.StyleSpansBuilder;

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
		addStyle("instruction", ctx);
	}

	@Override
	public void enterComment(SimpParser.CommentContext ctx) {
		addStyle("comment", ctx);
	}

	@Override
	public void enterDirective(SimpParser.DirectiveContext ctx) {
		int start = ctx.getStart().getStartIndex();
		int length = ctx.getChild(1).getText().length();
		addStyle("directiveid", start, start + length);
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
