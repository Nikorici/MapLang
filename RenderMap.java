import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class RenderMap {

    static final int TILE_SIZE = 32;

    public static void render(MapAst map) throws Exception {

        int width = map.width * TILE_SIZE;
        int height = map.height * TILE_SIZE;

        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();

        // load textures
        BufferedImage grass = ImageIO.read(new File("textures/grass.png"));
        BufferedImage water = ImageIO.read(new File("textures/water.png"));
        BufferedImage player = ImageIO.read(new File("textures/player.png"));

        HashMap<String, BufferedImage> tiles = new HashMap<>();
        tiles.put("GRASS", grass);
        tiles.put("WATER", water);

        // background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // draw tiles
        for (TileAst t : map.tiles) {

            BufferedImage tex = tiles.get(t.type);

            if (tex != null) {
                g.drawImage(tex,
                        t.x * TILE_SIZE,
                        t.y * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE,
                        null);
            }
        }

        // draw player
        if (map.player != null) {
            g.drawImage(player,
                    map.player.x * TILE_SIZE,
                    map.player.y * TILE_SIZE,
                    TILE_SIZE,
                    TILE_SIZE,
                    null);
        }

        g.dispose();

        ImageIO.write(canvas, "png", new File("render.png"));

        System.out.println("Map rendered → render.png");
    }
}