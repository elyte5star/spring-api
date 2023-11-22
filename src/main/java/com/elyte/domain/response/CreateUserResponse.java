package com.elyte.domain.response;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class CreateUserResponse implements Serializable{
    private static final long serialVersionUID = -8191879091924046844L;

    private Status status;
    
    private String userid;

    
}
