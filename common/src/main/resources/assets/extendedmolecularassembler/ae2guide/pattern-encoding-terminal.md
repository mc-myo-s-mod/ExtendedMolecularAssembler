---
navigation:
  parent: index.md
  title: Pattern Encoding Terminal
  icon: extendedmolecularassembler:extended_pattern_encoding_terminal
  position: 10
categories:
- tools
item_ids:
- extendedmolecularassembler:extended_pattern_encoding_terminal
- extendedmolecularassembler:extended_crafting_pattern
---

# Extended Pattern Encoding Terminal

<ItemImage id="extendedmolecularassembler:extended_pattern_encoding_terminal" scale="4" />

The Extended Pattern Encoding Terminal encodes large crafting-table recipes into <ItemLink id="extendedmolecularassembler:extended_crafting_pattern" /> items.

It is intended for recipes that do not fit in AE2's normal 3x3 crafting pattern workflow.

## Supported Recipe Families

The terminal can encode large table recipes from supported mods when they are installed:

* Extended Crafting tables.
* Re:Avaritia tables.
* Avaritia Neo extreme crafting.

Vanilla 3x3 crafting recipes are intentionally left to AE2's normal pattern terminal.

## Recipe Cycling

Some modpacks contain multiple large-table recipes with the same visible input layout. When several extended recipes match the current grid, the cycle button appears. Use it to choose the exact table/provider recipe before encoding.

The encoded pattern remembers the selected extended recipe.

## Substitutions

The terminal has separate options for item and fluid substitution:

* Item substitution controls ordinary AE2 crafting-pattern substitution.
* Fluid substitution controls AE2's fluid-substitution flag.

These options are independent. Turning on one does not imply the other.

## Matrix Upload

When ExtendedAE Plus integration is present, the upload button can send a freshly encoded pattern into an eligible EMA Matrix.

Auto-upload only succeeds when an active Matrix on the same ME network contains:

* At least one EMA Pattern Core or EMA Pattern Core Plus.
* A Pattern Uploader block.
* Free pattern storage.

If no eligible Matrix exists, the encoded pattern remains in the terminal output slot.
