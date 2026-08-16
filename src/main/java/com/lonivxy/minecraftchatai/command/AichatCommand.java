package com.lonivxy.minecraftchatai.command;

import com.lonivxy.minecraftchatai.MinecraftChatAI;
import com.lonivxy.minecraftchatai.ai.AiServices;
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
   * @param services the shared AI services
   * @param main the owning plugin
   * @param plugin the owning plugin
   */
  public AichatCommand(AiServices services, MinecraftChatAI main, Plugin plugin) {
    this.command =
        new CommandAPICommand("aichat")
            .withAliases("aic")
            .withPermission("minecraftchatai.aichat")
            .withSubcommand(
                new CommandAPICommand("resetsession")
                    .withSubcommand(
                        new CommandAPICommand("public")
                            .withPermission("aichat.publicsessionreset")
                            .executes(new AichatResetExecutor(services, true)))
                    .executes(new AichatResetExecutor(services, false)))
            .withSubcommand(
                new CommandAPICommand("reload")
                    .withPermission("minecraftchatai.reload")
                    .executes(new AichatReloadExecutor(main)))
            .withArguments(new GreedyStringArgument("message"))
            .executes(new AichatExecutor(services, plugin));
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
