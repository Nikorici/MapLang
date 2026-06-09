import java.nio.file.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class AstBuilder extends MapLangBaseVisitor<Object> {

    private final Map<String, Integer> vars = new HashMap<>();
    private final Map<String, PartAst> parts;
    private final Set<String> importedPaths;
    private final Path baseDir;
    private final Random rng = new Random();

    public AstBuilder() { this((String) null); }

    public AstBuilder(String sourceFilePath) {
        this.parts = new HashMap<>();
        this.importedPaths = new HashSet<>();
        this.baseDir = sourceFilePath != null
            ? Paths.get(sourceFilePath).toAbsolutePath().getParent()
            : null;
    }

    /** Used when an import recursively parses another file — shares the parts
     *  registry and the imported-paths set with the parent builder. */
    private AstBuilder(Map<String, PartAst> parts, Set<String> imported, Path baseDir) {
        this.parts = parts;
        this.importedPaths = imported;
        this.baseDir = baseDir;
    }

    /** Resolve "x.map" against: current file's dir → ./parts/ → ../parts/ → literal. */
    private Path resolveImport(String importPath) {
        List<Path> tries = new ArrayList<>();
        if (baseDir != null) tries.add(baseDir.resolve(importPath));
        tries.add(Paths.get("parts").resolve(importPath));
        tries.add(Paths.get("../parts").resolve(importPath));
        tries.add(Paths.get(importPath));
        for (Path p : tries)
            if (p.toFile().isFile()) return p.toAbsolutePath().normalize();
        return null;
    }

    /** Evaluate an expression context to an integer value. */
    private int evalExpr(MapLangParser.ExprContext ctx) {
        return (Integer) visit(ctx);
    }

    /** Recursively collect all AST nodes from a visitor result into the map. */
    private void addNodes(MapAst map, Object node) {
        if (node == null) return;
        if (node instanceof TileAst t) map.tiles.add(t);
        else if (node instanceof PlayerAst p) map.player = p;
        else if (node instanceof NeighborAst n) map.neighbors.put(n.direction, n.filePath);
        else if (node instanceof PortalAst p) map.portals.add(p);
        else if (node instanceof List<?> list) {
            for (Object item : list) addNodes(map, item);
        }
    }

    private void addPartNodes(PartAst part, Object node) {
        if (node == null) return;
        if (node instanceof TileAst t) part.tiles.add(t);
        else if (node instanceof List<?> list) {
            for (Object item : list) addPartNodes(part, item);
        }
        // Player ignored inside a part — it's a scene-level concept.
    }

    // =======================
    // Expression visitors
    // =======================

    @Override
    public Object visitNum(MapLangParser.NumContext ctx) {
        return Integer.parseInt(ctx.NUMBER().getText());
    }

    @Override
    public Object visitVar(MapLangParser.VarContext ctx) {
        String name = ctx.ID().getText();
        Integer val = vars.get(name);
        if (val == null)
            throw new RuntimeException("Undefined variable: '" + name
                    + "' at line " + ctx.getStart().getLine());
        return val;
    }

    @Override
    public Object visitMulDivMod(MapLangParser.MulDivModContext ctx) {
        int left = evalExpr(ctx.expr(0));
        int right = evalExpr(ctx.expr(1));
        return switch (ctx.op.getType()) {
            case MapLangParser.STAR  -> left * right;
            case MapLangParser.SLASH -> right != 0 ? left / right : 0;
            case MapLangParser.MOD   -> right != 0 ? left % right : 0;
            default -> 0;
        };
    }

    @Override
    public Object visitAddSub(MapLangParser.AddSubContext ctx) {
        int left = evalExpr(ctx.expr(0));
        int right = evalExpr(ctx.expr(1));
        return ctx.op.getType() == MapLangParser.PLUS ? left + right : left - right;
    }

    @Override
    public Object visitParen(MapLangParser.ParenContext ctx) {
        return visit(ctx.expr());
    }

    // =======================
    // Top-level
    // =======================

    @Override
    public Object visitProgram(MapLangParser.ProgramContext ctx) {
        for (MapLangParser.ImportStmtContext imp : ctx.importStmt()) visit(imp);
        for (MapLangParser.PartDeclContext p : ctx.partDecl()) visit(p);
        return visit(ctx.mapDecl());
    }

    @Override
    public Object visitImportStmt(MapLangParser.ImportStmtContext ctx) {
        String raw = ctx.STRING().getText();
        String requested = raw.substring(1, raw.length() - 1);
        Path resolved = resolveImport(requested);
        if (resolved == null)
            throw new RuntimeException("Import not found: '" + requested
                    + "' at line " + ctx.getStart().getLine()
                    + " (looked in current dir, parts/, ../parts/)");
        String canonical = resolved.toString();
        if (!importedPaths.add(canonical)) return null;  // already imported
        try {
            CharStream input = CharStreams.fromFileName(canonical);
            MapLangLexer lexer = new MapLangLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MapLangParser parser = new MapLangParser(tokens);
            ParseTree tree = parser.program();
            MapLangParser.ProgramContext prog = (MapLangParser.ProgramContext) tree;
            AstBuilder child = new AstBuilder(parts, importedPaths, resolved.getParent());
            for (MapLangParser.ImportStmtContext imp : prog.importStmt()) child.visit(imp);
            for (MapLangParser.PartDeclContext p : prog.partDecl()) child.visit(p);
            // The imported file's mapDecl is intentionally ignored.
        } catch (Exception e) {
            throw new RuntimeException("Failed to import '" + requested + "': " + e.getMessage());
        }
        return null;
    }

    @Override
    public Object visitPartDecl(MapLangParser.PartDeclContext ctx) {
        String name = ctx.ID().getText();
        for (MapLangParser.LetStmtContext let : ctx.letStmt()) visit(let);
        int w = evalExpr(ctx.sizeDecl().expr(0));
        int h = evalExpr(ctx.sizeDecl().expr(1));
        PartAst part = new PartAst(name, w, h);
        for (MapLangParser.StatementContext st : ctx.statement()) {
            Object node = visit(st);
            addPartNodes(part, node);
        }
        parts.put(name, part);
        return null;
    }

    @Override
    public Object visitMapDecl(MapLangParser.MapDeclContext ctx) {
        String mapName = ctx.ID().getText();
        MapLangParser.MapBodyContext body = ctx.mapBody();

        for (MapLangParser.LetStmtContext let : body.letStmt()) visit(let);

        MapLangParser.SizeDeclContext size = body.sizeDecl();
        int w = evalExpr(size.expr(0));
        int h = evalExpr(size.expr(1));

        // Seed RNG from map name for reproducible random placement
        rng.setSeed(mapName.hashCode());

        MapAst map = new MapAst(mapName, w, h);
        for (MapLangParser.StatementContext st : body.statement()) {
            Object node = visit(st);
            addNodes(map, node);
        }
        return map;
    }

    // =======================
    // Statement visitors
    // =======================

    @Override
    public Object visitTileStmt(MapLangParser.TileStmtContext ctx) {
        String type = ctx.ID().getText();
        int x = evalExpr(ctx.expr(0));
        int y = evalExpr(ctx.expr(1));
        return new TileAst(type, x, y);
    }

    @Override
    public Object visitFillStmt(MapLangParser.FillStmtContext ctx) {
        String type = ctx.ID().getText();
        int x1 = evalExpr(ctx.expr(0));
        int y1 = evalExpr(ctx.expr(1));
        int x2 = evalExpr(ctx.expr(2));
        int y2 = evalExpr(ctx.expr(3));

        List<TileAst> tiles = new ArrayList<>();
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++)
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++)
                tiles.add(new TileAst(type, x, y));
        return tiles;
    }

    @Override
    public Object visitRowStmt(MapLangParser.RowStmtContext ctx) {
        String type = ctx.ID().getText();
        int x1 = evalExpr(ctx.expr(0));
        int y1 = evalExpr(ctx.expr(1));
        int x2 = evalExpr(ctx.expr(2));
        int y2 = evalExpr(ctx.expr(3));

        List<TileAst> tiles = new ArrayList<>();
        int dx = Integer.signum(x2 - x1);
        int dy = Integer.signum(y2 - y1);
        int x = x1, y = y1;
        while (true) {
            tiles.add(new TileAst(type, x, y));
            if (x == x2 && y == y2) break;
            if (x != x2) x += dx;
            if (y != y2) y += dy;
        }
        return tiles;
    }

    @Override
    public Object visitPlayerStmt(MapLangParser.PlayerStmtContext ctx) {
        int x = evalExpr(ctx.expr(0));
        int y = evalExpr(ctx.expr(1));
        return new PlayerAst(x, y);
    }

    @Override
    public Object visitLetStmt(MapLangParser.LetStmtContext ctx) {
        String name = ctx.ID().getText();
        int value = evalExpr(ctx.expr());
        vars.put(name, value);
        return null;
    }

    @Override
    public Object visitRepeatStmt(MapLangParser.RepeatStmtContext ctx) {
        String varName = ctx.ID().getText();
        int from = evalExpr(ctx.expr(0));
        int to = evalExpr(ctx.expr(1));

        Integer savedVar = vars.get(varName);
        List<Object> results = new ArrayList<>();

        int step = from <= to ? 1 : -1;
        for (int i = from; step > 0 ? i <= to : i >= to; i += step) {
            vars.put(varName, i);
            for (MapLangParser.StatementContext st : ctx.statement()) {
                Object node = visit(st);
                if (node != null) results.add(node);
            }
        }

        if (savedVar != null) vars.put(varName, savedVar);
        else vars.remove(varName);

        return results;
    }

    @Override
    public Object visitRandomStmt(MapLangParser.RandomStmtContext ctx) {
        String type = ctx.ID().getText();
        int count = evalExpr(ctx.expr(0));
        int x1 = evalExpr(ctx.expr(1));
        int y1 = evalExpr(ctx.expr(2));
        int x2 = evalExpr(ctx.expr(3));
        int y2 = evalExpr(ctx.expr(4));

        int minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2), maxY = Math.max(y1, y2);

        List<TileAst> tiles = new ArrayList<>();
        Set<Long> used = new HashSet<>();

        for (int i = 0; i < count; i++) {
            int attempts = 0;
            while (attempts < 100) {
                int x = minX + rng.nextInt(maxX - minX + 1);
                int y = minY + rng.nextInt(maxY - minY + 1);
                long key = ((long) y << 16) | (x & 0xFFFF);
                if (used.add(key)) {
                    tiles.add(new TileAst(type, x, y));
                    break;
                }
                attempts++;
            }
        }
        return tiles;
    }

    @Override
    public Object visitNeighborStmt(MapLangParser.NeighborStmtContext ctx) {
        // child(1) is the direction keyword (north/south/east/west)
        String dir = ctx.getChild(1).getText();
        // child(2) is either a STRING (target file) or the `back` keyword (null sentinel).
        String path = null;
        if (ctx.STRING() != null) {
            String raw = ctx.STRING().getText();
            path = raw.substring(1, raw.length() - 1);
        }
        return new NeighborAst(dir, path);
    }

    @Override
    public Object visitPortalStmt(MapLangParser.PortalStmtContext ctx) {
        int x = evalExpr(ctx.expr(0));
        int y = evalExpr(ctx.expr(1));
        // Target is either a quoted filename or the `back` keyword (which
        // means "return to wherever you came from"; null sentinel).
        String path = null;
        if (ctx.STRING() != null) {
            String raw = ctx.STRING().getText();
            path = raw.substring(1, raw.length() - 1);
        }
        return new PortalAst(x, y, path);
    }

    @Override
    public Object visitPlaceStmt(MapLangParser.PlaceStmtContext ctx) {
        String name = ctx.ID().getText();
        int ox = evalExpr(ctx.expr(0));
        int oy = evalExpr(ctx.expr(1));
        PartAst part = parts.get(name);
        if (part == null)
            throw new RuntimeException("Unknown part: '" + name
                    + "' at line " + ctx.getStart().getLine()
                    + ". Known parts: " + parts.keySet());
        List<Object> out = new ArrayList<>(part.tiles.size());
        for (TileAst t : part.tiles)
            out.add(new TileAst(t.type, t.x + ox, t.y + oy));
        return out;
    }
}
