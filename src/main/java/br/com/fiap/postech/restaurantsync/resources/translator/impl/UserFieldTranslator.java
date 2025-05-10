package br.com.fiap.postech.restaurantsync.resources.translator.impl;

import br.com.fiap.postech.restaurantsync.resources.translator.FieldTranslator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserFieldTranslator implements FieldTranslator {
    private static final Map<String, String> TRANSLATIONS = Map.of(
            "name", "nome",
            "email", "email",
            "login", "login",
            "password", "senha",
            "address", "endereço"
    );

    @Override
    public String translate(String fieldName) {
        return TRANSLATIONS.getOrDefault(fieldName, fieldName);
    }
}
