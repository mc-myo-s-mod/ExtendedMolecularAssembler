# Forge 1.20.1 rebuild

Updated: 2026-09-28

The Forge sources were rebuilt from the NeoForge 1.21.1 implementation against
Forge 47.4.17, AE2 15.4.10, Java 17 and Myotus 15.1.0 from Maven Central.
The pre-rebuild sources remain outside the repository in
`H:/IntelliJ/Minecraft/.codex-tmp/ema-forge-rebuild-20260905`.

## Shared and version-specific code

- `common` owns unchanged 1.21.1 textures, translations, guides, compatible models,
  blockstates and screen styles. Both loaders compile shared Java directly.
- Assembler speed profiles, Quantum batch calculations and recipe-bridge IDs are
  shared Java 17 code. `:common:check` executes their assertion-based checks.
- Forge owns NBT pattern storage, capability lifecycles, SimpleChannel networking,
  native menu registration, recipe serializers and Forge mixin mappings.
- Forge assembler models use AE2 1.20.1's `outside` texture binding and cutout render
  layer. The shared textures are not converted or replaced.
- The terminal's emissive model uses `forge_data`; the wireless style includes
  AE2WTLib 15's `universal_terminal_with_viewcells.json`. These small adapters stay
  in the loader modules.
- Assembler layout is shared, but its includes are loader-specific: AE2 15's
  `common.json` already includes `toolbox.json`. Including it twice causes a
  `Recursive style includes` exception when opening the screen.
- Guide text remains in common. Forge resource processing substitutes the
  ExtendedAE integration ID with `expatternprovider`; Forge screens use explicit
  namespaced guide anchors because AE2 15's JSON help-topic parser forces `ae2`.
  The Forge development watcher reads processed resources too; run
  `:forge-1-20-1:processResources` after editing shared guide text to preview it.

## Content

Assembler/Ex assembler, providers, wired/wireless encoding terminals, extended-table
patterns, JEI/EMI transfer, matrix cores/Plus cores/uploader, pattern viewing and
Export-mode crafting confirmation are implemented using the matching Forge APIs.
Optional content is guarded by Myotus annotation integrations, without reflection.
Quantum content remains AdvancedAE-only WIP with no crafting recipe.

### Epic and Legendary wireless terminals (2026-09-28)

- Epic and Legendary wireless pattern terminals reuse the existing host/menu/screen
  with 11x11 and 13x13 grids. Their AE2WTLib styles include view cells, upgrades,
  singularity slots and universal-terminal cycling.
- Each recipe combines its wired terminal with a wireless receiver and dense
  energy cell. Both tiers also have universal-terminal upgrade recipes; items and
  recipes load only with AE2WTLib.
- WUT factories capture a fixed grid size. Each tier stores pattern data and its
  remembered recipe table under a separate NBT key; the existing 9x9 key is unchanged.
- Forge compilation, datagen, both loader builds and common checks passed. All 41
  GameTests passed with Reforked + ExtendedAE + AE2WTLib and again without optional
  integrations (unavailable integration cases skip). The new test checks direct
  item/WUT menu capacities, stable host sizes and separate last-slot/table NBT roundtrips.
- The JEI client reached the title screen and passed all 15 native style checks
  without EMA model/texture errors. All 183 Forge PNG/JSON and generated data
  resources match the built JAR. This includes the terminal edge layers and the
  assembler frame revisions described below.
- A dedicated `runServer` reached `Done` in a separate smoke-test world without
  opening the existing development world. ExtendedAE's known `ex_emc_interface`
  loot diagnostic remains unrelated to EMA.
- Actual wireless linking, encoding and WUT-cycle clicks remain manual checks;
  title-screen style checks do not verify those interactions.

Hovering an encoded extended crafting pattern and pressing ExtendedAE's preview
key (default `P`) opens its 9x9, 11x11 or 13x13 preview. Forge ExtendedAE's `CPatternKey` dispatch
is extended for EMA patterns; the original key binding and packet are reused.

ExtendedAE 1.20.1 has neither Crystal Assembler recipes nor a Concurrent Processor.
The native crafting equivalents are:

- Ex assembler: four EMA, one Compat Processor, one Engineering Processor,
  one Ender Dust and one Acceleration Card (shapeless).
- Matrix core: one matrix wall, one Ex EMA and one Compat Processor, with two each
  of the three corresponding lumen paintballs. Blue/light blue/cyan makes the
  crafting core; purple/magenta/pink makes the pattern core.

## Large Extended Crafting tables

Epic uses an 11x11 terminal/assembler and Legendary uses a 13x13 terminal/assembler.
Each tier has separate matrix crafting/pattern cores and an Export-mode provider.
Expanded adds Epic; Expanded Reforked adds both tiers. These replace Extended
Crafting and share its mod ID: install only one Extended Crafting implementation.
Recipes require the corresponding table item, so missing tiers have no recipes.

Development runs can choose one fork without changing the original compile API:

```text
-Penable_excrafting=true -PextendedCraftingRuntime=curse.maven:extended-crafting-expanded-1017786:5613251
-Penable_excrafting=true -PextendedCraftingRuntime=curse.maven:extended-crafting-expanded-reforked-1531359:8596061
```

The new placeholder PNGs live in this module's `src/main/resources/assets`:

- `ae2/textures/guis/{epic,legendary}_molecular_assembler.png`
- `ae2/textures/guis/{epic,legendary}_pattern_encoding_terminal.png`
- `extendedmolecularassembler/textures/block/{epic,legendary}_molecular_assembler.png`
- `extendedmolecularassembler/textures/block/assembler_matrix/{epic,legendary}_assembler_matrix_{crafting,pattern}_core.png`
- `extendedmolecularassembler/textures/block/provider/{epic,legendary}_me_crafting_provider.png`
- `extendedmolecularassembler/textures/part/{epic,legendary}_pattern_encoding_terminal_edge.png`

GUI canvases are 512x512; matching JSONs are under
`assets/ae2/screens/extended_molecular_assembler`.
Matrix pattern-core screens reuse ExtendedAE's `ae2:textures/guis/assembler_matrix.png`;
their right-side navigation buttons remain outside the original 195x199 background.
Forge pattern previews use AE2's `generatedBackground` at 9x9, 11x11 and 13x13,
so neither screen family needs its own placeholder PNG.

The 2026-09-27 background refresh passed Forge `compileJava` and `build`, plus
all 13 client style checks (including unchanged preview/inventory slot positions).
The final JAR excludes the five retired placeholder PNGs. In-world interaction
still needs manual confirmation; this resource refresh has not been deployed.

### Large-tier palette and Ex variants (2026-09-27)

- Epic accents use `#504650` / `#403640`; Legendary accents use `#D80000` /
  `#A10303`, sampled from ExtendedTerminal's two-tone edge textures.
- The Forge terminal layers and matrix-core accents use those palettes.
  Fixed terminal accent faces no longer multiply the cable tint; the
  remaining terminal model retains its existing tint behavior.
- Recoloring preserves the original 16x16 geometry, transparency and neutral
  metal pixels. Originals are backed up in `output/tier-palette-backup-2026-09-27`.
- `ex_epic_molecular_assembler` (11x11) and `ex_legendary_molecular_assembler`
  (13x13) reuse the existing Ex eight-lane implementation and speed/power config.
  They register only with ExtendedAE. Their recipes also require Extended
  Crafting and the corresponding table item; loot and mining tags remain optional.
- `compileJava`, `runData` and `build` passed. All 40 GameTests passed separately
  with Reforked + ExtendedAE, Expanded + ExtendedAE, and optional integrations
  disabled (unavailable integration tests skip). Ex checks cover all eight lane
  inventories across save/reload, full-queue rejection, and powered completion
  for both new block types. Tier-5/6 fixtures check real edge-slot recipe assembly.
- The Reforked + ExtendedAE + AE2WTLib + JEI client reached the title screen and
  passed all 13 style checks without EMA model/texture load errors. All 14 changed
  PNGs match the final JAR byte-for-byte; 17 new resource entries are packaged.
- Dedicated-server verification used `runGameTestServer`; the normal `runServer`
  world was not reopened, to avoid changing the existing development world while
  switching optional-mod profiles. Reused GameTest-world missing-mapping warnings
  and ExtendedAE's existing `ex_emc_interface` loot diagnostic remain unrelated.
- This update has not been deployed to the modpack. In-world visual appearance
  and GUI interactions still need manual confirmation.

### Pattern terminal edge layers (2026-09-27)

- Epic/Legendary on, off and item models reuse the shared bright/medium masks
  and `extended_pattern_encoding_terminal_edge_dark`. Their untinted `edge`
  layer uses each tier's `*_pattern_encoding_terminal_edge` PNG.
- Shared layers retain AE2's 3/2/1 tint indices; the normal terminal keeps its
  original dark mask. PNG artwork is unchanged, and the old recolor script no
  longer expects or generates tier-specific bright/medium/dark copies.
- Build and the client title-screen resource/style checks passed; all 19
  packaged terminal assets match source. World/cable-color visuals still need
  manual confirmation. These changes have not been deployed or committed.

### Current assembler palette (2026-09-28)

- The bright Ex outer frames are replaced with the original dark AE2/ExtendedAE
  frame colors. Extended's normal cyan and muted-lavender Ex trims stay distinct.
- Epic and Legendary swap the complete normal/Ex PNGs from the muted-trim revision:
  normal Epic uses grey-purple, normal Legendary uses burgundy, while their Ex
  variants use the original dark Epic and vivid red Legendary trims.
- Only the existing texture contents change. Models, powered lighting, glass,
  alpha and gameplay behavior are unchanged.
- The user-updated Epic and Legendary crafting-provider textures are included
  with this palette revision.

### Assembler frame refinement (2026-09-27)

- Normal assemblers use AE2's frame pattern, and Ex assemblers use ExtendedAE's
  Ex pattern. Following the screenshot clarification, only the 60 grey-metal
  trim pixels are recolored to each EMA palette, preserving their shading.
  Ex variants additionally tint the 52 dark outer-frame pixels a bright grey-purple,
  preserving their shading. Normal outer frames, glass highlights and alpha stay unchanged.
- Powered inner lighting again uses AE2's original animated model. The mistaken
  four custom light models/textures and their registrations were removed, with
  copies in `output/assembler-grey-trim-backup-2026-09-27` for recovery.
- Only Forge assets are overridden; shared 1.21.1 textures are unchanged.
  Previous effective PNGs are backed up locally in `output/assembler-frame-backup-2026-09-27`.
- `compileJava` and `build` passed. The Reforked + ExtendedAE + AE2WTLib + JEI
  client passed 13 style checks at the title screen without EMA texture/model
  errors. The correction changes only metal/Ex outer-frame pixels and excludes custom-light
  assets from the JAR. In-world appearance remains a manual check.
  These metal-trim and Ex outer-frame corrections were deployed to `ae2-1.20.1`
  at 23:16 on 2026-09-27 without committing. The replaced JAR is backed up in
  `codex-backups/2026-09-27_231615-ema-purple-ex-frames` under the instance.
  Source/deployed SHA-256: `0E012E2EFFB58C6E2A04EAE33EDF67CD7B8C56AC142FF6FF8C5711EF2DB7992F`.
  All six packaged frame textures match source and exactly one EMA mod is installed.
  The modpack was not launched after replacement.
- The approved bright grey-purple preview was applied to all three Ex variants,
  using `#493956`, `#80699E`, and `#B09ACB` while preserving the frame shading.
  Only 52 outer pixels change per Ex; normal variants, alpha, glass and metal trim
  remain unchanged. Build and packaged PNG checks pass against the approved preview.
  This bright-frame revision is superseded by the current palette described above.
- Deployed the previous frame/light revision to `ae2-1.20.1` on 2026-09-27 at 22:52, without
  committing. The previous EMA JAR is backed up under the instance's
  `codex-backups/2026-09-27_225204-ema-assembler-frames` directory. Source and
  deployed SHA-256 both match
  `932377B7E462C4CF7BCA0BE1FCADE2EEA0BD5121443568BEAEC7481D7C4C3980`.
  Exactly one EMA mod is installed; the modpack was not launched after replacement.

### Large-grid verification (2026-09-26)

- Forge `compileJava`, `runData` and `build` passed; `:common:check` passed.
- The shared translation enum also passed NeoForge 1.21.1 `compileJava`.
- All 38 GameTests passed with Reforked, with Expanded, and with optional
  integrations disabled. Tests skip unavailable table tiers, but check fixture
  presence against the actual table registry before doing so.
- Large-grid checks cover pattern NBT/mapping, menu inventory sizes, actual
  tier-5/6 recipe assembly, exact-tier recipe transfer eligibility, and mixed
  matrix clusters dispatching to the matching crafting core instead of a 9x9 core.
- Reforked + ExtendedAE + AE2WTLib clients reached the title screen with JEI and
  with EMI. AE2 parsed all 13 native screen styles without EMA model/texture errors.
- Packaged JAR verification found all 39 required large-grid resource entries.
  In-world visual layout, recipe-viewer transfer clicks and matrix navigation
  remain manual checks; title-screen loading does not verify those interactions.

## Verification (prior 9x9 port)

- Both modules: `compileJava` and `build` passed.
- Forge `runData` passed; generated resources are checked in as source.
- `:common:check` passed.
- Forge `runGameTestServer`: 26 tests passed with no optional mods, with
  ExtendedAE/Extended Crafting/AdvancedAE/AE2WTLib, and with each Avaritia variant.
- Tests include actual block activation to server menu opening, menu locator
  packet round trips, powered single/eight-lane crafting, pattern NBT, missing item
  rejection, matrix dispatch/cancellation, uploader duplicates and the destroyed
  crafting-node regression. Unsupported optional cases skip; tests independently
  compare Forge mod presence with Myotus to catch erroneous skipping.
- Forge client reached the title screen with JEI/EMI and optional integrations;
  no EMA model/texture load errors were reported. Development client runs also
  enable `ema.checkClientStyles` to parse the screen JSON with AE2's own loader.
- All five native Forge screen styles passed that check after separating the
  duplicate toolbox include.
- Final Forge run passed 27 tests including server-selected result-slot sync.
  NeoForge's base-environment GameTests passed all 21 tests.
- The pattern preview test also sends ExtendedAE's serialized `CPatternKey` through
  its server handler, verifies the resulting 7x7 layout and rejects unencoded
  patterns. All 27 Forge tests passed after adding this route.

The client log subsequently recorded a real assembler screen instance in-world.
Visual appearance and full manual interaction still need confirmation; neither
the log nor a server-side menu test is a screenshot/interaction test.

Known dependency diagnostics in optional runs: ExtendedAE's unregistered EMC
interface loot entry, AdvancedAE's client model mixin on a dedicated server, and
Myotus's lowercase Avaritia test-recipe aliases. These are not EMA resources.
