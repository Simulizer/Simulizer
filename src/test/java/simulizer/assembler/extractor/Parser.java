package simulizer.assembler.extractor;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import simulizer.parser.SimpLexer;
import simulizer.parser.SimpParser;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * A parser to use with the testing suite, intercepts ANTLR message for
 * postmortem examination
 * @author mbway
 */
public class Parser {
    public class ANTLRErrorCounter implements ANTLRErrorListener {

        public List<String> errors;

        public ANTLRErrorCounter() {
            errors = new ArrayList<>();
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            errors.add(msg);
        }

        @Override
        public void reportAmbiguity(org.antlr.v4.runtime.Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
            // the language is context sensitive because of optional commas
            // so allow this
            //errors.add(msg);
        }

        @Override
        public void reportAttemptingFullContext(org.antlr.v4.runtime.Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
            // the language is context sensitive because of optional commas
            // so allow this
            //errors.add(msg);
        }

        @Override
        public void reportContextSensitivity(org.antlr.v4.runtime.Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
            // the language is context sensitive because of optional commas
            // so allow this
            //errors.add(msg);
        }
    }

    ANTLRErrorCounter err;
    public SimpParser p;
    private boolean used;

    public Parser() {
        err = new ANTLRErrorCounter();
        used = false;
    }

    public SimpParser parseWithTrace(String input) {
        assert !used; // ensure never used twice

        SimpLexer lexer = new SimpLexer(new ANTLRInputStream(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(err);

        p = new SimpParser(new CommonTokenStream(lexer));
        p.setTrace(true);
        p.removeErrorListeners();
        p.addErrorListener(err);

        used = true;
        return p;
    }

    public SimpParser parse(String input) {
        assert !used; // ensure never used twice

        SimpLexer lexer = new SimpLexer(new ANTLRInputStream(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(err);

        p = new SimpParser(new CommonTokenStream(lexer));
        p.removeErrorListeners();
        p.addErrorListener(err);

        used = true;
        return p;
    }

    public List<String> getErrors() {
        return err.errors;
    }
}
