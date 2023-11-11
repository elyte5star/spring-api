package com.elyte.domain;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;



@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class CreateUserRequest implements Serializable{

    private static final long serialVersionUID = 1234567L;

    private String username;
    private String password;
    private String email;
    private String telephone;
    
}
