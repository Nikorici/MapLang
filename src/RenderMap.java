import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

public class RenderMap {

    static final int TILE = 32;
    static final int OVERHEAD = 32;

    /** Default animation frame interval in milliseconds. */
    static final int FRAME_MS = 220;

    /** Pick the current frame for an animated tile type given a wall-clock ms.
     *  Frame length is FRAME_MS; static tiles (length 1) return frame 0. */
    static BufferedImage currentFrame(BufferedImage[] frames, long ms) {
        if (frames == null || frames.length == 0) return null;
        if (frames.length == 1) return frames[0];
        int idx = (int) ((ms / FRAME_MS) % frames.length);
        return frames[idx];
    }

    /** Loads all PNGs in textures/. Filenames ending in a digit form animation
     *  sequences: water1.png, water2.png, water3.png → WATER has 3 frames.
     *  Unnumbered files (water.png) are used only when no numbered ones exist. */
    static HashMap<String, BufferedImage[]> loadTextures() throws Exception {
        File dir = new File("textures");
        if (!dir.isDirectory()) dir = new File("../textures");
        HashMap<String, TreeMap<Integer, BufferedImage>> raw = new HashMap<>();
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                String n = f.getName();
                if (!n.endsWith(".png")) continue;
                n = n.substring(0, n.length() - 4);
                int split = n.length();
                while (split > 0 && Character.isDigit(n.charAt(split - 1))) split--;
                String base = n.substring(0, split).toUpperCase();
                String num = n.substring(split);
                int frameNum = num.isEmpty() ? 0 : Integer.parseInt(num);
                raw.computeIfAbsent(base, k -> new TreeMap<>())
                   .put(frameNum, ImageIO.read(f));
            }
        }
        HashMap<String, BufferedImage[]> out = new HashMap<>();
        for (Map.Entry<String, TreeMap<Integer, BufferedImage>> e : raw.entrySet()) {
            TreeMap<Integer, BufferedImage> frames = e.getValue();
            List<BufferedImage> seq = new ArrayList<>();
            // Numbered frames (1, 2, 3, ...) form the animation, in order.
            for (Map.Entry<Integer, BufferedImage> fe : frames.entrySet())
                if (fe.getKey() >= 1) seq.add(fe.getValue());
            // No numbered frames? Fall back to the bare base file (water.png).
            if (seq.isEmpty() && frames.containsKey(0)) seq.add(frames.get(0));
            if (!seq.isEmpty()) out.put(e.getKey(), seq.toArray(new BufferedImage[0]));
        }
        return out;
    }

    static File getOutputDir() {
        File outDir = new File("output");
        if (!outDir.isDirectory())
            outDir = new File("../output");
        outDir.mkdirs();
        return outDir;
    }

    static String getBaseName(String mapFilePath) {
        return new File(mapFilePath).getName().replaceFirst("\\.[^.]+$", "");
    }

    public static BufferedImage renderFrame(MapAst map,
                                             HashMap<String, BufferedImage[]> tex,
                                             int playerX, int playerY) {
        long now = System.currentTimeMillis();
        int canvasW = map.width * TILE;
        int canvasH = map.height * TILE + OVERHEAD;

        BufferedImage canvas = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Pass 0: dark background
        g.setColor(new Color(55, 85, 35));
        g.fillRect(0, 0, canvasW, canvasH);

        // Pass 1: fill with GRASS
        BufferedImage grassTex = currentFrame(tex.get("GRASS"), now);
        if (grassTex != null) {
            for (int py = 0; py < canvasH; py += TILE)
                for (int px = 0; px < canvasW; px += TILE)
                    g.drawImage(grassTex, px, py, TILE, TILE, null);
        }

        // Classify tiles
        List<TileAst> groundTiles = new ArrayList<>();
        List<TileAst> objectTiles = new ArrayList<>();

        for (TileAst t : map.tiles) {
            if (t.type.equals("GRASS")) continue;
            BufferedImage[] frames = tex.get(t.type);
            if (frames == null || frames.length == 0) continue;
            if (frames[0].getHeight() <= TILE)
                groundTiles.add(t);
            else
                objectTiles.add(t);
        }

        // Pass 2: ground tiles
        for (TileAst t : groundTiles) {
            BufferedImage img = currentFrame(tex.get(t.type), now);
            g.drawImage(img, t.x * TILE, OVERHEAD + t.y * TILE, TILE, TILE, null);
        }

        // Pass 3: objects + player, Y-sorted
        List<int[]> posList = new ArrayList<>();
        List<BufferedImage> imgList = new ArrayList<>();

        for (TileAst t : objectTiles) {
            posList.add(new int[]{t.x, t.y});
            imgList.add(currentFrame(tex.get(t.type), now));
        }

        if (playerX >= 0 && playerY >= 0) {
            posList.add(new int[]{playerX, playerY});
            imgList.add(currentFrame(tex.get("PLAYER"), now));
        }

        Integer[] order = new Integer[posList.size()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(i -> posList.get(i)[1]));

        for (int idx : order) {
            int[] pos = posList.get(idx);
            BufferedImage img = imgList.get(idx);
            if (img == null) continue;
            int texH = img.getHeight();
            int drawX = pos[0] * TILE;
            int drawY = OVERHEAD + (pos[1] + 1) * TILE - texH;
            g.drawImage(img, drawX, drawY, null);
        }

        g.dispose();
        return canvas;
    }

    public static void render(MapAst map, String mapFilePath) throws Exception {
        HashMap<String, BufferedImage[]> tex = loadTextures();

        int playerX = map.player != null ? map.player.x : -1;
        int playerY = map.player != null ? map.player.y : -1;

        BufferedImage canvas = renderFrame(map, tex, playerX, playerY);

        File outFile = new File(getOutputDir(), getBaseName(mapFilePath) + ".png");
        ImageIO.write(canvas, "png", outFile);
        System.out.println("Map rendered -> " + outFile.getPath()
                + "  (" + canvas.getWidth() + "x" + canvas.getHeight() + ")");
    }
}
