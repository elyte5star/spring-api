package com.elyte.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity {
   
    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false,nullable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable=false,columnDefinition = "timestamp default '1970-04-10 20:47:05.967394'", updatable = false)
    private Timestamp created_at;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY",length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_AT", columnDefinition = "timestamp default '1970-04-10 20:47:05.967394'")
    private Timestamp lastModifiedAt;

 
}