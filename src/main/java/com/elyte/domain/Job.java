package com.elyte.domain;
import java.io.Serializable;
import java.util.List;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.elyte.domain.enums.JobType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="JOBS")
public class Job implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "JOB_ID")
    private String jid;

    @Column(name = "CREATED",updatable = false)
    private String created;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER,mappedBy = "job")
    @OrderBy
    private List<Task> tasks;

    @Column(name = "JOB_REQUEST", columnDefinition = "json")
    private String jobRequest;


    @Column(name = "JOB_TYPE")
    private Enum<JobType> jobType;


    @Column(name = "JOB_STATUS")
    private JobStatus jobStatus;

    @Column(name = "NUMBER_OF_TASKS")
    private int numberOfTasks;

    
}
