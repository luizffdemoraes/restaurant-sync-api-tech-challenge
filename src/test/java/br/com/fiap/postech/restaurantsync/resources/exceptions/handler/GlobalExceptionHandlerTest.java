package br.com.fiap.postech.restaurantsync.resources.exceptions.handler;

import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.StandardError;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.ValidationError;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {


    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
       // handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/test-uri");
    }

    @Test
    void handleNoResourceFound_ShouldReturnNotFoundResponse() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/non-existent-endpoint");

        ResponseEntity<StandardError> response = handler.handleNoResourceFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Endpoint não encontrado", response.getBody().error());
        assertEquals("/test-uri", response.getBody().path());
    }

    @Test
    void handleHttpMessageNotReadable_WithJsonMappingException_ShouldReturnBadRequestWithMappingMessage() {
        JsonMappingException cause = mock(JsonMappingException.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Error", cause);

        ResponseEntity<StandardError> response = handler.handleHttpMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Erro no mapeamento do JSON"));
    }

    @Test
    void handleHttpMessageNotReadable_WithOtherCause_ShouldReturnBadRequestWithDefaultMessage() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Error", new RuntimeException());

        ResponseEntity<StandardError> response = handler.handleHttpMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Formato de JSON inválido", response.getBody().message());
    }

    @Test
    void validation_ShouldReturnValidationErrorWithTranslatedFields() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(org.springframework.validation.BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "field1", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "field2", "must be a number");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));


        ResponseEntity<ValidationError> response = handler.validation(ex, request);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        ValidationError body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.getErrors().size());
        assertTrue(body.getErrors().stream().anyMatch(e -> e.fieldName().equals("campo1") && e.message().equals("must not be blank")));
        assertTrue(body.getErrors().stream().anyMatch(e -> e.fieldName().equals("campo2") && e.message().equals("must be a number")));
    }

    @Test
    void handleBusinessException_ShouldReturnUnauthorizedResponse() {
        BusinessException ex = new BusinessException("Access denied");

        ResponseEntity<StandardError> response = handler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Acesso não autorizado", response.getBody().error());
        assertEquals("Access denied", response.getBody().message());
        assertEquals("/test-uri", response.getBody().path());
    }

    @Test
    void handleHttpMessageNotReadable_WhenCauseIsJsonParseException_ShouldReturnSpecificMessage() {
        JsonParseException cause = new JsonParseException(new RuntimeException("Mock cause"));

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Erro ao ler mensagem", cause);

        String testUri = "/test-uri";
        when(request.getRequestURI()).thenReturn(testUri);

        ResponseEntity<StandardError> response = handler.handleHttpMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro na requisição", response.getBody().error());
        assertEquals(testUri, response.getBody().path());

        String mensagemEsperada = "Formato de JSON inválido";
        assertEquals(mensagemEsperada, response.getBody().message());
    }


}
