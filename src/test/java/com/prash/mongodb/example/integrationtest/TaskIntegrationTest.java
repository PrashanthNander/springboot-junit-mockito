package com.prash.mongodb.example.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prash.mongodb.example.collection.Task;
import com.prash.mongodb.example.container.BaseContainer;
import com.prash.mongodb.example.enums.TaskSeverity;
import com.prash.mongodb.example.enums.TaskType;
import com.prash.mongodb.example.repository.TaskRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskIntegrationTest extends BaseContainer {

    Task task;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

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
     * Integration test case for create task
     *
     * @throws Exception -
     */
    @Test
    public void whenValidInput_thenReturnsCreatedTask() throws Exception {
        Task taskToBeCreated = Task.builder()
                .taskId("200")
                .taskType(TaskType.TECHNICAL)
                .assignee("Mike")
                .description("Tech Case")
                .severity(TaskSeverity.LOW)
                .build();
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskToBeCreated)));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId", CoreMatchers.is("200")));

    }

    /**
     * Integration test case for create task
     *
     * @throws Exception - If the task already exists
     */
    @Test
    public void whenInValidInput_thenReturnsCreatedTask() throws Exception {

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    /**
     * Integration test case for find tasks
     *
     * @throws Exception -
     */
    @Test
    public void whenValidTaskId_thenReturnsTask() throws Exception {
        insertSingleTask();
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/task/{taskId}", task.getTaskId())
                .contentType(MediaType.APPLICATION_JSON));


        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId", CoreMatchers.is("100")));
    }

    /**
     * Integration test case for find task
     *
     * @throws Exception -
     */
    @Test
    public void whenInvalidTaskId_thenReturnsEmpty() throws Exception {

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/task/{taskId}", "")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Integration test case for find all task
     *
     * @throws Exception -
     */
    @Test
    public void whenValidRequest_thenReturnsAllTasks() throws Exception {
        insertTasks();
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(2)));

    }

    /**
     * Integration test case for update task
     *
     * @throws Exception -
     */
    @Test
    public void whenValidInput_thenReturnsUpdatedTask() throws Exception {
        insertSingleTask();
        task.setAssignee("Mike");
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignee", CoreMatchers.is("Mike")));

    }

    /**
     * Integration test case for delete task
     *
     * @throws Exception -
     */
    @Test
    public void whenValidInput_thenReturnsDeletedTask() throws Exception {
        insertSingleTask();
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/task/{taskId}", task.getTaskId()));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignee", CoreMatchers.is("John")));

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
