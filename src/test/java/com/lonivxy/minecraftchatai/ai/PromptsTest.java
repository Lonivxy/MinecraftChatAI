package com.lonivxy.minecraftchatai.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lonivxy.minecraftchatai.chat.ChatMessage;
import java.util.List;
import org.junit.jupiter.api.Test;

class PromptsTest {

  @Test
  void nekoPromptContainsKeyRules() {
    String prompt = Prompts.nekoSystemPrompt(300);

    assertTrue(prompt.contains("neko"));
    assertTrue(prompt.contains("300 characters"));
    assertTrue(prompt.contains("UNTRUSTED data"));
    assertTrue(prompt.contains("Never reveal"));
  }

  @Test
  void translatePromptNamesLanguage() {
    String prompt = Prompts.translateSystemPrompt("Chinese");

    assertTrue(prompt.contains("Chinese"));
    assertTrue(prompt.contains("UNTRUSTED player input"));
    assertTrue(prompt.contains("prompt injections"));
  }

  @Test
  void formatsMessagesWithPlayerPrefix() {
    List<ChatMessage> messages =
        List.of(
            new ChatMessage("PlayerB", "bonjour!"),
            new ChatMessage("PlayerD", "你们在说什么"));

    String formatted = Prompts.formatMessages(messages);

    assertEquals("Messages:\n<PlayerB> bonjour!\n<PlayerD> 你们在说什么\n", formatted);
  }

  @Test
  void formatsMessagesStripsLineBreaks() {
    List<ChatMessage> messages =
        List.of(new ChatMessage("PlayerA", "line one\nline two"));

    String formatted = Prompts.formatMessages(messages);

    assertEquals("Messages:\n<PlayerA> line one line two\n", formatted);
  }
}
