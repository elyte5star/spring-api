package com.elyte.exception;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.elyte.domain.response.Status;
import com.elyte.utils.ApplicationConsts;
import jakarta.servlet.http.HttpServletRequest;



@RestControllerAdvice
public class ResourceNotFoundExceptionHandler{

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetail> handleUserNotFoundException(ResourceNotFoundException rnfe,HttpServletRequest request) {
        ErrorDetail errorDetail = new ErrorDetail("Resource Not Found");
        LocalDateTime current = LocalDateTime.now();
        Status status = Status.build(HttpStatus.NOT_FOUND.value(), rnfe.getMessage(), false, rnfe.getClass().getName(),current.format(ApplicationConsts.dtf));
        errorDetail.setStatus(status);
        return new ResponseEntity<>(errorDetail, null, HttpStatus.NOT_FOUND);
      
    }
    
}
