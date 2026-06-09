// Generated from MapLang.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MapLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MapLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MapLangParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MapLangParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#importStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportStmt(MapLangParser.ImportStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#partDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPartDecl(MapLangParser.PartDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#mapDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapDecl(MapLangParser.MapDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#mapBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapBody(MapLangParser.MapBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#sizeDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSizeDecl(MapLangParser.SizeDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(MapLangParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#tileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTileStmt(MapLangParser.TileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#fillStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFillStmt(MapLangParser.FillStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#rowStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRowStmt(MapLangParser.RowStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#playerStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayerStmt(MapLangParser.PlayerStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#repeatStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatStmt(MapLangParser.RepeatStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#randomStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRandomStmt(MapLangParser.RandomStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#letStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLetStmt(MapLangParser.LetStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#placeStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlaceStmt(MapLangParser.PlaceStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#neighborStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNeighborStmt(MapLangParser.NeighborStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MapLangParser#portalStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPortalStmt(MapLangParser.PortalStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDivMod(MapLangParser.MulDivModContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSub(MapLangParser.AddSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Var}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(MapLangParser.VarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Num}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNum(MapLangParser.NumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Paren}
	 * labeled alternative in {@link MapLangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParen(MapLangParser.ParenContext ctx);
}