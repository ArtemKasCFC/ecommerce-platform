package com.petproject.ecommerce.handler;

import com.petproject.ecommerce.product.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class SecurityErrorHandler {

    private final ObjectMapper objectMapper;

    public SecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void writeError(HttpServletResponse response, int status, String message) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        new ErrorResponse(
                                status,
                                message,
                                LocalDateTime.now()
                        )
                )
        );
    }
}