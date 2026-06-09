import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Crops a section of the Kenney roguelike spritesheet, upscales 4x, and
 * overlays the (col, row) coordinate of each tile so we can pick them.
 *
 * Usage:
 *   java -cp src SheetView <startCol> <startRow> <cols> <rows> <outName>
 * Example:
 *   java -cp src SheetView 0 0 16 12 region_topleft
 */
public class SheetView {
    static final int TS = 16;       // tile size
    static final int MARGIN = 1;    // pixel margin between tiles
    static final int STEP = TS + MARGIN;
    static final int ZOOM = 4;

    public static void main(String[] args) throws Exception {
        if (args.length < 6) {
            System.out.println("Usage: SheetView <sheetPath> <startCol> <startRow> <cols> <rows> <outName>");
            return;
        }
        String sheetPath = args[0];
        int sc = Integer.parseInt(args[1]);
        int sr = Integer.parseInt(args[2]);
        int cols = Integer.parseInt(args[3]);
        int rows = Integer.parseInt(args[4]);
        String name = args[5];

        File sheetFile = new File(sheetPath);
        BufferedImage sheet = ImageIO.read(sheetFile);

        int outW = cols * TS * ZOOM;
        int outH = rows * TS * ZOOM;
        BufferedImage out = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Light checkerboard background so transparent tiles are visible
        for (int y = 0; y < outH; y += 16)
            for (int x = 0; x < outW; x += 16) {
                g.setColor(((x + y) / 16) % 2 == 0
                    ? new Color(60, 60, 70) : new Color(80, 80, 90));
                g.fillRect(x, y, 16, 16);
            }

        // Copy each tile, upscaled
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int srcX = (sc + c) * STEP;
                int srcY = (sr + r) * STEP;
                if (srcX + TS > sheet.getWidth() || srcY + TS > sheet.getHeight()) continue;
                BufferedImage sub = sheet.getSubimage(srcX, srcY, TS, TS);
                g.drawImage(sub,
                    c * TS * ZOOM, r * TS * ZOOM,
                    TS * ZOOM, TS * ZOOM, null);
            }
        }

        // Label each tile with its (col, row)
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * TS * ZOOM;
                int y = r * TS * ZOOM;
                String label = (sc + c) + "," + (sr + r);
                // shadow
                g.setColor(new Color(0, 0, 0, 200));
                g.drawString(label, x + 3, y + 12);
                g.setColor(new Color(255, 230, 80));
                g.drawString(label, x + 2, y + 11);
                // outline
                g.setColor(new Color(255, 255, 0, 80));
                g.drawRect(x, y, TS * ZOOM - 1, TS * ZOOM - 1);
            }
        }

        g.dispose();
        File outDir = new File("/tmp/sheet");
        outDir.mkdirs();
        File outFile = new File(outDir, name + ".png");
        ImageIO.write(out, "png", outFile);
        System.out.println("Wrote " + outFile.getPath() + "  (" + outW + "x" + outH + ")");
    }
}
