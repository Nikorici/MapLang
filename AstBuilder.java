public class AstBuilder extends MapLangBaseVisitor<Object> {

    @Override
    public Object visitProgram(MapLangParser.ProgramContext ctx) {
        return visit(ctx.mapDecl());
    }

    @Override
    public Object visitMapDecl(MapLangParser.MapDeclContext ctx) {
        String mapName = ctx.ID().getText();

        MapLangParser.MapBodyContext body = ctx.mapBody();
        MapLangParser.SizeDeclContext size = body.sizeDecl();

        int w = Integer.parseInt(size.NUMBER(0).getText());
        int h = Integer.parseInt(size.NUMBER(1).getText());

        MapAst map = new MapAst(mapName, w, h);

        for (MapLangParser.StatementContext st : body.statement()) {
            Object node = visit(st);
            if (node instanceof TileAst) map.tiles.add((TileAst) node);
            else if (node instanceof PlayerAst) map.player = (PlayerAst) node;
        }

        return map;
    }

    @Override
    public Object visitTileStmt(MapLangParser.TileStmtContext ctx) {
        String type = ctx.ID().getText();
        int x = Integer.parseInt(ctx.NUMBER(0).getText());
        int y = Integer.parseInt(ctx.NUMBER(1).getText());
        return new TileAst(type, x, y);
    }

    @Override
    public Object visitPlayerStmt(MapLangParser.PlayerStmtContext ctx) {
        int x = Integer.parseInt(ctx.NUMBER(0).getText());
        int y = Integer.parseInt(ctx.NUMBER(1).getText());
        return new PlayerAst(x, y);
    }
}