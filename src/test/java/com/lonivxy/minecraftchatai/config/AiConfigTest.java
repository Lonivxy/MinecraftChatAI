package com.lonivxy.minecraftchatai.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AiConfigTest {

  @Test
  void configuredWhenRealKey() {
    AiConfig config =
        new AiConfig("https://api.deepseek.com", "sk-test", "deepseek-chat", 300, 30);

    assertTrue(config.isConfigured());
  }

  @Test
  void notConfiguredWhenPlaceholderKey() {
    AiConfig config =
        new AiConfig("https://api.deepseek.com", "REPLACE_ME", "deepseek-chat", 300, 30);

    assertFalse(config.isConfigured());
  }

  @Test
  void notConfiguredWhenKeyBlank() {
    AiConfig config =
        new AiConfig("https://api.deepseek.com", "  ", "deepseek-chat", 300, 30);

    assertFalse(config.isConfigured());
  }

  @Test
  void notConfiguredWhenBaseUrlMissing() {
    AiConfig config = new AiConfig("", "sk-test", "deepseek-chat", 300, 30);

    assertFalse(config.isConfigured());
  }

  @Test
  void trimsValues() {
    AiConfig config =
        new AiConfig("  https://api.deepseek.com  ", "  sk-test  ", "  deepseek-chat  ", 300, 30);

    assertTrue(config.isConfigured());
  }
}
