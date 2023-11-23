package com.elyte.domain;
import java.util.List;

import com.elyte.domain.enums.JobStatus;
import com.elyte.domain.enums.JobType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Setter
@Getter
@Entity
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Table(name="JOBS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Job extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "JOB_ID")
    private String job_id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "USER_ID")
    private User owner;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TASK_ID")
    @OrderBy
    private List<Task> tasks;


    @Column(name = "NUMBER_OF_TASKS")
    private int numberOfTasks;


    @Column(name = "JOB_REQUEST", columnDefinition = "json")
    private String jobRequest;


    @Column(name = "JOB_TYPE")
    private Enum<JobType> jobType;


    @Column(name = "JOB_STATUS")
    private Enum<JobStatus> jobStatus;



    
}
