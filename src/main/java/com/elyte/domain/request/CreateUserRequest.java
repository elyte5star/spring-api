package com.elyte.domain.request;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

import com.elyte.validators.ValidEmail;
import com.elyte.validators.ValidPassword;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateUserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567L;

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    @ValidPassword
    private String password;
   
    @ValidEmail
    @NotNull
    @Size(min = 1, message = "invalid email address")
    private String email;

    @Digits(fraction = 0, integer = 15)
    private String telephone;


}
