package com.elyte.domain.response;
import com.elyte.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {
    private String userId;
    private String startTime;
    private String endTime;
    private long totalTime;
    private String jobId;
    private Status jobStatus;

    public JobResponse(String userId, String startTime, String jobId, Status jobStatus) {
        this.userId = userId;
        this.startTime = startTime;
        this.jobId = jobId;
        this.jobStatus = jobStatus;
    }
}
