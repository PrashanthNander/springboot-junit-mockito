package com.prash.mongodb.example.service;

import com.prash.mongodb.example.collection.Task;
import com.prash.mongodb.example.exception.TaskAlreadyExistsException;
import com.prash.mongodb.example.exception.TaskNotFoundException;
import com.prash.mongodb.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    /**
     * Method to create a new task
     * Before insert, validates if the task already exists
     *
     * @param task - input
     * @return task object as output
     */
    @Override
    public Task createTask(Task task) {

        Optional<Task> optionalTask = taskRepository.findByTaskId(task.getTaskId());
        if (optionalTask.isPresent()) {
            throw new TaskAlreadyExistsException(String.format("Task [%s] already Exists.", task.getTaskId()));
        }
        //task.setTaskId(UUID.randomUUID().toString().split("-")[0]);
        task = taskRepository.save(task);
        return task;
    }


    /**
     * Method to update the existing task
     * Before update, validates if the task exists
     *
     * @param task - input
     * @return task object as the out
     */
    @Override
    public Task updateTask(Task task) {
        final String taskId = task.getTaskId();
        Task existingTask = taskRepository.findByTaskId(taskId).orElseThrow(() -> new TaskNotFoundException(String.format("Task [%s] not found.", taskId)));
        existingTask.setTaskType(task.getTaskType());
        existingTask.setTaskId(task.getTaskId());
        existingTask.setAssignee(task.getAssignee());
        existingTask.setSeverity(task.getSeverity());
        existingTask.setDescription(task.getDescription());
        task = taskRepository.save(task);
        return task;
    }

    /**
     * Method to delete the existing Task
     * Before update, validates if the task exists
     *
     * @param taskId - input
     * @return task object as the out
     */
    @Override
    public Task deleteTask(String taskId) {
        Task task = taskRepository.findByTaskId(taskId).orElseThrow(() -> new TaskNotFoundException(String.format("Task [%s] not found.", taskId)));
        taskRepository.deleteById(taskId);
        return task;
    }


    /**
     * Method to fetch all the tasks from database
     *
     * @return List of tasks
     */
    @Override
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }


    /**
     * Method to fetch task based on taskId
     *
     * @param taskId - input
     * @return task object
     */
    @Override
    public Optional<Task> findTaskById(String taskId) {
        return taskRepository.findByTaskId(taskId);
    }

}
