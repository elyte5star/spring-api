package com.elyte.domain.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class CreateEnquiryRequest implements Serializable{

    @Serial
    private static final long serialVersionUID = 1234567L;
  
    @NotBlank(message = "client name is required")
    private String clientName;

  
    @Email(message = "invalid email address")
    private String clientEmail;

 
    @NotBlank(message = "country name is required")
    private String  country;


   
    @NotBlank(message = "subject is required")
    private String subject;

    
    @NotBlank(message = "message is required")
    private String message;
    
    
}
