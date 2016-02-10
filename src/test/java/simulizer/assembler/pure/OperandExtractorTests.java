package simulizer.assembler.pure;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Test;
import simulizer.assembler.extractor.OperandExtractor;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.Register;
import simulizer.parser.SmallMipsLexer;
import simulizer.parser.SmallMipsParser;

public class OperandExtractorTests {

    public SmallMipsParser parse(String input) {
        SmallMipsLexer lexer = new SmallMipsLexer(new ANTLRInputStream(input));
        return new SmallMipsParser(new CommonTokenStream(lexer));
    }

    @Test
    public void testExtractRegister() {
        StoreProblemLogger log = new StoreProblemLogger();
        OperandExtractor ex = new OperandExtractor(log);


        Register r = ex.extract(parse("$s0").register());

        Assert.assertEquals(Register.s0, r);
        Assert.assertEquals(Register.fromString("s0"), r);
        Assert.assertEquals(Register.fromID(16), r);

        Assert.assertEquals(log.getProblems().size(), 0);// no problems
        Assert.assertEquals(ex.operands.size(), 0);



        r = ex.extract(parse("$26").register());

        Assert.assertEquals(Register.k0, r);
        Assert.assertEquals(Register.fromString("k0"), r);
        Assert.assertEquals(Register.fromID(26), r);

        Assert.assertEquals(log.getProblems().size(), 0);// no problems
        Assert.assertEquals(ex.operands.size(), 0);
    }

}
