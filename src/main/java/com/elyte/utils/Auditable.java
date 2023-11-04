package com.elyte.utils;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.sql.Timestamp;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

   
    @CreatedBy
    @Column(name = "CREATED_BY", columnDefinition = "bigint default 1", updatable = false)
    protected Long createdBy;

    @CreatedDate
    @Column(name = "CREATED_AT", columnDefinition = "timestamp default '2020-04-10 20:47:05.967394'", updatable = false)
    protected Timestamp createdAt;

    @LastModifiedBy
    @Column(name = "MODIFIED_BY", columnDefinition = "bigint default 1")
    protected Long lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_AT", columnDefinition = "timestamp default '2020-04-10 20:47:05.967394'")
    protected Timestamp lastModifiedAt;

    
    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Timestamp getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Timestamp lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }
}