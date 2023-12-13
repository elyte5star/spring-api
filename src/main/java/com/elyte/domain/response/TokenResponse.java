package com.elyte.domain.response;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@AllArgsConstructor()
@NoArgsConstructor
@Data
public class TokenResponse implements Serializable{

    private static final long serialVersionUID = -8191879091924046844L;
    
    private String access_token;
    private String token_type;
    private String username;
    private String email;
    private boolean enabled;
    private boolean admin;
    private String userid;
   
}
