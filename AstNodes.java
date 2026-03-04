import java.util.*;

class MapAst {
    public final String name;
    public final int width;
    public final int height;
    public final List<TileAst> tiles = new ArrayList<>();
    public PlayerAst player; // null if missing

    MapAst(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }
}

class TileAst {
    public final String type;
    public final int x;
    public final int y;

    TileAst(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }
}

class PlayerAst {
    public final int x;
    public final int y;

    PlayerAst(int x, int y) {
        this.x = x;
        this.y = y;
    }
}