package br.com.fiap.postech.restaurantsync.infrastructure.exceptions.handler;


import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.StandardError;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.ValidationError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Endpoint not found",
                "The requested endpoint does not exist or the HTTP method is incorrect",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = "Invalid JSON format";
        if (ex.getCause() instanceof JsonParseException) {
            message = "JSON formatting error. Please check if all fields are correctly formatted";
        } else if (ex.getCause() instanceof JsonMappingException) {
            message = "JSON mapping error. Please verify that all fields have the correct types";
        }

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Request error",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError err = new ValidationError();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            err.addError(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String msg = ex.getMessage().toLowerCase();
        HttpStatus status = msg.contains("not found")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.UNAUTHORIZED;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                status == HttpStatus.NOT_FOUND ? "Resource not found" : "Unauthorized access",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Invalid request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}
