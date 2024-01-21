package com.elyte.domain.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordUpdate {

    private String oldPassword;

    private String newPassword;
    
}
