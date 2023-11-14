package com.elyte.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.elyte.domain.response.ErrorResponse;
import com.elyte.domain.response.Status;
import com.elyte.utils.ApplicationConsts;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(ResourceNotFoundException rnfe, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Resource Not Found");
        LocalDateTime current = LocalDateTime.now();
        Status status = Status.build(HttpStatus.NOT_FOUND.value(), rnfe.getMessage(), false, rnfe.getClass().getName(),
                current.format(ApplicationConsts.dtf));
        errorResponse.setStatus(status);
        return new ResponseEntity<>(errorResponse, null, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgument(MethodArgumentNotValidException exception) {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = new ErrorResponse("Validation Failed");
        Map<String, Object> map = objectMapper.convertValue(errorResponse, new TypeReference<>() {
        });
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        LocalDateTime current = LocalDateTime.now();
        Status status = Status.build(HttpStatus.BAD_REQUEST.value(), "Input validation failed", false,
                exception.getClass().getName(), current.format(ApplicationConsts.dtf));
        errorResponse.setStatus(status);

        return new ResponseEntity<>(errorResponse, null, HttpStatus.BAD_REQUEST);
    }

}
