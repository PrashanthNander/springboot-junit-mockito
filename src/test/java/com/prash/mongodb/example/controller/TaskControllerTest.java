package com.prash.mongodb.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prash.mongodb.example.collection.Task;
import com.prash.mongodb.example.enums.TaskSeverity;
import com.prash.mongodb.example.enums.TaskType;
import com.prash.mongodb.example.repository.TaskRepository;
import com.prash.mongodb.example.service.TaskServiceImpl;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = TaskController.class)
public class TaskControllerTest {

    Task task;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskServiceImpl taskService;
    @MockBean
    private TaskRepository taskRepository;
    @Autowired
    private ObjectMapper objectMapper;

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

    /**
     *  Junit test case for create task REST API
     * @throws Exception -
     */
    @Test
    public void whenValidInput_thenReturnsCreatedTask() throws Exception {
        Mockito.when(taskService.createTask(task)).thenReturn(task);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId", CoreMatchers.is("100")));

        Mockito.verify(taskService, Mockito.times(1)).createTask(Mockito.any(Task.class));
        Mockito.verify(taskRepository, Mockito.times(0)).findByTaskId(Mockito.any());

    }

    /**
     *  Junit test case for find tasks REST API
     * @throws Exception -
     */
    @Test
    public void whenValidTaskId_thenReturnsTask() throws Exception {
        Mockito.when(taskService.findTaskById(task.getTaskId())).thenReturn(Optional.of(task));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/task/{taskId}", task.getTaskId())
                .contentType(MediaType.APPLICATION_JSON));


        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId", CoreMatchers.is("100")));
    }

    /**
     *  Junit test case for find task REST API
     * @throws Exception -
     */
    @Test
    public void whenInvalidTaskId_thenReturnsEmpty() throws Exception {
        Mockito.when(taskService.findTaskById(task.getTaskId())).thenReturn(Optional.empty());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/task/{taskId}", task.getTaskId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Junit test case for find all task REST API
     * @throws Exception
     */
    @Test
    public void whenValidRequest_thenReturnsAllTasks() throws Exception {
        Mockito.when(taskService.findAllTasks()).thenReturn(taskList());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(2)));

    }

    /**
     * Junit test case for update task REST API
     * @throws Exception -
     */
    @Test
    public void whenValidInput_thenReturnsUpdatedTask() throws Exception {
        task.setAssignee("Mike");
        Mockito.when(taskService.updateTask(task)).thenReturn(task);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignee", CoreMatchers.is("Mike")));

    }

    /**
     *  Junit test case for delete task REST API
     * @throws Exception -
     */
    @Test
    public void whenValidInput_thenReturnsDeletedTask() throws Exception {

        Mockito.when(taskService.deleteTask(task.getTaskId())).thenReturn(task);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/task/{taskId}", task.getTaskId()));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignee", CoreMatchers.is("John")));

    }

    /**
     * Junit test case for delete task REST API
     * @throws Exception -
     */
    @Test
    public void whenInValidInput_thenReturnsEmptyTask() throws Exception {

        Mockito.when(taskService.deleteTask(task.getTaskId())).thenReturn(task);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/task/{taskId}", "200"))
                .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString()).isEmpty();

    }

    private List<Task> taskList() {
        List<Task> tasks = new ArrayList<>();
        Task task1 = Task.builder().taskId("200").taskType(TaskType.NONTECHNICAL).assignee("Mike").description("Tech Case").severity(TaskSeverity.HIGH).build();
        tasks.add(task);
        tasks.add(task1);
        return tasks;

    }
}
