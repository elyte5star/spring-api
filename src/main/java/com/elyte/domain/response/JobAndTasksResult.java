package com.elyte.domain.response;
import java.util.List;
import com.elyte.domain.Job;
import com.elyte.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobAndTasksResult {

    private Job job;

    private List<Task> tasks;

    private String lastTaskEndedAt;

    
}
