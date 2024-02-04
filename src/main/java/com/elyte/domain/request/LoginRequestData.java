package com.elyte.domain.request;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestData implements Serializable{

    @Serial
    private static final long serialVersionUID = 1234567L;

    @NotBlank(message = "username is required")
    private String username;

    
    @NotBlank(message = "password is required")
    private String password;
    
}
