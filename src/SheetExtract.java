import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Extracts hand-picked tiles from multiple Kenney/3rd-party sheets, upscales 2x.
 *   java -cp src SheetExtract
 */
public class SheetExtract {
    static final int ZOOM = 2;

    /** Sheet definitions: key → {file, tileSize, step}. */
    static final Map<String, Object[]> SHEETS = new HashMap<>();
    static {
        SHEETS.put("rl", new Object[]{
            "assets/kenney_roguelike-rpg-pack/Spritesheet/roguelikeSheet_transparent.png",
            16, 17});
        SHEETS.put("tt", new Object[]{
            "assets/kenney_tiny-town/Tilemap/tilemap.png", 16, 17});
        SHEETS.put("chicken", new Object[]{
            "assets/farm_rpg/Farm RPG FREE 16x16 - Tiny Asset Pack/Farm Animals/Chicken Red.png",
            16, 16});
        SHEETS.put("cow", new Object[]{
            "assets/farm_rpg/Farm RPG FREE 16x16 - Tiny Asset Pack/Farm Animals/Male Cow Brown.png",
            16, 16});
        SHEETS.put("crops", new Object[]{
            "assets/farm_rpg/Farm RPG FREE 16x16 - Tiny Asset Pack/Objects/Spring Crops.png",
            16, 16});
        SHEETS.put("interior", new Object[]{
            "assets/modern_interiors/Modern tiles_Free/Interiors_free/16x16/Room_Builder_free_16x16.png",
            16, 16});
    }

    /** {sheetKey, col, row, widthCells, heightCells, outName} */
    static final Object[][] PICKS = {
        // ────────── ROGUELIKE PACK ──────────
        { "rl", 5,  0, 1, 1, "grass"        },
        { "rl", 6,  2, 1, 1, "dirt"         },
        { "rl", 5, 18, 1, 1, "path"         },
        { "rl", 6, 21, 1, 1, "sand"         },
        { "rl", 0,  9, 1, 1, "flower"       },
        { "rl", 7,  3, 1, 1, "stone"        },
        { "rl", 0, 25, 1, 1, "wood"         },
        { "rl", 6,  4, 1, 1, "brick"        },
        { "rl",15, 10, 1, 2, "tree"         },
        { "rl",16, 10, 1, 2, "tree_oak"     },
        { "rl",18, 10, 1, 2, "tree_pine"    },
        { "rl",49,  0, 1, 2, "banner"       },
        { "rl",49,  3, 1, 2, "banner_teal"  },
        { "rl",49,  6, 1, 2, "banner_green" },

        // ────────── TINY TOWN PACK ──────────
        { "tt", 1,  4, 1, 1, "roof_grey"      },
        { "tt", 2,  5, 1, 1, "roof_grey_peak" },
        { "tt", 5,  4, 1, 1, "roof_red"       },
        { "tt", 6,  5, 1, 1, "roof_red_peak"  },
        { "tt", 0,  6, 1, 1, "wall_wood"      },
        { "tt", 4,  6, 1, 1, "wall_stone"     },
        { "tt", 1,  7, 1, 1, "window_wood"    },
        { "tt", 2,  7, 1, 1, "door_wood"      },
        { "tt", 5,  7, 1, 1, "window_stone"   },
        { "tt", 6,  7, 1, 1, "door_stone"     },
        { "tt", 9,  6, 1, 1, "signpost"       },
        { "tt", 5,  2, 1, 1, "mushroom"       },
        { "tt", 3,  8, 1, 1, "arch_stone"     },

        // ────────── FARM RPG — animated farm animals ──────────
        // Chicken — 2-frame bob/peck animation
        { "chicken", 0, 0, 1, 1, "chicken1" },
        { "chicken", 1, 0, 1, 1, "chicken2" },
        // Cow — 16x32 sprite (1 wide x 2 tall), 2-frame animation
        { "cow", 0, 0, 1, 2, "cow1" },
        { "cow", 2, 0, 1, 2, "cow2" },

        // ────────── FARM RPG — mature crops ──────────
        { "crops", 5, 1, 1, 1, "crop_strawberry" },
        { "crops", 5, 3, 1, 1, "crop_leek"       },
        { "crops", 5, 5, 1, 1, "crop_potato"     },

        // ────────── MODERN INTERIORS — floor tiles ──────────
        { "interior", 12, 13, 1, 1, "floor_parquet" },  // orange chevron
        { "interior", 12, 11, 1, 1, "floor_stone"   },  // grey stone
    };

    public static void main(String[] args) throws Exception {
        Map<String, BufferedImage> sheetCache = new HashMap<>();
        File texDir = new File("textures");
        texDir.mkdirs();

        for (Object[] pick : PICKS) {
            String sheetKey = (String) pick[0];
            int col      = (int) pick[1];
            int row      = (int) pick[2];
            int wCells   = (int) pick[3];
            int hCells   = (int) pick[4];
            String name  = (String) pick[5];

            Object[] info = SHEETS.get(sheetKey);
            String path  = (String) info[0];
            int ts       = (int) info[1];
            int step     = (int) info[2];
            int margin   = step - ts;

            BufferedImage sheet = sheetCache.computeIfAbsent(path, p -> {
                try { return ImageIO.read(new File(p)); }
                catch (Exception e) { throw new RuntimeException(e); }
            });

            int srcX = col * step;
            int srcY = row * step;
            int srcW = ts * wCells + margin * (wCells - 1);
            int srcH = ts * hCells + margin * (hCells - 1);

            BufferedImage sub = sheet.getSubimage(srcX, srcY, srcW, srcH);

            int outW = srcW * ZOOM;
            int outH = srcH * ZOOM;
            BufferedImage out = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = out.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(sub, 0, 0, outW, outH, null);
            g.dispose();

            File outFile = new File(texDir, name + ".png");
            ImageIO.write(out, "png", outFile);
            System.out.printf("  %s  (%dx%d)  from %s (%d,%d)%n",
                outFile.getPath(), outW, outH, sheetKey, col, row);
        }
    }
}
