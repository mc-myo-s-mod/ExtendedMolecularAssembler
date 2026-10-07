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
- extendedmolecularassembler:epic_molecular_assembler
- extendedmolecularassembler:ex_epic_molecular_assembler
- extendedmolecularassembler:legendary_molecular_assembler
- extendedmolecularassembler:ex_legendary_molecular_assembler
- extendedmolecularassembler:epic_molecular_assembler_upgrade_kit
- extendedmolecularassembler:legendary_molecular_assembler_upgrade_kit
- extendedmolecularassembler:ex_extended_molecular_assembler_upgrade_kit
- extendedmolecularassembler:ex_epic_molecular_assembler_upgrade_kit
- extendedmolecularassembler:ex_legendary_molecular_assembler_upgrade_kit
- extendedmolecularassembler:epic_molecular_assembler_ex_upgrade_kit
- extendedmolecularassembler:legendary_molecular_assembler_ex_upgrade_kit
---

# Extended Molecular Assemblers

<BlockImage id="extendedmolecularassembler:extended_molecular_assembler" scale="5" />

The Extended Molecular Assembler is a large-recipe crafting machine for AE2 automation.

It accepts <ItemLink id="extendedmolecularassembler:extended_crafting_pattern" /> jobs and executes them using a large internal crafting grid.

## Extended Molecular Assembler

The normal Extended Molecular Assembler handles one extended crafting job at a time and is suitable for compact setups attached to AE2 pattern providers.

<myotus:condition load="extendedae" silent="true">
## Ex Extended Molecular Assembler

<BlockImage id="extendedmolecularassembler:ex_extended_molecular_assembler" scale="5" />

The Ex Extended Molecular Assembler is the stronger ExtendedAE-gated variant. It provides 8 parallel lanes for high-throughput large recipe automation.
</myotus:condition>

## Epic and Legendary Molecular Assemblers

<BlockImage id="extendedmolecularassembler:epic_molecular_assembler" scale="5" />

The Epic Molecular Assembler provides an 11×11 crafting grid and handles one extended crafting job at a time.

<BlockImage id="extendedmolecularassembler:legendary_molecular_assembler" scale="5" />

The Legendary Molecular Assembler provides a 13×13 crafting grid and handles one extended crafting job at a time.

Use the matching [Epic or Legendary Pattern Encoding Terminal](pattern-encoding-terminal.md) to encode recipes that exceed the normal terminal's 9×9 grid. Recipes also require an installed integration that supports their table size.

<myotus:condition load="extendedae" silent="true">
## Ex Epic and Ex Legendary Molecular Assemblers

<BlockImage id="extendedmolecularassembler:ex_epic_molecular_assembler" scale="5" />

<BlockImage id="extendedmolecularassembler:ex_legendary_molecular_assembler" scale="5" />

These ExtendedAE variants retain the Epic 11×11 and Legendary 13×13 grids and provide 8 parallel crafting lanes each. The previous/next job buttons select which lane is displayed; the selected page does not limit the other lanes.

Parallel crafting requires the AE2 crafting CPU and pattern provider to supply enough jobs and ingredients.
</myotus:condition>

## Assembler Upgrade Kits

Use a kit on its matching assembler to upgrade it. When converting a non-Ex assembler to its Ex version, the installed pattern returns to the player; if their inventory is full, it drops nearby.

| Kit | Upgrade |
|---|---|
| <ItemLink id="extendedmolecularassembler:epic_molecular_assembler_upgrade_kit" /> | Extended → Epic |
| <ItemLink id="extendedmolecularassembler:legendary_molecular_assembler_upgrade_kit" /> | Epic → Legendary |
| <ItemLink id="extendedmolecularassembler:ex_extended_molecular_assembler_upgrade_kit" /> | Extended → Ex Extended |
| <ItemLink id="extendedmolecularassembler:ex_epic_molecular_assembler_upgrade_kit" /> | Ex Extended → Ex Epic |
| <ItemLink id="extendedmolecularassembler:ex_legendary_molecular_assembler_upgrade_kit" /> | Ex Epic → Ex Legendary |
| <ItemLink id="extendedmolecularassembler:epic_molecular_assembler_ex_upgrade_kit" /> | Epic → Ex Epic |
| <ItemLink id="extendedmolecularassembler:legendary_molecular_assembler_ex_upgrade_kit" /> | Legendary → Ex Legendary |

Ex assembler kits require ExtendedAE.

## Using with AE2 Autocrafting

1. Encode a large recipe in the Extended Pattern Encoding Terminal.
2. Store or provide the resulting extended pattern through the appropriate setup.
3. Ensure the assembler can receive the ingredients and return the output to the ME network.

For Matrix-based setups, prefer the dedicated EMA Matrix Pattern Core and Crafting Core blocks instead of placing extended patterns in ordinary ExtendedAE pattern slots.
