package com.elyte.queue;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import com.elyte.domain.Job;
import com.elyte.domain.Task;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueueItem implements Serializable{

    private static final long serialVersionUID = 1L;

    private Job job;

    private Task task;
    
}
