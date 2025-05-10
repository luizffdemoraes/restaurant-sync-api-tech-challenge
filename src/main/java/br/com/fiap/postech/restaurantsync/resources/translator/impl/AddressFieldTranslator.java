package br.com.fiap.postech.restaurantsync.resources.translator.impl;

import br.com.fiap.postech.restaurantsync.resources.translator.FieldTranslator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AddressFieldTranslator implements FieldTranslator {
    private static final Map<String, String> TRANSLATIONS = Map.of(
            "street", "rua",
            "number", "número",
            "city", "cidade",
            "state", "estado",
            "zipCode", "cep"
    );

    @Override
    public String translate(String fieldName) {
        return TRANSLATIONS.getOrDefault(fieldName, fieldName);
    }
}

