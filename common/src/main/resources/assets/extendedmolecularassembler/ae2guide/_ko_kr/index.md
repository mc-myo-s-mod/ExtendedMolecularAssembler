---
navigation:
  title: 확장 분자 조립기
  position: 900
---

# 확장 분자 조립기

<ItemImage id="extendedmolecularassembler:extended_molecular_assembler" scale="4" />

Extended Molecular Assembler는 Extended Crafting, Re:Avaritia, Avaritia Neo와 같은 모드의 대형 작업대 조합법을 AE2 자동 조합으로 처리할 수 있게 합니다.

이 모드는 크게 두 가지 방식을 제공합니다.

* 패턴 공급기를 사용하는 독립형 조립기 자동 조합.
* 대용량 패턴 저장과 병렬 실행을 위한 ExtendedAE 매트릭스 연동.

## 빠른 링크

* [확장 패턴 인코딩 터미널](pattern-encoding-terminal.md)
* [확장 분자 조립기](extended-molecular-assemblers.md)
* [ME 조합 공급기](me-crafting-providers.md)
* [EMA 매트릭스 코어](matrix-cores.md)
* [ExtendedAE Plus 매트릭스 블록](matrix-plus.md)
* [호환성 안내](compatibility.md)

## 용량 요약

| 블록 | 용량 |
|---|---:|
| EMA 패턴 코어 | 확장 패턴 36개 |
| EMA 패턴 코어 플러스 | 확장 패턴 72개 |
| EMA 조합 코어 | 작업 8개 |
| EMA 조합 코어 플러스 | 작업 32개 |

패턴 저장과 조합 실행은 의도적으로 분리되어 있습니다. 패턴 코어 블록은 확장 조합 패턴을 AE2에 제공하고, 조합 코어 블록은 작업을 실행합니다.
