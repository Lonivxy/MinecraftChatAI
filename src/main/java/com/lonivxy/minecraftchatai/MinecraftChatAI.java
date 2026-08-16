package com.lonivxy.minecraftchatai;

import com.lonivxy.minecraftchatai.ai.AiServices;
import com.lonivxy.minecraftchatai.chat.ChatHistory;
import com.lonivxy.minecraftchatai.command.AichatCommand;
import com.lonivxy.minecraftchatai.command.TranslateCommand;
import com.lonivxy.minecraftchatai.config.AiConfig;
import com.lonivxy.minecraftchatai.listener.ChatListener;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point for MinecraftChatAI, an AI chat + chat-translation plugin.
 *
 * <p>Provides /aichat (neko assistant with multi-turn sessions) and /translate (private
 * translation of recent chat) backed by any OpenAI-compatible AI provider configured in
 * config.yml.
 */
// CHECKSTYLE.SUPPRESS: AbbreviationAsWordInName for 1 lines
public final class MinecraftChatAI extends JavaPlugin {

  /** The config.yml schema version this build expects. Bump it whenever the structure changes. */
  private static final int CONFIG_VERSION = 2;

  private AiServices services;

  @Override
  public void onLoad() {
    CommandAPI.onLoad(new CommandAPIPaperConfig(this));
  }

  @Override
  public void onEnable() {
    CommandAPI.onEnable();
    suggestPaper();
    saveDefaultConfig();
    reloadConfig();

    services = new AiServices(loadAiConfig(getConfig()));
    checkConfigVersion(getConfig());

    if (!services.getConfig().isConfigured()) {
      getLogger().warning(
          "AI is not configured. Set ai.api-key (and optionally ai.base-url) in config.yml.");
    }

    ChatHistory history = new ChatHistory(64);
    getServer().getPluginManager().registerEvents(new ChatListener(history), this);

    new AichatCommand(services, this, this).register();
    new TranslateCommand(services, history, this).register();
  }

  @Override
  public void onDisable() {
    CommandAPI.onDisable();
  }

  /**
   * Reloads config.yml, rebuilds the AI client, and re-checks the config schema version.
   *
   * <p>Used by /aichat reload so an admin can change the provider or API key without a restart.
   */
  public void reloadAiConfig() {
    reloadConfig();
    services.reload(loadAiConfig(getConfig()));
    checkConfigVersion(getConfig());
  }

  private AiConfig loadAiConfig(FileConfiguration config) {
    return new AiConfig(
        config.getString("ai.base-url", "https://api.deepseek.com"),
        config.getString("ai.api-key", ""),
        config.getString("ai.model", "deepseek-chat"),
        config.getInt("ai.max-reply-length", 300),
        config.getInt("ai.timeout-seconds", 30),
        config.getInt("cooldown-seconds", 30),
        config.getBoolean("aichat-public", false));
  }

  private void checkConfigVersion(FileConfiguration config) {
    int found = config.getInt("config-version", -1);
    if (found == CONFIG_VERSION) {
      return;
    }

    String message =
        getName()
            + ": config.yml is out of date (config-version "
            + found
            + ", expected "
            + CONFIG_VERSION
            + "). Recommend deleting config.yml so a fresh one is generated. Your config was NOT "
            + "modified automatically to avoid data loss (e.g. your api-key).";
    getLogger().warning(message);

    for (Player player : getServer().getOnlinePlayers()) {
      if (player.isOp()) {
        player.sendRichMessage("<yellow>[MinecraftChatAI] " + message + "</yellow>");
      }
    }
  }

  private void suggestPaper() {
    if (isPaper()) {
      return;
    }

    getLogger().warning(getName() + " recommends using Paper.");
  }

  private boolean isPaper() {
    try {
      Class.forName("io.papermc.paper.ServerBuildInfo");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
