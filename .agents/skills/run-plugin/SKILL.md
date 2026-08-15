---
name: run-plugin
description: Build, test, and release this PaperMC/Spigot plugin from source. Use when asked to build the plugin, run its tests, run a single test class, or produce a release JAR.
---

This is a PaperMC/Spigot plugin — "running" it means building from source, executing the test
suite, and optionally producing the shaded JAR that gets dropped into a server's `plugins/`
folder.

## Commands

**OneDrive locking**: If the project resides in OneDrive, the build fails with `Unable to delete directory '...\build\test-results\test\binary'`, delete that directory manually before retrying — OneDrive holds a sync lock on it.

```bash
# Build (runs Checkstyle, SpotBugs, and tests)
./gradlew build
```

### Testing

```bash
# Run tests only
./gradlew test

# Run a single test class
./gradlew test --tests "com.lonivxy.minecraftchatai.chat.ChatHistoryTest"

# Run a single test method
./gradlew test --tests "com.lonivxy.minecraftchatai.ai.PromptsTest.nekoPromptContainsKeyRules"
```

Checkstyle enforces Google Java style with `maxWarnings = 0` — the build fails on any warning. SpotBugs runs FindSecBugs. Both run as part of `build`; fix all findings before committing.

Unit tests cover the pure logic classes (`ChatHistory`, `AiConfig`, `Prompts`) directly with JUnit.
Mockito is available (`testImplementation 'org.mockito:mockito-core:...'`) for mocking external
boundaries. There is no `integrationTest` suite.

## Release JAR

```bash
./gradlew -Pver="v1.0.0" release
# → build/libs/MinecraftChatAI-1.0.0.jar (keeps the version in the filename)
```

The `release` task just runs `build`; it no longer strips the version from the filename. For a
stable `vX.Y.Z` tag the artifact is `MinecraftChatAI-X.Y.Z.jar`, and for a `vX.Y.Z-RC-N` tag it is
`MinecraftChatAI-X.Y.Z-RC-N-SNAPSHOT.jar`.

Versioning logic (`build.gradle.kts`):
- No `-Pver` → `yyMMdd-HHmm-SNAPSHOT`
- `-Pver=vX.Y.Z-RC-N` -> `X.Y.Z-RC-N-SNAPSHOT`
- `-Pver=vX.Y.Z` -> `X.Y.Z` (stable; the `release` task then renames the shadow jar to
  `${rootProject.name}.jar`)

Quote the `-Pver` value to stop the shell/PowerShell from mangling the `=`.

## Maintenance

After adding a dependency, check whether `shadowJar` in `build.gradle.kts` needs a new
`relocate(...)` entry (to avoid classloader clashes with other plugins on the same server) and
a `minimize { exclude(...) }` entry — see the first Gotcha below for why this matters.

Keep these in sync with the current state of the project:

- **`AGENTS.md`** — architecture, versioning logic
- **`.agents/skills/fill-template-plugin/SKILL.md`** — customization checklist
- **`README.md`**, **`docs/usage.md`**, **`docs/customization.md`**, **`docs/releases.md`** —
  feature list, extension recipes, fork checklist, PaperMC version mapping
- **`.agents/skills/run-plugin/SKILL.md`** (this file) — build commands, release
  process, shaded package list

## Gotchas

- **`minimize()` silently strips reflection/SPI-only dependencies** — `shadowJar`'s `minimize()`
  only treats a dependency as "used" if *this project's own compiled classes* reference it
  directly. A library only referenced from inside an already-`exclude`d dependency looks unused
  and gets stripped down to empty `package-info.class` stubs, causing `NoClassDefFoundError` at
  runtime even though the build succeeds. Add an `exclude(dependency("group:artifact:.*"))` entry
  under `minimize` for any such dependency (the `commandapi` and `gson` entries are already there).
- **OneDrive build lock** — if this repo lives under OneDrive, `./gradlew build` can fail with
  `Unable to delete directory '...\build\classes\java\main'` (or `...\test\binary`, or other
  `build/` subdirectories) because OneDrive holds a sync lock on it. Delete the offending
  directory manually (e.g. `rm -rf build/classes/java/main`) and retry.
- **Commands declared in both `plugin.yml` and `onEnable()`** — CommandAPI registers commands
  programmatically; don't add a matching `commands:` entry to `plugin.yml` or Bukkit
  double-registers it, which CommandAPI flags at startup with a warning.
- **Mockito javaagent** — `mockito-core` is wired into a dedicated `mockitoAgent` configuration
  and attached via `-javaagent` in `tasks.test` to avoid the inline-mock-maker self-attach
  warning. Don't drop this wiring when customizing the template, and remember Mockito must stay
  declared explicitly as `testImplementation` — it isn't provided transitively.
