---
navigation:
  parent: index.md
  title: EMA 매트릭스 코어
  position: 30
categories:
- machines
item_ids:
  - extendedmolecularassembler:extended_assembler_matrix_crafting_core
  - extendedmolecularassembler:extended_assembler_matrix_pattern_core
---

# EMA 매트릭스 코어

<myotus:condition load="extendedae">
<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_pattern_core" scale="5" />

EMA 매트릭스 블록은 ExtendedAE 조합기 매트릭스와 연동되지만, 확장 패턴 저장소와 확장 작업 실행은 기존 매트릭스 패턴 슬롯과 별도로 관리합니다.

## 패턴 코어

<ItemLink id="extendedmolecularassembler:extended_assembler_matrix_pattern_core" />는 확장 조합 패턴을 저장하고 AE2 조합 공급기 시스템에 제공합니다.

각 패턴 코어가 제공하는 기능:

* 확장 패턴 슬롯 36개.
* 스크롤 가능한 패턴 페이지.
* 패턴 액세스 터미널 표시 여부 설정.
* 진행 중인 EMA 작업 표시 및 중지 기능.

패턴 코어 블록만으로는 작업을 실행할 수 없습니다.

## 조합 코어

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_crafting_core" scale="5" />

<ItemLink id="extendedmolecularassembler:extended_assembler_matrix_crafting_core" />는 패턴 코어 블록이 공급한 확장 조합 작업을 실행합니다.

각 조합 코어가 제공하는 기능:

* 확장 조합 작업 8개.
* ME 삽입이 일시적으로 막혔을 때 출력을 다시 삽입하는 동작.
* 보관 중인 입력과 출력을 삭제하지 않고 반환하는 작업 취소 기능.

조합 코어 블록만으로는 패턴을 저장할 수 없습니다.

## 필수 매트릭스 구성

EMA 매트릭스에는 두 종류의 코어가 모두 필요합니다.

* 패턴을 제공할 패턴 코어가 하나 이상 필요합니다.
* 작업을 실행할 조합 코어가 하나 이상 필요합니다.

조합 코어만 있는 매트릭스는 EMA 확장 패턴을 받거나 실행하지 않습니다. 이를 통해 일반 ExtendedAE 패턴 저장소의 패턴이 의도치 않게 조합되는 것을 방지합니다.

## 공유 속도

EMA 매트릭스 작업은 ExtendedAE 매트릭스의 속도 코어 수를 공유합니다. 패턴 저장소와 작업 수는 EMA에서 별도로 관리합니다.
</myotus:condition>
