package simulizer.assembler.extractor.problem;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import simulizer.parser.SimpBaseListener;

import java.util.List;

/**
 * Given a parse tree, make sure that it does not contain any error nodes
 * @author mbway
 */
public class ValidityListener extends SimpBaseListener {
    public boolean valid;

    public ValidityListener() {
        valid = true;
    }

    /**
     * test whether the children of a grammar rule contain any error nodes
     * @param children the children to test
     * @return whether any of the children contain errors
     */
    public static boolean goodChildren(List<ParseTree> children) {
        if(children == null) {
            return true;
        }

        ValidityListener v = new ValidityListener();
        for(ParseTree t : children) {
            ParseTreeWalker.DEFAULT.walk(v, t);
            if(!v.valid) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        valid = false;
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        valid &= !(node instanceof ErrorNode);
    }
}
