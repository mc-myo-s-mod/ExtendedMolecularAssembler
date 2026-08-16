---
navigation:
  parent: index.md
  title: 패턴 인코딩 터미널
  icon: extendedmolecularassembler:extended_pattern_encoding_terminal
  position: 10
categories:
- tools
item_ids:
- extendedmolecularassembler:extended_pattern_encoding_terminal
- extendedmolecularassembler:wireless_extended_pattern_encoding_terminal
---

# 확장 패턴 인코딩 터미널

<ItemImage id="extendedmolecularassembler:extended_pattern_encoding_terminal" scale="4" />

확장 패턴 인코딩 터미널은 대형 작업대 조합법을 <ItemLink id="extendedmolecularassembler:extended_crafting_pattern" /> 아이템으로 인코딩합니다.

AE2의 일반 3×3 조합 패턴 방식에 들어가지 않는 조합법을 위해 사용합니다.

## 지원하는 조합법 종류

해당 모드가 설치되어 있으면 다음 대형 작업대 조합법을 인코딩할 수 있습니다.

* Extended Crafting 작업대.
* Re:Avaritia 작업대.
* Avaritia Neo 익스트림 조합.

바닐라 3×3 조합법은 의도적으로 AE2의 일반 패턴 터미널에서 처리하도록 남겨 둡니다.

## 조합법 순환

일부 모드팩에는 화면에 보이는 입력 배치가 같은 대형 작업대 조합법이 여러 개 있을 수 있습니다. 현재 격자와 일치하는 확장 조합법이 여러 개면 순환 버튼이 나타납니다. 인코딩하기 전에 이 버튼으로 원하는 작업대와 공급기의 조합법을 선택하세요.

인코딩된 패턴은 선택한 확장 조합법을 기억합니다.

## 대체 허용

터미널에는 아이템 대체와 유체 대체를 위한 별도 설정이 있습니다.

* 아이템 대체는 일반 AE2 조합 패턴의 아이템 대체 허용 여부를 제어합니다.
* 유체 대체는 AE2의 유체 대체 플래그를 제어합니다.

두 설정은 서로 독립적이므로 하나를 켜도 다른 설정이 함께 켜지지 않습니다.

## 매트릭스 업로드

ExtendedAE Plus 연동이 활성화되어 있으면 업로드 버튼으로 새로 인코딩한 패턴을 사용 가능한 EMA 매트릭스로 보낼 수 있습니다.

같은 ME 네트워크의 활성 매트릭스가 다음 조건을 만족해야 자동 업로드에 성공합니다.

* EMA 패턴 코어 또는 EMA 패턴 코어 플러스가 하나 이상 있어야 합니다.
* 패턴 업로더 블록이 있어야 합니다.
* 패턴 저장 공간이 남아 있어야 합니다.

조건을 만족하는 매트릭스가 없으면 인코딩된 패턴은 터미널 출력 슬롯에 그대로 남습니다.
