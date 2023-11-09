package com.elyte.domain;



import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class Status {
    private String replyCode;
    private String error;
    private String reason;
    
}
