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
	 * Enter a parse tree produced by {@link MapLangParser#importStmt}.
	 * @param ctx the parse tree
	 */
	void enterImportStmt(MapLangParser.ImportStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#importStmt}.
	 * @param ctx the parse tree
	 */
	void exitImportStmt(MapLangParser.ImportStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#partDecl}.
	 * @param ctx the parse tree
	 */
	void enterPartDecl(MapLangParser.PartDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#partDecl}.
	 * @param ctx the parse tree
	 */
	void exitPartDecl(MapLangParser.PartDeclContext ctx);
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
	 * Enter a parse tree produced by {@link MapLangParser#fillStmt}.
	 * @param ctx the parse tree
	 */
	void enterFillStmt(MapLangParser.FillStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#fillStmt}.
	 * @param ctx the parse tree
	 */
	void exitFillStmt(MapLangParser.FillStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#rowStmt}.
	 * @param ctx the parse tree
	 */
	void enterRowStmt(MapLangParser.RowStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#rowStmt}.
	 * @param ctx the parse tree
	 */
	void exitRowStmt(MapLangParser.RowStmtContext ctx);
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
	/**
	 * Enter a parse tree produced by {@link MapLangParser#repeatStmt}.
	 * @param ctx the parse tree
	 */
	void enterRepeatStmt(MapLangParser.RepeatStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#repeatStmt}.
	 * @param ctx the parse tree
	 */
	void exitRepeatStmt(MapLangParser.RepeatStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#randomStmt}.
	 * @param ctx the parse tree
	 */
	void enterRandomStmt(MapLangParser.RandomStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#randomStmt}.
	 * @param ctx the parse tree
	 */
	void exitRandomStmt(MapLangParser.RandomStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#letStmt}.
	 * @param ctx the parse tree
	 */
	void enterLetStmt(MapLangParser.LetStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#letStmt}.
	 * @param ctx the parse tree
	 */
	void exitLetStmt(MapLangParser.LetStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#placeStmt}.
	 * @param ctx the parse tree
	 */
	void enterPlaceStmt(MapLangParser.PlaceStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#placeStmt}.
	 * @param ctx the parse tree
	 */
	void exitPlaceStmt(MapLangParser.PlaceStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#neighborStmt}.
	 * @param ctx the parse tree
	 */
	void enterNeighborStmt(MapLangParser.NeighborStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#neighborStmt}.
	 * @param ctx the parse tree
	 */
	void exitNeighborStmt(MapLangParser.NeighborStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MapLangParser#portalStmt}.
	 * @param ctx the parse tree
	 */
	void enterPortalStmt(MapLangParser.PortalStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MapLangParser#portalStmt}.
	 * @param ctx the parse tree
	 */
	void exitPortalStmt(MapLangParser.PortalStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDivMod(MapLangParser.MulDivModContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDivMod(MapLangParser.MulDivModContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(MapLangParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(MapLangParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Var}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVar(MapLangParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Var}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVar(MapLangParser.VarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Num}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNum(MapLangParser.NumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Num}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNum(MapLangParser.NumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Paren}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParen(MapLangParser.ParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Paren}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParen(MapLangParser.ParenContext ctx);
}