package com.elyte.domain;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.elyte.domain.enums.JobType;
import com.elyte.domain.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "JOBS")
public class Job implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "JOB_ID")
    private String jid;

    @Column(name = "CREATED", updatable = false)
    private String created;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "job")
    @OrderBy
    private List<Task> tasks;

    @Column(name = "JOB_REQUEST", columnDefinition = "json")
    private String jobRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "JOB_TYPE")
    private JobType jobType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "state", column = @Column(name = "STATE")),
            @AttributeOverride(name = "finished", column = @Column(name = "FINISHED")),
            @AttributeOverride(name = "successful", column = @Column(name = "SUCCESSFUL"))

    })
    private Status jobStatus;

    @Column(name = "NUMBER_OF_TASKS")
    private int numberOfTasks;

    @Override
    public String toString() {
        return "Job{" +
                "jid='" + jid + '\'' +
                ", created='" + created + '\'' +
                ", user=" + user +
                ", tasks=" + tasks +
                ", jobRequest='" + jobRequest + '\'' +
                ", jobType=" + jobType +
                ", jobStatus=" + jobStatus +
                ", numberOfTasks=" + numberOfTasks +
                '}';
    }
}
