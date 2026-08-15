package com.lonivxy.minecraftchatai.ai;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lonivxy.minecraftchatai.config.AiConfig;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Minimal OpenAI-compatible chat completions client backed by {@link java.net.http.HttpClient}.
 *
 * <p>Works with any provider exposing the standard {@code /chat/completions} endpoint, such as
 * DeepSeek. Requests are sent asynchronously so the server's main thread is never blocked.
 */
public final class AiClient {

  private final AiConfig config;
  private final HttpClient httpClient;
  private final Gson gson = new Gson();

  /**
   * Creates an AiClient using the given configuration and a default HTTP client.
   *
   * @param config the AI provider configuration
   */
  public AiClient(AiConfig config) {
    this(
        config,
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(config.getTimeoutSeconds()))
            .build());
  }

  AiClient(AiConfig config, HttpClient httpClient) {
    this.config = config;
    this.httpClient = httpClient;
  }

  /**
   * Sends a chat request and completes with the model's text reply.
   *
   * @param systemPrompt the system prompt
   * @param userContent the user message content
   * @return a future completing with the model's reply
   */
  public CompletableFuture<String> chat(String systemPrompt, String userContent) {
    String payload = buildPayload(systemPrompt, userContent);
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(config.getBaseUrl() + "/chat/completions"))
            .header("Authorization", "Bearer " + config.getApiKey())
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();

    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::extractContent);
  }

  private String buildPayload(String systemPrompt, String userContent) {
    JsonObject payload = new JsonObject();
    payload.addProperty("model", config.getModel());

    JsonArray messages = new JsonArray();
    messages.add(roleMessage("system", systemPrompt));
    messages.add(roleMessage("user", userContent));
    payload.add("messages", messages);

    payload.addProperty("temperature", 0.7);
    return gson.toJson(payload);
  }

  private JsonObject roleMessage(String role, String content) {
    JsonObject message = new JsonObject();
    message.addProperty("role", role);
    message.addProperty("content", content);
    return message;
  }

  private String extractContent(HttpResponse<String> response) {
    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      throw new IllegalStateException(
          "AI request failed with status " + response.statusCode() + ": " + response.body());
    }

    JsonObject root = gson.fromJson(response.body(), JsonObject.class);
    JsonArray choices = root.getAsJsonArray("choices");
    if (choices == null || choices.isEmpty()) {
      throw new IllegalStateException("AI response contained no choices.");
    }

    JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
    if (message == null || !message.has("content")) {
      throw new IllegalStateException("AI response contained no message content.");
    }

    return message.get("content").getAsString();
  }
}
