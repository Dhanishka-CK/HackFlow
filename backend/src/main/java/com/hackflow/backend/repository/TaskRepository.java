package com.hackflow.backend.repository;

import com.hackflow.backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find tasks by status
    List<Task> findByStatus(String status);
    
    // Find tasks by assignee name
    List<Task> findByAssigneeName(String assigneeName);
    
    // Find tasks by priority
    List<Task> findByPriority(String priority);
    
    // Find tasks by status and priority
    List<Task> findByStatusAndPriority(String status, String priority);
    
    // Search tasks by keyword (title or description)
    @Query("SELECT t FROM Task t WHERE t.title LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<Task> searchByKeyword(@Param("keyword") String keyword);
    
    // Count tasks by status
    long countByStatus(String status);
    
    // Check if task exists by title
    boolean existsByTitle(String title);
}