// Generated from MapLang.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MapLangParser}.
 */
public interface MapLangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MapLangParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MapLangParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MapLangParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#mapDecl}.
	 * @param ctx the parse tree
	 */
	void enterMapDecl(MapLangParser.MapDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#mapDecl}.
	 * @param ctx the parse tree
	 */
	void exitMapDecl(MapLangParser.MapDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#mapBody}.
	 * @param ctx the parse tree
	 */
	void enterMapBody(MapLangParser.MapBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#mapBody}.
	 * @param ctx the parse tree
	 */
	void exitMapBody(MapLangParser.MapBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#sizeDecl}.
	 * @param ctx the parse tree
	 */
	void enterSizeDecl(MapLangParser.SizeDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#sizeDecl}.
	 * @param ctx the parse tree
	 */
	void exitSizeDecl(MapLangParser.SizeDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MapLangParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MapLangParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#tileStmt}.
	 * @param ctx the parse tree
	 */
	void enterTileStmt(MapLangParser.TileStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#tileStmt}.
	 * @param ctx the parse tree
	 */
	void exitTileStmt(MapLangParser.TileStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#playerStmt}.
	 * @param ctx the parse tree
	 */
	void enterPlayerStmt(MapLangParser.PlayerStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#playerStmt}.
	 * @param ctx the parse tree
	 */
	void exitPlayerStmt(MapLangParser.PlayerStmtContext ctx);
}