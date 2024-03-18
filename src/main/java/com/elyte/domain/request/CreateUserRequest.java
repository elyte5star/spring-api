package com.elyte.domain.request;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

import com.elyte.validators.ValidEmail;
import com.elyte.validators.ValidPassword;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



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

    @Email(message = "invalid email address")
    @ValidEmail
    private String email;

    @Digits(fraction = 0, integer = 10)
    private String telephone;


}
