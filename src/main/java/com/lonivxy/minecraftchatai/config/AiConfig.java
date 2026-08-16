package com.lonivxy.minecraftchatai.config;

/**
 * Holds the AI provider settings loaded from config.yml.
 *
 * <p>All requests are sent to {@code <baseUrl>/chat/completions} using the OpenAI-compatible chat
 * completions API, so any provider that supports it (DeepSeek, OpenAI, etc.) works.
 */
public final class AiConfig {

  /** Sentinel value the shipped config.yml uses until the owner sets a real key. */
  public static final String PLACEHOLDER_KEY = "REPLACE_ME";

  private final String baseUrl;
  private final String apiKey;
  private final String model;
  private final int maxReplyLength;
  private final int timeoutSeconds;
  private final int cooldownSeconds;
  private final boolean aichatPublic;

  /**
   * Creates an AiConfig from the given values.
   *
   * @param baseUrl the provider's base URL, e.g. {@code https://api.deepseek.com}
   * @param apiKey the provider API key
   * @param model the model name, e.g. {@code deepseek-chat}
   * @param maxReplyLength maximum number of characters for /aichat replies
   * @param timeoutSeconds request timeout in seconds
   * @param cooldownSeconds minimum seconds between AI/translate commands per player
   * @param aichatPublic whether /aichat replies are broadcast to everyone (public) or private
   */
  public AiConfig(
      String baseUrl,
      String apiKey,
      String model,
      int maxReplyLength,
      int timeoutSeconds,
      int cooldownSeconds,
      boolean aichatPublic) {
    this.baseUrl = baseUrl == null ? "" : baseUrl.trim();
    this.apiKey = apiKey == null ? "" : apiKey.trim();
    this.model = model == null ? "" : model.trim();
    this.maxReplyLength = maxReplyLength;
    this.timeoutSeconds = timeoutSeconds;
    this.cooldownSeconds = Math.max(0, cooldownSeconds);
    this.aichatPublic = aichatPublic;
  }

  /**
   * Returns whether enough configuration is present to call the AI provider.
   *
   * @return true when a real API key, base URL, and model are set
   */
  public boolean isConfigured() {
    return !apiKey.isEmpty()
        && !apiKey.equals(PLACEHOLDER_KEY)
        && !baseUrl.isEmpty()
        && !model.isEmpty();
  }

  /**
   * Returns the provider's base URL.
   *
   * @return the base URL
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  /**
   * Returns the provider API key.
   *
   * @return the API key
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * Returns the model name.
   *
   * @return the model name
   */
  public String getModel() {
    return model;
  }

  /**
   * Returns the maximum number of characters for /aichat replies.
   *
   * @return the maximum reply length
   */
  public int getMaxReplyLength() {
    return maxReplyLength;
  }

  /**
   * Returns the request timeout in seconds.
   *
   * @return the timeout in seconds
   */
  public int getTimeoutSeconds() {
    return timeoutSeconds;
  }

  /**
   * Returns the minimum seconds between AI/translate commands per player.
   *
   * @return the cooldown in seconds
   */
  public int getCooldownSeconds() {
    return cooldownSeconds;
  }

  /**
   * Returns whether /aichat replies are broadcast to everyone.
   *
   * @return true when replies are public, false when private
   */
  public boolean isAichatPublic() {
    return aichatPublic;
  }
}
