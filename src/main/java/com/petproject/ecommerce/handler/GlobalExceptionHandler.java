package com.petproject.ecommerce.handler;

import com.petproject.ecommerce.exception.AlreadyExistsException;
import com.petproject.ecommerce.exception.NotFoundException;
import com.petproject.ecommerce.product.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(AlreadyExistsException e) {
        ErrorResponse response = new ErrorResponse(409, e.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {

        ErrorResponse response = new ErrorResponse(404, e.getMessage(), LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidationErrors(Exception e) {

        Map<String, String> errors = new HashMap<>();

        if (e instanceof MethodArgumentNotValidException ex) {

            ex.getBindingResult()
                    .getFieldErrors()
                    .forEach(error ->
                            errors.put(
                                    error.getField(),
                                    error.getDefaultMessage()
                            )
                    );
        } else if (e instanceof HandlerMethodValidationException ex) {
            ex.getParameterValidationResults()
                    .forEach(result -> {
                        if (result instanceof ParameterErrors parameterErrors) {
                            parameterErrors.getFieldErrors()
                                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
                        } else {
                            result.getResolvableErrors().forEach(error -> errors.put(result.getMethodParameter().getParameterName(), error.getDefaultMessage()));
                        }
                    });
        }

        ErrorResponse response = new ErrorResponse(400, "Validation failed", LocalDateTime.now(), errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}