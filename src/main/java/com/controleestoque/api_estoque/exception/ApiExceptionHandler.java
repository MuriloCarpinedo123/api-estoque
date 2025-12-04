package com.controleestoque.api_estoque.exception;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<String> tratar(EstoqueInsuficienteException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}