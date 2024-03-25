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
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
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
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "USERS")
public class User extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_ID")
    private String userid;

    @Column(name = "USERNAME", unique = true)
    @NotBlank(message = "username is required")
    @Size(min = 5)
    private String username;

    @Column(name = "PASSWORD",length=60)
    @NotBlank(message = "password is required")
    @JsonIgnore
    private String password;

    @Column(name = "EMAIL", unique = true)
    @Email(message = "invalid email address")
    @Size(max = 50)
    private String email;

    @Column(name = "ACCOUNT_NOT_LOCKED")
    private boolean accountNonLocked = true;

    @Column(name = "FAILED_ATTEMPT", columnDefinition = "integer default 0")
    private int failedAttempt;

    @Column(name = "LOCK_TIME")
    private Date lockTime = null;

    @Column(name = "ADMIN", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean admin;

    @Column(name = "ENABLED", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean enabled;

    @Column(name = "TWO_FACTOR", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isUsing2FA;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    @OrderBy
    private List<Booking> bookings;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    @OrderBy
    @JsonIgnore
    private List<UserLocation> locations;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    @OrderBy
    private UserAddress address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "otp_id")
    @OrderBy
    @JsonIgnore
    private Otp otp;

    @Column(name = "TELEPHONE", unique = true)
    @Digits(fraction = 0, integer = 14)
    @Size(min = 7)
    private String telephone;

    @Column(name = "DISCOUNT")
    @Digits(integer = 10, fraction = 2)
    private String userDiscount;

}
