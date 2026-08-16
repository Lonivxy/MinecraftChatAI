package com.lonivxy.minecraftchatai.command;

import com.lonivxy.minecraftchatai.ai.AiMessage;
import com.lonivxy.minecraftchatai.ai.AiServices;
import com.lonivxy.minecraftchatai.ai.Prompts;
import com.lonivxy.minecraftchatai.chat.ChatHistory;
import com.lonivxy.minecraftchatai.chat.ChatMessage;
import com.lonivxy.minecraftchatai.config.AiConfig;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Executor for the /translate command.
 *
 * <p>Takes the last {@code count} (1-5) genuine player messages and asks the AI to translate them
 * into the chosen language, then sends the result privately to the requesting player (never
 * broadcast, to prevent spam). Output is sent as plain text so the model cannot inject formatting.
 */
public final class TranslateExecutor implements CommandExecutor {

  private final AiServices services;
  private final ChatHistory history;
  private final Plugin plugin;

  /**
   * Creates a TranslateExecutor.
   *
   * @param services the shared AI services
   * @param history the shared chat history
   * @param plugin the owning plugin, used to reschedule replies onto the main thread
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "History is a shared singleton and Plugin is owned by the caller")
  public TranslateExecutor(AiServices services, ChatHistory history, Plugin plugin) {
    this.services = services;
    this.history = history;
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
            .tryUse(player.getUniqueId(), "translate", config.getCooldownSeconds() * 1000L);
    if (remaining > 0L) {
      player.sendRichMessage(
          "<red>Please wait " + formatSeconds(remaining) + " before using /translate again.</red>");
      return;
    }

    int count = (int) args.get("count");
    String language = displayName((String) args.get("language"));

    List<ChatMessage> recent = history.last(count);
    if (recent.isEmpty()) {
      player.sendRichMessage("<red>No recent chat messages to translate.</red>");
      return;
    }

    String system = Prompts.translateSystemPrompt(language);
    String user = Prompts.formatMessages(recent);

    services
        .getAiClient()
        .chat(List.of(new AiMessage("system", system), new AiMessage("user", user)))
        .thenAccept(reply -> runOnMain(() -> sendTranslation(player, language, reply)))
        .exceptionally(
            error -> {
              runOnMain(
                  () ->
                      player.sendRichMessage(
                          "<red>AI request failed: " + error.getMessage() + "</red>"));
              return null;
            });
  }

  private void sendTranslation(Player player, String language, String reply) {
    player.sendMessage(Component.text("[Translate -> " + language + "]"));
    for (String line : reply.split("\n")) {
      player.sendMessage(Component.text(line));
    }
  }

  private void runOnMain(Runnable task) {
    plugin.getServer().getScheduler().runTask(plugin, task);
  }

  private static String formatSeconds(long millis) {
    return ((millis + 999L) / 1000L) + "s";
  }

  private static String displayName(String language) {
    switch (language) {
      case "english":
        return "English";
      case "chinese":
        return "Chinese";
      case "french":
        return "French";
      case "japanese":
        return "Japanese";
      default:
        return "English";
    }
  }
}
