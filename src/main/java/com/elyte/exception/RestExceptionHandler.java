package com.elyte.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.elyte.domain.response.ErrorResponse;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.utils.ApplicationConsts;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.boot.json.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ApplicationConsts.E404_MSG);
        CustomResponseStatus status = CustomResponseStatus.build(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), null);
        errorResponse.setStatus(status);
        log.error("Exception.getMessage--{}", e.getMessage());
        log.error("Exception.getClass--{}", e.getClass());
        return new ResponseEntity<>(errorResponse, null, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgument(MethodArgumentNotValidException e) {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetail = new ErrorResponse(ApplicationConsts.I202_MSG);
        CustomResponseStatus status = CustomResponseStatus.build(HttpStatus.BAD_REQUEST.value(),
                "Input validation failed",
                ApplicationConsts.FAILURE,
                e.getClass().getName(), ApplicationConsts.timeNow(), null);
        errorDetail.setStatus(status);
        Map<String, Object> map = objectMapper.convertValue(errorDetail, new TypeReference<>() {
        });
        Map<String, Object> mp = new HashMap<String, Object>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            mp.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        map.put("errors", mp);
        log.error("Exception.getMessage--{}", e.getMessage());
        log.error("Exception.getClass--{}", e.getClass());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(ApplicationConsts.E401_MSG);
        CustomResponseStatus status = CustomResponseStatus.build(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), null);
        errorResponse.setStatus(status);
        log.error("Exception.getMessage--{}", e.getMessage());
        log.error("Exception.getClass--{}", e.getClass());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointer(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(ApplicationConsts.I999_MSG);
        CustomResponseStatus status = CustomResponseStatus.build(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), null);
        errorResponse.setStatus(status);
        log.error("Exception.getMessage--{}", e.getMessage());
        log.error("Exception.getClass--{}", e.getClass());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFound(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(ApplicationConsts.E404_MSG);

        CustomResponseStatus status = CustomResponseStatus.build(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), null);
        errorResponse.setStatus(status);
        log.error("Exception.getMessage--{}", e.getMessage());
        log.error("Exception.getClass--{}", e.getClass());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabledException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(ApplicationConsts.E409_MSG);
        CustomResponseStatus status = CustomResponseStatus.build(HttpStatus.CONFLICT.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), null);
        errorResponse.setStatus(status);
        log.error("Exception.getMessage--{}", e.getMessage());
        log.error("Exception.getClass--{}", e.getClass());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(ApplicationConsts.E205_MSG);
        CustomResponseStatus status = CustomResponseStatus.build(HttpStatus.CONFLICT.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), null);
        errorResponse.setStatus(status);
        log.error("Exception.getMessage--{}", e.getMessage());
        log.error("Exception.getClass--{}", e.getClass());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    public ResponseEntity<?> handleAccessDeniedException(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            AccessDeniedException accessDeniedException) throws IOException {
        CustomResponseStatus status = CustomResponseStatus.build(HttpServletResponse.SC_FORBIDDEN, "NOT_PERMITTED",
                ApplicationConsts.FAILURE, accessDeniedException.getClass().getName(), ApplicationConsts.timeNow(),
                accessDeniedException.getMessage());
        ErrorResponse errorResponse = ErrorResponse.build(status, ApplicationConsts.ADR_MSG);
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.error("AccessDenied error: {}", accessDeniedException.getMessage());
        log.error("Exception.getClass--{}", accessDeniedException.getClass());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e)
            throws Throwable {
        final Throwable cause = e.getCause();
        CustomResponseStatus status = new CustomResponseStatus();
        if (cause == null) {
            status.setMessage(null);
        } else if (cause instanceof JsonParseException) {
            status.setMessage(cause.toString());
        } else if (cause instanceof JsonMappingException) {
            status.setMessage(cause.toString());
        } else {
            status.setMessage(ApplicationConsts.E400_MSG);

        }
        ErrorResponse errorResponse = new ErrorResponse(ApplicationConsts.E400_MSG);
        status.setCode(HttpStatus.BAD_REQUEST.value());
        status.setPath(e.getClass().getName());
        status.setTimeStamp(ApplicationConsts.timeNow());
        status.setSuccess(ApplicationConsts.FAILURE);
        errorResponse.setStatus(status);
        log.error("Exception.getMessage--{}", e.getMessage());
        log.error("Exception.getClass--{}", e.getClass());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
