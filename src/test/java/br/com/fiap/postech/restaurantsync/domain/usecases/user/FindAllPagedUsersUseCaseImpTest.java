package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.Address;
import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindAllPagedUsersUseCaseImpTest {

    @InjectMocks
    private FindAllPagedUsersUseCaseImp findAllPagedUsersUseCaseImp;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_shouldReturnPagedUserResponses_whenAdminValidated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        // Adiciona o parâmetro de cidade para atender ao construtor
        Address address = new Address("Test Street", 123L, "Test City", "Test State", "Test Zipcode");

        User user1 = new User("User 1", "user1@test.com", "user1", "pass1", address);
        user1.setId(1);
        User user2 = new User("User 2", "user2@test.com", "user2", "pass2", address);
        user2.setId(2);

        List<User> users = Arrays.asList(user1, user2);
        Page<User> userPage = new PageImpl<>(users, pageRequest, users.size());

        doNothing().when(userGateway).validateAdmin();
        when(userGateway.findAllPagedUsers(pageRequest)).thenReturn(userPage);

        Page<UserResponse> responsePage = findAllPagedUsersUseCaseImp.execute(pageRequest);

        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(1, responsePage.getContent().get(0).id());
        assertEquals(2, responsePage.getContent().get(1).id());
        verify(userGateway).validateAdmin();
        verify(userGateway).findAllPagedUsers(pageRequest);
    }

    @Test
    void execute_shouldThrowBusinessException_whenAdminValidationFails() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        doThrow(new BusinessException("Não autorizado")).when(userGateway).validateAdmin();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> findAllPagedUsersUseCaseImp.execute(pageRequest));
        assertEquals("Não autorizado", exception.getMessage());
        verify(userGateway).validateAdmin();
        verify(userGateway, never()).findAllPagedUsers(pageRequest);
    }
}