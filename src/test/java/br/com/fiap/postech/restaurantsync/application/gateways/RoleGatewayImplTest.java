package br.com.fiap.postech.restaurantsync.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Role;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RoleEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RoleGatewayImplTest {

    @InjectMocks
    private RoleGatewayImpl roleGateway;

    @Mock
    private RoleRepository roleRepository;

    private RoleEntity adminRoleEntity;
    private RoleEntity clientRoleEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminRoleEntity = new RoleEntity(1, "ROLE_ADMIN");
        clientRoleEntity = new RoleEntity(2, "ROLE_CLIENT");
    }

    @Test
    void findByAuthority_shouldReturnRole_whenRoleExists() {
        when(roleRepository.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.of(adminRoleEntity));
        Optional<Role> role = roleGateway.findByAuthority("ROLE_ADMIN");
        assertTrue(role.isPresent());
        assertEquals("ROLE_ADMIN", role.get().getAuthority());
    }

    @Test
    void findByAuthority_shouldReturnEmptyOptional_whenRoleDoesNotExist() {
        when(roleRepository.findByAuthority("ROLE_UNKNOWN")).thenReturn(Optional.empty());
        Optional<Role> role = roleGateway.findByAuthority("ROLE_UNKNOWN");
        assertFalse(role.isPresent());
    }

    @Test
    void getRoleForEmail_shouldReturnAdminRole_whenEmailEndsWithRestaurantsync() {
        when(roleRepository.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.of(adminRoleEntity));
        Role role = roleGateway.getRoleForEmail("user@restaurantsync.com");
        assertNotNull(role);
        assertEquals("ROLE_ADMIN", role.getAuthority());
    }

    @Test
    void getRoleForEmail_shouldReturnClientRole_whenEmailDoesNotEndWithRestaurantsync() {
        when(roleRepository.findByAuthority("ROLE_CLIENT")).thenReturn(Optional.of(clientRoleEntity));
        Role role = roleGateway.getRoleForEmail("user@example.com");
        assertNotNull(role);
        assertEquals("ROLE_CLIENT", role.getAuthority());
    }

    @Test
    void getRoleForEmail_shouldThrowException_whenAdminRoleNotFound() {
        when(roleRepository.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () ->
                roleGateway.getRoleForEmail("user@restaurantsync.com")
        );
        assertEquals("Role ADMIN não encontrada.", exception.getMessage());
    }

    @Test
    void getRoleForEmail_shouldThrowException_whenClientRoleNotFound() {
        when(roleRepository.findByAuthority("ROLE_CLIENT")).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () ->
                roleGateway.getRoleForEmail("user@example.com")
        );
        assertEquals("Role CLIENT não encontrada.", exception.getMessage());
    }
}