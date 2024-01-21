package com.elyte.domain;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
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

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "DEVICE_METADATA")
public class DeviceInfo{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "DEVICE_ID")
    private String deviceId;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @Column(name = "LAST_LOGIN_DATE")
    private String lastLoginDate;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "DEVICE_DETAILS")
    private String deviceDetails;
    
    
}
