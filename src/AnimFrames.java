import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Generates the animation frames for tiles that should shimmer or flicker.
 * Run once:  java -cp src AnimFrames
 *
 * Emits:
 *   textures/water1.png, water2.png, water3.png  (water shimmer, 3 frames)
 *   textures/lava1.png,  lava2.png,  lava3.png   (lava bubble, 3 frames)
 *   textures/torch1.png, torch2.png              (torch flicker, 2 frames, 32x64)
 *
 * The loader in RenderMap groups *.N.png by basename and cycles them.
 */
public class AnimFrames {

    static final int T = 32;

    public static void main(String[] args) throws Exception {
        for (int i = 1; i <= 3; i++) save(water(i - 1), "water" + i);
        for (int i = 1; i <= 3; i++) save(lava(i - 1),  "lava"  + i);
        for (int i = 1; i <= 2; i++) save(torch(i - 1), "torch" + i);
        System.out.println("Animation frames generated.");
    }

    // ----------------------------------------------------------------
    // WATER — 3 frames; wave highlights drift horizontally with phase.
    // ----------------------------------------------------------------
    static BufferedImage water(int phase) {
        BufferedImage img = new BufferedImage(T, T, BufferedImage.TYPE_INT_ARGB);
        // Slight base-colour variation per phase (subtle "breathing")
        int b = 180 + (phase % 2) * 8;
        noiseFill(img, 40, 110, b, 12, 7L + phase);

        Graphics2D g = gfx(img);
        // Foreshortened wave highlights — drift right by 5 px per phase
        int driftX = (phase * 5) % 14;
        for (int wy = 5; wy < T; wy += 7) {
            int off = ((wy / 7) % 2 == 0 ? 0 : 6) + driftX;
            g.setColor(new Color(90, 170, 225, 140));
            for (int wx = -14 + (off % 14); wx < T; wx += 14)
                g.fillOval(wx, wy, 9, 3);
        }
        // Sparkles — different positions per frame (deterministic per phase)
        Random sparkleRng = new Random(11L + phase * 17L);
        g.setColor(new Color(210, 235, 255));
        for (int i = 0; i < 4; i++)
            g.fillRect(2 + sparkleRng.nextInt(T - 4),
                       2 + sparkleRng.nextInt(T - 4), 1, 1);
        // Top-edge depth shading
        for (int y = 0; y < 3; y++) {
            g.setColor(new Color(0, 0, 0, 22 - y * 7));
            g.drawLine(0, y, T - 1, y);
        }
        g.setColor(new Color(180, 220, 240, 50));
        g.drawLine(0, T - 1, T - 1, T - 1);
        g.dispose();
        return img;
    }

    // ----------------------------------------------------------------
    // LAVA — 3 frames; bubble positions and brightness shift.
    // ----------------------------------------------------------------
    static BufferedImage lava(int phase) {
        BufferedImage img = new BufferedImage(T, T, BufferedImage.TYPE_INT_ARGB);
        // Pulsing red-orange base
        int r = 200 + phase * 10;
        noiseFill(img, r, 70, 30, 18, 23L + phase);

        Graphics2D g = gfx(img);
        // Hot cracks
        g.setColor(new Color(255, 200, 60, 200 - phase * 30));
        for (int y = 4; y < T; y += 8) {
            int wave = (int)(Math.sin((y + phase * 3) * 0.6) * 3);
            g.drawLine(2 + wave, y, T - 3 - wave, y + 1);
        }
        // Bubbles — drift up and shrink across frames
        Random rng = new Random(31L + phase * 13L);
        for (int i = 0; i < 6; i++) {
            int bx = 3 + rng.nextInt(T - 6);
            int by = 4 + rng.nextInt(T - 8);
            int sz = 3 + (phase + i) % 3;
            g.setColor(new Color(255, 230, 100, 200));
            g.fillOval(bx, by, sz, sz);
            g.setColor(new Color(255, 255, 200, 160));
            g.fillRect(bx + 1, by + 1, 1, 1);
        }
        // Subtle dark crust on top edges
        for (int y = 0; y < 2; y++) {
            g.setColor(new Color(0, 0, 0, 30 - y * 10));
            g.drawLine(0, y, T - 1, y);
        }
        g.dispose();
        return img;
    }

    // ----------------------------------------------------------------
    // TORCH — 2 frames; 32x64 object tile with a wooden pole and a
    // flame that shrinks/grows. Same texture footprint as a tree/house
    // so existing depth-sorting handles it correctly.
    // ----------------------------------------------------------------
    static BufferedImage torch(int phase) {
        int W = T, H = T * 2;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gfx(img);

        // Pole (wood) — vertical stick from middle to bottom
        int poleX = W / 2;
        for (int y = T; y < H; y++) {
            g.setColor(new Color(80, 50, 25));
            g.drawLine(poleX - 1, y, poleX + 1, y);
        }
        // Bracket / cup at the top of the pole
        g.setColor(new Color(60, 40, 20));
        g.fillRect(poleX - 3, T - 2, 7, 3);
        g.setColor(new Color(35, 22, 10));
        g.fillRect(poleX - 3, T + 1, 7, 1);

        // Flame — two sizes by phase. Bottom of flame anchored at the bracket.
        int flameH = phase == 0 ? 14 : 11;
        int flameW = phase == 0 ? 8 : 7;
        int baseY = T - 2;
        // Outer (orange) flame
        g.setColor(new Color(255, 130, 30, 230));
        g.fillOval(poleX - flameW / 2, baseY - flameH, flameW, flameH);
        // Inner (yellow) flame
        g.setColor(new Color(255, 220, 80, 240));
        g.fillOval(poleX - flameW / 2 + 1, baseY - flameH + 2, flameW - 2, flameH - 4);
        // Hot core (white)
        g.setColor(new Color(255, 255, 220));
        g.fillRect(poleX - 1, baseY - flameH + 4, 2, 3);

        // Soft glow disc around the flame (low alpha, larger)
        int glow = phase == 0 ? 22 : 18;
        g.setColor(new Color(255, 180, 80, 35));
        g.fillOval(poleX - glow / 2, baseY - flameH - 4, glow, glow);

        g.dispose();
        return img;
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    static int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    static void noiseFill(BufferedImage img, int r, int g, int b, int variance, long seed) {
        Random rng = new Random(seed);
        for (int y = 0; y < img.getHeight(); y++)
            for (int x = 0; x < img.getWidth(); x++) {
                int cr = clamp(r + rng.nextInt(variance * 2 + 1) - variance);
                int cg = clamp(g + rng.nextInt(variance * 2 + 1) - variance);
                int cb = clamp(b + rng.nextInt(variance * 2 + 1) - variance);
                img.setRGB(x, y, new Color(cr, cg, cb).getRGB());
            }
    }

    static Graphics2D gfx(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        return g;
    }

    static void save(BufferedImage img, String name) throws Exception {
        File texDir = new File("textures");
        if (!texDir.isDirectory()) texDir = new File("../textures");
        texDir.mkdirs();
        File outFile = new File(texDir, name + ".png");
        ImageIO.write(img, "png", outFile);
        System.out.printf("  %s  (%dx%d)%n", outFile.getPath(), img.getWidth(), img.getHeight());
    }
}
