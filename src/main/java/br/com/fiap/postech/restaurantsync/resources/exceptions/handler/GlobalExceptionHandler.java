package br.com.fiap.postech.restaurantsync.resources.exceptions.handler;


import br.com.fiap.postech.restaurantsync.resources.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.resources.exceptions.StandardError;
import br.com.fiap.postech.restaurantsync.resources.exceptions.ValidationError;
import br.com.fiap.postech.restaurantsync.resources.translator.FieldTranslationService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final FieldTranslationService translationService;

    public GlobalExceptionHandler(FieldTranslationService translationService) {
        this.translationService = translationService;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Endpoint não encontrado",
                "O endpoint solicitado não existe ou o método HTTP está incorreto",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String mensagem = "Formato de JSON inválido";
        if (ex.getCause() instanceof JsonParseException) {
            mensagem = "Erro na formatação do JSON. Verifique se todos os campos estão corretamente formatados";
        } else if (ex.getCause() instanceof JsonMappingException) {
            mensagem = "Erro no mapeamento do JSON. Verifique se todos os campos possuem os tipos corretos";
        }

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Erro na requisição",
                mensagem,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        ValidationError err = new ValidationError();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            String translatedField = translationService.translateField(f.getField());
            err.addError(translatedField, f.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Acesso não autorizado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}
