package simulizer.assembler.extractor.problem;

import java.util.ArrayList;
import java.util.List;

public class StoreProblemLogger extends ProblemLogger {

    private List<Problem> problems;

    public StoreProblemLogger() {
        problems = new ArrayList<Problem>();
    }

    public List<Problem> getProblems() {
        return problems;
    }

    @Override
    public void logProblem(Problem p) {
        problems.add(p);
    }
}
