package com.elyte.domain;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;



@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class LoginRequestData implements Serializable{

    private static final long serialVersionUID = 5226468583005150707L;
    
    private String username;
    private String password;
    
}
