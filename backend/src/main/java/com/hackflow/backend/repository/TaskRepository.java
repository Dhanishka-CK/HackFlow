package com.hackflow.backend.repository;

import com.hackflow.backend.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    // Find all tasks by status
    List<Task> findByStatus(String status);

    // Find all tasks by assignee ID
    List<Task> findByAssigneeId(String assigneeId);

    // Find all tasks by priority
    List<Task> findByPriority(String priority);

    // Find tasks by status and assignee
    List<Task> findByStatusAndAssigneeId(String status, String assigneeId);
}