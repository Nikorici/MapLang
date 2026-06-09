import java.util.*;

class MapAst {
    public final String name;
    public final int width;
    public final int height;
    public final List<TileAst> tiles = new ArrayList<>();
    public PlayerAst player; // null if missing
    public final Map<String, String> neighbors = new HashMap<>(); // "east" -> "forest.map"
    public final List<PortalAst> portals = new ArrayList<>();

    MapAst(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }
}

class PartAst {
    public final String name;
    public final int width;
    public final int height;
    public final List<TileAst> tiles = new ArrayList<>();

    PartAst(String name, int width, int height) {
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

class PortalAst {
    public final int x;
    public final int y;
    public final String filePath;
    PortalAst(int x, int y, String filePath) {
        this.x = x; this.y = y; this.filePath = filePath;
    }
}

class NeighborAst {
    public final String direction; // "north" | "south" | "east" | "west"
    public final String filePath;

    NeighborAst(String direction, String filePath) {
        this.direction = direction;
        this.filePath = filePath;
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
