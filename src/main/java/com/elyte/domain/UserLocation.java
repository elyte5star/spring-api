package com.elyte.domain;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="USER_LOCATION")
public class UserLocation extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "LOCATION_ID")
    private String locationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    private User user;

    @Column(name = "COUNTRY")
    @NotBlank(message = "Token is required")
    private String country;


    @Column(name = "ENABLED", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean enabled;


    
}
