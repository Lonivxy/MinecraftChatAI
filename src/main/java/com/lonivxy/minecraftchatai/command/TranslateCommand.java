package com.lonivxy.minecraftchatai.command;

import com.lonivxy.minecraftchatai.ai.AiServices;
import com.lonivxy.minecraftchatai.chat.ChatHistory;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import org.bukkit.plugin.Plugin;

/**
 * Registers the /translate command.
 */
public final class TranslateCommand {

  private final CommandAPICommand command;

  /**
   * Builds the /translate command.
   *
   * @param services the shared AI services
   * @param history the shared chat history
   * @param plugin the owning plugin
   */
  public TranslateCommand(AiServices services, ChatHistory history, Plugin plugin) {
    this.command =
        new CommandAPICommand("translate")
            .withPermission("minecraftchatai.translate")
            .withArguments(
                new IntegerArgument("count", 1, 5),
                new MultiLiteralArgument(
                    "language", "english", "chinese", "french", "japanese"))
            .executes(new TranslateExecutor(services, history, plugin));
  }

  /**
   * Registers the command with CommandAPI.
   *
   * @return this command
   */
  public TranslateCommand register() {
    command.register();
    return this;
  }
}
