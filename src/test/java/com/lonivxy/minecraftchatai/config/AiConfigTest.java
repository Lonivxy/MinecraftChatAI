package com.lonivxy.minecraftchatai.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AiConfigTest {

  private static AiConfig config(
      String baseUrl, String apiKey, int cooldownSeconds, boolean aichatPublic) {
    return new AiConfig(baseUrl, apiKey, "deepseek-chat", 300, 30, cooldownSeconds, aichatPublic);
  }

  @Test
  void configuredWhenRealKey() {
    assertTrue(config("https://api.deepseek.com", "sk-test", 30, false).isConfigured());
  }

  @Test
  void notConfiguredWhenPlaceholderKey() {
    assertFalse(config("https://api.deepseek.com", "REPLACE_ME", 30, false).isConfigured());
  }

  @Test
  void notConfiguredWhenKeyBlank() {
    assertFalse(config("https://api.deepseek.com", "  ", 30, false).isConfigured());
  }

  @Test
  void notConfiguredWhenBaseUrlMissing() {
    assertFalse(config("", "sk-test", 30, false).isConfigured());
  }

  @Test
  void trimsValues() {
    AiConfig config =
        new AiConfig(
            "  https://api.deepseek.com  ", "  sk-test  ", "  deepseek-chat  ", 300, 30, 30, true);

    assertTrue(config.isConfigured());
    assertTrue(config.isAichatPublic());
  }

  @Test
  void clampsNegativeCooldown() {
    AiConfig config = config("https://api.deepseek.com", "sk-test", -5, false);

    assertTrue(config.getCooldownSeconds() >= 0);
  }
}
