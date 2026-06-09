import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java -cp src:lib/antlr.jar Main <file.map> [--play]");
            System.out.println();
            System.out.println("Options:");
            System.out.println("  --play    Launch interactive viewer (Swing window with hot reload)");
            return;
        }

        String mapFile = args[0];
        boolean playMode = false;
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--play")) playMode = true;
        }

        CharStream input = CharStreams.fromFileName(mapFile);
        MapLangLexer lexer = new MapLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MapLangParser parser = new MapLangParser(tokens);

        ParseTree tree = parser.program();

        AstBuilder builder = new AstBuilder(mapFile);
        MapAst ast = (MapAst) builder.visit(tree);

        if (playMode) {
            System.out.println("Launching viewer...");
            PlayMap.launch(ast, mapFile);
        } else {
            RenderMap.render(ast, mapFile);
        }
    }
}
