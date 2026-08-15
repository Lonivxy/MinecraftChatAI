package com.lonivxy.minecraftchatai.listener;

import com.lonivxy.minecraftchatai.chat.ChatHistory;
import com.lonivxy.minecraftchatai.chat.ChatMessage;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Captures genuine player chat messages into the shared history used by /translate.
 *
 * <p>Plugin messages and AI replies never fire {@link AsyncChatEvent}, so they are never recorded.
 */
public final class ChatListener implements Listener {

  private static final PlainTextComponentSerializer PLAIN =
      PlainTextComponentSerializer.plainText();

  private final ChatHistory history;

  /**
   * Creates a ChatListener that writes captured messages into the given history.
   *
   * @param history the shared chat history
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "History is a shared singleton owned by the plugin")
  public ChatListener(ChatHistory history) {
    this.history = history;
  }

  /**
   * Records a player's chat message, stripping line breaks so a single message stays on one line.
   *
   * @param event the chat event
   */
  @EventHandler
  public void onChat(AsyncChatEvent event) {
    Player player = event.getPlayer();
    String text = PLAIN.serialize(event.message()).replace('\r', ' ').replace('\n', ' ');
    history.add(new ChatMessage(player.getName(), text));
  }
}
