<p align="center">
    <img width="200" src="neoforge-1-21-1/src/main/resources/assets/extendedmolecularassembler/textures/block/extended_molecular_assembler.png" alt="logo">
</p>
<h1 align="center">Extended Molecular Assembler</h1>
<p align="center">
    <img alt="Minecraft 1.21.1" src="https://img.shields.io/badge/Minecraft-1.21.1-62b47a?style=flat-square">
    <img alt="Minecraft 1.20.1" src="https://img.shields.io/badge/Minecraft-1.20.1-62b47a?style=flat-square">
    <img alt="Loader NeoForge / Forge" src="https://img.shields.io/badge/Loader-NeoForge%20%2F%20Forge-f16436?style=flat-square">
    <img alt="GitHub License" src="https://img.shields.io/github/license/mc-myo-s-mod/ExtendedMolecularAseembler?style=flat-square">
</p>

## Feature

- Adds an AE2 Molecular Assembler-style machine for larger crafting tables.
- Supports encoding and crafting patterns beyond vanilla 3x3 grids.
- Provides an Extended Pattern Encoding Terminal for selecting supported recipe table providers.
- Includes Wireless Universal Terminal support for the Extended Pattern Encoding Terminal on 1.21.1.
- You can add **Extended Molecular Assembler** to your modpack.

## Support Version

| Loader   | Minecraft | Module             | Status |
|----------|-----------|--------------------|--------|
| NeoForge | 1.21.1    | `neoforge-1-21-1`  | ✔️     |
| Forge    | 1.20.1    | `forge-1-20-1`     | ✔️     |

## Support Recipe Table

| Mod                                                                     | Version        | Pattern Encoding | Link                                                                                                                                         |
|-------------------------------------------------------------------------|----------------|------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| [Extended Crafting](https://modrinth.com/mod/extended-crafting)         | 1.21.1, 1.20.1 | ✔️               | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/extended-crafting), [Modrinth](https://modrinth.com/mod/extended-crafting)         |
| [Re:Avaritia](https://modrinth.com/mod/re-avaritia)                     | 1.21.1, 1.20.1 | ✔️               | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/re-avaritia), [Modrinth](https://modrinth.com/mod/re-avaritia)                     |
| [AvaritiaNeo](https://www.curseforge.com/minecraft/mc-mods/avaritianeo) | 1.21.1, 1.20.1 | ✔️               | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/avaritianeo), [Modrinth](https://www.curseforge.com/minecraft/mc-mods/avaritianeo) |

## Integrated My Mod

| Mod                                                                 | Version        | Support | Link                                                                                                                 |
|---------------------------------------------------------------------|----------------|---------|----------------------------------------------------------------------------------------------------------------------|
| [Myotus](https://modrinth.com/mod/myotus)                           | 1.21.1, 1.20.1 | ✔️      | [Modrinth](https://modrinth.com/mod/myotus)                                                                          |
| [Extended Terminal](https://modrinth.com/mod/extended-terminal)      | 1.21.1, 1.20.1 | ✔️      | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/extended-terminal), [Modrinth](https://modrinth.com/mod/extended-terminal) |

## Build

Run from the repository root:

```bash
./gradlew :neoforge-1-21-1:build
./gradlew :forge-1-20-1:build
```

If you are building on Windows from WSL and Gradle artifacts are locked by IntelliJ or Minecraft, close the process that holds the files or build from a clean copy on the Linux filesystem.

## License

- Code: LGPL 3.0
- Assets:
  - AE2-derived screen assets follow their original asset license where applicable.
  - Mod-specific assets are distributed with this project unless otherwise noted.

### Badges

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/mc-myo-s-mod/ExtendedMolecularAseembler)
