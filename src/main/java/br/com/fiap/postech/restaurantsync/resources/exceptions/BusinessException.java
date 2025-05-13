package br.com.fiap.postech.restaurantsync.resources.exceptions;

public class BusinessException extends RuntimeException{

    public BusinessException(String msg) {
        super(msg);
    }
}