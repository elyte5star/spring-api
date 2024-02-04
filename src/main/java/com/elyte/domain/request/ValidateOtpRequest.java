package com.elyte.domain.request;

import java.io.Serial;
import java.io.Serializable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor()
@NoArgsConstructor
@Data
public class ValidateOtpRequest  implements Serializable{

    @Serial
    private static final long serialVersionUID = 1234567L;

    @Email(message = "invalid email address")
    private String email;

    @NotBlank(message = "otp is required")
    private String otpString;

    
}
