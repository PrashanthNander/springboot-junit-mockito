package com.prash.mongodb.example.service;

import com.prash.mongodb.example.collection.Task;
import com.prash.mongodb.example.enums.TaskSeverity;
import com.prash.mongodb.example.enums.TaskType;
import com.prash.mongodb.example.exception.TaskAlreadyExistsException;
import com.prash.mongodb.example.exception.TaskNotFoundException;
import com.prash.mongodb.example.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskServiceImpl taskService;

    Task task;

    @BeforeEach
    public void init() {
        task = Task.builder()
                .taskId("100")
                .taskType(TaskType.TECHNICAL)
                .assignee("John")
                .description("Tech Case")
                .severity(TaskSeverity.LOW)
                .build();
    }

    @Test
    public void onCreate_returnCreatedTask_ifSuccess() {
        
        Mockito.when(taskRepository.findByTaskId(task.getTaskId())).thenReturn(Optional.empty());
        Mockito.when(taskRepository.save(task)).thenReturn(task);
        
        Task savedTask = taskService.createTask(task);
        
        Assertions.assertThat(savedTask).isNotNull();
    }

    @Test
    public void onCreateTask_throwException_ifTaskExists() {
        
        Mockito.when(taskRepository.findByTaskId(task.getTaskId())).thenThrow(TaskAlreadyExistsException.class);
        
        org.junit.jupiter.api.Assertions.assertThrows(TaskAlreadyExistsException.class, () -> taskService.createTask(task));
        //Verify if the save method was invoked after the exception was thrown
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    public void onFindAll_returnTasks_ifExists() {
        
        Mockito.when(taskRepository.findAll()).thenReturn(taskList());
        
        List<Task> tasks = taskService.findAllTasks();
        
        Assertions.assertThat(tasks).isNotNull();
        Assertions.assertThat(tasks.size()).isEqualTo(2);
    }

    @Test
    public void onFindByTaskId_returnTask_ifExists() {
        
        Mockito.when(taskRepository.findByTaskId(task.getTaskId())).thenReturn(Optional.of(task));
        
        Optional<Task> taskFound = taskService.findTaskById(task.getTaskId());

        Assertions.assertThat(taskFound.isPresent()).isNotNull();
        Assertions.assertThat(taskFound.get().getTaskId()).isEqualTo("100");
    }

    @Test
    public void onUpdate_returnUpdatedTask_ifExists() {
        
        Mockito.when(taskRepository.findByTaskId(task.getTaskId())).thenReturn(Optional.ofNullable(task));
        task.setTaskType(TaskType.NONTECHNICAL);
        task.setDescription("Updating the description");
        task.setAssignee("Trump");
        
        Mockito.when(taskRepository.save(task)).thenReturn(task);
        Task updatedTask = taskService.updateTask(task);
        
        Assertions.assertThat(updatedTask.getAssignee()).isEqualTo("Trump");
        Assertions.assertThat(updatedTask).isNotNull();
    }

    @Test
    public void onUpdate_throwException_ifTaskNotFound() {

        Mockito.when(taskRepository.findByTaskId(task.getTaskId())).thenThrow(TaskNotFoundException.class);

        org.junit.jupiter.api.Assertions.assertThrows(TaskNotFoundException.class,
                                                            () -> taskService.updateTask(task));
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    public void onDelete_returnDeletedTask_ifExists() {

        Mockito.when(taskRepository.findByTaskId(task.getTaskId())).thenReturn(Optional.ofNullable(task));
        Mockito.doNothing().when(taskRepository).deleteById(task.getTaskId());
        Task deletedTask = taskService.deleteTask(task.getTaskId());
        Assertions.assertThat(deletedTask).isNotNull();
    }

    @Test
    public void onDelete_throwException_ifTaskNotFound() {

        Mockito.when(taskRepository.findByTaskId(task.getTaskId())).thenThrow(TaskNotFoundException.class);

        org.junit.jupiter.api.Assertions.assertThrows(TaskNotFoundException.class,
                                                            () -> taskService.deleteTask(task.getTaskId()));

        Mockito.verify(taskRepository, Mockito.never()).deleteById(Mockito.any());

    }

    private List<Task> taskList() {
        List<Task> tasks = new ArrayList<>();
        Task task1 = Task.builder()
                .taskId("200")
                .taskType(TaskType.NONTECHNICAL)
                .assignee("Mike")
                .description("Tech Case")
                .severity(TaskSeverity.HIGH)
                .build();
        tasks.add(task);
        tasks.add(task1);
        return tasks;

    }
}
