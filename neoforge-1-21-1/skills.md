# Extended Molecular Assembler Handoff

이 문서는 다음 세션에서 작업을 이어받기 위한 진행 기록이다. 대상 모듈은
`/mnt/f/IntelliJ/Minecraft/ExtendedMolecularAseembler/neoforge-1-21-1` 이며,
NeoForge 1.21.1 / Java 21 / mod id `extendedmolecularassembler` 기준이다.

## 현재 진행도

- Extended Crafting, Re:Avaritia, AvaritiaNeo 계열 확장 조합을 AE2 패턴으로 인코딩하고
  `ExtendedTableCraftingPattern`으로 디코딩하는 구조가 들어가 있다.
- `Extended Molecular Assembler`는 단일 작업 처리, `Ex Extended Molecular Assembler`는 8개 lane 병렬 작업 처리로 분리되어 있다.
- Ex assembler screen은 lane page 전환 시 각 page의 현재 pattern stack을 서버에서 GUI sync로 받아서 보여주도록 수정했다.
- block renderer는 AE2 Molecular Assembler처럼 조합 진행 애니메이션을 표시하는 방향으로 구현되어 있다.
- ExtendedAE가 있을 때만 Assembler Matrix 연동과 `Extended Assembler Matrix Pattern Core`가 등록된다.
- Myotus의 mod integration manager를 사용해 optional mod loading을 판단한다.
- dev/test용 EC, Re:Avaritia, AvaritiaNeo 조합법은 바닐라 재료만 사용하도록 추가되어 있다.
- `runGameTestServer`에는 EC/Re:Avaritia/AvaritiaNeo tier 인코딩 및 조합 검증이 들어가 있다.
- 최신 수정: AE2 Molecular Assembler 연결 로직을 따라 assembler block entity 두 종류 모두
  `AECapabilities.IN_WORLD_GRID_NODE_HOST` capability를 노출하도록 했다.

## 마지막 검증 결과

아래 명령은 모두 성공했다.

```bash
cmd.exe /c "gradlew.bat compileJava --console=plain"
cmd.exe /c "gradlew.bat runGameTestServer --console=plain"
cmd.exe /c "gradlew.bat build --console=plain"
```

`runGameTestServer` 결과는 4개 required GameTest 모두 통과했다. 기존 경고로 `run/logs/latest.log`
삭제 실패와 ExtendedAE `ex_emc_interface` loot table unknown item 경고가 보이지만, 현재 테스트 실패 원인은 아니다.

## 최근 연결 로직 수정

AE2 원본은 `MolecularAssemblerBlockEntity`를 `CRAFTING_MACHINE`뿐 아니라 in-world grid node host로도
capability 등록한다. 이 모듈은 `CRAFTING_MACHINE`과 item handler만 등록되어 있어서 케이블/노드 탐색이
AE2 블록과 다르게 동작할 수 있었다.

수정 위치:

- `src/main/java/me/myogoo/extendedmolecularassembler/init/EMACapabilities.java`
  - `AECapabilities.IN_WORLD_GRID_NODE_HOST` 등록 추가
  - `AECapabilities.CRAFTING_MACHINE` 등록 유지
  - `Capabilities.ItemHandler.BLOCK` 등록 유지

추가 테스트:

- `ExtendedPatternGameTests.assemblerConnectionCapabilitiesFollowAe2`
  - Extended / Ex assembler 둘 다 `IN_WORLD_GRID_NODE_HOST`를 노출하는지 확인
  - cable type이 AE2처럼 `AECableType.COVERED`인지 확인
  - `ICraftingMachine.of(...)` lookup이 되는지 확인

## 빠뜨린 점 / 리스크

- 실제 인게임 자동조합 요청 흐름은 GameTest보다 넓다. connection capability 수정 후 `runClient`에서
  ME network, pattern provider, crafting CPU, storage를 직접 구성해 자동조합 요청을 다시 확인해야 한다.
- ExtendedAE Assembler Matrix 연동은 mixin으로 `ClusterAssemblerMatrix.pushCraftingJob`를 가로챈다.
  동작은 컴파일되지만, 실제 멀티블럭 queue 공유와 busy 상태가 ExtendedAE 내부 상태와 완전히 일치하는지는
  더 테스트해야 한다.
- `Extended Assembler Matrix Pattern Core`는 36 pattern 지원 의도로 구현되었지만, 실제 Matrix의 craft core,
  speed core, shared queue와 장시간 동작하는 상황은 아직 충분히 검증되지 않았다.
- Ex assembler는 parallel lane별 job state를 자체 관리한다. lane별 출력 push, matrix job reservation release,
  강제 패턴 NBT 복구가 edge case에서 꼬이지 않는지 더 확인해야 한다.
- screen/left toolbar 관련 변경은 사용자 요구가 여러 번 바뀌었다. Assembler Matrix 쪽 toolbar 버튼,
  guide 버튼, matrix screen 전환 버튼이 현재 리소스 JSON과 실제 화면에서 모두 보이는지 인게임 확인이 필요하다.
- reflection 사용은 최대한 줄여야 한다. 특히 mixin에서 private field를 reflection으로 읽는 방식은 피하고,
  가능한 accessor/invoker, public API, capability, bridge class를 우선 사용한다.
- `run/logs/latest.log`는 생성 파일이므로 직접 수정하지 않는다. 문제 분석용으로 읽기만 한다.

## 개선할 점

- 자동조합 연결은 AE2의 `PatternProviderLogic` 흐름을 계속 기준으로 삼는다.
  - pattern provider는 adjacent side 기준으로 `ICraftingMachine.of(level, adjPos, adjBeSide)`를 호출한다.
  - assembler는 AE2처럼 `IN_WORLD_GRID_NODE_HOST`, `CRAFTING_MACHINE`, covered cable type을 제공해야 한다.
- Ex assembler의 `acceptsPlans()`는 현재 빈 lane이 있으면 true를 반환한다. ExtendedAE 원본은 항상 true를 반환하지만,
  이 모듈은 resource reservation과 lane 상태를 고려하는 쪽이 더 안전하다. 다만 pattern provider 재시도 로직과
  충돌하지 않는지 확인해야 한다.
- `pushPattern`은 table의 모든 input counter를 소비하지 못하면 AE2처럼 예외를 던진다. 크래시 로그가 생기면
  pattern fill 위치, tier side length, sparse input ordering을 먼저 확인한다.
- matrix integration은 가능하면 ExtendedAE 내부 class에 대한 직접 의존을 integration package 안에 가둔다.
  always-loaded common code에서는 optional mod class를 직접 참조하지 않는다.
- GameTest를 더 추가할 때는 실제 block placement와 capability lookup을 포함해 자동조합 경로를 좁게 재현한다.
  단순 pattern encode/decode 테스트만으로는 ME network 연결 문제를 잡기 어렵다.

## 다음 세션 우선순위

1. `runClient`로 실제 월드에서 pattern provider -> Extended/Ex assembler 자동조합 요청을 재현한다.
2. 자동조합이 실패하면 `run/logs/latest.log`에서 첫 예외와 `pushPattern`, `fillCraftingGrid`,
   `CraftingLane.tick`, `pushOut` 관련 stack trace를 먼저 확인한다.
3. Assembler Matrix 멀티블럭에 Extended Assembler Matrix Pattern Core를 붙인 상태에서 36 pattern 인식,
   speed core 반영, craft core queue 공유, busy 상태를 테스트한다.
4. GUI는 실제 화면에서 확인한다.
   - Extended/Ex assembler menu/screen
   - Ex assembler lane page별 pattern 표시
   - Assembler Matrix left toolbar screen 전환 버튼
   - Extended assembler matrix pattern screen에서 원래 matrix로 돌아가는 버튼
   - guide 버튼
5. 문제가 생기면 먼저 `/mnt/f/IntelliJ/Minecraft/fork/Applied-Energistics-2_1_21_1`의 AE2 원본과
   `/mnt/f/IntelliJ/Minecraft/fork/ExtendedAE_1_21_1`의 ExtendedAE 원본을 비교한다.

## 작업 규칙 메모

- Gradle은 이 모듈에서 `cmd.exe /c "gradlew.bat <task> --console=plain"` 형식으로 실행한다.
- `build/`, `.gradle/`, `run/`, crash report, log 파일은 수정하지 않는다.
- 수동 편집은 `apply_patch`를 사용한다.
- 파일 검색은 `rg`를 우선 사용한다.
- optional integration API가 불확실하면 먼저 `fork/`의 해당 1.21.1 소스를 확인한다.
