package com.hackflow.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/api/health")
    public Map<String, String> checkHealth() {
        Map<String, String> response = new HashMap<>();
        try {
            String dbName = mongoTemplate.getDb().getName();
            response.put("status", "UP");
            response.put("database", dbName);
            response.put("message", "HackFlow Backend is connected to MongoDB!");
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("message", "Database connection failed: " + e.getMessage());
        }
        return response;
    }
}