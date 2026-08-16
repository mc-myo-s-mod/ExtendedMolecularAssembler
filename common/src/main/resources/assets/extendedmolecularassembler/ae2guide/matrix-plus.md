---
navigation:
  parent: index.md
  title: ExtendedAE Plus Matrix Blocks
  position: 40
categories:
- machines
item_ids:
  - extendedmolecularassembler:extended_assembler_matrix_pattern_core_plus
  - extendedmolecularassembler:extended_assembler_matrix_crafting_core_plus
  - extendedmolecularassembler:extended_assembler_matrix_pattern_uploader
---

# ExtendedAE Plus Matrix Blocks

<myotus:condition load="extendedae">
EMA always keeps these registry entries available alongside ExtendedAE so worlds remain stable when ExtendedAE Plus is added or removed.

Install ExtendedAE Plus, or enable `general.StandaloneExtendedAEPlusContent` in `extendedmolecularassembler-common.toml`, to expose the blocks, Pattern Uploader support, and standalone recipes. Restart the game after changing the setting.

## Pattern Core Plus

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_pattern_core_plus" scale="5" />

The <ItemLink id="extendedmolecularassembler:extended_assembler_matrix_pattern_core_plus" /> is the high-capacity pattern storage block.

Each Pattern Core Plus provides:

* 72 extended pattern slots.
* The same Pattern Access and search behavior as the normal Pattern Core.

## Crafting Core Plus

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_crafting_core_plus" scale="5" />

The <ItemLink id="extendedmolecularassembler:extended_assembler_matrix_crafting_core_plus" /> is the high-throughput execution block.

Each Crafting Core Plus provides:

* 32 extended crafting jobs.

Use it when a Matrix has enough patterns but not enough parallel execution capacity.

## Pattern Uploader

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_pattern_uploader" scale="5" />

The <ItemLink id="extendedmolecularassembler:extended_assembler_matrix_pattern_uploader" /> enables direct upload from the Extended Pattern Encoding Terminal to an eligible EMA Matrix on the same ME network.

It does not provide pattern capacity by itself. It only enables the upload route.
</myotus:condition>
