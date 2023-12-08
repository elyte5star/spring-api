package com.elyte.domain;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
@Table(name="USERS")
public class User extends AuditEntity{

   
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_ID")
    private String userid;

    @Column(name = "LAST_LOGIN_DATE" )
    private String lastLoginDate;

    @Column(name = "USERNAME",unique = true)
    @NotBlank(message = "username is required")
    @Size(min = 5)
    private String username;

    
    @Column(name = "PASSWORD")
    @NotBlank(message = "password is required")
    @JsonIgnore
    private String password;

    @Column(name = "EMAIL", unique = true)
    @Email(message = "invalid email address")
    private String email;

    
    @Column(name = "ACTIVE", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean active;

    
    @Column(name = "ADMIN", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean admin;

    @Column(name = "ENABLED", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean enabled;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "BOOKING_IDS")
    private List<Booking> bookings;

    
    @Column(name = "TELEPHONE",unique = true)
    @Digits(fraction = 0, integer = 10)
    @Size(min = 7)
    private String telephone;

    @Column(name = "DISCOUNT")
    @Digits(integer = 10,fraction = 2)
    private String discount;

    
}
