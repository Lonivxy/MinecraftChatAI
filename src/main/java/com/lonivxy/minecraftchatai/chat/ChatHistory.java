package com.lonivxy.minecraftchatai.chat;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A bounded, thread-safe buffer of the most recent player chat messages on the server.
 *
 * <p>Used by the {@code /translate} command to fetch the last {@code n} messages. Reads and
 * writes are synchronized so the async chat event and the main-thread command handlers can share
 * the buffer safely.
 */
public final class ChatHistory {

  private final int maxSize;
  private final ArrayDeque<ChatMessage> messages = new ArrayDeque<>();

  /**
   * Creates a ChatHistory that keeps at most the given number of messages.
   *
   * @param maxSize the maximum number of messages to retain
   */
  public ChatHistory(int maxSize) {
    this.maxSize = Math.max(1, maxSize);
  }

  /**
   * Appends a message, dropping the oldest message if the buffer is full.
   *
   * @param message the message to add
   */
  public synchronized void add(ChatMessage message) {
    messages.addLast(message);
    while (messages.size() > maxSize) {
      messages.removeFirst();
    }
  }

  /**
   * Returns the last {@code count} messages, oldest first. If fewer messages are available, all
   * of them are returned.
   *
   * @param count the number of messages to return
   * @return an immutable list of the most recent messages, oldest first
   */
  public synchronized List<ChatMessage> last(int count) {
    int take = Math.max(0, Math.min(count, messages.size()));
    if (take == 0) {
      return Collections.emptyList();
    }
    List<ChatMessage> result = new ArrayList<>(take);
    Object[] snapshot = messages.toArray();
    for (int i = snapshot.length - take; i < snapshot.length; i++) {
      result.add((ChatMessage) snapshot[i]);
    }
    return Collections.unmodifiableList(result);
  }

  /**
   * Returns the number of messages currently stored.
   *
   * @return the current size
   */
  public synchronized int size() {
    return messages.size();
  }
}
