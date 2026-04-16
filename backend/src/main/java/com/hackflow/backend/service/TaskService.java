package com.hackflow.backend.service;

import com.hackflow.backend.model.Task;
import com.hackflow.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private DiscordNotificationService discordNotificationService;
    
    // CREATE: Add new task
    public Task createTask(Task task) {
        // Set timestamps
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        // Set default status if not provided
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("Backlog");
        }
        
        // Save to database
        Task savedTask = taskRepository.save(task);
        
        // Send Discord notification (async, doesn't block)
        try {
            discordNotificationService.sendTaskCreatedNotification(
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getAssigneeName(),
                savedTask.getPriority(),
                savedTask.getDifficulty(),
                savedTask.getStatus()
            );
        } catch (Exception e) {
            System.err.println("Discord notification failed: " + e.getMessage());
        }
        
        return savedTask;
    }
    
    // READ: Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    // READ: Get task by ID (⚠️ Long instead of String)
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    // READ: Filter by status
    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }
    
    // UPDATE: Modify existing task (⚠️ Long instead of String)
    public Task updateTask(Long id, Task updatedTask) {
        Optional<Task> existingTask = taskRepository.findById(id);
        
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            
            // Update fields if provided (null-check pattern)
            if (updatedTask.getTitle() != null) {
                task.setTitle(updatedTask.getTitle());
            }
            if (updatedTask.getDescription() != null) {
                task.setDescription(updatedTask.getDescription());
            }
            if (updatedTask.getStatus() != null) {
                task.setStatus(updatedTask.getStatus());
                
                // Auto-set completedAt when status becomes "Done"
                if ("Done".equals(updatedTask.getStatus()) && task.getCompletedAt() == null) {
                    task.setCompletedAt(LocalDateTime.now());
                }
            }
            if (updatedTask.getPriority() != null) {
                task.setPriority(updatedTask.getPriority());
            }
            if (updatedTask.getDifficulty() != null) {
                task.setDifficulty(updatedTask.getDifficulty());
            }
            if (updatedTask.getAssigneeName() != null) {
                task.setAssigneeName(updatedTask.getAssigneeName());
            }
            
            task.setUpdatedAt(LocalDateTime.now());
            return taskRepository.save(task);
        }
        return null;  // Task not found
    }
    
    // DELETE: Remove task (⚠️ Long instead of String)
    public boolean deleteTask(Long id) {
        try {
            taskRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting task: " + e.getMessage());
            return false;
        }
    }
    
    // NEW: Search tasks by keyword
    public List<Task> searchTasks(String keyword) {
        return taskRepository.searchByKeyword(keyword);
    }
    
    // NEW: Count tasks by status
    public long countByStatus(String status) {
        return taskRepository.countByStatus(status);
    }
}