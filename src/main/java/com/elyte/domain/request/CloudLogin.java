package com.elyte.domain.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class CloudLogin {

    @NotBlank(message = "token is required")
    private String token;

    @NotBlank(message = "type of cloud provider is required")
    private String type;
    
}
