package antlrprototype;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import antlrprototype.SmallMipsParser.ProgContext;

public class Anty {

	public static void parseFile(String filePath) {
		try {
			// Create a scanner that reads from the input stream passed to us
			Lexer lexer = new SmallMipsLexer(new ANTLRFileStream(filePath));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			// Make a parser to parse the tokens
			SmallMipsParser parser = new SmallMipsParser(tokens);
			// Get the top level `prog` context
			ProgContext prog = parser.prog();
			// e.g. get the text content of child 2
			System.out.println(prog.getChild(2).getText());
		} catch (Exception e) {
			System.err.println("parser exception: " + e);
			e.printStackTrace(); // so we can get stack trace
		}
	}
	
	public static void main(String[] args) {
		parseFile("smallmips-example.txt");
	}
}
