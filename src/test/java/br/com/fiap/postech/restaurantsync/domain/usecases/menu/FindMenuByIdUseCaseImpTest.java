package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.MenuMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class FindMenuByIdUseCaseImpTest {

    @InjectMocks
    private FindMenuByIdUseCaseImp findMenuByIdUseCaseImp;

    @Mock
    private MenuGateway menuGateway;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        menuGateway = mock(MenuGateway.class);
        userGateway = mock(UserGateway.class);
        findMenuByIdUseCaseImp = new FindMenuByIdUseCaseImp(menuGateway, userGateway);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        Integer menuId = 1;
        Menu dummyMenu = MenuMapper.toDomain(TestDataFactory.createMenuRequest());
        dummyMenu.setId(menuId);
        doNothing().when(userGateway).validateAdmin();
        when(menuGateway.findMenuById(menuId)).thenReturn(dummyMenu);

        // Act
        Menu response = findMenuByIdUseCaseImp.execute(menuId);

        // Assert
        verify(userGateway, times(1)).validateAdmin();
        verify(menuGateway, times(1)).findMenuById(menuId);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(menuId, response.getId());
    }

    @Test
    void testExecute_InvalidAdmin() {
        // Arrange
        Integer menuId = 1;
        doThrow(new IllegalArgumentException("Usuário não é admin"))
                .when(userGateway).validateAdmin();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            findMenuByIdUseCaseImp.execute(menuId);
        });
        verify(menuGateway, never()).findMenuById(anyInt());
    }
}