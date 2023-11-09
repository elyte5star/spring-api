package com.elyte.exception;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.elyte.utils.ApplicationConsts;

import jakarta.servlet.http.HttpServletRequest;



@RestControllerAdvice
public class ResourceNotFoundExceptionHandler{

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetail> handleUserNotFoundException(ResourceNotFoundException rnfe,HttpServletRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        LocalDateTime current = LocalDateTime.now();
        errorDetail.setTime_stamp(current.format(ApplicationConsts.dtf));
        errorDetail.setStatus(HttpStatus.NOT_FOUND.value());
        errorDetail.setTitle("Resource Not Found");
        errorDetail.setDetail(rnfe.getMessage());
        errorDetail.setDeveloperMessage(rnfe.getClass().getName());
        errorDetail.setSuccess(false);
        return new ResponseEntity<>(errorDetail, null, HttpStatus.NOT_FOUND);
      
    }
    
}
