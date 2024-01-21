package com.elyte.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity {

    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_AT", columnDefinition = "timestamp default '1970-04-10 20:47:05.967394'", updatable = false)
    private LocalDateTime created_at;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY", length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_AT", columnDefinition = "timestamp default '1970-04-10 20:47:05.967394'")
    private LocalDateTime lastModifiedAt;

}