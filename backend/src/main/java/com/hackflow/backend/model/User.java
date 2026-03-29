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
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Field(name = "username")
    private String username;

    @Field(name = "email")
    private String email;

    @Field(name = "discordId")
    private String discordId;

    @Field(name = "role")
    private String role; // Backend, Frontend, Full Stack, Designer, QA, DevOps, etc.

    @Field(name = "isActive")
    private Boolean isActive;

    @Field(name = "joinedAt")
    private LocalDateTime joinedAt;
}