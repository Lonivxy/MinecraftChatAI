package com.lonivxy.minecraftchatai.command;

import com.lonivxy.minecraftchatai.ai.AiClient;
import com.lonivxy.minecraftchatai.config.AiConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.plugin.Plugin;

/**
 * Registers the /aichat command (alias /aic).
 */
public final class AichatCommand {

  private final CommandAPICommand command;

  /**
   * Builds the /aichat command.
   *
   * @param aiClient the AI client
   * @param config the AI configuration
   * @param plugin the owning plugin
   */
  public AichatCommand(AiClient aiClient, AiConfig config, Plugin plugin) {
    this.command =
        new CommandAPICommand("aichat")
            .withAliases("aic")
            .withPermission("minecraftchatai.aichat")
            .withArguments(new GreedyStringArgument("message"))
            .executes(new AichatExecutor(aiClient, config, plugin));
  }

  /**
   * Registers the command with CommandAPI.
   *
   * @return this command
   */
  public AichatCommand register() {
    command.register();
    return this;
  }
}
