---
navigation:
  parent: index.md
  title: ExtendedAE Plus 매트릭스 블록
  position: 40
categories:
- machines
item_ids:
  - extendedmolecularassembler:extended_assembler_matrix_pattern_core_plus
  - extendedmolecularassembler:extended_assembler_matrix_crafting_core_plus
  - extendedmolecularassembler:extended_assembler_matrix_pattern_uploader
---

# ExtendedAE Plus 매트릭스 블록

<myotus:condition load="extendedae">
ExtendedAE Plus를 추가하거나 제거해도 월드가 안정적으로 유지되도록, EMA는 ExtendedAE가 있으면 이 블록들의 레지스트리 항목을 항상 유지합니다.

블록, 패턴 업로더 기능 및 독립형 조합법을 노출하려면 ExtendedAE Plus를 설치하거나 `extendedmolecularassembler-common.toml`에서 `general.StandaloneExtendedAEPlusContent`를 활성화하세요. 설정을 변경한 뒤에는 게임을 다시 시작해야 합니다.

## 패턴 코어 플러스

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_pattern_core_plus" scale="5" />

<ItemLink id="extendedmolecularassembler:extended_assembler_matrix_pattern_core_plus" />는 대용량 패턴 저장 블록입니다.

각 패턴 코어 플러스가 제공하는 기능:

* 확장 패턴 슬롯 72개.
* 일반 패턴 코어와 같은 패턴 액세스 및 검색 기능.

## 조합 코어 플러스

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_crafting_core_plus" scale="5" />

<ItemLink id="extendedmolecularassembler:extended_assembler_matrix_crafting_core_plus" />는 처리량이 높은 작업 실행 블록입니다.

각 조합 코어 플러스가 제공하는 기능:

* 확장 조합 작업 32개.

매트릭스의 패턴 공간은 충분하지만 병렬 실행 용량이 부족할 때 사용하세요.

## 패턴 업로더

<BlockImage id="extendedmolecularassembler:extended_assembler_matrix_pattern_uploader" scale="5" />

<ItemLink id="extendedmolecularassembler:extended_assembler_matrix_pattern_uploader" />는 확장 패턴 인코딩 터미널에서 같은 ME 네트워크의 사용 가능한 EMA 매트릭스로 패턴을 직접 업로드할 수 있게 합니다.

이 블록 자체는 패턴 용량을 제공하지 않으며 업로드 경로만 활성화합니다.
</myotus:condition>
