package com.elyte.domain;
import java.io.Serializable;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor(staticName = "build")
public class LoginResponseData  implements Serializable{

    private static final long serialVersionUID = -8191879091924046844L;

    private Status status;

    private final String jwttoken;

    private String username;

    public LoginResponseData(String name, String jwttoken) {
        this.username = name;
        this.jwttoken = jwttoken;
    }
    
}
