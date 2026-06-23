---
navigation:
  parent: index.md
  title: Extended Assemblers
  icon: extendedmolecularassembler:extended_molecular_assembler
  position: 20
categories:
- machines
item_ids:
- extendedmolecularassembler:extended_molecular_assembler
- extendedmolecularassembler:ex_extended_molecular_assembler
---

# Extended Molecular Assemblers

<BlockImage id="extendedmolecularassembler:extended_molecular_assembler" scale="5" />

The Extended Molecular Assembler is a large-recipe crafting machine for AE2 automation.

It accepts <ItemLink id="extendedmolecularassembler:extended_crafting_pattern" /> jobs and executes them using a large internal crafting grid.

## Extended Molecular Assembler

The normal Extended Molecular Assembler handles one extended crafting job at a time and is suitable for compact setups attached to AE2 pattern providers.

## Ex Extended Molecular Assembler

<BlockImage id="extendedmolecularassembler:ex_extended_molecular_assembler" scale="5" />

The Ex Extended Molecular Assembler is the stronger ExtendedAE-gated variant. It provides 8 parallel lanes for high-throughput large recipe automation.

## Using with AE2 Autocrafting

1. Encode a large recipe in the Extended Pattern Encoding Terminal.
2. Store or provide the resulting extended pattern through the appropriate setup.
3. Ensure the assembler can receive the ingredients and return the output to the ME network.

For Matrix-based setups, prefer the dedicated EMA Matrix Pattern Core and Crafting Core blocks instead of placing extended patterns in ordinary ExtendedAE pattern slots.

## Recipes

<RecipeFor id="extendedmolecularassembler:extended_molecular_assembler" />

<RecipeFor id="extendedmolecularassembler:ex_extended_molecular_assembler" />
