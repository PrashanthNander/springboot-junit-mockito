package com.prash.mongodb.example.controller;

import com.prash.mongodb.example.collection.Task;
import com.prash.mongodb.example.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable String taskId) {
        Optional<Task> task = taskService.findTaskById(taskId);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/task")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        task = taskService.createTask(task);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("/task")
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        task = taskService.updateTask(task);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<Task> deleteTask(@PathVariable String taskId) {
        Task task = taskService.deleteTask(taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.findAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

}
