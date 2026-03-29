package com.hackflow.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    @Field(name = "title")
    private String title;

    @Field(name = "description")
    private String description;

    @Field(name = "status")
    private String status; // Backlog, Todo, In Progress, Done

    @Field(name = "priority")
    private String priority; // Low, Medium, High

    @Field(name = "difficulty")
    private String difficulty; // Easy, Medium, Hard

    @Field(name = "assigneeId")
    private String assigneeId;

    @Field(name = "assigneeName")
    private String assigneeName;

    @Field(name = "createdBy")
    private String createdBy;

    @Field(name = "createdAt")
    private LocalDateTime createdAt;

    @Field(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Field(name = "completedAt")
    private LocalDateTime completedAt;

    @Field(name = "tags")
    private String[] tags;

    @Field(name = "comments")
    private String[] comments;
}