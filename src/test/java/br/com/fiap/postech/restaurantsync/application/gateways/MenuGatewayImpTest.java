package br.com.fiap.postech.restaurantsync.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.MenuEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class MenuGatewayImpTest {

    @InjectMocks
    private MenuGatewayImp menuGatewayImp;

    @Mock
    private MenuRepository menuRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveRestaurant_Success() {
        // Arrange
        Menu menu = new Menu(TestDataFactory.createMenuRequest());
        MenuEntity menuEntity = MenuEntity.fromDomain(menu);
        menuEntity.setId(1);
        when(menuRepository.save(any(MenuEntity.class))).thenReturn(menuEntity);

        // Act
        Menu savedMenu = menuGatewayImp.saveRestaurant(menu);

        // Assert
        assertNotNull(savedMenu);
        assertEquals(1, savedMenu.getId());
        verify(menuRepository).save(any(MenuEntity.class));
    }

    @Test
    void testDeleteMenuById_Success() {
        // Arrange
        Integer menuId = 1;
        doNothing().when(menuRepository).deleteById(menuId);

        // Act
        menuGatewayImp.deleteMenuById(menuId);

        // Assert
        verify(menuRepository, times(1)).deleteById(menuId);
    }

    @Test
    void testDeleteMenuById_DataIntegrityViolation() {
        // Arrange
        Integer menuId = 1;
        doThrow(new RuntimeException("Some DB error")).when(menuRepository).deleteById(menuId);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () ->
                menuGatewayImp.deleteMenuById(menuId)
        );
        assertEquals("Integrity violaton.", exception.getMessage());
    }

    @Test
    void testFindAllPagedMenus_Success() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Menu menu = new Menu(TestDataFactory.createMenuRequest());
        menu.setId(1);
        MenuEntity menuEntity = MenuEntity.fromDomain(menu);
        Page<MenuEntity> pageEntity = new PageImpl<>(Collections.singletonList(menuEntity), pageRequest, 1);
        when(menuRepository.findAll(pageRequest)).thenReturn(pageEntity);

        // Act
        Page<Menu> pageMenus = menuGatewayImp.findAllPagedMenus(pageRequest);

        // Assert
        assertNotNull(pageMenus);
        assertEquals(1, pageMenus.getTotalElements());
        assertEquals(1, pageMenus.getContent().get(0).getId());
        verify(menuRepository).findAll(pageRequest);
    }

    @Test
    void testFindMenuById_Success() {
        // Arrange
        Integer menuId = 1;
        Menu menu = new Menu(TestDataFactory.createMenuRequest());
        menu.setId(menuId);
        MenuEntity menuEntity = MenuEntity.fromDomain(menu);
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menuEntity));

        // Act
        Menu foundMenu = menuGatewayImp.findMenuById(menuId);

        // Assert
        assertNotNull(foundMenu);
        assertEquals(menuId, foundMenu.getId());
        verify(menuRepository).findById(menuId);
    }

    @Test
    void testFindMenuById_NotFound() {
        // Arrange
        Integer menuId = 1;
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () ->
                menuGatewayImp.findMenuById(menuId)
        );
        assertEquals("Id not found: " + menuId, exception.getMessage());
    }

    @Test
    void testUpdateMenu_Success() {
        // Arrange
        Integer menuId = 1;
        Menu menuRequest =  new Menu(TestDataFactory.createMenuRequest());
        Menu existingMenu = new Menu(TestDataFactory.createMenuRequest());
        existingMenu.setId(menuId);
        MenuEntity existingEntity = MenuEntity.fromDomain(existingMenu);
        Menu updatedMenu = menuRequest;
        updatedMenu.setId(menuId);
        MenuEntity updatedEntity = MenuEntity.fromDomain(updatedMenu);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(existingEntity));
        when(menuRepository.save(any(MenuEntity.class))).thenReturn(updatedEntity);

        // Act
        Menu response = menuGatewayImp.updateMenu(menuId, menuRequest);

        // Assert
        assertNotNull(response);
        assertEquals(menuId, response.getId());
        verify(menuRepository).findById(menuId);
        verify(menuRepository).save(any(MenuEntity.class));
    }

    @Test
    void testUpdateAvailableOnlyRestaurant_Success() {
        // Arrange
        Integer menuId = 1;
        Boolean availableOnlyRestaurant = true;
        Menu existingMenu = new Menu(TestDataFactory.createMenuRequest());
        existingMenu.setId(menuId);
        existingMenu.setAvailableOnlyRestaurant(!availableOnlyRestaurant);
        MenuEntity existingEntity = MenuEntity.fromDomain(existingMenu);
        Menu updatedMenu = new Menu(TestDataFactory.createMenuRequest());
        updatedMenu.setId(menuId);
        updatedMenu.setAvailableOnlyRestaurant(availableOnlyRestaurant);
        MenuEntity updatedEntity = MenuEntity.fromDomain(updatedMenu);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(existingEntity));
        when(menuRepository.save(any(MenuEntity.class))).thenReturn(updatedEntity);

        // Act
        Menu response = menuGatewayImp.updateAvailableOnlyRestaurant(menuId, availableOnlyRestaurant);

        // Assert
        assertNotNull(response);
        assertTrue(response.isAvailableOnlyRestaurant());
        verify(menuRepository).findById(menuId);
        verify(menuRepository).save(any(MenuEntity.class));
    }
}