package com.elyte.domain.response;
import java.util.UUID;

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
    private UUID userid;
    private boolean admin;
    
}
