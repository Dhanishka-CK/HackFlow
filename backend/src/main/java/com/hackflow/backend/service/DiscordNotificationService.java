package com.hackflow.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class DiscordNotificationService {

    @Value("${discord.webhook.url:}")
    private String webhookUrl;

    private static final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    /**
     * Send notification when a new task is created - WITH ALL DETAILS
     */
    public void sendTaskCreatedNotification(String title, String description, String assignee, 
                                           String priority, String difficulty, String status) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            return;
        }

        try {
            String json = String.format(
                "{" +
                "\"embeds\": [{" +
                "\"title\": \"✅ New Task Created\"," +
                "\"description\": \"**%s**\"," +
                "\"color\": 3066993," +
                "\"fields\": [" +
                "{\"name\": \"📝 Description\", \"value\": \"%s\", \"inline\": false}," +
                "{\"name\": \"📊 Status\", \"value\": \"%s\", \"inline\": true}," +
                "{\"name\": \"🔥 Priority\", \"value\": \"%s\", \"inline\": true}," +
                "{\"name\": \"💪 Difficulty\", \"value\": \"%s\", \"inline\": true}," +
                "{\"name\": \"👤 Assignee\", \"value\": \"%s\", \"inline\": true}," +
                "{\"name\": \"🔗 View Board\", \"value\": \"http://localhost:5173\", \"inline\": false}" +
                "]," +
                "\"footer\": {\"text\": \"HackFlow Bot\"}," +
                "\"timestamp\": \"%s\"" +
                "}]" +
                "}",
                escapeJson(title),
                escapeJson(description != null ? description : "No description provided"),
                escapeJson(status != null ? status : "Backlog"),
                escapeJson(priority != null ? priority : "Medium"),
                escapeJson(difficulty != null ? difficulty : "Medium"),
                escapeJson(assignee != null ? assignee : "Unassigned"),
                java.time.Instant.now().toString()
            );

            sendWebhook(json);
        } catch (Exception e) {
            System.err.println("Failed to send Discord notification: " + e.getMessage());
        }
    }

    /**
     * Send notification when task status changes
     */
    public void sendTaskStatusUpdateNotification(String title, String newStatus, String assignee) {
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        try {
            int color = getStatusColor(newStatus);
            String emoji = getStatusEmoji(newStatus);
            
            String json = String.format(
                "{" +
                "\"embeds\": [{" +
                "\"title\": \"%s Task Updated\"," +
                "\"description\": \"**%s**\"," +
                "\"color\": %d," +
                "\"fields\": [" +
                "{\"name\": \"📊 New Status\", \"value\": \"%s\", \"inline\": true}," +
                "{\"name\": \"👤 Assignee\", \"value\": \"%s\", \"inline\": true}," +
                "{\"name\": \"🔗 View Board\", \"value\": \"http://localhost:5173\", \"inline\": false}" +
                "]," +
                "\"footer\": {\"text\": \"HackFlow Bot\"}," +
                "\"timestamp\": \"%s\"" +
                "}]" +
                "}",
                emoji,
                escapeJson(title),
                color,
                escapeJson(newStatus),
                escapeJson(assignee != null ? assignee : "Unassigned"),
                java.time.Instant.now().toString()
            );

            sendWebhook(json);
        } catch (Exception e) {
            System.err.println("Failed to send Discord notification: " + e.getMessage());
        }
    }

    /**
     * Send notification when a task is deleted
     */
    public void sendTaskDeletedNotification(String title) {
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        try {
            String json = String.format(
                "{" +
                "\"embeds\": [{" +
                "\"title\": \"🗑️ Task Deleted\"," +
                "\"description\": \"**%s** has been removed from the board\"," +
                "\"color\": 15158332," +
                "\"fields\": [" +
                "{\"name\": \"🔗 View Board\", \"value\": \"http://localhost:5173\", \"inline\": false}" +
                "]," +
                "\"footer\": {\"text\": \"HackFlow Bot\"}," +
                "\"timestamp\": \"%s\"" +
                "}]" +
                "}",
                escapeJson(title),
                java.time.Instant.now().toString()
            );

            sendWebhook(json);
        } catch (Exception e) {
            System.err.println("Failed to send Discord notification: " + e.getMessage());
        }
    }

    /**
     * Helper: Send HTTP POST to Discord webhook
     */
    private void sendWebhook(String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .timeout(Duration.ofSeconds(10))
            .build();

        HttpResponse<String> response = httpClient.send(
            request, 
            HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 204) {
            throw new RuntimeException("Discord webhook returned: " + response.statusCode());
        }
    }

    /**
     * Helper: Escape special characters for JSON
     */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    /**
     * Helper: Get embed color based on status
     */
    private int getStatusColor(String status) {
        if (status == null) return 9807270; // Gray
        return switch (status) {
            case "Done" -> 3066993;      // Green
            case "In Progress" -> 3447003; // Blue
            case "Todo" -> 15105570;      // Yellow
            case "Backlog" -> 9807270;    // Gray
            default -> 9807270;
        };
    }

    /**
     * Helper: Get emoji based on status
     */
    private String getStatusEmoji(String status) {
        if (status == null) return "📋";
        return switch (status) {
            case "Done" -> "✅";
            case "In Progress" -> "⚡";
            case "Todo" -> "📝";
            case "Backlog" -> "📦";
            default -> "📋";
        };
    }
}