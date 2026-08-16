package com.lonivxy.minecraftchatai.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

  private static final UUID PLAYER = UUID.randomUUID();

  @Test
  void privateSessionStartsEmpty() {
    SessionManager manager = new SessionManager();

    assertTrue(manager.privateSession(PLAYER).isEmpty());
  }

  @Test
  void privateSessionRetainsTurns() {
    SessionManager manager = new SessionManager();
    manager.appendPrivate(PLAYER, new AiMessage("user", "hi"), new AiMessage("assistant", "hello"));
    manager.appendPrivate(PLAYER, new AiMessage("user", "bye"), new AiMessage("assistant", "cya"));

    List<AiMessage> session = manager.privateSession(PLAYER);

    assertEquals(4, session.size());
    assertEquals("hi", session.get(0).content());
    assertEquals("cya", session.get(3).content());
  }

  @Test
  void resetPrivateClears() {
    SessionManager manager = new SessionManager();
    manager.appendPrivate(PLAYER, new AiMessage("user", "hi"), new AiMessage("assistant", "hello"));

    manager.resetPrivate(PLAYER);

    assertTrue(manager.privateSession(PLAYER).isEmpty());
  }

  @Test
  void publicSessionIndependentFromPrivate() {
    SessionManager manager = new SessionManager();
    manager.appendPublic(new AiMessage("user", "pub"), new AiMessage("assistant", "reply"));

    assertTrue(manager.privateSession(PLAYER).isEmpty());
    assertEquals(2, manager.publicSession().size());
  }

  @Test
  void resetPublicClears() {
    SessionManager manager = new SessionManager();
    manager.appendPublic(new AiMessage("user", "pub"), new AiMessage("assistant", "reply"));

    manager.resetPublic();

    assertTrue(manager.publicSession().isEmpty());
  }

  @Test
  void capsSessionSize() {
    SessionManager manager = new SessionManager();
    for (int i = 0; i < 50; i++) {
      manager.appendPrivate(
          PLAYER, new AiMessage("user", "u" + i), new AiMessage("assistant", "a" + i));
    }

    assertTrue(manager.privateSession(PLAYER).size() <= 40);
  }
}
