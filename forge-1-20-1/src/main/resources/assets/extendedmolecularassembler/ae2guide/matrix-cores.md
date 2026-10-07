---
navigation:
  parent: index.md
  title: EMA Matrix Cores
  position: 30
categories:
- machines
item_ids:
  - extendedmolecularassembler:extended_assembler_matrix_pattern_core
  - extendedmolecularassembler:extended_assembler_matrix_crafting_core
  - extendedmolecularassembler:epic_assembler_matrix_crafting_core
  - extendedmolecularassembler:legendary_assembler_matrix_crafting_core
  - extendedmolecularassembler:epic_assembler_matrix_crafting_core_plus
  - extendedmolecularassembler:legendary_assembler_matrix_crafting_core_plus
---

# EMA Matrix Cores

<myotus:condition load="expatternprovider">
EMA cores add separate extended pattern storage and crafting jobs to an ExtendedAE Assembler Matrix.

## Pattern Core

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_pattern_core" scale="5" />

The <ItemLink id="extendedmolecularassembler:extended_assembler_matrix_pattern_core" /> stores all supported EMA patterns, including Epic 11×11 and Legendary 13×13 patterns. It does not have separate grid-size tiers.

* Each Pattern Core holds 36 patterns; Pattern Core Plus holds 72.
* The Extended Assembler Matrix screen combines all EMA Pattern Cores in the Matrix into one searchable, scrollable list.
* Pattern Access visibility, running job display and job cancellation remain available.
* Patterns can be stored even without a compatible Craft Core, but the Pattern Core does not execute jobs itself.

## Craft Core

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_crafting_core" scale="5" />

Craft Cores execute patterns stored in EMA Pattern Cores in the same Matrix. Larger tiers also support smaller patterns.

| Craft Core | Maximum grid | Jobs | Plus jobs |
| --- | --- | --- | --- |
| Extended | 9×9 | 8 | 32 |
| Epic | 11×11 | 8 | 32 |
| Legendary | 13×13 | 8 | 32 |

An Epic pattern requires an Epic or Legendary Craft Core; a Legendary pattern requires a Legendary Craft Core. If no provider can execute the pattern, crafting confirmation blocks the request before it starts. A Craft Core in another Matrix does not satisfy this requirement for the Matrix holding the pattern. Busy but compatible cores may queue more work.

Craft Cores do not store patterns. EMA jobs share the Matrix's speed cores, retry blocked outputs and return held items when cancelled. Ordinary ExtendedAE pattern slots remain separate from EMA pattern storage.
</myotus:condition>
