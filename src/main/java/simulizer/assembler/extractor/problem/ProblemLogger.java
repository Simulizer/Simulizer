package simulizer.assembler.extractor.problem;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * An abstract class for describing objects which may receive and handle
 * problems as they are encountered.
 * @author mbway
 */
public abstract class ProblemLogger {

    /**
     * log an error. May be overridden to serve different purposes
     * @param p the problem to log
     */
    abstract public void logProblem(Problem p);

    public void logProblem(String message, int lineNum, Problem.Severity severity) {
        logProblem(new Problem(message, lineNum, severity));
    }
    public void logProblem(String message, int lineNum, int rangeStart, int rangeEnd, Problem.Severity severity) {
        logProblem(new Problem(message, lineNum, rangeStart, rangeEnd, severity));
    }

    /**
     * log a generic error regarding a particular stretch of text
     */
    public void logProblem(String message, ParserRuleContext ctx, Problem.Severity severity) {
        logProblem(new Problem(message, ctx, severity));
    }

    /**
     * log an error relating to an invalid parse of a grammar rule
     * @param ruleName the grammar rule that did not parse correctly
     * @param ctx the context of the rule (may be null)
     */
    public void logParseError(String ruleName, ParserRuleContext ctx) {
        if(ctx == null) {
            logProblem(
                "invalid parse for " + ruleName +
                " grammar rule for null input", Problem.NO_LINE_NUM, Problem.Severity.CRITICAL);
        } else {
            logProblem(
                "invalid parse for " + ruleName +
                " grammar rule for the input: \"" + ctx.getText() + "\". " +
                "context exception: " +
                (ctx.exception == null ? "null" : ctx.exception.getMessage()), ctx, Problem.Severity.CRITICAL);
        }
    }
}
