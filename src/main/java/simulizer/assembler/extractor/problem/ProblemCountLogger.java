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
    public ProblemLogger redirect;

    public int problemCount;

    public ProblemCountLogger(ProblemLogger redirect) {
        this.redirect = redirect;
        problemCount = 0;
    }


    @Override
    public void logProblem(Problem p) {
        problemCount++;
        if(redirect != null) {
            redirect.logProblem(p);
        }
    }
}
