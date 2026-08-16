package com.lonivxy.minecraftchatai.ai;

/**
 * A single message in an AI conversation.
 *
 * @param role the message role, e.g. {@code system}, {@code user}, or {@code assistant}
 * @param content the message text
 */
public record AiMessage(String role, String content) {}
