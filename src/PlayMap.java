import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Interactive map-design viewer.
 *  - WASD / arrow keys to walk the placed player around (for sanity-checking)
 *  - Hot reload: edit the .map file in any editor, save, and the world refreshes
 *  - ESC to quit, R to reset player to spawn
 */
public class PlayMap extends JPanel implements KeyListener {

    static final int TILE = 32;
    static final int OVERHEAD = 32;
    static final int HUD_HEIGHT = 28;

    private MapAst map;
    private final HashMap<String, BufferedImage[]> tex;
    private String mapFilePath;
    private final boolean hasAnimatedTiles;
    private final Set<String> walls = Set.of(
        "WATER", "LAVA", "ROCK", "TREE", "TREE_OAK", "TREE_PINE",
        "HOUSE", "FENCE", "TORCH", "BRICK",
        "BANNER", "BANNER_TEAL", "BANNER_GREEN",
        // Tiny Town building pieces
        "ROOF_GREY", "ROOF_GREY_PEAK", "ROOF_RED", "ROOF_RED_PEAK",
        "WALL_WOOD", "WALL_STONE", "WINDOW_WOOD", "WINDOW_STONE",
        "SIGNPOST",
        // Farm RPG animals
        "CHICKEN", "COW",
        // New pixel-asset pack — multi-cell house sprites + decorations
        "HOUSE_BLUE", "HOUSE_ORANGE", "HOUSE_RED", "HOUSE_SMALL_BLUE",
        "TREE_OAK_BIG", "TREE_PINE_BIG",
        "BUSH", "BENCH",
        "SIGN_SMALL", "SIGN_WOODEN", "SIGN_FLOWER",
        // Pond auto-tile pieces (all block — they're water/stone)
        "POND_NW", "POND_N", "POND_NE",
        "POND_W",  "POND_C", "POND_E",
        "POND_SW", "POND_S", "POND_SE",
        // Modern Interiors — furniture (RUG_GREEN stays walkable as a rug)
        "BED", "SOFA", "TABLE", "CHAIR", "PLANT", "LAMP",
        "FRUIT_BOWL", "BOOKSHELF", "CABINET"
    );

    private int playerX, playerY;

    // Smooth animation state
    private float animOffsetX = 0, animOffsetY = 0;
    private boolean animating = false;
    private final javax.swing.Timer animTimer;
    private int pendingDX = 0, pendingDY = 0;

    // Hot-reload toast
    private long toastUntil = 0;
    private String toastText = null;
    private Color toastColor = new Color(120, 220, 140);

    // Portal return memory: if the *next* scene swap brings us back to this
    // map, restore the player to this position instead of the destination's
    // own spawn. Cleared after one use or when a swap goes elsewhere.
    private String pendingReturnMap = null;
    private int pendingReturnX, pendingReturnY;

    public PlayMap(MapAst map, HashMap<String, BufferedImage[]> tex, String mapFilePath) {
        this.map = map;
        this.tex = tex;
        this.mapFilePath = mapFilePath;
        boolean anyAnim = false;
        for (BufferedImage[] frames : tex.values())
            if (frames != null && frames.length > 1) { anyAnim = true; break; }
        this.hasAnimatedTiles = anyAnim;
        this.playerX = map.player != null ? map.player.x : 0;
        this.playerY = map.player != null ? map.player.y : 0;

        int canvasW = map.width * TILE;
        int canvasH = map.height * TILE + OVERHEAD + HUD_HEIGHT;
        setPreferredSize(new Dimension(canvasW, canvasH));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        animTimer = new javax.swing.Timer(16, e -> {
            if (animating) {
                float speed = 0.2f;
                if (Math.abs(animOffsetX) > 0.5f)
                    animOffsetX -= Math.signum(animOffsetX) * speed * TILE;
                else animOffsetX = 0;
                if (Math.abs(animOffsetY) > 0.5f)
                    animOffsetY -= Math.signum(animOffsetY) * speed * TILE;
                else animOffsetY = 0;
                if (animOffsetX == 0 && animOffsetY == 0) {
                    animating = false;
                    if (pendingDX != 0 || pendingDY != 0) {
                        int dx = pendingDX, dy = pendingDY;
                        pendingDX = pendingDY = 0;
                        tryMove(dx, dy);
                    }
                }
                repaint();
            } else if (hasAnimatedTiles
                    || (toastUntil > 0 && System.currentTimeMillis() < toastUntil)) {
                repaint();
            }
        });
        animTimer.start();

        if (mapFilePath != null) startFileWatcher();
    }

    // =======================
    // Hot reload
    // =======================

    private void startFileWatcher() {
        Path file = Paths.get(mapFilePath).toAbsolutePath();
        Path dir = file.getParent();
        if (dir == null) return;
        Thread t = new Thread(() -> {
            try (WatchService ws = FileSystems.getDefault().newWatchService()) {
                dir.register(ws, StandardWatchEventKinds.ENTRY_MODIFY,
                                 StandardWatchEventKinds.ENTRY_CREATE);
                long lastFire = 0;
                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key = ws.take();
                    boolean hit = false;
                    // Compare each event against the CURRENT map file (may have changed
                    // due to an edge crossing).
                    String currentName = Paths.get(mapFilePath).getFileName().toString();
                    for (WatchEvent<?> ev : key.pollEvents()) {
                        Path changed = (Path) ev.context();
                        if (changed != null && currentName.equals(changed.toString())) hit = true;
                    }
                    key.reset();
                    if (hit) {
                        long now = System.currentTimeMillis();
                        if (now - lastFire < 150) continue;
                        lastFire = now;
                        try { Thread.sleep(60); } catch (InterruptedException ie) {}
                        SwingUtilities.invokeLater(this::reloadFromDisk);
                    }
                }
            } catch (Exception ex) {
                System.err.println("File watcher stopped: " + ex.getMessage());
            }
        }, "maplang-watcher");
        t.setDaemon(true);
        t.start();
    }

    /** Parse a .map file from disk into a MapAst. */
    private static MapAst parseMapFile(String path) throws Exception {
        CharStream input = CharStreams.fromFileName(path);
        MapLangLexer lexer = new MapLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MapLangParser parser = new MapLangParser(tokens);
        parser.removeErrorListeners();
        lexer.removeErrorListeners();
        ParseTree tree = parser.program();
        if (parser.getNumberOfSyntaxErrors() > 0)
            throw new RuntimeException("syntax errors");
        return (MapAst) new AstBuilder(path).visit(tree);
    }

    private void reloadFromDisk() {
        try {
            MapAst fresh = parseMapFile(mapFilePath);
            applyReloaded(fresh);
            flashToast("RELOADED", new Color(120, 220, 140));
        } catch (Exception ex) {
            flashToast("PARSE ERROR — fix and save", new Color(230, 110, 110));
        }
    }

    private void applyReloaded(MapAst fresh) {
        this.map = fresh;
        if (playerX >= fresh.width || playerY >= fresh.height || !isWalkable(playerX, playerY)) {
            playerX = fresh.player != null ? fresh.player.x : 0;
            playerY = fresh.player != null ? fresh.player.y : 0;
        }
        animating = false;
        animOffsetX = animOffsetY = 0;
        pendingDX = pendingDY = 0;

        int canvasW = fresh.width * TILE;
        int canvasH = fresh.height * TILE + OVERHEAD + HUD_HEIGHT;
        setPreferredSize(new Dimension(canvasW, canvasH));
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.pack();
        repaint();
    }

    private void flashToast(String text, Color color) {
        this.toastText = text;
        this.toastColor = color;
        this.toastUntil = System.currentTimeMillis() + 1400;
        repaint();
    }

    // =======================
    // Movement
    // =======================

    private boolean isWalkable(int x, int y) {
        if (x < 0 || y < 0 || x >= map.width || y >= map.height) return false;
        for (TileAst t : map.tiles)
            if (t.x == x && t.y == y && walls.contains(t.type)) return false;
        return true;
    }

    private void tryMove(int dx, int dy) {
        if (animating) {
            pendingDX = dx;
            pendingDY = dy;
            return;
        }
        int nx = playerX + dx;
        int ny = playerY + dy;

        // Edge crossing? If the next step leaves the grid AND a neighbor is
        // declared in that direction, swap maps and land on the matching edge.
        String dir = null;
        if (nx < 0) dir = "west";
        else if (nx >= map.width) dir = "east";
        else if (ny < 0) dir = "north";
        else if (ny >= map.height) dir = "south";

        if (dir != null) {
            // A `back` neighbor stores null as the value but the key exists, so
            // use containsKey instead of a null check.
            if (map.neighbors.containsKey(dir)) {
                String neighborPath = map.neighbors.get(dir);
                // Save pending for the return trip — same idea as portals. This
                // lets a `neighbor west back;` actually go back to where the
                // player came from.
                if (pendingReturnMap == null) {
                    pendingReturnMap = Paths.get(mapFilePath).toAbsolutePath().normalize().toString();
                    pendingReturnX = playerX;
                    pendingReturnY = playerY;
                }
                crossToNeighbor(dir, neighborPath, playerX, playerY);
            }
            return; // edge with no neighbor → block
        }

        if (!isWalkable(nx, ny)) return;

        int prevX = playerX, prevY = playerY;
        animOffsetX = -dx * TILE;
        animOffsetY = -dy * TILE;
        animating = true;
        playerX = nx;
        playerY = ny;
        repaint();

        // Stepping onto a portal triggers a scene switch.
        PortalAst portal = portalAt(nx, ny);
        if (portal != null) {
            // Remember: if a later swap brings us back to THIS map, place the
            // player one step BEFORE the portal so they don't immediately
            // re-trigger it. Only set if empty — don't overwrite the outer
            // journey when stepping through a return portal in a sub-scene.
            if (pendingReturnMap == null) {
                pendingReturnMap = Paths.get(mapFilePath).toAbsolutePath().normalize().toString();
                pendingReturnX = prevX;
                pendingReturnY = prevY;
            }
            stepThroughPortal(portal);
        }
    }

    private PortalAst portalAt(int x, int y) {
        for (PortalAst p : map.portals)
            if (p.x == x && p.y == y) return p;
        return null;
    }

    private void stepThroughPortal(PortalAst portal) {
        try {
            Path target;
            if (portal.filePath == null) {
                // `back` portal — return to wherever the pending slot points.
                // The slot was set by the FIRST portal step (e.g. when entering
                // an interior), so a `back` portal works regardless of which
                // map sent the player here.
                if (pendingReturnMap == null) {
                    flashToast("NOWHERE TO RETURN", new Color(230, 110, 110));
                    return;
                }
                target = Paths.get(pendingReturnMap);
            } else {
                Path current = Paths.get(mapFilePath).toAbsolutePath();
                target = current.getParent().resolve(portal.filePath).normalize();
            }
            MapAst dest = parseMapFile(target.toString());
            int px = dest.player != null ? dest.player.x : 0;
            int py = dest.player != null ? dest.player.y : 0;
            switchToMap(dest, target.toString(), px, py);
        } catch (Exception ex) {
            flashToast("PORTAL FAILED — " + (portal.filePath == null ? "back" : portal.filePath),
                       new Color(230, 110, 110));
        }
    }

    /** Walk across an edge into a neighbor map. */
    private void crossToNeighbor(String dir, String neighborRelPath,
                                  int leavingX, int leavingY) {
        try {
            Path neighborFull;
            if (neighborRelPath == null) {
                // `back` neighbor — use the pending return slot.
                if (pendingReturnMap == null) {
                    flashToast("NOWHERE TO RETURN", new Color(230, 110, 110));
                    return;
                }
                neighborFull = Paths.get(pendingReturnMap);
            } else {
                Path current = Paths.get(mapFilePath).toAbsolutePath();
                neighborFull = current.getParent().resolve(neighborRelPath).normalize();
            }
            MapAst neighbor = parseMapFile(neighborFull.toString());

            // Compute landing position on the matching edge of the neighbor.
            int landX, landY;
            switch (dir) {
                case "east"  -> { landX = 0; landY = leavingY; }
                case "west"  -> { landX = neighbor.width - 1; landY = leavingY; }
                case "south" -> { landX = leavingX; landY = 0; }
                case "north" -> { landX = leavingX; landY = neighbor.height - 1; }
                default -> { return; }
            }
            // Clamp to neighbor bounds in case maps differ in size.
            landX = Math.max(0, Math.min(neighbor.width - 1, landX));
            landY = Math.max(0, Math.min(neighbor.height - 1, landY));

            // If the landing tile is a wall, scan along the entry edge for the
            // nearest walkable cell — keeps the player from getting stuck.
            int[] safe = findWalkableLanding(neighbor, landX, landY, dir);
            switchToMap(neighbor, neighborFull.toString(), safe[0], safe[1]);
        } catch (Exception ex) {
            flashToast("CROSS FAILED — " + (neighborRelPath == null ? "back" : neighborRelPath),
                       new Color(230, 110, 110));
        }
    }

    private int[] findWalkableLanding(MapAst m, int wantX, int wantY, String dir) {
        if (cellWalkable(m, wantX, wantY)) return new int[]{wantX, wantY};
        boolean horizontalEdge = dir.equals("east") || dir.equals("west");
        int span = horizontalEdge ? m.height : m.width;
        for (int d = 1; d < span; d++) {
            if (horizontalEdge) {
                if (cellWalkable(m, wantX, wantY - d)) return new int[]{wantX, wantY - d};
                if (cellWalkable(m, wantX, wantY + d)) return new int[]{wantX, wantY + d};
            } else {
                if (cellWalkable(m, wantX - d, wantY)) return new int[]{wantX - d, wantY};
                if (cellWalkable(m, wantX + d, wantY)) return new int[]{wantX + d, wantY};
            }
        }
        return new int[]{wantX, wantY}; // give up; player can step off
    }

    private boolean cellWalkable(MapAst m, int x, int y) {
        if (x < 0 || y < 0 || x >= m.width || y >= m.height) return false;
        for (TileAst t : m.tiles)
            if (t.x == x && t.y == y && walls.contains(t.type)) return false;
        return true;
    }

    private void switchToMap(MapAst newMap, String newPath, int px, int py) {
        // If this swap brings us back to the map the player just left via a
        // portal, restore them to the cell they were standing on before the
        // portal — not the destination map's default spawn.
        String canonicalNew = Paths.get(newPath).toAbsolutePath().normalize().toString();
        if (pendingReturnMap != null && pendingReturnMap.equals(canonicalNew)) {
            px = pendingReturnX;
            py = pendingReturnY;
            pendingReturnMap = null;  // consumed
        }
        // else: leave pending alone; it may match a later swap.

        this.map = newMap;
        this.mapFilePath = newPath;
        this.playerX = px;
        this.playerY = py;
        this.animating = false;
        this.animOffsetX = this.animOffsetY = 0;
        this.pendingDX = this.pendingDY = 0;

        int canvasW = newMap.width * TILE;
        int canvasH = newMap.height * TILE + OVERHEAD + HUD_HEIGHT;
        setPreferredSize(new Dimension(canvasW, canvasH));
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof JFrame frame) {
            frame.setTitle("MapLang — " + newMap.name);
            frame.pack();
        } else if (w != null) {
            w.pack();
        }
        flashToast("→ " + newMap.name, new Color(140, 180, 240));
        repaint();
    }

    // =======================
    // Rendering
    // =======================

    @Override
    protected void paintComponent(Graphics g1) {
        super.paintComponent(g1);
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int yOff = HUD_HEIGHT;

        // Background
        g.setColor(new Color(55, 85, 35));
        g.fillRect(0, yOff, map.width * TILE, map.height * TILE + OVERHEAD);

        long now = System.currentTimeMillis();

        // Grass fill
        BufferedImage grassTex = RenderMap.currentFrame(tex.get("GRASS"), now);
        if (grassTex != null) {
            for (int py = 0; py < map.height * TILE + OVERHEAD; py += TILE)
                for (int px = 0; px < map.width * TILE; px += TILE)
                    g.drawImage(grassTex, px, yOff + py, TILE, TILE, null);
        }

        // Classify
        List<TileAst> groundTiles = new ArrayList<>();
        List<TileAst> objectTiles = new ArrayList<>();
        for (TileAst t : map.tiles) {
            if (t.type.equals("GRASS")) continue;
            BufferedImage[] frames = tex.get(t.type);
            if (frames == null || frames.length == 0) continue;
            if (frames[0].getHeight() <= TILE) groundTiles.add(t);
            else objectTiles.add(t);
        }

        // Ground
        for (TileAst t : groundTiles) {
            BufferedImage img = RenderMap.currentFrame(tex.get(t.type), now);
            g.drawImage(img, t.x * TILE, yOff + OVERHEAD + t.y * TILE, TILE, TILE, null);
        }

        // Objects + player, Y-sorted
        List<int[]> posList = new ArrayList<>();
        List<BufferedImage> imgList = new ArrayList<>();
        for (TileAst t : objectTiles) {
            posList.add(new int[]{t.x, t.y});
            imgList.add(RenderMap.currentFrame(tex.get(t.type), now));
        }
        int playerIdx = posList.size();
        posList.add(new int[]{playerX, playerY});
        imgList.add(RenderMap.currentFrame(tex.get("PLAYER"), now));

        Integer[] order = new Integer[posList.size()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(i -> posList.get(i)[1]));
        for (int idx : order) {
            int[] pos = posList.get(idx);
            BufferedImage img = imgList.get(idx);
            if (img == null) continue;
            int texH = img.getHeight();
            int drawX = pos[0] * TILE;
            int drawY = yOff + OVERHEAD + (pos[1] + 1) * TILE - texH;
            if (idx == playerIdx) {
                drawX += Math.round(animOffsetX);
                drawY += Math.round(animOffsetY);
            }
            g.drawImage(img, drawX, drawY, null);
        }

        // HUD bar — minimal: just the map name and a controls hint
        g.setColor(new Color(20, 20, 30));
        g.fillRect(0, 0, getWidth(), HUD_HEIGHT);
        g.setColor(new Color(50, 50, 70));
        g.drawLine(0, HUD_HEIGHT - 1, getWidth(), HUD_HEIGHT - 1);

        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.setColor(new Color(200, 210, 230));
        g.drawString(map.name, 10, 19);

        g.setColor(new Color(120, 120, 140));
        String hint = "WASD  |  R reset  |  ESC quit  |  edit .map → live reload";
        int hintW = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, getWidth() - hintW - 10, 19);

        // Hot-reload toast
        if (toastText != null && now < toastUntil) {
            long remaining = toastUntil - now;
            float a = Math.min(1f, remaining / 400f);
            g.setFont(new Font("Monospaced", Font.BOLD, 16));
            int tw = g.getFontMetrics().stringWidth(toastText);
            int pad = 14;
            int bx = (getWidth() - tw) / 2 - pad;
            int by = HUD_HEIGHT + 12;
            g.setColor(new Color(20, 20, 30, Math.round(200 * a)));
            g.fillRoundRect(bx, by, tw + pad * 2, 30, 10, 10);
            g.setColor(new Color(toastColor.getRed(), toastColor.getGreen(),
                                 toastColor.getBlue(), Math.round(255 * a)));
            g.drawRoundRect(bx, by, tw + pad * 2, 30, 10, 10);
            g.drawString(toastText, bx + pad, by + 21);
        }
    }

    // =======================
    // Input
    // =======================

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W    -> tryMove(0, -1);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S  -> tryMove(0, 1);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A  -> tryMove(-1, 0);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> tryMove(1, 0);
            case KeyEvent.VK_ESCAPE -> {
                animTimer.stop();
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w != null) w.dispose();
            }
            case KeyEvent.VK_R -> resetPlayer();
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    private void resetPlayer() {
        playerX = map.player != null ? map.player.x : 0;
        playerY = map.player != null ? map.player.y : 0;
        animating = false;
        animOffsetX = animOffsetY = 0;
        pendingDX = pendingDY = 0;
        repaint();
    }

    // =======================
    // Launch
    // =======================

    public static void launch(MapAst map, String mapFilePath) throws Exception {
        HashMap<String, BufferedImage[]> tex = RenderMap.loadTextures();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MapLang — " + map.name);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            PlayMap panel = new PlayMap(map, tex, mapFilePath);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
            panel.requestFocusInWindow();
        });
    }
}
