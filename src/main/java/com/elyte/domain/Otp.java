package com.elyte.domain;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="OTP_DATA")
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

    
}
