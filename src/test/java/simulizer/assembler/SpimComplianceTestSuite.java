package simulizer.assembler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import simulizer.assembler.spim_compliance.InvalidParseTests;
import simulizer.assembler.spim_compliance.ValidParseTests;

/**
 * Test without external dependencies such as a SPIM binary to compare with
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    InvalidParseTests.class,
    ValidParseTests.class
})
public class SpimComplianceTestSuite {

}
