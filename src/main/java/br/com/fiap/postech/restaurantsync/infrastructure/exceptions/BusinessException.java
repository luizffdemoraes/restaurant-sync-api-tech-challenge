package br.com.fiap.postech.restaurantsync.infrastructure.exceptions;

public class BusinessException extends RuntimeException{

    public BusinessException(String msg) {
        super(msg);
    }
}