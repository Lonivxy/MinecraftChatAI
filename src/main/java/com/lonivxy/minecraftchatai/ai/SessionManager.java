package com.lonivxy.minecraftchatai.ai;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps short, bounded conversation histories so /aichat can hold multi-turn context.
 *
 * <p>Each player has their own private session, and there is one shared public session used when
 * /aichat replies are broadcast. Sessions are capped so prompt sizes stay reasonable.
 */
public final class SessionManager {

  /** Maximum number of user+assistant turns retained per session. */
  private static final int MAX_TURNS = 20;

  private final Map<UUID, Deque<AiMessage>> privateSessions = new ConcurrentHashMap<>();
  private final Deque<AiMessage> publicSession = new ArrayDeque<>();

  /**
   * Returns an immutable snapshot of a player's private session.
   *
   * @param playerId the player UUID
   * @return the session history
   */
  public synchronized List<AiMessage> privateSession(UUID playerId) {
    Deque<AiMessage> session = privateSessions.get(playerId);
    return session == null ? List.of() : List.copyOf(session);
  }

  /**
   * Appends a user/assistant turn to a player's private session.
   *
   * @param playerId the player UUID
   * @param userMessage the player's message
   * @param assistantMessage the AI's reply
   */
  public synchronized void appendPrivate(
      UUID playerId, AiMessage userMessage, AiMessage assistantMessage) {
    Deque<AiMessage> session = privateSessions.computeIfAbsent(playerId, key -> new ArrayDeque<>());
    session.addLast(userMessage);
    session.addLast(assistantMessage);
    trim(session);
  }

  /**
   * Clears a player's private session.
   *
   * @param playerId the player UUID
   */
  public synchronized void resetPrivate(UUID playerId) {
    privateSessions.remove(playerId);
  }

  /**
   * Returns an immutable snapshot of the shared public session.
   *
   * @return the public session history
   */
  public synchronized List<AiMessage> publicSession() {
    return List.copyOf(publicSession);
  }

  /**
   * Appends a user/assistant turn to the shared public session.
   *
   * @param userMessage the player's message
   * @param assistantMessage the AI's reply
   */
  public synchronized void appendPublic(AiMessage userMessage, AiMessage assistantMessage) {
    publicSession.addLast(userMessage);
    publicSession.addLast(assistantMessage);
    trim(publicSession);
  }

  /**
   * Clears the shared public session.
   */
  public synchronized void resetPublic() {
    publicSession.clear();
  }

  private static void trim(Deque<AiMessage> session) {
    while (session.size() > MAX_TURNS * 2) {
      session.removeFirst();
    }
  }
}
