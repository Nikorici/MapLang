import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/** Like SheetView, but works on packed sheets without margins (step = tile size). */
public class SheetView2 {
    static final int ZOOM = 6;

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Usage: SheetView2 <sheetPath> <tileSize> <step> <outName>");
            return;
        }
        String sheetPath = args[0];
        int ts = Integer.parseInt(args[1]);
        int step = Integer.parseInt(args[2]);
        String name = args[3];

        BufferedImage sheet = ImageIO.read(new File(sheetPath));
        int cols = sheet.getWidth() / step;
        int rows = sheet.getHeight() / step;

        int outW = cols * ts * ZOOM;
        int outH = rows * ts * ZOOM;
        BufferedImage out = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        for (int y = 0; y < outH; y += 16)
            for (int x = 0; x < outW; x += 16) {
                g.setColor(((x + y) / 16) % 2 == 0
                    ? new Color(60, 60, 70) : new Color(80, 80, 90));
                g.fillRect(x, y, 16, 16);
            }

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                int sx = c * step, sy = r * step;
                if (sx + ts > sheet.getWidth() || sy + ts > sheet.getHeight()) continue;
                BufferedImage sub = sheet.getSubimage(sx, sy, ts, ts);
                g.drawImage(sub, c * ts * ZOOM, r * ts * ZOOM, ts * ZOOM, ts * ZOOM, null);
            }

        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                int x = c * ts * ZOOM;
                int y = r * ts * ZOOM;
                String lbl = c + "," + r;
                g.setColor(new Color(0, 0, 0, 220));
                g.drawString(lbl, x + 3, y + 13);
                g.setColor(new Color(255, 230, 80));
                g.drawString(lbl, x + 2, y + 12);
                g.setColor(new Color(255, 255, 0, 80));
                g.drawRect(x, y, ts * ZOOM - 1, ts * ZOOM - 1);
            }
        g.dispose();

        File outDir = new File("/tmp/sheet");
        outDir.mkdirs();
        File outFile = new File(outDir, name + ".png");
        ImageIO.write(out, "png", outFile);
        System.out.println("Wrote " + outFile.getPath() + "  (" + outW + "x" + outH + ")");
    }
}
