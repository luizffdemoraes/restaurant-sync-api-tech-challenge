package br.com.fiap.postech.restaurantsync.resources.exceptions;

public record FieldMessage(String fieldName, String message) {

    public FieldMessage(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }
}