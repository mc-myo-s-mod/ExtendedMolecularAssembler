# Extended Molecular Assembler Forge 1.20.1 Porting Notes

Last updated: 2026-06-02

## Current status

This module is no longer only an empty Forge shell. The first 1.20.1 migration slice is present and compiles.

Implemented so far:

- Forge 1.20.1 mod entrypoint now initializes EMA integration code and registers Forge deferred registries for blocks, items, and block entities.
- `EMAModIntegration` registers an AE2 `IPatternDetailsDecoder` for EMA extended encoded patterns.
- `EncodedExtendedCraftingPattern` stores EMA pattern metadata in item NBT instead of 1.21 DataComponents.
- `ExtendedTableCraftingPattern` is ported to AE2 15 / Minecraft 1.20.1 APIs.
- Vanilla + Extended Crafting recipe adapter layer is ported and compiles on Forge 1.20.1:
  - `RecipeGridHelper`
  - `AbstractTableRecipeAdapter`
  - `ShapedCraftingRecipeAdapter`
  - `ShapelessCraftingRecipeAdapter`
  - `TableRecipeAdapters`
  - `adapter.recipe.extendedcrafting.*`
- `ExtendedPatternTableTypes` is available for shared table metadata.
- `EMAModPresence` uses Forge `ModList`.
- Forge registrations are present for:
  - `EMABlocks`
  - `EMAItems`
  - `EMABlockEntities`
  - `EMAMenus`
  - `ExtendedMolecularAssemblerBlock`
  - `ExtendedMolecularAssemblerBlockEntity`
  - `ExtendedMolecularAssemblerMenu`
- Forge resources are present for the two assembler blockstates, block models, item models, assembler AE2 screen style, and English/Korean block names.
- Client-side assembler screen and block entity renderer are ported and registered through Forge client setup/render events.
- JEI integration classes have been brought into the 1.20.1 source set and currently compile.
- AvaritiaNeo's available 1.20.1 artifact was inspected, but its class files are Java 21
  (`class file version 65.0`) while Forge 1.20.1 compiles with Java 17 (`61.0`), so only an
  isolated safe stub is present for `adapter.recipe.avaritianeo` until a Java-17-compatible
  artifact/API is chosen.
- Re:Avaritia's configured artifact (`ReAvaritia_file=7695278`) exposes 1.21+ recipe classes
  (`CraftingInput`/`RecipeInput`, `HolderLookup.Provider` signatures), so only an isolated safe
  stub is present for `adapter.recipe.reavaritia` until a 1.20.1-compatible artifact/API is chosen.
- Minimal Forge/AE2-15 pattern encoding terminal part registration is present:
  - `EMAParts` registers `extended_pattern_encoding_terminal` as an AE2 `PartItem`.
  - `ExtendedPatternEncodingTerminalPart` uses EMA's part models but intentionally reuses AE2 15's stock `PatternEncodingTerminalPart` behavior.
  - This is a compile-safe placeholder/porting slice, not the full 1.21 extended-table encoding menu.

Verified:

```bash
/mnt/c/Windows/System32/cmd.exe /c "gradlew.bat :forge-1-20-1:compileJava --console=plain"
/mnt/c/Windows/System32/cmd.exe /c "gradlew.bat :neoforge-1-21-1:compileJava :forge-1-20-1:compileJava --console=plain"
/mnt/c/Windows/System32/cmd.exe /c "gradlew.bat :forge-1-20-1:processResources :forge-1-20-1:compileJava :neoforge-1-21-1:compileJava --console=plain"
```

The first two commands passed on 2026-05-30. The third command passed on 2026-06-01 after the minimal Forge block/item/block-entity registration and resource slice. On 2026-06-02, `:forge-1-20-1:compileJava` and `:forge-1-20-1:build` both passed after the assembler menu/client/renderer, Extended Crafting adapter, JEI hardening, minimal AE2 part registration, and optional-adapter stub slice. `:forge-1-20-1:runGameTestServer` now finds `myotus`, `ae2`, and `guideme`, but fails during AE2 mixin application: `ae2.mixins.json:spatial.MinecraftServerMixin` cannot find target `MinecraftServer.m_129815_(ChunkProgressListener)` in the Forge GameTest dev runtime.

## Important API differences handled

- 1.21 `DataComponent` storage was replaced with an NBT payload under
  `extendedmolecularassembler:extended_crafting_pattern`.
- 1.21 `RecipeHolder<?>` usage was replaced with direct 1.20 `Recipe<?>` / `recipe.getId()` usage.
- 1.21 `CraftingInput` was replaced with a small 1.20 `CraftingContainer` adapter.
- 1.21 AE2 `IPatternDetails#getOutputs()` returns a list, while AE2 15 / 1.20 returns `GenericStack[]`.
- 1.20 AE2 does not expose the same `MISSING_CONTENT` helper used by the 1.21 implementation, so missing-content detection is temporarily a no-op in the NBT record.

## Not ported yet

The following 1.21.1 features are still pending for 1.20.1:

- Full EMA / Ex EMA behavior parity: the current Forge block entity is a narrow single-lane compileable port and does not yet implement the full NeoForge 1.21.1 parallel-lane Ex EMA behavior.
- Forge capability registration parity with AE2's in-world grid node host path should be smoke-tested in an actual server/client run.
- Network packets for assembler animation are not ported yet.
- Full Pattern Encoding Terminal extended-table menu/encoding is not ported yet. The AE2 part item now registers, but it opens/behaves like AE2 15's stock pattern encoding terminal. A direct 1.21 copy still does not compile on AE2 15 because `RecipeHolder`, `CraftingInput`, pattern terminal slots, and transfer APIs changed.
- JEI recipe transfer integration compiles, but still needs in-game validation with JEI installed and the terminal/menu path completed.
- Re:Avaritia concrete adapters are blocked by the configured artifact/API mismatch noted above.
- Avaritia Neo concrete adapter package is disabled as a compile-time stub because the available 1.20.1 artifact on the classpath is compiled for Java 21 (`class file has wrong version 65.0`) while Forge 1.20.1 compiles with Java 17 (`61.0`).
- Extended Crafting concrete adapter package is ported at compile level and needs runtime recipe validation.
- ExtendedAE Assembler Matrix integration.
- AdvancedAE Extended Quantum Crafter integration.
- Forge 1.20.1 GameTests.

## Recommended next slice

1. Add a Forge 1.20.1 GameTest or server smoke test for placing both assembler blocks and verifying the block entity/crafting-machine capability lookup.
2. Add AE2 and Myotus runtime artifacts to the Forge run configuration so `runGameTestServer` can reach actual mod initialization.
3. Decide whether to finish full Ex EMA parallel lane parity now or keep it single-lane until the base machine runtime is proven.
4. Replace the minimal Pattern Encoding Terminal placeholder with a Forge/AE2-15-specific extended-table menu/encoding rewrite instead of a direct 1.21 copy.
5. Validate JEI transfer integration after the extended terminal/menu path exists.
6. Leave ExtendedAE / AdvancedAE integrations until the base Forge 1.20.1 machine works.
