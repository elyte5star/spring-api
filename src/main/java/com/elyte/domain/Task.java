package com.elyte.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.elyte.domain.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TASKS")
public class Task implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TASK_ID")
    private String tid;

    @Column(name = "CREATED", updatable = false)
    private String created;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "JOB_ID")
    @JsonIgnore
    private Job job;

    @Column(name = "STARTED_AT", updatable = false)
    private String startedAt;

    @Column(name = "ENDED_AT", updatable = false)
    private String endedAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "state", column = @Column(name = "STATE")),
            @AttributeOverride(name = "finished", column = @Column(name = "FINISHED")),
            @AttributeOverride(name = "successful", column = @Column(name = "SUCCESSFUL"))

    })
    private Status taskStatus;


    @Column(name = "RESULT", columnDefinition = "json")
    private String result;

}
