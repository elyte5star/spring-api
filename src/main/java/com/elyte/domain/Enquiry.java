package com.elyte.domain;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Enquiry extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ENQUIRY_ID")
    private String enquiryId;

    @Column(name = "CLIENT_NAME")
    @NotBlank(message = "client name is required")
    private String clientName;

    @Column(name = "EMAIL")
    @Email(message = "invalid email address")
    private String clientEmail;

    @Column(name = "COUNTRY")
    @NotBlank(message = "country name is required")
    private String  country;


    @Column(name = "SUBJECT")
    @NotBlank(message = "subject is required")
    private String subject;

    @Column(name = "MESSAGE", length = 5000)
    @NotBlank(message = "message is required")
    private String message;
    
}
