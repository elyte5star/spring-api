package com.elyte.domain.response;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class TokenResponse {
    private String access_token;
    private String token_type;
    private String username;
    private boolean active;
   
}
