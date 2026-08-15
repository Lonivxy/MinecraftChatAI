package com.lonivxy.minecraftchatai.chat;

/**
 * A single player chat message captured by the plugin.
 *
 * <p>Only genuine player chat is stored here; plugin messages and AI replies are never captured
 * because they do not fire the player chat event.
 *
 * @param playerName the name of the player who sent the message
 * @param message the plain-text message content
 */
public record ChatMessage(String playerName, String message) {}
