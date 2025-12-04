package com.controleestoque.api_estoque.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String msg) {
        super(msg);
    }
}