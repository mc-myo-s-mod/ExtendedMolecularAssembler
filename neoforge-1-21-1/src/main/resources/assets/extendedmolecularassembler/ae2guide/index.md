---
navigation:
  title: Extended Molecular Assembler
  position: 900
---

# Extended Molecular Assembler

<ItemImage id="extendedmolecularassembler:extended_molecular_assembler" scale="4" />

Extended Molecular Assembler adds AE2 autocrafting support for large crafting-table recipes from mods such as Extended Crafting, Re:Avaritia, and Avaritia Neo.

The mod provides two main approaches:

* Standalone assemblers for pattern-provider based autocrafting.
* ExtendedAE Matrix integration for large shared pattern storage and parallel execution.

## Quick Links

* [Extended Pattern Encoding Terminal](pattern-encoding-terminal.md)
* [Extended Molecular Assemblers](extended-molecular-assemblers.md)
* [ME Crafting Providers](me-crafting-providers.md)
* [EMA Matrix Cores](matrix-cores.md)
* [ExtendedAE Plus Matrix Blocks](matrix-plus.md)
* [Compatibility Notes](compatibility.md)

## Capacity Summary

| Block | Capacity |
|---|---:|
| EMA Pattern Core | 36 extended patterns |
| EMA Pattern Core Plus | 72 extended patterns |
| EMA Crafting Core | 8 jobs |
| EMA Crafting Core Plus | 32 jobs |

Pattern storage and crafting execution are intentionally separate. Pattern Core blocks expose extended crafting patterns to AE2; Crafting Core blocks execute jobs.
