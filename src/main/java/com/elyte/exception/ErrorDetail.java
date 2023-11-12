package com.elyte.exception;

import lombok.Data;
import lombok.AllArgsConstructor;
import com.elyte.domain.response.Status;
import java.io.Serializable;


@AllArgsConstructor(staticName = "build")
@Data
public class ErrorDetail implements Serializable{

    private static final long serialVersionUID = -6191879091924046844L;

    private Status status;
    private String title;
    private String developerMessage;

    public ErrorDetail(String title, String developerMessage) {
        this.title = title;
        this.developerMessage = developerMessage;
    }
    
}
