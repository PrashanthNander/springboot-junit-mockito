package com.prash.mongodb.example.collection;

import com.prash.mongodb.example.enums.TaskSeverity;
import com.prash.mongodb.example.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    @Id
    private String taskId;
    private String description;
    private TaskType taskType;
    private TaskSeverity severity;
    private String assignee;

}
