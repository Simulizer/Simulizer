package simulizer.assembler.extractor.problem;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public abstract class ProblemLogger {
    /**
     * log an error. May be overridden to serve different tasks
     * @param p the problem to log
     */
    abstract public void logProblem(Problem p);

    public void logProblem(String message, int lineNum) {
        logProblem(new Problem(message, lineNum));
    }
    public void logProblem(String message, int lineNum, int rangeStart, int rangeEnd) {
        logProblem(new Problem(message, lineNum, rangeStart, rangeEnd));
    }
    public void logProblem(String message, ParserRuleContext ctx) {
        logProblem(new Problem(message, ctx));
    }
}
