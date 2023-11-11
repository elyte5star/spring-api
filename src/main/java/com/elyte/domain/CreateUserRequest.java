package com.elyte.domain;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class CreateUserRequest implements Serializable{

    private static final long serialVersionUID = 1234567L;

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    @JsonIgnore
    private String password;


    @Email(message = "invalid email address")
    private String email;

    @Digits(fraction = 0, integer = 10)
    private String telephone;
    
}
