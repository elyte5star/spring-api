package com.elyte.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="TASKS")
public class Task implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TASK_ID")
    private String tid;

    @Column(name = "CREATED",updatable = false)
    private String created;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "JOB_ID")
    @JsonIgnore
    private Job job;

    @Column(name = "RESULT", columnDefinition = "json")
    private String result;

    @Column(name = "STARTED",updatable = false)
    private String started;

    @Column(name = "FINISHED",updatable = false)
    private String finished;

    @Column(name = "STATUS")
    private JobStatus status;

    
}
