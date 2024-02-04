package com.elyte.domain.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor()
@NoArgsConstructor
@Data
public class CustomResponseStatus implements Serializable{

    @Serial
    private static final long serialVersionUID = -101879091924046844L;
    
    private int code;
    private String message;
    private boolean success=false;
    private String path;
    private String timeStamp;
    private Object result = null;
    
}
