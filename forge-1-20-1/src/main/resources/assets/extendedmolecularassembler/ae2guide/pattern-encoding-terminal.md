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
- extendedmolecularassembler:wireless_extended_pattern_encoding_terminal
- extendedmolecularassembler:epic_pattern_encoding_terminal
- extendedmolecularassembler:legendary_pattern_encoding_terminal
- extendedmolecularassembler:epic_wireless_extended_pattern_encoding_terminal
- extendedmolecularassembler:legendary_wireless_extended_pattern_encoding_terminal
---

# Extended Pattern Encoding Terminal

<ItemImage id="extendedmolecularassembler:extended_pattern_encoding_terminal" scale="4" />

The Extended Pattern Encoding Terminal encodes large crafting-table recipes into <ItemLink id="extendedmolecularassembler:extended_crafting_pattern" /> items.

It is intended for recipes that do not fit in AE2's normal 3x3 crafting pattern workflow.

## Epic and Legendary Terminals

<ItemImage id="extendedmolecularassembler:epic_pattern_encoding_terminal" scale="4" />

The Epic Pattern Encoding Terminal provides an 11×11 grid for recipes too large for the normal 9×9 terminal.

<ItemImage id="extendedmolecularassembler:legendary_pattern_encoding_terminal" scale="4" />

The Legendary Pattern Encoding Terminal provides a 13×13 grid. Both variants keep recipe cycling and the separate item/fluid substitution controls.

Encoding requires an installed integration that supports the recipe's table size. For standalone autocrafting, use an [assembler](extended-molecular-assemblers.md) with a grid large enough for the encoded recipe.

<myotus:condition load="ae2wtlib" silent="true">
## Wireless Epic and Legendary Terminals

<ItemImage id="extendedmolecularassembler:epic_wireless_extended_pattern_encoding_terminal" scale="4" />

<ItemImage id="extendedmolecularassembler:legendary_wireless_extended_pattern_encoding_terminal" scale="4" />

With AE2WTLib installed, the wireless Epic and Legendary variants provide the same 11×11 and 13×13 encoding grids. Link and power them as AE2 wireless terminals to access the ME network without a mounted terminal.
</myotus:condition>

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
