package com.elyte.domain.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordChange {
    private String password;
    private String resetToken;
    
}
