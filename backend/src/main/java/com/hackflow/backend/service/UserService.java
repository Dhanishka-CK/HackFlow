package com.hackflow.backend.service;

import com.hackflow.backend.model.User;
import com.hackflow.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // CREATE: Add new user
    public User createUser(User user) {
        // Set timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Save to database
        return userRepository.save(user);
    }
    
    // READ: Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // READ: Get user by ID (⚠️ Long instead of String)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // READ: Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // READ: Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // UPDATE: Modify existing user (⚠️ Long instead of String)
    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            
            // Update fields if provided (null-check pattern)
            if (updatedUser.getUsername() != null) {
                user.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getEmail() != null) {
                user.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPasswordHash() != null) {
                user.setPasswordHash(updatedUser.getPasswordHash());
            }
            
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        return null;  // User not found
    }
    
    // DELETE: Remove user (⚠️ Long instead of String)
    public boolean deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    // CHECK: Username exists
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    // CHECK: Email exists
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}