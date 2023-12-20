package com.elyte.domain;
import java.io.Serializable;

import com.elyte.domain.enums.WorkerType;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="WORKERS")
public class Worker implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "WORKER_ID")
    private String wid;

    @Column(name = "CREATED",updatable = false)
    private String created;

    @Column(name = "WORKER_TYPE")
    private Enum<WorkerType> workerType;


    @Column(name = "QUEUE_NAME")
    private String queueName;
    
}
