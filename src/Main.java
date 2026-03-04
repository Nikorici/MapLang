import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java -cp src;lib/antlr.jar Main maps/<file.map>");
            return;
        }

        CharStream input = CharStreams.fromFileName(args[0]);
        MapLangLexer lexer = new MapLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MapLangParser parser = new MapLangParser(tokens);

        // parse
        ParseTree tree = parser.program();

        // build AST
        AstBuilder builder = new AstBuilder();
        MapAst ast = (MapAst) builder.visit(tree);

        // print AST as JSON
        System.out.println(toJson(ast));
        RenderMap.render(ast, args[0]);
    }

    static String toJson(MapAst m) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"map\": \"").append(m.name).append("\",\n");
        sb.append("  \"size\": [").append(m.width).append(", ").append(m.height).append("],\n");

        sb.append("  \"tiles\": [\n");
        for (int i = 0; i < m.tiles.size(); i++) {
            TileAst t = m.tiles.get(i);
            sb.append("    {\"type\":\"").append(t.type)
                    .append("\",\"x\":").append(t.x)
                    .append(",\"y\":").append(t.y).append("}");
            if (i < m.tiles.size() - 1)
                sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");

        if (m.player != null) {
            sb.append("  \"player\": {\"x\":").append(m.player.x)
                    .append(",\"y\":").append(m.player.y).append("}\n");
        } else {
            sb.append("  \"player\": null\n");
        }

        sb.append("}\n");
        return sb.toString();
    }
}