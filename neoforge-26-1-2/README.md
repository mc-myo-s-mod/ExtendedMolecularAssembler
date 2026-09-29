# EMA for Minecraft 26.1.2

Development port for NeoForge 26.1.2.100, AE2 26.1.10-beta and Myotus 26.0.0
from Maven Central. Requires Java 25. This module uses Gradle 9.2.1 independently
of the older modules' Gradle 8 wrapper.

Run from this directory on Windows:

```bat
gradlew.bat compileJava --console=plain
gradlew.bat runData --console=plain
gradlew.bat build --console=plain
gradlew.bat runGameTestServer --console=plain
gradlew.bat runClient --console=plain
gradlew.bat runServer --console=plain
```

The root project's `:neoforge-26-1-2:*` tasks forward to this native wrapper.
Shared textures, Matrix core art and guide pages remain in `../common`.
Version-specific models and native item/part definitions override shared resources here.

## Integrations

- Extended Crafting 8.0.1 and Re:Avaritia 1.4.2: table patterns and JEI transfer.
- ExtendedAE 26.1-1.0.4: Ex Assembler, Matrix cores and pattern preview.
- AE2WTLib 26.1.1-beta: wireless terminal; optional independently of ExtendedAE's API dependency.
- AdvancedAE 26.1.7: Quantum Crafter integration remains WIP, without a crafting recipe.
- ExtendedAE Plus-style cores retain the standalone configuration; the uploader recipe
  still requires the external ExtendedAE Plus upload core.
- EMI and AvaritiaNeo are not included in this port: no compatible 26.1.2 artifact was
  available during the port. Older-version integration code is unchanged.

## Verification

On 2026-09-20: compilation, data generation, eight unit tests and 21 EMA GameTests
passed with the configured optional integrations. The GameTests also passed with all
optional runtime integrations disabled (integration-specific tests return early there).
The full suite covers pattern encoding, preview slots, assembler lane persistence,
Matrix job persistence/cancellation and transactional pattern upload rollback.

Client startup/resource loading and dedicated-server startup were smoke-tested.
In-world GUI interaction, visual appearance and multiplayer use still need manual testing.
Known upstream log messages include Re:Avaritia's `ae2_creative_energy_cell` recipe,
Jade's shearable-block scan and third-party model texture warnings; these are not EMA resources.
With optional mods absent, GuideME also warns about their unregistered items in the shared guide pages.
