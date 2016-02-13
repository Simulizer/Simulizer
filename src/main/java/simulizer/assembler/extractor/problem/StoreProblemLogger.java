package simulizer.assembler.extractor.problem;

import java.util.ArrayList;
import java.util.List;

/**
 * A problem logger which stores the encountered problems in a chronological list
 */
public class StoreProblemLogger extends ProblemLogger {

    private List<Problem> problems;

    public StoreProblemLogger() {
        problems = new ArrayList<>();
    }

    public List<Problem> getProblems() {
        return problems;
    }

    @Override
    public void logProblem(Problem p) {
        problems.add(p);
    }
}
