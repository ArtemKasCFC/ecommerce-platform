package com.petproject.ecommerce.handler;

import com.petproject.ecommerce.exception.ProductNotFoundException;
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

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
            ProductNotFoundException exception) {

        ErrorResponse response =
                new ErrorResponse(404, exception.getMessage(), LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            Exception exception) {

        Map<String, String> errors = new HashMap<>();

        if (exception instanceof MethodArgumentNotValidException ex) {

            ex.getBindingResult()
                    .getFieldErrors()
                    .forEach(error ->
                            errors.put(
                                    error.getField(),
                                    error.getDefaultMessage()
                            )
                    );
        } else if (exception instanceof HandlerMethodValidationException ex) {
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

        ErrorResponse response =
                new ErrorResponse(400, "Validation failed", LocalDateTime.now(), errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}