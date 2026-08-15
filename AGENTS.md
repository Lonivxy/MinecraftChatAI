This repository is **MinecraftChatAI**, a PaperMC/Spigot plugin that connects an
OpenAI-compatible AI provider (e.g. DeepSeek) to Minecraft chat.

## Architecture

**Entry point**: `MinecraftChatAI extends JavaPlugin`. Bukkit/Paper calls `onEnable()` and
`onDisable()`. In `plugin.yml`, `${PACKAGE}.${NAME}` identifies the main class; the
`processResources` task fills these placeholders at build time from `group` in
`build.gradle.kts` and `rootProject.name` in `settings.gradle.kts` (both are
`com.lonivxy.minecraftchatai` / `MinecraftChatAI`).

**JAR packaging**: `jar` is disabled; `shadowJar` is the sole output. It shades CommandAPI
(relocated to `<group>.commandapi`) and Gson (relocated to `<group>.gson`). `assemble` depends on
`shadowJar`. There are **no** cw-commons, Jackson, or Hibernate dependencies — configuration uses
Bukkit's native `getConfig()`.

**AI requests**: `AiClient` uses the JDK's `java.net.http.HttpClient` to POST an
OpenAI-compatible `/chat/completions` payload built/parsed with Gson. Requests run off the main
thread via `sendAsync`; replies are rescheduled to the main thread before being sent to players.

**Prompts**: `Prompts` builds the system prompts for `/aichat` (neko assistant, reply capped at
`max-reply-length`) and `/translate`. Both tell the model that player input is untrusted data and
to ignore prompt injection. AI output is sent as plain Adventure `Component` text (never parsed as
MiniMessage) so the model cannot inject formatting or commands.

**Chat capture**: `ChatListener` records every Paper `AsyncChatEvent` into `ChatHistory`, a
bounded thread-safe buffer of the most recent player messages. Only genuine player chat fires this
event, so plugin/system messages and AI replies are never stored.

**Command declaration**: CommandAPI registers commands programmatically in `onEnable()` via the
`AichatCommand`/`TranslateCommand` classes. `/aichat` has alias `/aic`; `/translate` takes
`<count 1-5>` and a `MultiLiteralArgument` (`english|chinese|french|japanese`). Do not add these
under `commands:` in `plugin.yml`. `permissions:` entries are required and are declared there.

**Versioning logic** (in `build.gradle.kts`):

- No `-Pver` supplied -> `yyMMdd-HHmm-SNAPSHOT`
- `-Pver=vX.Y.Z-RC-N` -> `X.Y.Z-RC-N-SNAPSHOT`
- `-Pver=vX.Y.Z` -> `X.Y.Z` (stable release)

**CI workflows** (`.github/workflows/`):

- `pr.yml`: builds and tests on Ubuntu + Windows for PRs and merge queue
- `main.yml`: builds, tests, and uploads a snapshot artifact on push to `main`
- `tag.yml` / `release.yml`: handle tagged releases and Discord notifications

**Test suites**: `test` contains isolated unit tests for the pure logic classes (`ChatHistory`,
`AiConfig`, `Prompts`) and may mock external boundaries. There is no `integrationTest` suite.

## Agent instructions

1. Canonical skills live in `.agents/skills/`. The `.claude/skills/` directory is a generated mirror.
2. `CLAUDE.md` is a generated copy of this `AGENTS.md`.
3. Do not edit or create `CLAUDE.md` or files under `.claude/skills/`. Claude hooks configured in `.claude/settings.json` synchronize these mirrors on `SessionStart` and `PostToolUse`.
