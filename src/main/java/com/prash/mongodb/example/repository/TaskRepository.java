package com.prash.mongodb.example.repository;

import com.prash.mongodb.example.collection.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {


    public List<Task> findBySeverity(String severity);

    @Query("{severity: ?0, assignee: ?1}")
    public List<Task> findBySeverityAndAssignee(String severity, String assignee);

    Optional<Task> findByTaskId(String taskId);
}
