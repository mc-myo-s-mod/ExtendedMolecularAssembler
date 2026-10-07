---
navigation:
  parent: index.md
  title: EMA 매트릭스 코어
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

# EMA 매트릭스 코어

<myotus:condition load="expatternprovider">
EMA 코어는 ExtendedAE 조합기 매트릭스에 별도의 확장 패턴 저장소와 조합 작업을 추가합니다.

## 패턴 코어

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_pattern_core" scale="5" />

<ItemLink id="extendedmolecularassembler:extended_assembler_matrix_pattern_core" />는 에픽 11×11 및 레전더리 13×13을 포함한 모든 지원 EMA 패턴을 저장합니다. 패턴 크기에 따른 별도 등급은 없습니다.

* 패턴 코어 하나당 패턴 36개, 패턴 코어 플러스는 72개를 보관합니다.
* 확장 조합기 매트릭스 화면에서 같은 매트릭스의 모든 EMA 패턴 코어를 하나의 검색·스크롤 목록으로 확인합니다.
* 패턴 액세스 표시 설정, 실행 중인 작업 표시 및 작업 취소 기능을 사용할 수 있습니다.
* 호환되는 조합 코어가 없어도 패턴을 보관할 수 있지만, 패턴 코어 자체는 조합하지 않습니다.

## 조합 코어

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_crafting_core" scale="5" />

조합 코어는 같은 매트릭스의 EMA 패턴 코어에 저장된 패턴을 실행합니다. 상위 등급은 작은 크기의 패턴도 지원합니다.

| 조합 코어 | 최대 크기 | 작업 수 | 플러스 작업 수 |
| --- | --- | --- | --- |
| 확장 | 9×9 | 8 | 32 |
| 에픽 | 11×11 | 8 | 32 |
| 레전더리 | 13×13 | 8 | 32 |

에픽 패턴에는 에픽 또는 레전더리 조합 코어가, 레전더리 패턴에는 레전더리 조합 코어가 필요합니다. 패턴을 실행할 제공기가 없다면 작업 시작 전 조합 확인 단계에서 요청을 차단합니다. 다른 매트릭스의 조합 코어로는 패턴이 보관된 매트릭스의 조건을 충족할 수 없습니다. 호환 코어가 이미 작업 중이어도 추가 요청은 가능합니다.

조합 코어는 패턴을 저장하지 않습니다. EMA 작업은 매트릭스의 속도 코어를 공유하고, 출력이 막히면 재시도하며, 취소하면 보관 중인 아이템을 반환합니다. 기존 ExtendedAE 패턴 슬롯과 EMA 패턴 저장소는 별개입니다.
</myotus:condition>
