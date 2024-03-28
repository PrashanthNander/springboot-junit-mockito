package com.prash.mongodb.example.service;

import com.prash.mongodb.example.collection.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    Task createTask(Task task);

    Task updateTask(Task task);

    Task deleteTask(String taskId);

    List<Task> findAllTasks();

    Optional<Task> findTaskById(String taskId);

    List<Task> findTasksBySeverity(String severity);

    List<Task> findTasksBySeverityAndAssignee(String severity, String assignee);


}
