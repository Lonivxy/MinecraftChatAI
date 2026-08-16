package com.lonivxy.minecraftchatai.ai;

import com.lonivxy.minecraftchatai.chat.CooldownManager;
import com.lonivxy.minecraftchatai.config.AiConfig;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Shared holder for the AI client, live configuration, conversation sessions, and cooldowns.
 *
 * <p>All executors depend on this single object so that a config reload ({@code /aichat reload})
 * can swap the {@link AiConfig} and {@link AiClient} in place without re-registering commands.
 */
public final class AiServices {

  private volatile AiConfig config;
  private volatile AiClient aiClient;
  private final SessionManager sessions;
  private final CooldownManager cooldowns;

  /**
   * Creates an AiServices from the given initial configuration.
   *
   * @param config the initial AI configuration
   */
  public AiServices(AiConfig config) {
    this.config = config;
    this.aiClient = new AiClient(config);
    this.sessions = new SessionManager();
    this.cooldowns = new CooldownManager();
  }

  /**
   * Replaces the configuration and rebuilds the AI client (used by /aichat reload).
   *
   * @param newConfig the new configuration
   */
  public void reload(AiConfig newConfig) {
    this.config = newConfig;
    this.aiClient = new AiClient(newConfig);
  }

  /**
   * Returns the current AI configuration.
   *
   * @return the configuration
   */
  public AiConfig getConfig() {
    return config;
  }

  /**
   * Returns the current AI client.
   *
   * @return the AI client
   */
  public AiClient getAiClient() {
    return aiClient;
  }

  /**
   * Returns the conversation session manager.
   *
   * @return the session manager
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Sessions are a shared singleton deliberately exposed to executors")
  public SessionManager getSessions() {
    return sessions;
  }

  /**
   * Returns the cooldown manager.
   *
   * @return the cooldown manager
   */
  public CooldownManager getCooldowns() {
    return cooldowns;
  }
}
