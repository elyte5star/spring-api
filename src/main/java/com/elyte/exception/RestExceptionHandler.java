package com.elyte.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.utils.ApplicationConsts;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.web.servlet.error.ErrorController;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestExceptionHandler implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @GetMapping("/error")
    public ResponseEntity<CustomResponseStatus> handleError(HttpServletRequest request) {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (null != code) {
            log.debug("Error thrown -- statusCode {}", code);
            switch (code) {
                case 404:
                    CustomResponseStatus status = new CustomResponseStatus(code, ApplicationConsts.E404_MSG,
                            ApplicationConsts.FAILURE,
                            request.getRequestURL().toString(),
                            ApplicationConsts.timeNow(), null);

                    return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.BAD_REQUEST);
                case 500:
                    CustomResponseStatus status1 = new CustomResponseStatus(code, ApplicationConsts.E500_MSG,
                            ApplicationConsts.FAILURE,
                            request.getRequestURL().toString(),
                            ApplicationConsts.timeNow(), null);
                    return new ResponseEntity<>(status1, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        CustomResponseStatus status = new CustomResponseStatus(code, ApplicationConsts.I999_MSG,
                ApplicationConsts.SUCCESS, request.getRequestURL().toString(), ApplicationConsts.timeNow(), null);
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), ApplicationConsts.E404_MSG);

        log.error("[+] ResourceNotFoundException--{}", e.getMessage());
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgument(MethodArgumentNotValidException e) {
        Map<String, Object> mp = new HashMap<String, Object>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            mp.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.BAD_REQUEST.value(),
                "Input validation failed",
                ApplicationConsts.FAILURE,
                e.getClass().getName(), ApplicationConsts.timeNow(), Map.of("errors", mp));
        log.error("[+] MethodArgumentNotValidException--{}", e.getMessage());
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(Exception e) {
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), ApplicationConsts.E401_MSG);

        log.error("[+] BadCredentialsException--{}", e.getMessage());
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointer(Exception e) {
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), ApplicationConsts.I999_MSG);

        log.error("[+] NullPointerException--{}", e.getMessage());
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFound(Exception e) {
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), ApplicationConsts.E404_MSG);

        log.error("[+] NoHandlerFoundException--{}", e.getMessage());
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabledException(Exception e) {
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.LOCKED.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), ApplicationConsts.E423_MSG);
        log.error("[+] DisabledException--{}", e.getMessage());
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.LOCKED);
    }

    // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(Exception e) {
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.CONFLICT.value(), e.getMessage(),
                ApplicationConsts.FAILURE,
                e.getClass().getName(),
                ApplicationConsts.timeNow(), ApplicationConsts.E205_MSG);

        log.error("[+] DataIntegrityViolationException--{}", e.getMessage());
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    public ResponseEntity<?> handleAccessDeniedException(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            AccessDeniedException accessDeniedException) throws IOException {
        CustomResponseStatus status = new CustomResponseStatus(HttpServletResponse.SC_FORBIDDEN, "NOT_PERMITTED",
                ApplicationConsts.FAILURE, accessDeniedException.getClass().getName(), ApplicationConsts.timeNow(),
                accessDeniedException.getMessage());

        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.error("[+] AccessDenied error: {}", accessDeniedException.getMessage());
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.FORBIDDEN);

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
        status.setCode(HttpStatus.BAD_REQUEST.value());
        status.setPath(e.getClass().getName());
        status.setTimeStamp(ApplicationConsts.timeNow());
        status.setSuccess(ApplicationConsts.FAILURE);
        log.error("[+] HttpMessageNotReadableException --{}", e.getMessage());
        return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException exc, HttpServletRequest request,
            HttpServletResponse response) {
        CustomResponseStatus customStatus = new CustomResponseStatus(exc.getStatusCode().value(), "File too large!",
                ApplicationConsts.FAILURE, exc.getClass().getName(), ApplicationConsts.timeNow(),
                ApplicationConsts.E413_MSG);
        log.error("[+] MaxUploadSizeExceededException --{}", exc.getMessage());
        return new ResponseEntity<>(customStatus, new HttpHeaders(), exc.getStatusCode());
    }

    @ExceptionHandler({ MailAuthenticationException.class })
    public ResponseEntity<?> handleMail(final RuntimeException ex, final WebRequest request) {
        log.error("[+] 500 Status Code- MailError", ex.getMessage());
        CustomResponseStatus customStatus = new CustomResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "message.email.config.error", ApplicationConsts.FAILURE, ex.getClass().getName(),
                ApplicationConsts.timeNow(),
                "MailError");
        return new ResponseEntity<>(customStatus, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    // fallback method
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleFallback(HttpServletRequest request, Exception e) {
        log.error("[+] Fallback Exception.getMessage--{}", e.getMessage());
        CustomResponseStatus customStatus = new CustomResponseStatus(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                ApplicationConsts.FAILURE, e.getClass().getName(), ApplicationConsts.timeNow(),
                ApplicationConsts.I999_MSG);
        return new ResponseEntity<>(customStatus, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnusualLocationException.class)
    public ResponseEntity<?> handleUnusualLocationException(HttpServletRequest request, Exception e) {
        log.error("[+] Unusual location--{}", e.getMessage());
        CustomResponseStatus customStatus = new CustomResponseStatus(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                ApplicationConsts.FAILURE, e.getClass().getName(), ApplicationConsts.timeNow(),
                ApplicationConsts.I999_MSG);
        return new ResponseEntity<>(customStatus, new HttpHeaders(), HttpStatus.BAD_REQUEST);

    }

}
