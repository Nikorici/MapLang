# MapLang

> A small domain-specific language for designing, composing and
> exploring 2D top-down tile maps. Write a level like a notebook —
> declarative, composable, and immediately previewable.

Built with **ANTLR 4** (parsing) and **Java 2D / Swing** (rendering +
live viewer). For the full design write-up see **[PROJECT.md](PROJECT.md)**.

```
import "featured.map";

map Demo {
  size 20, 12;
  fill WATER from (0,0) to (4,11);     // a little sea
  row  PATH  from (5,6) to (19,6);     // a road
  place Fountain at (9,3);             // a reusable part
  random FLOWER count 8 in (6,1) to (18,10);
  portal at (12,6) -> "cottage_interior.map";
  player at (6,6);
}
```

---

## Quick start

JDK 25 is the only requirement; the ANTLR runtime is bundled in
`lib/antlr.jar`.

```bash
# Compile the Java sources once (only after editing *.java)
javac -cp "src:lib/antlr.jar" -d src src/*.java

# Render a map to output/<name>.png
java -cp "src:lib/antlr.jar" Main maps/eldgate.map

# Open the interactive viewer (WASD/arrows to walk, hot-reloads on save)
java -cp "src:lib/antlr.jar" Main maps/eldgate.map --play
```

On Windows PowerShell, swap the classpath separator `:` for `;`.

**Viewer keys:** `W A S D` / arrows move · `R` reset to spawn · `Esc` quit.

---

## File anatomy

The grammar fixes one shape per file:

```
program : importStmt* partDecl* mapDecl EOF ;
```

…so every `.map` file is, in order: **imports**, then **part
definitions**, then **exactly one `map`**. The map is what renders; the
parts are reusable fragments. A library file (`parts/*.map`) ships its
parts plus a small `Preview` map so it stays openable on its own.

- Coordinates: `(0,0)` is **top-left**; X grows right, Y grows **down**.
- The grid auto-fills with `GRASS` — you only place what differs.
- A tile type is any uppercase name; `tile WATER` draws `textures/water.png`.
  Drop a new PNG into `textures/` and the name just works.

## Statements

| Statement | Effect |
|---|---|
| `size W, H;` | grid dimensions (once, required) |
| `let NAME = expr;` | bind an integer (arithmetic: `+ - * / %`, parens) |
| `tile TYPE at (x,y);` | one cell |
| `fill TYPE from (x1,y1) to (x2,y2);` | filled rectangle |
| `row TYPE from (x1,y1) to (x2,y2);` | one-thick line |
| `random TYPE count N in (x1,y1) to (x2,y2);` | N random non-overlapping tiles in a box |
| `repeat I from A to B { … }` | loop; index `I` usable in expressions |
| `place NAME at (x,y);` | stamp a part, every tile offset by (x,y) |
| `player at (x,y);` | spawn position |
| `neighbor DIR "file.map";` / `neighbor DIR back;` | walk off the DIR edge → load that file (or return to sender) |
| `portal at (x,y) -> "file.map";` / `-> back;` | step on the cell → swap maps (or return) |

`DIR` ∈ `north | south | east | west`. Comments are `// …`.

## Parts — the key abstraction

A `part` is a named fragment with its own local origin:

```
part Fountain {
  size 3, 3;
  fill STONE from (0,0) to (2,2);
  tile WATER at (1,1);
}
```

`place Fountain at (8,9);` copies every tile, **adding (8,9)** to each
local coordinate. It's a parse-time expansion — parts cost nothing at
runtime, and imports share one part registry across files (mind name
clashes: `buildings.map` and `town.map` both define `Cottage`).

`neighbor` and `portal` stitch scenes together; `back` uses a
single-slot return memory, so one interior file can serve every door
that leads into it.

---

## Three rules that drive layout

The renderer and the collision model decide how you compose a scene:

1. **Ground vs object is by texture height.** A texture ≤ 32px tall is
   ground (fills its cell); taller textures (trees, houses) are objects,
   drawn bottom-anchored and Y-sorted for a 3/4-perspective overlap.
2. **Last placed wins (visually).** Among ground tiles at the same cell,
   the later one covers the earlier — so paint bottom-up: water/sand
   first, then paths, then a bridge deck on top.
3. **Collision checks *every* tile in a cell.** A cell is blocked if
   **any** tile there is a wall (`WATER`, `LAVA`, trees, `WALL_*`,
   `WINDOW_*`, `ROOF_*`, `FENCE`, `TORCH`, `BANNER*`, `SIGNPOST`,
   `CHICKEN`, `COW`, house sprites). Everything else — grass, paths,
   floors, `DOOR_*`, crops, flowers — is walkable.

Two corollaries: **grass is walkable**, so paths are guidance, not
plumbing; and a `BRIDGE` painted over `WATER` is *still blocked* (the
water tile is still there). To get a crossable bridge, leave a **gap in
the water fill** and lay the deck in the gap — see `maps/rivervale.map`.

## Authoring a map — the workflow

The order the language wants you to think in:

1. **Concept + size.** `let W = 42; let H = 28; size W, H;`
2. **Imports.** Pull in the part libraries you need; check for name clashes.
3. **Base ground, bottom-up.** `fill` water/sand, then the `random`
   scatter early so later buildings/paths cover stray tiles.
4. **Structure.** `repeat` loops for borders, `row` for roads, the
   bridge deck *last* over its water gap.
5. **`place` buildings region by region**, tracking each part's footprint
   and door cell so nothing overlaps a river or an edge exit.
6. **Connect with paths** — trace each entrance back to the main road.
7. **Wire the exits** — `neighbor` aligned to the road rows, `portal`s
   on walkable door cells.
8. **Place the player** somewhere that reveals the scene.
9. **Iterate live** — run with `--play` and edit the `.map` (or an
   imported part); saving re-parses and rebuilds in ≤200ms.

---

## Layout

```
src/        Java sources + MapLang.g4 grammar (ANTLR-generated lexer/parser/visitor)
lib/        antlr.jar (runtime + tool)
parts/      reusable part libraries: buildings, town, featured, cabins, pixel
maps/       scenes: eldgate (flagship), harbor, farmstead, rivervale,
            pixelvillage, castle_interior, cottage_interior
textures/   one PNG per tile type (32×32 ground, taller = objects; numbered = animated)
assets/     unmodified third-party tile packs (Kenney, Farm RPG, Modern Interiors, Pixel Woods)
output/     rendered PNGs (gitignored)
```

## Credits

Author: **Daniel Nicorici** · Grammar tooling: ANTLR 4. Tile art under
each pack's own license (Kenney CC0; Farm RPG, Modern Interiors, Pixel
Woods free) — see `assets/<pack>/License.txt`. Built as a university
project.
