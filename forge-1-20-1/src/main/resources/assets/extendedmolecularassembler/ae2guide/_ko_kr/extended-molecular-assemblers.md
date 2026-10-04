---
navigation:
  parent: index.md
  title: 확장 분자 조합기
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
---

# 확장 분자 조합기

<BlockImage id="extendedmolecularassembler:extended_molecular_assembler" scale="5" />

확장 분자 조합기는 AE2 자동화를 위한 대형 조합법 처리 장치입니다.

<ItemLink id="extendedmolecularassembler:extended_crafting_pattern" /> 작업을 받아 대형 내부 조합 격자에서 실행합니다.

## 확장 분자 조합기

일반 확장 분자 조합기는 한 번에 하나의 확장 조합 작업을 처리하며, AE2 패턴 공급기에 연결하는 소규모 구성에 적합합니다.

<myotus:condition load="extendedae" silent="true">
## Ex 확장 분자 조합기

<BlockImage id="extendedmolecularassembler:ex_extended_molecular_assembler" scale="5" />

Ex 확장 분자 조합기는 ExtendedAE가 있을 때 활성화되는 강화형입니다. 대형 조합법을 빠르게 자동화할 수 있도록 8개의 병렬 작업 레인을 제공합니다.
</myotus:condition>

## 에픽·레전더리 분자 조합기

<BlockImage id="extendedmolecularassembler:epic_molecular_assembler" scale="5" />

에픽 분자 조합기는 11×11 조합 격자를 제공하며, 한 번에 하나의 확장 조합 작업을 처리합니다.

<BlockImage id="extendedmolecularassembler:legendary_molecular_assembler" scale="5" />

레전더리 분자 조합기는 13×13 조합 격자를 제공하며, 한 번에 하나의 확장 조합 작업을 처리합니다.

기본 터미널의 9×9 격자보다 큰 조합법은 [에픽 또는 레전더리 패턴 인코딩 터미널](pattern-encoding-terminal.md)에서 인코딩하세요. 해당 크기의 작업대 조합법을 지원하는 연동 모드도 설치되어 있어야 합니다.

<myotus:condition load="extendedae" silent="true">
## Ex 에픽·Ex 레전더리 분자 조합기

<BlockImage id="extendedmolecularassembler:ex_epic_molecular_assembler" scale="5" />

<BlockImage id="extendedmolecularassembler:ex_legendary_molecular_assembler" scale="5" />

ExtendedAE가 있을 때 활성화되는 강화형입니다. 에픽의 11×11, 레전더리의 13×13 격자를 유지하면서 각각 8개의 병렬 조합 레인을 제공합니다. 이전·다음 작업 버튼은 표시할 레인을 선택하며, 현재 보고 있는 페이지가 다른 레인의 실행을 제한하지 않습니다.

병렬 조합을 하려면 AE2 조합 CPU와 패턴 공급기에서 충분한 작업과 재료를 공급해야 합니다.
</myotus:condition>

## AE2 자동 조합에서 사용하기

1. 확장 패턴 인코딩 터미널에서 대형 조합법을 인코딩합니다.
2. 생성된 확장 패턴을 알맞은 구성에 저장하거나 공급합니다.
3. 조합기가 재료를 받고 결과를 ME 네트워크로 돌려보낼 수 있는지 확인합니다.

매트릭스 기반 구성에서는 일반 ExtendedAE 패턴 슬롯에 확장 패턴을 넣지 말고 전용 EMA 매트릭스 패턴 코어와 조합 코어 블록을 사용하세요.
