package com.elyte.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import com.elyte.utils.Auditable;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class User extends Auditable{

   
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Long userid;


    @Column(name = "USERNAME")
    @NotBlank(message = "username is required")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
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
