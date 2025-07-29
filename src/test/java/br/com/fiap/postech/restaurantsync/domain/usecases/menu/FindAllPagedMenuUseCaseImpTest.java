package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.mockito.Mockito.*;

class FindAllPagedMenuUseCaseImpTest {

    @InjectMocks
    private FindAllPagedMenuUseCaseImp findAllPagedMenuUseCaseImp;

    @Mock
    private MenuGateway menuGateway;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        menuGateway = mock(MenuGateway.class);
        userGateway = mock(UserGateway.class);
        findAllPagedMenuUseCaseImp = new FindAllPagedMenuUseCaseImp(menuGateway, userGateway);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Menu dummyMenu = new Menu(TestDataFactory.createMenuRequest());
        dummyMenu.setId(1);
        List<Menu> menus = List.of(dummyMenu);
        Page<Menu> pagedMenus = new PageImpl<>(menus, pageRequest, menus.size());
        doNothing().when(userGateway).validateAdmin();
        when(menuGateway.findAllPagedMenus(pageRequest)).thenReturn(pagedMenus);

        // Act
        Page<MenuResponse> responsePage = findAllPagedMenuUseCaseImp.execute(pageRequest);

        // Assert
        verify(userGateway, times(1)).validateAdmin();
        verify(menuGateway, times(1)).findAllPagedMenus(pageRequest);
        Assertions.assertNotNull(responsePage);
        Assertions.assertEquals(1, responsePage.getTotalElements());
        Assertions.assertEquals(dummyMenu.getId(), responsePage.getContent().get(0).id());
    }

    @Test
    void testExecute_InvalidAdmin() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        doThrow(new IllegalArgumentException("Usuário não é admin"))
                .when(userGateway).validateAdmin();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            findAllPagedMenuUseCaseImp.execute(pageRequest);
        });
        verify(menuGateway, never()).findAllPagedMenus(any());
    }
}