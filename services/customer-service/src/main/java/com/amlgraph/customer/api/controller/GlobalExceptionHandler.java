package com.amlgraph.customer.api.controller;

import com.amlgraph.common.api.ErrorResponse;
import com.amlgraph.common.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(NotFoundException ex, HttpServletRequest request) {
        return ErrorResponse.of("NOT_FOUND", ex.getMessage(), request.getRequestId());
    }
}
