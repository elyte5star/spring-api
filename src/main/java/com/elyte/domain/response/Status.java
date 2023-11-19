package com.elyte.domain.response;



import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class Status {
    
    private int code;
    private String message;
    private boolean success;
    private String path;
    private String time;
    
}
