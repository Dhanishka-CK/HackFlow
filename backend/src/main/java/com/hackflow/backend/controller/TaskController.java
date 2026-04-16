package com.hackflow.backend.controller;

import com.hackflow.backend.model.Task;
import com.hackflow.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    // GET /api/tasks - Get all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }
    
    // GET /api/tasks/{id} - Get task by ID (⚠️ Long instead of String)
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/tasks - Create new task
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }
    
    // PUT /api/tasks/{id} - Update task (⚠️ Long instead of String)
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody Task updatedTask) {
        Task result = taskService.updateTask(id, updatedTask);
        return result != null
            ? ResponseEntity.ok(result)
            : ResponseEntity.notFound().build();
    }
    
    // DELETE /api/tasks/{id} - Delete task (⚠️ Long instead of String)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }
    
    // NEW: GET /api/tasks/search?keyword=... - Search tasks
    @GetMapping("/search")
    public List<Task> searchTasks(@RequestParam String keyword) {
        return taskService.searchTasks(keyword);
    }
    
    // NEW: GET /api/tasks/count/by-status?status=... - Count tasks
    @GetMapping("/count/by-status")
    public long countByStatus(@RequestParam String status) {
        return taskService.countByStatus(status);
    }
}