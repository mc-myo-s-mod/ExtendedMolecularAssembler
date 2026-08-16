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

## Troubleshooting

If extended patterns are visible but jobs do not start, check that the assembler is online and can receive ingredients from the ME network.

If a job appears blocked, make sure the ME network can accept the output item. EMA jobs retry output insertion rather than deleting the result.
