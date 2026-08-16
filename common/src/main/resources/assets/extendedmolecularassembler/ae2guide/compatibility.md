---
navigation:
  parent: index.md
  title: Compatibility Notes
  icon: extendedmolecularassembler:extended_crafting_pattern
  position: 50
categories:
- mechanics
item_ids:
- extendedmolecularassembler:extended_crafting_pattern
---

# Compatibility Notes

<ItemImage id="extendedmolecularassembler:extended_crafting_pattern" scale="4" />

Extended Molecular Assembler focuses on large crafting-table recipes and AE2 autocrafting integration.

## Large Crafting Recipe Mods

Supported recipe providers depend on which mods are installed in the pack:

* Extended Crafting.
* Re:Avaritia.
* Avaritia Neo.

When multiple providers match the same inputs, use the recipe cycle button in the Extended Pattern Encoding Terminal before encoding.

## ExtendedAE Matrix Boundary

EMA Matrix support is a dedicated subsystem attached to the ExtendedAE Matrix.

The Matrix shares:

* Cluster formation.
* Speed core count.

The Matrix does not share:

* Pattern storage slots.
* Extended job counts.
* EMA job cancellation state.

Use EMA Pattern Core blocks for extended pattern storage and EMA Crafting Core blocks for extended job execution.

## AdvancedAE Quantum Crafter

The Extended Quantum Crafter is currently a work in progress. It remains available for development and
creative-mode testing, but has no survival recipe and is marked `[WIP]` in its tooltip.

## Capacity Reference

| Block | Capacity |
|---|---:|
| EMA Pattern Core | 36 patterns |
| EMA Pattern Core Plus | 72 patterns |
| EMA Crafting Core | 8 jobs |
| EMA Crafting Core Plus | 32 jobs |

## Troubleshooting

If extended patterns are visible but jobs do not start, check that the formed Matrix has at least one EMA Crafting Core with free jobs.

If the encoder cannot upload to the Matrix, check that the Matrix has a Pattern Uploader and free Pattern Core storage.

If a job appears blocked, make sure the ME network can accept the output item. EMA jobs retry output insertion rather than deleting the result.
