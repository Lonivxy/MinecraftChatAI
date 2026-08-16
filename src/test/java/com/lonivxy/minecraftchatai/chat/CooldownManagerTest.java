package com.lonivxy.minecraftchatai.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CooldownManagerTest {

  private static final UUID PLAYER = UUID.randomUUID();

  @Test
  void allowsFirstUse() {
    CooldownManager manager = new CooldownManager();

    assertEquals(0L, manager.tryUse(PLAYER, "aichat", 1000L));
  }

  @Test
  void blocksImmediateSecondUse() {
    CooldownManager manager = new CooldownManager();
    manager.tryUse(PLAYER, "aichat", 1000L);

    long remaining = manager.tryUse(PLAYER, "aichat", 1000L);

    assertTrue(remaining > 0L);
  }

  @Test
  void allowsAfterCooldownElapses() throws InterruptedException {
    CooldownManager manager = new CooldownManager();
    manager.tryUse(PLAYER, "aichat", 10L);
    Thread.sleep(20L);

    assertEquals(0L, manager.tryUse(PLAYER, "aichat", 10L));
  }

  @Test
  void commandsTrackedIndependently() {
    CooldownManager manager = new CooldownManager();
    manager.tryUse(PLAYER, "aichat", 1000L);

    assertEquals(0L, manager.tryUse(PLAYER, "translate", 1000L));
  }

  @Test
  void differentPlayersTrackedIndependently() {
    CooldownManager manager = new CooldownManager();
    manager.tryUse(PLAYER, "aichat", 1000L);

    assertEquals(0L, manager.tryUse(UUID.randomUUID(), "aichat", 1000L));
  }
}
