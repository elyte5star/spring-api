package com.elyte.domain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name="TASKS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TASK_ID")
    private String task_id;


    @ManyToOne(targetEntity =Job.class,fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false,name = "job_id")
    @JsonIgnore
    private Job job;

    @Column(name = "RESULT", columnDefinition = "json")
    private String result;

    @Column(name = "STARTED")
    private String started;

    @Column(name = "FINISHED")
    private String finished;

    @Column(name = "JOB_STATUS", columnDefinition = "json")
    private JobStatus jobStatus;

    
}
