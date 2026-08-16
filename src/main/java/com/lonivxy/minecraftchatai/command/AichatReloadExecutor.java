package com.lonivxy.minecraftchatai.command;

import com.lonivxy.minecraftchatai.MinecraftChatAI;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.command.CommandSender;

/**
 * Executor for {@code /aichat reload}.
 *
 * <p>Reloads config.yml so an admin can change the provider/API key without restarting the server.
 */
public final class AichatReloadExecutor implements CommandExecutor {

  private final MinecraftChatAI plugin;

  /**
   * Creates an AichatReloadExecutor.
   *
   * @param plugin the owning plugin
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Plugin is the owning JavaPlugin instance; caller owns it")
  public AichatReloadExecutor(MinecraftChatAI plugin) {
    this.plugin = plugin;
  }

  @Override
  public void run(CommandSender sender, CommandArguments args) {
    plugin.reloadAiConfig();
    sender.sendRichMessage("<green>Config reloaded.</green>");
  }
}
