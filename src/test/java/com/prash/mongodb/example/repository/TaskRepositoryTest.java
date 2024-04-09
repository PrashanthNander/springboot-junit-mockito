package com.prash.mongodb.example.repository;

import com.prash.mongodb.example.collection.Task;
import com.prash.mongodb.example.container.BaseContainer;
import com.prash.mongodb.example.enums.TaskSeverity;
import com.prash.mongodb.example.enums.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataMongoTest
public class TaskRepositoryTest extends BaseContainer {

    private Task task;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("onTaskCreate_returnCreatedTask_ifSuccess")
    public void onCreate_returnCreatedTask_ifSuccess() {
        Task savedTask = insertSingleTask();
        Assertions.assertNotNull(savedTask);
        Assertions.assertEquals(savedTask.getTaskId(), "200");
    }

    @Test
    public void onFindAll_returnTasks_ifExists() {
        insertTasks();
        List<Task> tasks = taskRepository.findAll();
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(tasks.size(), 2);
    }

    @Test
    public void onFindByTaskId_returnTask_ifExists() {
        insertSingleTask();
        Optional<Task> optionalTask = taskRepository.findByTaskId("200");
        optionalTask.ifPresent(value -> Assertions.assertEquals(value.getTaskId(), "200"));
        optionalTask.ifPresent(value -> Assertions.assertEquals(value.getAssignee(), "Fajig"));
    }

    @Test
    public void onUpdate_returnUpdatedTask_ifSuccessful() {
        Task taskToBeUpdated = insertSingleTask();
        taskToBeUpdated.setAssignee("Tamara");
        taskToBeUpdated.setTaskId("900");
        Task updatedTask = taskRepository.save(task);
        Assertions.assertNotNull(updatedTask);
        Assertions.assertEquals(updatedTask.getTaskId(), "900");
        Assertions.assertEquals(updatedTask.getAssignee(), "Tamara");
    }

    @Test
    public void onDelete_returnEmpty_ifSuccessful() {
        Task task = insertSingleTask();
        Optional<Task> taskIdBefore = taskRepository.findByTaskId(task.getTaskId());
        Assertions.assertTrue(taskIdBefore.isPresent());
        taskRepository.delete(task);
        Optional<Task> taskId = taskRepository.findByTaskId(task.getTaskId());
        Assertions.assertTrue(taskId.isEmpty());
    }

    @BeforeEach
    public void init() {
        task = Task.builder().taskId("200").taskType(TaskType.NONTECHNICAL).assignee("Fajig").severity(TaskSeverity.HIGH)
                .description("Non Tech Task").build();
    }

    public void insertTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(Task.builder().taskId("300").taskType(TaskType.NONTECHNICAL).assignee("Prash").severity(TaskSeverity.HIGH)
                .description("Germany consignment").build());
        tasks.add(Task.builder().taskId("400").taskType(TaskType.TECHNICAL).assignee("Spark").severity(TaskSeverity.HIGH)
                .description("Netherlands consignment").build());
        taskRepository.saveAll(tasks);
    }

    public Task insertSingleTask() {
        return taskRepository.save(task);
    }
}
