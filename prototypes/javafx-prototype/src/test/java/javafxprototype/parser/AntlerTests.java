package javafxprototype.parser;

import junit.framework.TestCase;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import java.io.IOException;
import java.net.URL;


public class AntlerTests extends TestCase {

    public void parseFile(String filePath) throws IOException {
        try {
            // Create a scanner that reads from the input stream passed to us
            Lexer lexer = new SmallMipsLexer(new ANTLRFileStream(filePath));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // Make a parser to parse the tokens
            SmallMipsParser parser = new SmallMipsParser(tokens);
            // Get the top level `program` context
            SmallMipsParser.ProgramContext program = parser.program();
            // e.g. get the text content of child 2
            System.out.println(program.getChild(2).getText());
        } catch (Exception e) {
            System.err.println("parser exception: " + e);
            e.printStackTrace(); // so we can get stack trace
            throw e;  // to be caught by the test suite
        }
    }

    /**
     * Get the absolute path for a resource specified relative to src/test/resources.
     *
     * @note the tests run with the root of the project as the working directory.
     *
     * @param relativePath the path of the resource relative to src/test/resources
     * @return the absolute path of the resource
     */
    public String getResourcePath(String relativePath) {
        ClassLoader loader = this.getClass().getClassLoader();
        assert loader != null;

        URL file = loader.getResource(relativePath);
        assert file != null;

        return file.getPath();
    }

    public void testParseFile() throws IOException, NullPointerException {
        parseFile(getResourcePath("smallmips-example.s"));
    }

}
