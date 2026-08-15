package com.lonivxy.minecraftchatai;

import com.lonivxy.minecraftchatai.ai.AiClient;
import com.lonivxy.minecraftchatai.chat.ChatHistory;
import com.lonivxy.minecraftchatai.command.AichatCommand;
import com.lonivxy.minecraftchatai.command.TranslateCommand;
import com.lonivxy.minecraftchatai.config.AiConfig;
import com.lonivxy.minecraftchatai.listener.ChatListener;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point for MinecraftChatAI, an AI chat + chat-translation plugin.
 *
 * <p>Provides /aichat (neko assistant) and /translate (translate the last few chat messages)
 * backed by any OpenAI-compatible AI provider configured in config.yml.
 */
// CHECKSTYLE.SUPPRESS: AbbreviationAsWordInName for 1 lines
public final class MinecraftChatAI extends JavaPlugin {

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
    FileConfiguration config = getConfig();

    AiConfig aiConfig =
        new AiConfig(
            config.getString("ai.base-url", "https://api.deepseek.com"),
            config.getString("ai.api-key", ""),
            config.getString("ai.model", "deepseek-chat"),
            config.getInt("ai.max-reply-length", 300),
            config.getInt("ai.timeout-seconds", 30));

    if (!aiConfig.isConfigured()) {
      getLogger().warning(
          "AI is not configured. Set ai.api-key (and optionally ai.base-url) in config.yml.");
    }

    ChatHistory history = new ChatHistory(64);
    AiClient aiClient = new AiClient(aiConfig);

    getServer().getPluginManager().registerEvents(new ChatListener(history), this);

    new AichatCommand(aiClient, aiConfig, this).register();
    new TranslateCommand(aiClient, history, aiConfig, this).register();
  }

  @Override
  public void onDisable() {
    CommandAPI.onDisable();
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
