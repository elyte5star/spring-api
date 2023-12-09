package com.elyte.domain;
import java.util.List;
import com.elyte.domain.enums.JobType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Setter
@Getter
@Entity
@AllArgsConstructor()
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
    @JoinColumn(name = "tasks")
    private List<Task> tasks;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;


    @Column(name = "JOB_REQUEST", columnDefinition = "json")
    private String jobRequest;


    @Column(name = "JOB_TYPE")
    private Enum<JobType> jobType;


    @Column(name = "JOB_STATUS", columnDefinition = "json")
    private JobStatus jobStatus;



    
}
