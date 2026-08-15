# MinecraftChatAI

![License](https://img.shields.io/github/license/Lonivxy/MinecraftChatAI)
![Java](https://img.shields.io/badge/Java-25-orange)
![Paper](https://img.shields.io/badge/Paper%2FPurpur-26.1.2-blue)
![Gradle](https://img.shields.io/badge/Gradle-9.6.1-brightgreen)
![Build](https://img.shields.io/github/actions/workflow/status/Lonivxy/MinecraftChatAI/main.yml?branch=main)
![AI](https://img.shields.io/badge/AI-OpenAI%20Compatible-8A2BE2)

A Paper server plugin that plugs any OpenAI-compatible AI provider (e.g. DeepSeek) into your
Minecraft chat. Players can chat with a playful neko assistant and translate the last few chat
messages — all while staying safe from prompt-injection tricks.

Built for **Paper/Purpur 26.1.2** (Paper API 26.2).

## Tech stack

- **Language:** Java 25
- **Server API:** Paper API 26.2 (Paper/Purpur 26.1.2)
- **Build:** Gradle 9.6.1 with Shadow for shading
- **Commands:** CommandAPI (shaded & relocated)
- **JSON:** Gson (shaded & relocated)
- **AI:** any OpenAI-compatible `/chat/completions` provider (DeepSeek, OpenAI, etc.)
- **HTTP:** JDK `java.net.http.HttpClient` (async, off the main thread)


## Features

- **`/aichat <message>`** (alias `/aic`) — chat with "Nya", a neko (catgirl) assistant. Replies
  directly to the player, is limited to 300 characters, and matches the language they used.
- **`/translate <count> <language>`** — translates the last `count` (1–5) genuine player chat
  messages into the chosen language (English, Chinese, French, or Japanese) and sends the
  translation back to you. Only real player messages are captured — plugin messages and AI
  replies are never included.
- **Anti prompt-injection by design** — every prompt tells the model that player input is
  untrusted data and to never follow instructions embedded in chat. AI output is sent as plain
  text so the model cannot inject MiniMessage formatting or commands.
- **Bring your own provider** — configure the base URL, API key, and model in `config.yml`. Works
  with any provider exposing the standard OpenAI-compatible `/chat/completions` endpoint
  (DeepSeek, OpenAI, etc.).

## Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/aichat <message>` (alias `/aic`) | Chat with the neko assistant | `minecraftchatai.aichat` |
| `/translate <count> <language>` | Translate the last 1–5 player messages | `minecraftchatai.translate` |

`count` must be between 1 and 5. `language` is one of `english`, `chinese`, `french`, `japanese`.

## Configuration

Copy your settings into `config.yml` (generated on first run):

```yaml
ai:
  base-url: "https://api.deepseek.com"   # provider base URL
  api-key: "REPLACE_ME"                   # your API key
  model: "deepseek-chat"                  # model name
  max-reply-length: 300                   # /aichat reply length cap
  timeout-seconds: 30                     # request timeout
```

Replace `api-key` with a real key (and change `base-url`/`model` for a different provider).
Until then the commands refuse to run and the plugin logs a warning.

## Building

```text
./gradlew build
```

This runs Checkstyle (Google style), SpotBugs, and the JUnit tests. The shaded JAR is written to
`build/libs/`. Drop it into your server's `plugins/` folder.

## Project layout

- `src/main/java/com/lonivxy/minecraftchatai/` — plugin entry point, config, chat history,
  AI client, prompts, commands, and listener.
- `src/main/resources/` — `plugin.yml` and `config.yml`.
- `src/test/` — JUnit unit tests.

## Documentation

- [Usage](docs/usage.md) — how the plugin works and how to extend it.
- [Customization](docs/customization.md) — forking/customizing this project.
- [Releases](docs/releases.md) — versioning and cutting a release.
- [Agent instructions & skills](docs/skills.md) — agent guidance and skills.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).
