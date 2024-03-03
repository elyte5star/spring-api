package com.elyte.domain;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="REGISTRATION_OTP")
public class Otp extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "OTP_ID")
    private String otpId;

    @Email(message = "invalid email address")
    @Column(name = "EMAIL")
    private String email;


    @Column(name = "OTP")
    @NotBlank(message = "otp is required")
    private String otpString;


    @Column(name = "EXPIRY_DATE")
    private Date expiryDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    
}
