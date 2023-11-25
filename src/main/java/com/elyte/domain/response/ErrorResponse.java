package com.elyte.domain.response;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.io.Serializable;


@AllArgsConstructor(staticName = "build")
@Data
public class ErrorResponse implements Serializable{

    private static final long serialVersionUID = -6191879091924046844L;

    private CustomResponseStatus status;
    private String title;
    

    public ErrorResponse(String title) {
        this.title = title;
        
    }
    
}
