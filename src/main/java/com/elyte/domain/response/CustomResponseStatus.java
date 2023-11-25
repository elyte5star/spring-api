package com.elyte.domain.response;

import lombok.Data;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class CustomResponseStatus implements Serializable{

    private static final long serialVersionUID = -101879091924046844L;
    
    private int code;
    private String message;
    private boolean success;
    private String path;
    private String timeStamp;
    private Object result = null;
    
}
