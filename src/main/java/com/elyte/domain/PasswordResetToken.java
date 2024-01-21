package com.elyte.domain;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="PASSWORD_RESET")
@Entity
public class PasswordResetToken extends AuditEntity{

    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TOKEN_ID")
    private String tokenid;


    @Column(name = "TOKEN")
    @NotBlank(message = "Token is required")
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "EXPIRY_DATE")
    private Date expiryDate;

    
}
