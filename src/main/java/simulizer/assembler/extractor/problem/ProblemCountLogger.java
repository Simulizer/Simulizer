package simulizer.assembler.extractor.problem;

/**
 * Count the number of problems being logged and pass the messages onto another
 * logger
 *
 * @author mbway
 */
public class ProblemCountLogger extends ProblemLogger {
    /**
     * the logger to redirect messages to (can be null)
     */
    private ProblemLogger redirect;

    public int problemCount;
    public int nonCriticalCount;
    public int criticalCount;

    public ProblemCountLogger(ProblemLogger redirect) {
        this.redirect = redirect;
        problemCount = 0;
        nonCriticalCount = 0;
        criticalCount = 0;
    }


    @Override
    public void logProblem(Problem p) {
        problemCount++;
        switch (p.severity) {
            case NON_CRITICAL: nonCriticalCount++; break;
            case CRITICAL: criticalCount++; break;
            default: throw new RuntimeException("unexpected error severity");
        }
        if(redirect != null) {
            redirect.logProblem(p);
        }
    }
}
