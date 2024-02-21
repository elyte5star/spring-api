package com.elyte.domain;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Entity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="USER_ADDRESS")
@Entity
public class UserAddress extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ADDRESS_ID")
    private String addressId;


    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;


    @Column(name = "STREET_ADDRESS", length = 3000)
    @NotBlank(message = "Street description is required")
    private String description;

    @Column(name = "CITY")
    @NotBlank(message = "City is required")
    private String city;
    

    @Column(name = "STATE")
    @NotBlank(message = "State is required")
    private String state;

    @Column(name = "ZIP")
    @NotBlank(message = "Zip code is required")
    private String zip;
}
