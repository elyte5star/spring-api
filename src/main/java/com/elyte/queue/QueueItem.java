package com.elyte.queue;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import com.elyte.domain.Job;
import com.elyte.domain.Task;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueueItem implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("Job")
    private Job job;

    @JsonProperty("Task")
    private Task task;
    
}
