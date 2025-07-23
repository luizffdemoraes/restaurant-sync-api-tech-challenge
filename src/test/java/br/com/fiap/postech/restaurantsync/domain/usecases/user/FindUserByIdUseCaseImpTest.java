package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.Address;
import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindUserByIdUseCaseImpTest {
    @InjectMocks
    private FindUserByIdUseCaseImp findUserByIdUseCaseImp;

    @Mock
    private UserGateway userGateway;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("Test User", "user@test.com", "testLogin", "testPassword", null);
        user.setId(1);
    }

    @Test
    void execute_shouldReturnUserResponse_whenUserFoundAndAuthorized() {
        Address address = new Address("Test Street", 123L, "Test State", "Test Zipcode", "08663-000");
        user.setAddress(address);

        when(userGateway.findUserById(1)).thenReturn(user);
        doNothing().when(userGateway).validateSelfOrAdmin(user.getId());

        UserResponse response = findUserByIdUseCaseImp.execute(1);

        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals("Test Street", response.address().street());
        verify(userGateway).findUserById(1);
        verify(userGateway).validateSelfOrAdmin(user.getId());
    }

    @Test
    void execute_shouldThrowException_whenNotAuthorized() {
        when(userGateway.findUserById(1)).thenReturn(user);
        doThrow(new BusinessException("Não autorizado")).when(userGateway).validateSelfOrAdmin(user.getId());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                findUserByIdUseCaseImp.execute(1)
        );
        assertEquals("Não autorizado", exception.getMessage());
        verify(userGateway).findUserById(1);
        verify(userGateway).validateSelfOrAdmin(user.getId());
    }
}