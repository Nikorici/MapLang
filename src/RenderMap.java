import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * 3/4-perspective map renderer with layered depth.
 *
 * Rendering passes:
 * 1. Auto-fill entire grid with GRASS
 * 2. Override ground tiles (texture height == TILE)
 * 3. Draw object tiles + player, Y-sorted back-to-front (texture height > TILE)
 *
 * Tile textures are loaded dynamically from textures/*.png.
 * Ground vs. object is determined automatically by texture height.
 */
public class RenderMap {

    static final int TILE = 32; // grid cell size
    static final int OVERHEAD = 32; // extra top space for tall sprites

    public static void render(MapAst map, String mapFilePath) throws Exception {

        int canvasW = map.width * TILE;
        int canvasH = map.height * TILE + OVERHEAD;

        BufferedImage canvas = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // --- Load every texture in textures/ automatically ---
        HashMap<String, BufferedImage> tex = new HashMap<>();
        File dir = new File("textures");
        if (!dir.isDirectory())
            dir = new File("../textures");
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f.getName().endsWith(".png")) {
                    String key = f.getName().replace(".png", "").toUpperCase();
                    tex.put(key, ImageIO.read(f));
                }
            }
        }

        // --- Pass 0 : dark background (visible only in overhead strip) ---
        g.setColor(new Color(55, 85, 35));
        g.fillRect(0, 0, canvasW, canvasH);

        // --- Pass 1 : fill entire canvas with GRASS ---
        BufferedImage grassTex = tex.get("GRASS");
        if (grassTex != null) {
            for (int py = 0; py < canvasH; py += TILE)
                for (int px = 0; px < canvasW; px += TILE)
                    g.drawImage(grassTex, px, py, TILE, TILE, null);
        }

        // --- Classify tiles into ground vs. object layers ---
        List<TileAst> groundTiles = new ArrayList<>();
        List<TileAst> objectTiles = new ArrayList<>();

        for (TileAst t : map.tiles) {
            if (t.type.equals("GRASS"))
                continue; // already drawn
            BufferedImage img = tex.get(t.type);
            if (img == null)
                continue; // unknown tile
            if (img.getHeight() <= TILE)
                groundTiles.add(t);
            else
                objectTiles.add(t);
        }

        // --- Pass 2 : draw ground tiles (override grass) ---
        for (TileAst t : groundTiles) {
            BufferedImage img = tex.get(t.type);
            g.drawImage(img,
                    t.x * TILE,
                    OVERHEAD + t.y * TILE,
                    TILE, TILE, null);
        }

        // --- Pass 3 : draw objects + player, Y-sorted ---
        // Build unified draw-list so player obeys depth too.
        List<int[]> posList = new ArrayList<>(); // {gridX, gridY}
        List<BufferedImage> imgList = new ArrayList<>();

        for (TileAst t : objectTiles) {
            posList.add(new int[] { t.x, t.y });
            imgList.add(tex.get(t.type));
        }
        if (map.player != null && tex.containsKey("PLAYER")) {
            posList.add(new int[] { map.player.x, map.player.y });
            imgList.add(tex.get("PLAYER"));
        }

        // Sort indices by Y (back-to-front)
        Integer[] order = new Integer[posList.size()];
        for (int i = 0; i < order.length; i++)
            order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(i -> posList.get(i)[1]));

        for (int idx : order) {
            int[] pos = posList.get(idx);
            BufferedImage img = imgList.get(idx);
            int texH = img.getHeight();
            int drawX = pos[0] * TILE;
            int drawY = OVERHEAD + (pos[1] + 1) * TILE - texH;
            g.drawImage(img, drawX, drawY, null);
        }

        g.dispose();

        File outDir = new File("output");
        if (!outDir.isDirectory())
            outDir = new File("../output");
        outDir.mkdirs();

        // Derive output name from input .map filename
        String baseName = new File(mapFilePath).getName().replaceFirst("\\.[^.]+$", "");
        File outFile = new File(outDir, baseName + ".png");
        ImageIO.write(canvas, "png", outFile);
        System.out.println("Map rendered -> " + outFile.getPath() + "  (" + canvasW + "x" + canvasH + ")");
    }
}