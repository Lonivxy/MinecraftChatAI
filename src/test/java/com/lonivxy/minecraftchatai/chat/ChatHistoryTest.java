package com.lonivxy.minecraftchatai.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ChatHistoryTest {

  @Test
  void returnsLastMessagesOldestFirst() {
    ChatHistory history = new ChatHistory(10);
    history.add(new ChatMessage("A", "one"));
    history.add(new ChatMessage("B", "two"));
    history.add(new ChatMessage("C", "three"));

    List<ChatMessage> last = history.last(2);

    assertEquals(2, last.size());
    assertEquals("B", last.get(0).playerName());
    assertEquals("two", last.get(0).message());
    assertEquals("C", last.get(1).playerName());
  }

  @Test
  void returnsAllWhenFewerAvailable() {
    ChatHistory history = new ChatHistory(10);
    history.add(new ChatMessage("A", "one"));

    List<ChatMessage> last = history.last(5);

    assertEquals(1, last.size());
    assertEquals("A", last.get(0).playerName());
  }

  @Test
  void returnsEmptyWhenNoMessages() {
    ChatHistory history = new ChatHistory(10);

    assertEquals(0, history.last(3).size());
  }

  @Test
  void capsSize() {
    ChatHistory history = new ChatHistory(2);
    history.add(new ChatMessage("A", "one"));
    history.add(new ChatMessage("B", "two"));
    history.add(new ChatMessage("C", "three"));

    assertEquals(2, history.size());
    List<ChatMessage> last = history.last(2);
    assertEquals("B", last.get(0).playerName());
    assertEquals("C", last.get(1).playerName());
  }
}
