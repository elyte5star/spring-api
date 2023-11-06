package com.elyte.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Date;

@RestControllerAdvice
public class MethodArgumentNotValidExceptionHandler {
    

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,Object> handleInvalidArgument(MethodArgumentNotValidException exception){
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetail.setTitle("Validation Failed");
        errorDetail.setDetail(exception.getMessage());
        errorDetail.setDeveloperMessage(exception.getClass().getName());
        errorDetail.setSuccess(false);
        Map<String, Object> map = objectMapper.convertValue(errorDetail, new TypeReference<>() {});
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->{
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
       
        return map;

    }
    
}
