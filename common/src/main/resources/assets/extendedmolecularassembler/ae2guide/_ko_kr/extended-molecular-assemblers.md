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

## AE2 자동 조합에서 사용하기

1. 확장 패턴 인코딩 터미널에서 대형 조합법을 인코딩합니다.
2. 생성된 확장 패턴을 알맞은 구성에 저장하거나 공급합니다.
3. 조합기가 재료를 받고 결과를 ME 네트워크로 돌려보낼 수 있는지 확인합니다.

매트릭스 기반 구성에서는 일반 ExtendedAE 패턴 슬롯에 확장 패턴을 넣지 말고 전용 EMA 매트릭스 패턴 코어와 조합 코어 블록을 사용하세요.
