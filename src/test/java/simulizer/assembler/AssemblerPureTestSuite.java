package simulizer.assembler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import simulizer.assembler.pure.ProblemTests;

/**
 * Test without external dependencies such as a SPIM binary to compare with
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
    ProblemTests.class
)
public class AssemblerPureTestSuite {

}
