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
	 * Visit a parse tree produced by {@link MapLangParser#playerStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayerStmt(MapLangParser.PlayerStmtContext ctx);
}