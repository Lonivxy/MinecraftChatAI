package com.lonivxy.minecraftchatai.chat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks per-player, per-command cooldowns to prevent spamming AI/translate requests.
 *
 * <p>Thread-safe so the async chat path and main-thread command handlers can share it.
 */
public final class CooldownManager {

  private final Map<UUID, Map<String, Long>> lastUsed = new ConcurrentHashMap<>();

  /**
   * Attempts to allow a command for a player, respecting the cooldown.
   *
   * @param playerId the player UUID
   * @param command the command key (e.g. {@code aichat}, {@code translate})
   * @param cooldownMillis the cooldown in milliseconds
   * @return {@code 0} when the command is allowed, otherwise the remaining cooldown in ms
   */
  public long tryUse(UUID playerId, String command, long cooldownMillis) {
    long now = System.currentTimeMillis();
    Map<String, Long> perCommand =
        lastUsed.computeIfAbsent(playerId, key -> new ConcurrentHashMap<>());
    Long last = perCommand.get(command);
    if (last != null) {
      long elapsed = now - last;
      if (elapsed < cooldownMillis) {
        return cooldownMillis - elapsed;
      }
    }
    perCommand.put(command, now);
    return 0L;
  }
}
