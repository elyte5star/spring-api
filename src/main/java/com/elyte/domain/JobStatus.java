package com.elyte.domain;
import java.io.Serializable;
import com.elyte.domain.enums.JobState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
public class JobStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Enum<JobState> jobState;

    private boolean finished;
    
}
