package com.elyte.exception;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;



@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class ErrorDetail {
    private String title;
    private int status;
    private String detail;
    private long timeStamp;
    private String developerMessage;
    private boolean success;

}
