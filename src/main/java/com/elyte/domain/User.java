package com.elyte.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.elyte.utils.Auditable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class User extends Auditable{

   
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_ID")
    private UUID userid;


    @Column(name = "USERNAME")
    @NotBlank(message = "username is required")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    @Email(message = "invalid email address")
    private String email;

    @Column(name = "ACTIVE", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean active;

    @Column(name = "ADMIN", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean admin;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "BOOKING_ID")
    @OrderBy
    private List<Booking> bookings;

    @Column(name = "TELEPHONE")
    @Digits(fraction = 0, integer = 10)
    private String telephone;

    
}
