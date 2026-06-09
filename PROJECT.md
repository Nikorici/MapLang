# MapLang

> A small domain-specific language for designing, composing and exploring
> 2D top-down tile maps.

---

## Table of contents

1. [Purpose](#1-purpose)
2. [Idea and solution](#2-idea-and-solution)
3. [Architecture and tools](#3-architecture-and-tools)
4. [Project structure](#4-project-structure)
5. [Language reference](#5-language-reference)
6. [Engine features](#6-engine-features)
7. [Asset packs and texture system](#7-asset-packs-and-texture-system)
8. [Demo maps](#8-demo-maps)
9. [How to run](#9-how-to-run)
10. [Implementation highlights](#10-implementation-highlights)
11. [Known limitations](#11-known-limitations)
12. [Future improvements](#12-future-improvements)

---

## 1. Purpose

MapLang is a small domain-specific language for **designing 2D top-down
tile worlds**. The goal is not to build a game engine but to make
*level design itself* feel like writing in a language: declarative,
composable, and immediately previewable.

A `.map` file looks like a level designer's notebook:

```
import "buildings.map";
import "town.map";

map Eldgate {
  size 40, 24;

  neighbor east "farmstead.map";
  neighbor west "harbor.map";

  place Castle at (12, 0);
  place Cottage at (1, 17);
  place Inn at (32, 17);

  portal at (19, 11) -> "castle_interior.map";

  row STONE from (0, 13) to (39, 13);
  tile BANNER_GREEN at (29, 13);

  player at (19, 22);
}
```

Reading the file you can immediately see the whole world: a road, a
castle with an enterable interior, two neighbors to walk to, a player
spawn. That readability *is* the point.

---

## 2. Idea and solution

### The problem with map editors

Standard tile-editors (Tiled, RPG Maker, custom Unity inspectors) treat
maps as **arrays of tile indices**. Editing is point-and-click; the
saved file is opaque binary or huge JSON. A handful of consequences
follow:

- you can't *read* a map; you have to open it in a tool
- you can't easily diff two maps or review someone else's level
- you can't reuse "a house" — you have to copy hundreds of cells
- you can't parameterise or generate scenes from rules
- there's no language stack: maps and assets are siloed

### The MapLang answer

MapLang treats a scene as a **program**, not a data file. The
fundamental gestures are:

| Gesture     | Syntax                                | Effect |
|-------------|---------------------------------------|--------|
| Place a tile| `tile WATER at (10, 5);`              | one cell |
| Fill region | `fill GRASS from (0,0) to (39,23);`   | rectangle |
| Define part | `part Castle { … }`                   | reusable fragment |
| Instance it | `place Castle at (12, 0);`            | translated copy |
| Connect map | `neighbor east "forest.map";`         | edge crossing |
| Enter scene | `portal at (5, 9) -> "interior.map";` | scene swap |
| Return up   | `portal at (4, 7) -> back;`           | matched return |
| Import lib  | `import "buildings.map";`             | bring in parts |

A *part* is the killer abstraction. A castle defined once is **reused
ten times across three maps** with three lines. A building is itself
composed of roof + wall + window + door parts — composition all the way
down.

### The reading experience

Because the language reads top-down (parts first, then the main map),
a `.map` file works like a stage script:

1. **The cast** — `part Cottage`, `part Inn`, `part Tower`
2. **The set** — `fill WATER`, `row STONE`
3. **The blocking** — `place Cottage at (3, 7); place Inn at (16, 5);`
4. **The exits** — `neighbor east "...";  portal at (...) -> "...";`
5. **The actor** — `player at (...);`

You can hand a `.map` to someone who's never used MapLang and they can
*describe the scene out loud*.

---

## 3. Architecture and tools

### Stack

| Layer        | Technology                       |
|--------------|----------------------------------|
| Grammar      | **ANTLR 4.13.2** (Java target)   |
| AST + visitor| Plain Java POJOs                 |
| Interpreter  | Java (no separate IR)            |
| Renderer     | Java 2D / `BufferedImage`        |
| Live viewer  | Swing (`JPanel`, `KeyListener`)  |
| Hot reload   | NIO `WatchService`               |
| Assets       | PNGs via `javax.imageio.ImageIO` |
| Build        | `javac` (no Maven / Gradle)      |
| Java target  | JDK 25                           |

### Pipeline

```
.map file ──> ANTLR lexer ──> ANTLR parser ──> AST
                                                │
                                                ▼
                                          AstBuilder
                                          (visitor)
                                                │
                                                ▼
                                            MapAst
                                                │
                              ┌─────────────────┴─────────────────┐
                              ▼                                   ▼
                        RenderMap.render                  PlayMap.launch
                        (writes PNG)                      (Swing window)
                                                                │
                                                                ▼
                                                          WatchService
                                                          (hot reload)
```

ANTLR generates a lexer, parser, and visitor base class. `AstBuilder`
extends `MapLangBaseVisitor<Object>`. Each visitor method either
returns an AST node (`TileAst`, `PartAst`, …) or `null`. A small
recursive helper `addNodes` walks lists and flattens them into the
final `MapAst`.

Imports are handled by recursively constructing a child `AstBuilder`
that **shares** the parts registry and the imported-paths set with the
parent — so a tree of imports collapses into one shared part library
without re-parsing the same file twice.

---

## 4. Project structure

```
MapLang/
├── PROJECT.md                  ← this file
├── README.md                   ← legacy
│
├── src/                        ← Java + grammar
│   ├── MapLang.g4              ← ANTLR grammar
│   ├── MapLang*.java           ← generated lexer / parser / visitor
│   ├── AstNodes.java           ← MapAst, PartAst, TileAst, NeighborAst, PortalAst, PlayerAst
│   ├── AstBuilder.java         ← grammar visitor → AST
│   ├── RenderMap.java          ← static PNG renderer + texture loader
│   ├── PlayMap.java            ← Swing interactive viewer
│   ├── Main.java               ← CLI entry point
│   ├── TextureGenerator.java   ← procedural fallback textures
│   ├── AnimFrames.java         ← procedural animation frames
│   ├── SheetExtract.java       ← extracts tiles from Kenney sheets
│   ├── SheetView.java          ← debug: labeled-grid view of a sheet
│   └── SheetView2.java         ← same, for marginless sheets
│
├── lib/
│   └── antlr.jar               ← ANTLR runtime + tool
│
├── parts/                      ← reusable `part` libraries
│   ├── buildings.map           ← Castle, Watchtower, Cottage (brick)
│   ├── town.map                ← Cottage, WoodCottage, Inn, Shop, Garden
│   ├── featured.map            ← Fountain, MarketStall, Lighthouse,
│   │                             ChickenCoop, CropField, Pasture
│   ├── cabins.map              ← BigCabin, TallHouse, Hut, Pond, Plot, Lamppost
│   └── pixel.map               ← HouseBlue/Orange/Red/SmallBlue, PondBig,
│                                 BushTile, BenchTile, TallOak, TallPine
│
├── maps/                       ← scenes
│   ├── eldgate.map             ← FLAGSHIP — forest-clearing village
│   ├── harbor.map              ← west neighbor (coastal)
│   ├── farmstead.map           ← east neighbor (pastoral)
│   ├── rivervale.map           ← riverside village split by a bridged river
│   ├── pixelvillage.map        ← pixel-asset village (multi-cell sprites)
│   ├── castle_interior.map     ← portal target (throne room)
│   └── cottage_interior.map    ← portal target (furnished home)
│
├── textures/                   ← 32×32 / 32×64 PNGs (one per tile type)
│   └── *.png                   ← grass, water1/2/3, torch1/2, …
│
├── assets/                     ← unmodified third-party tile packs
│   ├── kenney_roguelike-rpg-pack/
│   ├── kenney_tiny-town/
│   ├── kenney_pico-8-city/
│   ├── kenney_rpg-urban-pack/
│   ├── kenney_platformer-art-pixel/
│   ├── farm_rpg/
│   ├── modern_interiors/
│   └── woods_v2/
│
└── output/                     ← rendered PNGs (gitignored)
```

---

## 5. Language reference

### Top-level structure

```
program       : importStmt* partDecl* mapDecl EOF ;

importStmt    : IMPORT STRING SEMI ;
partDecl      : PART ID LBRACE letStmt* sizeDecl statement* RBRACE ;
mapDecl       : MAP ID LBRACE letStmt* sizeDecl statement* RBRACE ;
sizeDecl      : SIZE expr COMMA expr SEMI ;
```

A file declares **any number of imports**, **any number of parts**, and
**exactly one map**. The map is what gets rendered or played; the
parts are reusable fragments.

A `.map` file can play a dual role:

- **library** — defines parts plus a `Preview` map so it's still
  playable for live iteration
- **scene** — imports libraries and places parts to build a world

### Statements

| Statement | Effect |
|-----------|--------|
| `tile TYPE at (x, y);` | one tile at a cell |
| `fill TYPE from (x1,y1) to (x2,y2);` | rectangular fill |
| `row TYPE from (x1,y1) to (x2,y2);` | line from A to B (one tile thick) |
| `random TYPE count N in (x1,y1) to (x2,y2);` | N random non-overlapping tiles in a box |
| `repeat I from A to B { … }` | loop with index variable `I` |
| `let NAME = expr;` | bind a number |
| `player at (x, y);` | spawn position |
| `place NAME at (x, y);` | instance a part, all tiles translated by (x,y) |
| `neighbor DIR "file.map";` | walk off DIR-edge → load that file |
| `portal at (x, y) -> "file.map";` | step on cell → load that file |
| `portal at (x, y) -> back;` | step on cell → return to previous map |

`DIR` is one of `north`, `south`, `east`, `west`.

### Expressions

```
expr : expr (STAR|SLASH|MOD) expr   # MulDivMod
     | expr (PLUS|MINUS) expr       # AddSub
     | NUMBER                       # Num
     | ID                           # Var (must be defined with `let`)
     | LPAREN expr RPAREN           # Paren
     ;
```

Examples:

```
let GRID = 16;
let CENTER_X = GRID / 2;
size GRID * 2, GRID;
tile FLOWER at (CENTER_X, GRID - 1);
```

### Examples — every construct in one place

```
import "buildings.map";          // pulls in Castle, Watchtower

let W = 32;
let H = 22;

part Plaza {                     // reusable fragment
  size 3, 3;
  fill STONE from (0,0) to (2,2);
  tile TORCH at (1,1);
}

map World {
  size W, H;

  neighbor east "forest.map";    // edge crossing

  fill GRASS from (0,0) to (W-1, H-1);
  row PATH from (0, H/2) to (W-1, H/2);

  place Castle at (10, 1);       // imported
  place Plaza  at (4, 12);       // local

  random FLOWER count 12 in (1,1) to (W-2, H-2);

  repeat i from 0 to 4 {
    tile TREE at (i*3 + 2, 18);
  }

  portal at (17, 11) -> "castle_interior.map";

  player at (1, 11);
}
```

---

## 6. Engine features

### Static renderer (`RenderMap`)

- Loads all PNGs from `textures/`, grouped into animation frames by
  filename suffix (see [section 7](#7-asset-packs-and-texture-system))
- Tile types whose texture is taller than one cell (32×64) are treated
  as **objects** and depth-sorted by Y so closer objects overlap
  farther ones
- Ground tiles (≤ 32 px high) fill the grid; objects render above
- Player drawn at its cell, also Y-sorted with the objects
- Single PNG output: `output/<mapname>.png`

### Live viewer (`PlayMap`)

A Swing `JPanel` that re-renders every frame. Features:

- **WASD / arrow keys** to walk a player around
- **Smooth slide animation** between tiles (16 ms timer)
- **Input buffering** — one pending direction queued during the slide;
  chained presses feel responsive
- **Tile-type collision** — a tile type in the `walls` set blocks
  movement (`WATER`, `TREE`, `BRICK`, `TORCH`, `WALL_STONE`, …)
- **Hot reload** — a daemon `WatchService` thread on the map's
  directory; saving the current `.map` file re-parses and rebuilds
  the world in ≤ 200 ms, with debouncing for editor double-saves
- **Toast banner** — `RELOADED` (green) on success,
  `PARSE ERROR — fix and save` (red) on failure, last good state kept
- **`R` resets** the player to spawn, **`Esc` quits**

### Animation system

A simple filename convention:

```
textures/water.png       ← static, single frame
textures/water1.png      ┐
textures/water2.png      ├─  cycled as WATER (3 frames @ 220 ms)
textures/water3.png      ┘
textures/torch1.png      ┐
textures/torch2.png      ┘   cycled as TORCH (2 frames @ 220 ms)
```

The loader (`RenderMap.loadTextures`) groups files by stripping
trailing digits from the basename. Numbered frames win over an
unnumbered fallback. `RenderMap.currentFrame(frames, ms)` picks
`frames[(ms / 220) % len]`. No grammar changes — animation lives in
the asset layer.

The viewer's repaint timer keeps the canvas updating whenever any
loaded tile type has more than one frame.

### Edge crossings (`neighbor`)

`tryMove` intercepts attempts to step off the grid. If the map
declares a `neighbor <dir> "<file>"`, the engine:

1. Resolves the path relative to the current map's directory
2. Parses the neighbor file into a fresh `MapAst`
3. Places the player on the **matching opposite edge** with the same
   perpendicular coordinate (walking off east at y=8 lands at x=0, y=8
   in the east neighbor)
4. If that cell is a wall, scans along the entry edge for the nearest
   walkable cell — the player never gets stuck on entry
5. Resizes the window, updates the title, flashes `→ <DestName>`

Walking off an edge with no `neighbor` declared simply blocks
movement.

### Portals (`portal`)

Two forms:

```
portal at (x, y) -> "interior.map";   // explicit destination
portal at (x, y) -> back;             // return to wherever you came from
```

The engine swaps maps when the player steps onto the portal cell. The
new map's `player at (...)` is used as the spawn unless a return
position is pending (next subsection).

### Return-position memory

When the player enters a portal **for the first time** (the pending
slot is empty), the engine stores `(currentMapPath, prevX, prevY)` —
the cell they were standing on *before* the portal.

On any subsequent map swap whose destination matches that stored
path, the player is placed at `(prevX, prevY)` instead of the
destination's default spawn, and the slot is cleared.

This is a single-slot design (not a stack), but it covers the common
case cleanly: you enter a cottage, you exit the cottage, you land
right outside the cottage door — regardless of which cottage or which
map you came from. The cottage interior doesn't need to know.

A `back` portal is a one-liner version of "use the pending slot
directly": if pending is empty, the engine flashes
`NOWHERE TO RETURN`.

---

## 7. Asset packs and texture system

### Convention

- Tiles named in code are **uppercase** (`TREE`, `WALL_STONE`).
- The texture file is the lowercase form (`tree.png`, `wall_stone.png`).
- All files live in `textures/`. They can be any size; the renderer
  classifies anything taller than `TILE` (32 px) as an *object* and
  depth-sorts it.

### Extraction pipeline

Raw Kenney / 3rd-party sheets stay untouched in `assets/`. A small
tool, `SheetExtract`, declares the source (sheet, col, row, size) of
each tile we want, crops it, upscales 2× with nearest-neighbour
filtering, and saves a single PNG:

```java
{ "rl", 15, 10, 1, 2, "tree" },         // roguelike sheet (15,10), 1×2 cells → tree.png
{ "tt", 5,  4, 1, 1, "roof_red" },      // tiny town (5,4), 1×1 → roof_red.png
{ "interior", 12, 13, 1, 1, "floor_parquet" },
{ "chicken", 0, 0, 1, 1, "chicken1" },  // first frame of animation
{ "chicken", 1, 0, 1, 1, "chicken2" },  // second frame
```

Rerun whenever the picks change:

```bash
java -cp src SheetExtract
```

### Packs in use

| Pack | Source | License | Used for |
|------|--------|---------|----------|
| Kenney Roguelike RPG | kenney.nl | CC0 | grass, water, trees, banners |
| Kenney Tiny Town     | kenney.nl | CC0 | roofs, walls, doors, signposts, arch |
| Farm RPG 16×16       | itch.io   | free | chickens, cows, crops |
| Modern Interiors v2.2| itch.io   | free | interior floors + furniture (bed, sofa, table, chair, cabinet, bookshelf, lamp, plant, fruit bowl, rug) |
| Pixel Woods v2       | itch.io   | free | multi-cell house sprites, autotiled pond, big trees, bushes, benches, signs |

Packs that were considered and dropped:

- Kenney Pico-8 City — 8×8 modern urban; clashes with the medieval
  pixel-art style
- Kenney RPG Urban — never integrated
- Kenney Platformer Pixel — side-scrolling perspective, not top-down

### Procedural fallbacks

`TextureGenerator` and `AnimFrames` programmatically render a small
base set of textures (`grass`, `water1..3`, `lava1..3`, `torch1..2`)
so the project boots without the Kenney sheets if needed.

---

## 8. Demo maps

### The flagship — `maps/eldgate.map`

A 44×28 forest-clearing village demonstrating every MapLang feature in
one intentional scene (imports four part libraries):

- **Castle** dominating the north (from `parts/buildings.map`)
- **Dense tree perimeter** split around the river-less edges, with a
  sandy east-west road and a vertical path up to the castle gate
- **Sand plaza** at the central crossroads, with a **Fountain**
- **Houses** scattered around: two **Huts**, a **BigCabin**, a
  **TallHouse**, and an **Inn**
- **Garden plot**, **two ponds**, **lampposts** lining the paths,
  signposts at every fork
- **South gate** — stone arch flanked by banners
- `repeat` / `random` / `fill` / `row` / `let` / `tile` all exercised
- **Two portals**: castle gate → throne room, BigCabin door → cottage
  interior
- **Two edge crossings**: west to harbor, east to farmstead

### Connected regions

- **`harbor.map`** — coastal: sea, sandy beach, wooden pier with
  lamps, lighthouse, a coastal cottage. Walking east off its edge
  returns to Eldgate (`neighbor east back`).
- **`farmstead.map`** — pastoral: fenced cow pasture, chicken coop,
  crop fields, farmer's cottage, pond. Walking west off its edge
  returns to Eldgate.

### Interior scenes

- **`castle_interior.map`** — 15×11 throne hall: stone floor, a parquet
  runner from the south door to the throne (sofa) with side tables and
  goblets, three banners behind it, bookshelves, lamps, plants, wall
  torches, and visitor seating.
- **`cottage_interior.map`** — 11×10 furnished home: parquet floor, a
  bedroom corner (bed, rug, bookshelf, lamp), a kitchen corner (cabinets,
  plant, fruit bowl), and a central living room (sofa, coffee table,
  chairs, rug).

Both use `portal at (x,y) -> back;` so the same interior serves any
number of origins.

### Other maps

- `rivervale.map` — a riverside market village: a river bisects the map
  and flows off both edges, crossed by a single stone bridge laid in a
  gap in the water; a town on the west bank and farmland on the east,
  joined by a connected path network to every door.
- `pixelvillage.map` — a village built from the pixel-asset pack:
  multi-cell house sprites, autotiled pond, big trees, benches, signs.

---

## 9. How to run

### Build the Java sources once

```bash
cd ~/Work/Univer/MapLang
javac -cp "src:lib/antlr.jar" -d src src/*.java
```

### Run a map

```bash
# Interactive viewer (Swing window, hot reload)
java -cp "src:lib/antlr.jar" Main maps/eldgate.map --play

# Just render a PNG to output/
java -cp "src:lib/antlr.jar" Main maps/eldgate.map
```

### Controls in the viewer

| Key       | Action |
|-----------|--------|
| W / ↑     | Move up |
| A / ←     | Move left |
| S / ↓     | Move down |
| D / →     | Move right |
| R         | Reset player to spawn |
| Esc       | Quit |

Hold a direction — chained presses queue smoothly through input
buffering.

### Regenerate ANTLR after editing the grammar

```bash
cd src
java -jar ../lib/antlr.jar -Dlanguage=Java -visitor MapLang.g4
cd ..
javac -cp "src:lib/antlr.jar" -d src src/*.java
```

### Regenerate tile textures from raw sheets

```bash
java -cp src SheetExtract     # textures/*.png from Kenney sheets
java -cp src AnimFrames       # water1..3, lava1..3, torch1..2
```

### Recommended tour

1. `java -cp "src:lib/antlr.jar" Main maps/eldgate.map --play`
2. Walk north on the road, enter the castle gate, see the throne room.
3. Exit south through the throne room's door — you land back at the
   castle gate.
4. Walk west off the edge into Harbor; back east to Eldgate.
5. Walk east off the edge into Farmstead; back west to Eldgate.
6. With the window open, edit `parts/featured.map` (e.g. change a
   `tile FLOWER` to `tile MUSHROOM` inside `Fountain`) and save — the
   import-aware reload picks it up.

---

## 10. Implementation highlights

### Parts as a parse-time expansion

A part is *not* a special runtime concept — it's a registered AST
fragment. When `place X at (ox, oy)` is visited:

```java
PartAst part = parts.get(name);
List<Object> out = new ArrayList<>(part.tiles.size());
for (TileAst t : part.tiles)
    out.add(new TileAst(t.type, t.x + ox, t.y + oy));
return out;
```

The visitor's flatten step then merges these translated tiles into
the main map. Parts therefore cost nothing at runtime — the renderer
sees only a flat list of cells.

### Shared part registry across imports

```java
public AstBuilder(String sourceFilePath) {
    this.parts = new HashMap<>();
    this.importedPaths = new HashSet<>();
    this.baseDir = …;
}

private AstBuilder(Map<String, PartAst> parts,
                   Set<String> imported,
                   Path baseDir) {
    this.parts = parts;             // <-- shared
    this.importedPaths = imported;  // <-- shared
    this.baseDir = baseDir;
}
```

Imports build a child visitor with the **same** `parts` map and
`importedPaths` set as the parent. The parent's own visit pass then
sees every part registered by the imports. Cycles are caught by the
shared set; duplicate imports are no-ops.

### Frame-cycling animation with zero grammar cost

The loader scans `textures/`, splits each filename into basename +
trailing digits, and groups numbered files together:

```
water1.png, water2.png, water3.png  →  WATER = [img1, img2, img3]
torch1.png, torch2.png              →  TORCH = [img1, img2]
grass.png                           →  GRASS = [img]
```

The renderer picks `frames[(now_ms / 220) % len]` per cell each
frame. A single static water tile in a `.map` file animates
automatically once the second frame file is added to disk.

### Hot reload with debouncing

A daemon thread on the map's directory watches for `ENTRY_MODIFY` /
`ENTRY_CREATE` events. The watcher always compares against the
*current* `mapFilePath`, so after an edge crossing or portal swap it
correctly follows the new active file. A 150 ms debounce ignores
editor double-saves; a 60 ms sleep after the event lets the writer
finish before re-parsing.

### Portal return without a stack

The pending-return logic is a single mutable slot:

```java
// On portal entry, but ONLY if empty (don't overwrite outer journey):
if (pendingReturnMap == null) {
    pendingReturnMap = absolute(currentMapPath);
    pendingReturnX = prevX;
    pendingReturnY = prevY;
}

// On every map swap:
if (pendingReturnMap != null && pendingReturnMap.equals(absolute(newPath))) {
    px = pendingReturnX;
    py = pendingReturnY;
    pendingReturnMap = null;  // consumed
}
```

The slot survives the trip into the interior, isn't overwritten on
the way back, and matches the destination on return. A `back` portal
uses the same slot as its destination, so the same interior file
serves arbitrarily many entry points.

---

## 11. Known limitations

- **Single-slot return memory.** Nested portals (entering an interior
  from an interior) lose the outer return position. Acceptable for
  one-level hierarchies; would require a stack to fix.
- **No parameterised parts.** `part House(roof)` was scoped out;
  currently a coloured roof variant requires a copy of the part. The
  grammar reservation would be straightforward.
- **No auto-tiling.** Wall corners and ends are drawn as the same
  flat tile; the engine has no rules for picking the right edge
  sprite. A `connects` declaration was discussed but not built.
- **Walkability is a hard-coded set in `PlayMap.walls`.** Adding a
  new wall-type tile requires editing Java. A grammar-level
  `blocking TYPE;` declaration would generalise this.
- **Multi-tile sprites are convention, not grammar.** A tall PNG is
  drawn as one image anchored at its bottom-left cell (see `pixel.map`'s
  house sprites), but the rest of its footprint must be covered by hand
  with invisible ground-tile blockers — the grammar has no native
  "this sprite occupies W×H cells" declaration.
- **Same-directory neighbours only.** The file watcher follows the
  current map's directory. A `neighbor` or `portal` pointing into a
  different directory works for the first swap but stops hot-reloading.
- **Single player.** There is no concept of NPCs, enemies, or
  scripted entities; an earlier combat prototype was removed in
  favour of pure map-design focus.

---

## 12. Future improvements

Ranked by impact per effort.

1. **Auto-tiling.** Declare `tile WALL connects WALL;` and have the
   renderer pick corner / T / end sprites automatically. Single
   biggest visual upgrade — the difference between "rectangles of
   identical stones" and "buildings that actually look built."
2. **Parameterised parts.** `part House(roof) { tile ROOF_${roof} … }`
   — one definition, every cottage in the kingdom in a different
   colour, no copies.
3. **`blocking TYPE;` grammar.** Move the wall-list out of Java into
   the asset-pack files; each library declares its own blocking
   tiles.
4. **Portal stack.** Replace the single-slot return memory with a
   stack so nested interiors return correctly through multiple levels.
5. **Transition effects.** A fade / wipe between map swaps. Cheap to
   implement, makes the demo feel polished.
6. **Day/night tint.** One global `time` property that re-tints the
   palette per cell. The map is the same; the *feeling* changes.
   Strong demo trick.
7. **Inline editor.** A panel inside the viewer that shows the live
   `.map` source and lets you click-to-edit a tile. Bigger lift but
   closes the design loop entirely.
8. **NPC / signpost text.** A `say "..."` attached to a tile so
   walking over it shows a message. Adds narrative without bringing
   back the game-logic complexity that was removed.
9. **Tiled / TMX export.** Emit a `.tmx` file so MapLang scenes can
   be loaded into real game engines. Promotes MapLang from "school
   project" to "useful tool."
10. **VS Code language extension.** Syntax highlighting, hover docs
    for tile types, go-to-definition for parts. The language is
    small enough to support it well.

---

## Credits

- **Author:** Daniel Nicorici
- **Grammar tooling:** ANTLR 4 (Terence Parr et al.)
- **Tile art:**
  - Kenney Roguelike RPG Pack — Kenney.nl (CC0)
  - Kenney Tiny Town — Kenney.nl (CC0)
  - Farm RPG FREE 16×16 — itch.io (free, credit retained in `assets/`)
  - Modern Interiors Free v2.2 — itch.io (free, credit retained in `assets/`)
  - Pixel Woods v2 — itch.io (free, credit retained in `assets/`)

Built as a university project. Source code MIT-equivalent; tile art
under each pack's own license — see `assets/<pack>/License.txt`.
