import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Generates pixel-art textures in 3/4 perspective style.
 * Ground tiles are 32x32, object tiles are 32x64 (height above grid).
 * Run once: java TextureGenerator
 */
public class TextureGenerator {

    static final int T = 32; // base tile size
    static Random rng = new Random(42); // fixed seed for reproducibility

    public static void main(String[] args) throws Exception {
        File texDir = new File("textures");
        if (!texDir.isDirectory())
            texDir = new File("../textures");
        texDir.mkdirs();

        save(grass(), "grass");
        save(water(), "water");
        save(path(), "path");
        save(dirt(), "dirt");
        save(flower(), "flower");
        save(fence(), "fence");
        save(tree(), "tree");
        save(rock(), "rock");
        save(house(), "house");
        save(player(), "player");

        System.out.println("All textures generated in textures/");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    static void save(BufferedImage img, String name) throws Exception {
        File texDir = new File("textures");
        if (!texDir.isDirectory())
            texDir = new File("../textures");
        texDir.mkdirs();
        File outFile = new File(texDir, name + ".png");
        ImageIO.write(img, "png", outFile);
        System.out.printf("  %s  (%dx%d)%n", outFile.getPath(), img.getWidth(), img.getHeight());
    }

    static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    /** Fill an entire image with noisy colour. */
    static void noiseFill(BufferedImage img, int r, int g, int b, int variance) {
        for (int y = 0; y < img.getHeight(); y++)
            for (int x = 0; x < img.getWidth(); x++) {
                int cr = clamp(r + rng.nextInt(variance * 2 + 1) - variance);
                int cg = clamp(g + rng.nextInt(variance * 2 + 1) - variance);
                int cb = clamp(b + rng.nextInt(variance * 2 + 1) - variance);
                img.setRGB(x, y, new Color(cr, cg, cb).getRGB());
            }
    }

    /** Return a Graphics2D with anti-aliasing OFF (crisp pixel art). */
    static Graphics2D gfx(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        return g;
    }

    // ==================================================================
    // GROUND TILES (32 x 32)
    // ==================================================================

    static BufferedImage grass() {
        BufferedImage img = new BufferedImage(T, T, BufferedImage.TYPE_INT_ARGB);
        noiseFill(img, 96, 160, 50, 15);
        Graphics2D g = gfx(img);

        // Grass blade accents
        for (int i = 0; i < 10; i++) {
            int x = 2 + rng.nextInt(T - 4);
            int y = 4 + rng.nextInt(T - 6);
            g.setColor(new Color(clamp(65 + rng.nextInt(25)),
                    clamp(115 + rng.nextInt(30)), 30));
            g.drawLine(x, y + 3, x, y);
            g.drawLine(x, y, x + 1, y - 1);
        }

        // Subtle top-edge darkening (3/4 depth cue)
        for (int y = 0; y < 4; y++) {
            g.setColor(new Color(0, 0, 0, 18 - y * 5));
            g.drawLine(0, y, T - 1, y);
        }
        // Subtle bottom-edge lightening
        g.setColor(new Color(255, 255, 255, 10));
        g.drawLine(0, T - 1, T - 1, T - 1);

        g.dispose();
        return img;
    }

    static BufferedImage water() {
        BufferedImage img = new BufferedImage(T, T, BufferedImage.TYPE_INT_ARGB);
        noiseFill(img, 40, 110, 180, 12);
        Graphics2D g = gfx(img);

        // Elliptical wave highlights (foreshortened for 3/4 view)
        for (int wy = 5; wy < T; wy += 7) {
            int off = (wy / 7) % 2 == 0 ? 0 : 6;
            g.setColor(new Color(90, 170, 225, 140));
            for (int wx = off; wx < T; wx += 14)
                g.fillOval(wx, wy, 9, 3);
        }

        // Sparkle highlights
        g.setColor(new Color(210, 235, 255));
        for (int i = 0; i < 4; i++)
            g.fillRect(2 + rng.nextInt(T - 4), 2 + rng.nextInt(T - 4), 1, 1);

        // Top-edge depth
        for (int y = 0; y < 3; y++) {
            g.setColor(new Color(0, 0, 0, 22 - y * 7));
            g.drawLine(0, y, T - 1, y);
        }

        // Shore edge hint (bottom lighter)
        g.setColor(new Color(180, 220, 240, 50));
        g.drawLine(0, T - 1, T - 1, T - 1);

        g.dispose();
        return img;
    }

    static BufferedImage path() {
        BufferedImage img = new BufferedImage(T, T, BufferedImage.TYPE_INT_ARGB);
        noiseFill(img, 195, 165, 95, 12);
        Graphics2D g = gfx(img);

        // Pebbles
        g.setColor(new Color(165, 135, 75));
        for (int i = 0; i < 6; i++)
            g.fillOval(rng.nextInt(T - 3), rng.nextInt(T - 3), 2 + rng.nextInt(2), 2);

        // Wheel rut lines
        g.setColor(new Color(175, 145, 80, 70));
        g.drawLine(6, 0, 6, T - 1);
        g.drawLine(25, 0, 25, T - 1);

        // Edge darkening
        g.setColor(new Color(0, 0, 0, 15));
        g.drawRect(0, 0, T - 1, T - 1);
        g.dispose();
        return img;
    }

    static BufferedImage dirt() {
        BufferedImage img = new BufferedImage(T, T, BufferedImage.TYPE_INT_ARGB);
        noiseFill(img, 140, 100, 50, 14);
        Graphics2D g = gfx(img);

        g.setColor(new Color(110, 75, 30));
        for (int i = 0; i < 8; i++)
            g.fillRect(rng.nextInt(T), rng.nextInt(T), 2, 1);

        g.dispose();
        return img;
    }

    static BufferedImage flower() {
        // Grass base + colourful flowers
        BufferedImage img = grass();
        Graphics2D g = gfx(img);

        Color[] petals = {
                new Color(220, 50, 50), // red
                new Color(255, 220, 50), // yellow
                new Color(255, 140, 190), // pink
                new Color(255, 255, 255), // white
                new Color(150, 80, 220), // purple
        };

        for (int i = 0; i < 5; i++) {
            int fx = 3 + rng.nextInt(T - 6);
            int fy = 3 + rng.nextInt(T - 6);
            g.setColor(petals[i % petals.length]);
            g.fillRect(fx - 1, fy, 3, 1); // horizontal petals
            g.fillRect(fx, fy - 1, 1, 3); // vertical petals
            g.setColor(new Color(255, 210, 50));
            g.fillRect(fx, fy, 1, 1); // centre
        }

        g.dispose();
        return img;
    }

    static BufferedImage fence() {
        // Grass base + wooden fence planks (front-facing 3/4 view)
        BufferedImage img = grass();
        Graphics2D g = gfx(img);

        Color wood = new Color(139, 90, 43);
        Color woodDark = new Color(100, 65, 25);
        Color woodLight = new Color(175, 125, 65);

        // Left post
        g.setColor(woodDark);
        g.fillRect(2, 4, 5, 24);
        g.setColor(wood);
        g.fillRect(2, 4, 4, 24);
        g.setColor(woodLight);
        g.fillRect(2, 4, 1, 24);

        // Right post
        g.setColor(woodDark);
        g.fillRect(26, 4, 5, 24);
        g.setColor(wood);
        g.fillRect(26, 4, 4, 24);
        g.setColor(woodLight);
        g.fillRect(26, 4, 1, 24);

        // Top rail (top face visible + front face)
        g.setColor(woodLight);
        g.fillRect(2, 8, 29, 2);
        g.setColor(wood);
        g.fillRect(2, 10, 29, 3);
        g.setColor(woodDark);
        g.fillRect(2, 13, 29, 1);

        // Bottom rail
        g.setColor(woodLight);
        g.fillRect(2, 20, 29, 2);
        g.setColor(wood);
        g.fillRect(2, 22, 29, 3);
        g.setColor(woodDark);
        g.fillRect(2, 25, 29, 1);

        // Post top caps (bevelled)
        g.setColor(woodLight);
        g.fillRect(2, 3, 4, 1);
        g.fillRect(26, 3, 4, 1);

        g.dispose();
        return img;
    }

    // ==================================================================
    // OBJECT TILES (32 x 64 — bottom 32px anchors to grid cell)
    // ==================================================================

    static BufferedImage tree() {
        int h = 64;
        BufferedImage img = new BufferedImage(T, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gfx(img);

        // --- Ground shadow ---
        g.setColor(new Color(0, 0, 0, 45));
        g.fillOval(4, 54, 24, 8);

        // --- Trunk (front-facing 3/4) ---
        Color trunk = new Color(101, 67, 33);
        Color trunkDark = new Color(75, 50, 25);
        Color trunkLt = new Color(135, 95, 55);

        g.setColor(trunkDark);
        g.fillRect(12, 30, 10, 28);
        g.setColor(trunk);
        g.fillRect(12, 30, 8, 28);
        g.setColor(trunkLt);
        g.fillRect(12, 30, 3, 28);

        // Bark texture
        g.setColor(trunkDark);
        for (int i = 0; i < 5; i++) {
            int ty = 32 + rng.nextInt(24);
            g.drawLine(13, ty, 18, ty);
        }

        // --- Canopy layers ---
        // Shadow layer (behind)
        g.setColor(new Color(25, 85, 25));
        g.fillOval(0, 6, 32, 28);

        // Main canopy
        g.setColor(new Color(45, 135, 45));
        g.fillOval(2, 3, 28, 28);

        // Mid highlight
        g.setColor(new Color(65, 165, 55));
        g.fillOval(4, 4, 20, 18);

        // Top highlight (light source top-left)
        g.setColor(new Color(90, 195, 70));
        g.fillOval(6, 5, 14, 11);

        // Bright spot
        g.setColor(new Color(115, 210, 85));
        g.fillOval(8, 6, 8, 6);

        // Leaf detail spots
        g.setColor(new Color(30, 100, 30));
        for (int i = 0; i < 14; i++)
            g.fillRect(3 + rng.nextInt(24), 4 + rng.nextInt(24), 2, 2);

        // Canopy shadow on trunk
        g.setColor(new Color(0, 0, 0, 50));
        g.fillRect(12, 30, 8, 5);

        g.dispose();
        return img;
    }

    static BufferedImage rock() {
        int h = 64;
        BufferedImage img = new BufferedImage(T, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gfx(img);

        // Shadow
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(2, 52, 28, 10);

        // --- Rock body (3/4 view: top face lighter, front face darker) ---
        // Front face (darker)
        g.setColor(new Color(115, 115, 115));
        g.fillRoundRect(3, 36, 26, 20, 6, 6);

        // Side shadow (right)
        g.setColor(new Color(90, 90, 90));
        g.fillRoundRect(18, 38, 11, 16, 4, 4);

        // Top face (lighter, elliptical – foreshortened)
        g.setColor(new Color(160, 160, 160));
        g.fillOval(3, 30, 26, 14);

        // Top highlight
        g.setColor(new Color(185, 185, 185));
        g.fillOval(7, 32, 16, 8);

        // Bright spot
        g.setColor(new Color(205, 205, 200));
        g.fillOval(9, 33, 9, 5);

        // Edge definition
        g.setColor(new Color(75, 75, 75));
        g.drawRoundRect(3, 36, 25, 19, 6, 6);
        g.drawOval(3, 30, 25, 13);

        // Surface cracks
        g.setColor(new Color(100, 100, 95));
        for (int i = 0; i < 4; i++) {
            int rx = 5 + rng.nextInt(20);
            int ry = 36 + rng.nextInt(16);
            g.drawLine(rx, ry, rx + 2, ry + 1);
        }

        g.dispose();
        return img;
    }

    static BufferedImage house() {
        int h = 64;
        BufferedImage img = new BufferedImage(T, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gfx(img);

        // Shadow
        g.setColor(new Color(0, 0, 0, 35));
        g.fillRect(1, 56, 30, 8);

        // ===== Walls (front face) =====
        Color wall = new Color(165, 125, 75);
        Color wallDk = new Color(130, 95, 55);
        Color wallLt = new Color(190, 150, 95);

        g.setColor(wall);
        g.fillRect(2, 28, 28, 30);
        g.setColor(wallLt);
        g.fillRect(2, 28, 7, 30);
        g.setColor(wallDk);
        g.fillRect(24, 28, 6, 30);

        // Horizontal plank lines
        g.setColor(new Color(140, 100, 55));
        for (int wy = 32; wy < 58; wy += 5)
            g.drawLine(2, wy, 29, wy);

        // Foundation
        g.setColor(new Color(100, 90, 80));
        g.fillRect(1, 55, 30, 3);

        // ===== Roof (triangle, 3/4 view) =====
        Color roof = new Color(178, 50, 50);
        Color roofDk = new Color(135, 30, 30);
        Color roofLt = new Color(215, 80, 65);

        int[] rx = { 0, 16, 31 };
        int[] ry = { 28, 4, 28 };
        g.setColor(roof);
        g.fillPolygon(rx, ry, 3);

        // Right slope darker
        g.setColor(roofDk);
        g.fillPolygon(new int[] { 16, 31, 31 }, new int[] { 4, 28, 28 }, 3);

        // Left slope lighter
        g.setColor(roofLt);
        g.fillPolygon(new int[] { 0, 16, 0 }, new int[] { 28, 4, 28 }, 3);

        // Roof tile lines
        g.setColor(roofDk);
        for (int ty = 10; ty < 28; ty += 5) {
            int lx = (int) (16 - (28 - ty) * 16.0 / 24);
            int rrx = (int) (16 + (28 - ty) * 15.0 / 24);
            g.drawLine(lx, ty, rrx, ty);
        }

        // Roof outline
        g.setColor(new Color(100, 25, 25));
        g.drawLine(0, 28, 16, 4);
        g.drawLine(16, 4, 31, 28);
        g.drawLine(0, 28, 31, 28);
        g.drawLine(0, 29, 31, 29);

        // ===== Door =====
        g.setColor(new Color(85, 55, 25));
        g.fillRect(12, 42, 8, 16);
        g.setColor(new Color(65, 40, 15));
        g.drawRect(12, 42, 7, 15);
        // Knob
        g.setColor(new Color(210, 190, 60));
        g.fillRect(17, 49, 2, 2);

        // ===== Window (left) =====
        g.setColor(new Color(135, 210, 240));
        g.fillRect(4, 34, 6, 6);
        g.setColor(new Color(90, 60, 35));
        g.drawRect(4, 34, 5, 5);
        g.drawLine(7, 34, 7, 39);
        g.drawLine(4, 37, 9, 37);
        g.setColor(new Color(200, 235, 255));
        g.fillRect(5, 35, 2, 2);

        // ===== Window (right) =====
        g.setColor(new Color(135, 210, 240));
        g.fillRect(22, 34, 6, 6);
        g.setColor(new Color(90, 60, 35));
        g.drawRect(22, 34, 5, 5);
        g.drawLine(25, 34, 25, 39);
        g.drawLine(22, 37, 27, 37);
        g.setColor(new Color(200, 235, 255));
        g.fillRect(23, 35, 2, 2);

        g.dispose();
        return img;
    }

    static BufferedImage player() {
        int h = 64;
        BufferedImage img = new BufferedImage(T, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gfx(img);

        // Shadow
        g.setColor(new Color(0, 0, 0, 45));
        g.fillOval(7, 56, 18, 6);

        // === Shoes ===
        g.setColor(new Color(100, 60, 30));
        g.fillRect(10, 54, 5, 4);
        g.fillRect(17, 54, 5, 4);

        // === Legs / trousers ===
        g.setColor(new Color(70, 70, 130));
        g.fillRect(10, 44, 5, 10);
        g.fillRect(17, 44, 5, 10);
        // Highlight
        g.setColor(new Color(80, 80, 150));
        g.fillRect(10, 44, 2, 10);

        // === Belt ===
        g.setColor(new Color(85, 60, 25));
        g.fillRect(8, 43, 16, 2);

        // === Shirt / torso ===
        g.setColor(new Color(50, 120, 200));
        g.fillRect(8, 30, 16, 14);
        g.setColor(new Color(70, 140, 220));
        g.fillRect(8, 30, 6, 14);
        g.setColor(new Color(35, 95, 170));
        g.fillRect(20, 30, 4, 14);

        // === Arms ===
        g.setColor(new Color(50, 120, 200));
        g.fillRect(5, 31, 3, 12);
        g.fillRect(24, 31, 3, 12);
        // Hands (skin)
        g.setColor(new Color(240, 195, 145));
        g.fillRect(5, 42, 3, 3);
        g.fillRect(24, 42, 3, 3);

        // === Neck ===
        g.setColor(new Color(240, 195, 145));
        g.fillRect(13, 26, 6, 5);

        // === Head ===
        g.setColor(new Color(240, 195, 145));
        g.fillOval(8, 10, 16, 18);

        // === Hair (auburn/red like reference) ===
        g.setColor(new Color(195, 80, 30));
        g.fillOval(7, 8, 18, 12);
        g.fillRect(7, 13, 3, 7);
        g.fillRect(22, 13, 3, 7);
        // Hair highlight
        g.setColor(new Color(220, 110, 50));
        g.fillOval(9, 8, 10, 6);

        // === Eyes ===
        g.setColor(Color.WHITE);
        g.fillRect(11, 17, 4, 3);
        g.fillRect(18, 17, 4, 3);
        g.setColor(new Color(40, 40, 80));
        g.fillRect(13, 17, 2, 3);
        g.fillRect(20, 17, 2, 3);
        g.setColor(Color.WHITE);
        g.fillRect(13, 17, 1, 1);
        g.fillRect(20, 17, 1, 1);

        // === Nose ===
        g.setColor(new Color(220, 175, 125));
        g.fillRect(15, 20, 2, 2);

        // === Mouth ===
        g.setColor(new Color(185, 105, 85));
        g.drawLine(14, 23, 18, 23);

        g.dispose();
        return img;
    }
}
