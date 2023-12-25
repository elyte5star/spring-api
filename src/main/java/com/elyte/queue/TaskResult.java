package com.elyte.queue;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.elyte.domain.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="TASK_RESULTS")
public class TaskResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "RESULT_ID")
    private String resid;

    @Column(name = "CREATED",updatable = false)
    private String resultDate;

    @OneToOne(targetEntity = Task.class, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false, name = "task_id")
    private Task task;

    @Column(name = "DATA", columnDefinition = "json")
    private String data;

}
