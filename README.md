# MapLang

A custom domain-specific language (DSL) for designing and rendering 2D game maps with a **3/4 perspective** (top-down oblique) view.

Built with **ANTLR4** for parsing and **Java AWT** for rendering.

## Project Structure

```
ELSD/
├── src/            # Java source files & ANTLR grammar
│   ├── MapLang.g4              # Language grammar
│   ├── Main.java               # Entry point (parse + render)
│   ├── AstBuilder.java         # Visitor that builds the AST
│   ├── AstNodes.java           # AST node classes (MapAst, TileAst, PlayerAst)
│   ├── RenderMap.java          # 3/4 perspective renderer
│   ├── TextureGenerator.java   # Programmatic pixel-art texture generator
│   └── MapLang*.java           # ANTLR-generated lexer/parser/visitor
├── maps/           # Map definition files (.map)
├── textures/       # Tile PNG textures (32x32 ground, 32x64 objects)
├── output/         # Rendered PNG images
└── .gitignore
```

## Prerequisites

- **JDK 25** (Eclipse Adoptium or similar)
- **ANTLR 4** JAR at `C:\antlr\antlr.jar`

## Usage

### Compile (only needed after changing `.java` files)

```powershell
javac -cp "src;C:\antlr\antlr.jar" -d src src\*.java
```

### Render a map

```powershell
java -cp "src;C:\antlr\antlr.jar" Main maps\test.map
```

Output is saved to `output\<mapname>.png` (e.g. `maps\test.map` → `output\test.png`).

### Regenerate textures (optional)

Only needed if you modify `TextureGenerator.java`:

```powershell
java -cp src TextureGenerator
```

## Language Syntax

```
map <Name> {
  size <Width>, <Height>;

  tile <TYPE> at (<X>, <Y>);
  ...

  player at (<X>, <Y>);
}
```

### Example

```
map Village {
  size 16, 12;

  tile PATH at (0,6);
  tile PATH at (1,6);
  tile PATH at (2,6);
  tile PATH at (3,6);

  tile HOUSE at (5,3);
  tile TREE at (1,2);
  tile TREE at (8,4);
  tile ROCK at (10,7);
  tile WATER at (12,9);
  tile WATER at (13,9);
  tile FLOWER at (3,4);
  tile FENCE at (4,5);

  player at (2,5);
}
```

## Available Tile Types

### Ground tiles (32×32) — replace the auto-filled grass

| Type     | Description                                                      |
| -------- | ---------------------------------------------------------------- |
| `GRASS`  | Green ground (auto-filled everywhere, no need to place manually) |
| `PATH`   | Dirt road                                                        |
| `DIRT`   | Bare earth                                                       |
| `WATER`  | Blue water                                                       |
| `FLOWER` | Grass with flowers                                               |
| `FENCE`  | Wooden fence                                                     |

### Object tiles (32×64) — rendered with height and depth sorting

| Type    | Description           |
| ------- | --------------------- |
| `TREE`  | Tree with canopy      |
| `ROCK`  | Boulder               |
| `HOUSE` | Cottage with red roof |

## Rendering Details

- **Auto-fill**: The entire grid is filled with `GRASS` automatically — you only place other tile types where needed.
- **3/4 Perspective**: Object tiles (tree, rock, house, player) are taller than one grid cell and visually extend upward, giving depth.
- **Depth Sorting**: Objects and the player are drawn back-to-front by Y coordinate, so closer objects correctly overlap farther ones.
- **Custom Textures**: Drop any `yourtype.png` into `textures/` and use `tile YOURTYPE at (X, Y);`. If the texture is 32×32 it's treated as ground; if taller (e.g. 32×64), it's treated as a depth-sorted object.

## Coordinates

- `(0, 0)` is the **top-left** corner
- `(Width-1, Height-1)` is the **bottom-right** corner
- X increases to the right, Y increases downward
