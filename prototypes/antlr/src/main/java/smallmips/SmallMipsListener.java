package smallmips;


// Generated from SmallMips.g4 by ANTLR 4.5
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SmallMipsParser}.
 */
public interface SmallMipsListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SmallMipsParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(SmallMipsParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmallMipsParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(SmallMipsParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmallMipsParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(SmallMipsParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmallMipsParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(SmallMipsParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmallMipsParser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterInstruction(SmallMipsParser.InstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmallMipsParser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitInstruction(SmallMipsParser.InstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmallMipsParser#assemblerinstruction}.
	 * @param ctx the parse tree
	 */
	void enterAssemblerinstruction(SmallMipsParser.AssemblerinstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmallMipsParser#assemblerinstruction}.
	 * @param ctx the parse tree
	 */
	void exitAssemblerinstruction(SmallMipsParser.AssemblerinstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmallMipsParser#register}.
	 * @param ctx the parse tree
	 */
	void enterRegister(SmallMipsParser.RegisterContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmallMipsParser#register}.
	 * @param ctx the parse tree
	 */
	void exitRegister(SmallMipsParser.RegisterContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmallMipsParser#argumentlist}.
	 * @param ctx the parse tree
	 */
	void enterArgumentlist(SmallMipsParser.ArgumentlistContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmallMipsParser#argumentlist}.
	 * @param ctx the parse tree
	 */
	void exitArgumentlist(SmallMipsParser.ArgumentlistContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmallMipsParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(SmallMipsParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmallMipsParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(SmallMipsParser.ArgumentContext ctx);
}