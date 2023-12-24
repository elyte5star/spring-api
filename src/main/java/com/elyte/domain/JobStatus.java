package com.elyte.domain;
import java.io.Serializable;
import com.elyte.domain.enums.JobState;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class JobStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private JobState jobState;

    private boolean done=false;
    
}
