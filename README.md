# Extended Molecular Assembler Multi-Version Layout

This directory now acts as the multi-version Gradle root for Extended Molecular Assembler.

## Modules

- `:neoforge-1-21-1`
  - Project directory: `neoforge-1-21-1`
  - Status: existing NeoForge 1.21.1 implementation.

- `:common`
  - Project directory: `common`
  - Status: shared API/annotation sources copied into loader modules by Gradle sync tasks.

- `:forge-1-20-1`
  - Project directory: `forge-1-20-1`
  - Status: compileable Forge 1.20.1 shell.
  - Purpose: safe landing zone for feature-by-feature 1.20.1 porting.

## Commands

Run from this directory using Windows `cmd.exe` from WSL:

```bash
/mnt/c/Windows/System32/cmd.exe /c "gradlew.bat projects --console=plain"
/mnt/c/Windows/System32/cmd.exe /c "gradlew.bat :neoforge-1-21-1:compileJava --console=plain"
/mnt/c/Windows/System32/cmd.exe /c "gradlew.bat :forge-1-20-1:compileJava --console=plain"
/mnt/c/Windows/System32/cmd.exe /c "gradlew.bat :neoforge-1-21-1:compileJava :forge-1-20-1:compileJava --console=plain"
```

## Porting Strategy

Do not copy the full 1.21.1 source tree into 1.20.1 all at once. Port feature-by-feature:

1. Core constants and registration wrappers.
2. Pattern metadata storage using 1.20.1 NBT instead of 1.21.1 DataComponents.
3. Recipe adapter and 9x9 grid placement logic.
4. Basic EMA block/item/block entity.
5. Pattern encoding terminal menu/screen.
6. JEI transfer integration.
7. Optional Extended Crafting/Re:Avaritia/Avaritia Neo adapters.
8. ExtendedAE Matrix integration last.

Keep extracting only stable, loader-neutral code or resources into `common/`.
