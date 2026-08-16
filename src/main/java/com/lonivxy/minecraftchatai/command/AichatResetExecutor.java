package com.lonivxy.minecraftchatai.command;

import com.lonivxy.minecraftchatai.ai.AiServices;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executor for {@code /aichat resetsession [public]}.
 *
 * <p>Without the {@code public} argument it clears the sender's private conversation. With it
 * (permission {@code aichat.publicsessionreset}) it clears the shared public conversation.
 */
public final class AichatResetExecutor implements CommandExecutor {

  private final AiServices services;
  private final boolean isPublic;

  /**
   * Creates an AichatResetExecutor.
   *
   * @param services the shared AI services
   * @param isPublic true to reset the public conversation, false the private one
   */
  public AichatResetExecutor(AiServices services, boolean isPublic) {
    this.services = services;
    this.isPublic = isPublic;
  }

  @Override
  public void run(CommandSender sender, CommandArguments args) {
    if (!(sender instanceof Player player)) {
      sender.sendRichMessage("<red>Only players can use this command.</red>");
      return;
    }

    if (isPublic) {
      services.getSessions().resetPublic();
      player.sendRichMessage("<green>Public conversation has been reset.</green>");
    } else {
      services.getSessions().resetPrivate(player.getUniqueId());
      player.sendRichMessage("<green>Your conversation has been reset.</green>");
    }
  }
}
