package br.com.projectblog.exceptions.handlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.projectblog.exceptions.BusinessException;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
	
    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<ApiRequestException> haddlerBusinessException(BusinessException e, HttpServletRequest request) {

        ApiRequestException apiException = ApiRequestException.builder()
                .title("Bad Request")
                .message(e.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now()).build();

        log.error("Status Http: {}, message: {}\n", HttpStatus.BAD_REQUEST.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiException);
    }
    
    
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ApiRequestException> haddlerConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {

        ApiRequestException apiException = ApiRequestException.builder()
                .title("Bad Request")
                .message(e.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now()).build();

        log.error("Status Http: {}, message: {}\n", HttpStatus.BAD_REQUEST.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiException);
    }
    
    
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ApiRequestException> haddlerHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {

        ApiRequestException apiException = ApiRequestException.builder()
                .title("Bad Request")
                .message(e.getCause().toString())
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now()).build();

        log.error("Status Http: {}, message: {}\n", HttpStatus.BAD_REQUEST.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiException);
    }
    
    
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ApiRequestException> haddlerResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {

        ApiRequestException apiException = ApiRequestException.builder()
                .title("Resource Not Found")
                .message(e.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now()).build();

        log.error("Status Http: {}, message: {}\n", HttpStatus.NOT_FOUND.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiException);
    }
    
}
