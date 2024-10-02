package com.elyte.domain.response;
import org.springframework.http.ResponseCookie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor()
@NoArgsConstructor
@Data
public class JwtResponse {

    private ResponseCookie jwtCookie;

    private String jwtToken;
    
}
