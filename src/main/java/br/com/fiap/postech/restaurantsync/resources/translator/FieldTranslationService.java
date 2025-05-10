package br.com.fiap.postech.restaurantsync.resources.translator;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FieldTranslationService {
    private final Map<String, FieldTranslator> translators;

    public FieldTranslationService(List<FieldTranslator> translatorsList) {
        translators = new HashMap<>();
        translatorsList.forEach(translator ->
                translators.put(getTranslatorPrefix(translator), translator));
    }

    public String translateField(String field) {
        String[] parts = field.split("\\.");
        if (parts.length > 1) {
            FieldTranslator translator = translators.get(parts[0]);
            return translator != null ? translator.translate(parts[1]) : field;
        }

        return translators.get("user").translate(field);
    }

    private String getTranslatorPrefix(FieldTranslator translator) {
        String className = translator.getClass().getSimpleName();
        return className.substring(0, className.indexOf("FieldTranslator")).toLowerCase();
    }
}