package com.lonivxy.minecraftchatai.command;

import com.lonivxy.minecraftchatai.ai.AiMessage;
import com.lonivxy.minecraftchatai.ai.AiServices;
import com.lonivxy.minecraftchatai.ai.Prompts;
import com.lonivxy.minecraftchatai.config.AiConfig;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Executor for {@code /aichat <message>}.
 *
 * <p>Forwards the player's message with conversation context to the AI. Replies are private by
 * default or broadcast when {@code aichat-public} is enabled. Output is plain text (never parsed
 * as MiniMessage) so the model cannot inject formatting.
 */
public final class AichatExecutor implements CommandExecutor {

  private static final String PREFIX = "[Nya] ";

  private final AiServices services;
  private final Plugin plugin;

  /**
   * Creates an AichatExecutor.
   *
   * @param services the shared AI services
   * @param plugin the owning plugin, used to reschedule replies onto the main thread
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Plugin is owned by the caller; services is a shared singleton")
  public AichatExecutor(AiServices services, Plugin plugin) {
    this.services = services;
    this.plugin = plugin;
  }

  @Override
  public void run(CommandSender sender, CommandArguments args) {
    if (!(sender instanceof Player player)) {
      sender.sendRichMessage("<red>Only players can use this command.</red>");
      return;
    }
    AiConfig config = services.getConfig();
    if (!config.isConfigured()) {
      player.sendRichMessage(
          "<red>AI is not configured. Ask an admin to set ai.api-key in config.yml.</red>");
      return;
    }

    long remaining =
        services
            .getCooldowns()
            .tryUse(player.getUniqueId(), "aichat", config.getCooldownSeconds() * 1000L);
    if (remaining > 0L) {
      player.sendRichMessage(
          "<red>Please wait " + formatSeconds(remaining) + " before using /aichat again.</red>");
      return;
    }

    String message = (String) args.get("message");
    boolean isPublic = config.isAichatPublic();

    List<AiMessage> history =
        isPublic
            ? services.getSessions().publicSession()
            : services.getSessions().privateSession(player.getUniqueId());

    List<AiMessage> conversation = new ArrayList<>();
    conversation.add(new AiMessage("system", Prompts.nekoSystemPrompt(config.getMaxReplyLength())));
    conversation.addAll(history);
    conversation.add(new AiMessage("user", message));

    services
        .getAiClient()
        .chat(conversation)
        .thenAccept(reply -> runOnMain(() -> handleReply(player, message, reply, isPublic)))
        .exceptionally(
            error -> {
              runOnMain(
                  () ->
                      player.sendRichMessage(
                          "<red>AI request failed: " + error.getMessage() + "</red>"));
              return null;
            });
  }

  private void handleReply(Player player, String userMessage, String reply, boolean isPublic) {
    String limited = trim(reply);
    AiMessage user = new AiMessage("user", userMessage);
    AiMessage assistant = new AiMessage("assistant", limited);

    if (isPublic) {
      services.getSessions().appendPublic(user, assistant);
      plugin.getServer().broadcast(Component.text(PREFIX + player.getName() + ": " + limited));
    } else {
      services.getSessions().appendPrivate(player.getUniqueId(), user, assistant);
      player.sendMessage(Component.text(PREFIX + limited));
    }
  }

  private void runOnMain(Runnable task) {
    plugin.getServer().getScheduler().runTask(plugin, task);
  }

  private String trim(String value) {
    int max = Math.max(1, services.getConfig().getMaxReplyLength());
    return value.length() <= max ? value : value.substring(0, max);
  }

  private static String formatSeconds(long millis) {
    return ((millis + 999L) / 1000L) + "s";
  }
}
