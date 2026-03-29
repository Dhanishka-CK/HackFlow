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

    // Create a new task
    public Task createTask(Task task) {
    task.setCreatedAt(LocalDateTime.now());
    task.setUpdatedAt(LocalDateTime.now());
    
    // Set default status if not provided
    if (task.getStatus() == null || task.getStatus().isEmpty()) {
        task.setStatus("Backlog");
    }
    
    // Save to database
    Task savedTask = taskRepository.save(task);
    
    // Send Discord notification with ALL details
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
        // Don't fail the request if Discord notification fails
        System.err.println("Discord notification failed: " + e.getMessage());
    }
    
    return savedTask;
}

    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Get task by ID
    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    // Get tasks by status
    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    // Get tasks by assignee
    public List<Task> getTasksByAssignee(String assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }

    // Update a task
    public Task updateTask(String id, Task updatedTask) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            
            if (updatedTask.getTitle() != null) {
                task.setTitle(updatedTask.getTitle());
            }
            if (updatedTask.getDescription() != null) {
                task.setDescription(updatedTask.getDescription());
            }
            if (updatedTask.getStatus() != null) {
                task.setStatus(updatedTask.getStatus());
            }
            if (updatedTask.getPriority() != null) {
                task.setPriority(updatedTask.getPriority());
            }
            if (updatedTask.getDifficulty() != null) {
                task.setDifficulty(updatedTask.getDifficulty());
            }
            if (updatedTask.getAssigneeId() != null) {
                task.setAssigneeId(updatedTask.getAssigneeId());
            }
            if (updatedTask.getAssigneeName() != null) {
                task.setAssigneeName(updatedTask.getAssigneeName());
            }
            
            task.setUpdatedAt(LocalDateTime.now());
            
            // If status changed to Done, set completedAt
            if ("Done".equals(updatedTask.getStatus()) && task.getCompletedAt() == null) {
                task.setCompletedAt(LocalDateTime.now());
            }
            
            Task savedTask = taskRepository.save(task);
            
            // Send Discord notification for status change
            if (updatedTask.getStatus() != null) {
                discordNotificationService.sendTaskStatusUpdateNotification(
                    savedTask.getTitle(),
                    savedTask.getStatus(),
                    savedTask.getAssigneeName()
                );
            }
            
            return savedTask;
        }
        return null;
    }

    // Delete a task
    public boolean deleteTask(String id) {
        try {
            // Get task details before deleting (for notification)
            Optional<Task> taskToDelete = taskRepository.findById(id);
            
            taskRepository.deleteById(id);
            
            // Send Discord notification
            if (taskToDelete.isPresent()) {
                discordNotificationService.sendTaskDeletedNotification(
                    taskToDelete.get().getTitle()
                );
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting task: " + e.getMessage());
            return false;
        }
    }
}