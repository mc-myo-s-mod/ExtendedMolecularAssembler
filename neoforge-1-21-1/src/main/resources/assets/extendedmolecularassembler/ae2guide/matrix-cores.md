---
navigation:
  parent: index.md
  title: EMA Matrix Cores
  icon: extendedmolecularassembler:extended_assembler_matrix_pattern_core
  position: 30
categories:
- machines
item_ids:
- extendedmolecularassembler:extended_assembler_matrix_pattern_core
- extendedmolecularassembler:extended_assembler_matrix_crafting_core
---

# EMA Matrix Cores

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_pattern_core" scale="5" />

EMA Matrix blocks integrate with an ExtendedAE Assembler Matrix, but they keep extended pattern storage and extended job execution separate from the upstream Matrix pattern slots.

## Pattern Core

The <ItemLink id="extendedmolecularassembler:extended_assembler_matrix_pattern_core" /> stores extended crafting patterns and exposes them to AE2's crafting provider system.

Each Pattern Core provides:

* 36 extended pattern slots.
* A scrollable pattern page.
* Pattern Access visibility controls.
* Active EMA job display and stop control for EMA jobs.

Pattern Core blocks do not execute jobs by themselves.

## Crafting Core

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_crafting_core" scale="5" />

The <ItemLink id="extendedmolecularassembler:extended_assembler_matrix_crafting_core" /> executes extended crafting jobs supplied by Pattern Core blocks.

Each Crafting Core provides:

* 8 extended crafting jobs.
* Output retry behavior when ME insertion is temporarily blocked.
* Job cancellation that returns held inputs and outputs instead of deleting them.

Crafting Core blocks do not store patterns by themselves.

## Required Matrix Shape

An EMA Matrix setup needs both sides:

* At least one Pattern Core to expose patterns.
* At least one Crafting Core to run jobs.

A Matrix with only Crafting Cores must not accept or execute EMA extended patterns. This prevents accidental crafting from ordinary ExtendedAE pattern storage.

## Shared Speed

EMA Matrix jobs share the ExtendedAE Matrix speed core count. Pattern storage and job counts remain EMA-local.

## Recipes

<RecipeFor id="extendedmolecularassembler:extended_assembler_matrix_pattern_core" />

<RecipeFor id="extendedmolecularassembler:extended_assembler_matrix_crafting_core" />
