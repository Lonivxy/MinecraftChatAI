package com.lonivxy.minecraftchatai.command;

import com.lonivxy.minecraftchatai.ai.AiClient;
import com.lonivxy.minecraftchatai.ai.Prompts;
import com.lonivxy.minecraftchatai.config.AiConfig;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Executor for the /aichat command (alias /aic).
 *
 * <p>Forwards the player's message to the AI with a neko system prompt and replies directly. AI
 * output is sent as plain text (never parsed as MiniMessage) so the model cannot inject formatting.
 */
public final class AichatExecutor implements CommandExecutor {

  private static final String PREFIX = "[Nya] ";

  private final AiClient aiClient;
  private final AiConfig config;
  private final Plugin plugin;

  /**
   * Creates an AichatExecutor.
   *
   * @param aiClient the AI client
   * @param config the AI configuration
   * @param plugin the owning plugin, used to reschedule replies onto the main thread
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Plugin is the owning JavaPlugin instance; caller owns it")
  public AichatExecutor(AiClient aiClient, AiConfig config, Plugin plugin) {
    this.aiClient = aiClient;
    this.config = config;
    this.plugin = plugin;
  }

  @Override
  public void run(CommandSender sender, CommandArguments args) {
    if (!(sender instanceof Player player)) {
      sender.sendRichMessage("<red>Only players can use this command.</red>");
      return;
    }
    if (!config.isConfigured()) {
      player.sendRichMessage(
          "<red>AI is not configured. Ask an admin to set ai.api-key in config.yml.</red>");
      return;
    }

    String message = (String) args.get("message");
    String system = Prompts.nekoSystemPrompt(config.getMaxReplyLength());

    aiClient.chat(system, message)
        .thenAccept(
            reply -> runOnMain(() -> player.sendMessage(Component.text(PREFIX + trim(reply)))))
        .exceptionally(
            error -> {
              runOnMain(
                  () ->
                      player.sendRichMessage(
                          "<red>AI request failed: " + error.getMessage() + "</red>"));
              return null;
            });
  }

  private void runOnMain(Runnable task) {
    plugin.getServer().getScheduler().runTask(plugin, task);
  }

  private String trim(String value) {
    int max = Math.max(1, config.getMaxReplyLength());
    return value.length() <= max ? value : value.substring(0, max);
  }
}
