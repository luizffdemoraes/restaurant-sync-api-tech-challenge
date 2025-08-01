package br.com.fiap.postech.restaurantsync.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.MenuMapper;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.MenuEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class MenuGatewayImp implements MenuGateway {

    private final MenuRepository menuItemRepository;

    public MenuGatewayImp(MenuRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public Menu saveRestaurant(Menu menuItem) {
        MenuEntity entity = MenuMapper.toEntity(menuItem);
        MenuEntity saved = this.menuItemRepository.save(entity);
        return MenuMapper.toDomain(saved);
    }

    @Override
    public void deleteMenuById(Integer id) {
        try {
            this.menuItemRepository.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException("Integrity violaton.");
        }
    }

    @Override
    public Page<Menu> findAllPagedMenus(PageRequest pageRequest) {
        Page<MenuEntity> pagedMenus = this.menuItemRepository.findAll(pageRequest);
        return pagedMenus.map(MenuMapper::toDomain);
    }

    @Override
    public Menu findMenuById(Integer id) {
        return findMenuOrThrow(id);
    }

    @Override
    public Menu updateMenu(Integer id, Menu menuRequest) {
        Menu menu = findMenuOrThrow(id);
        menu.setName(menuRequest.getName() != null ? menuRequest.getName() : menu.getName());
        menu.setDescription(menuRequest.getDescription() != null ? menuRequest.getDescription() : menu.getDescription());
        menu.setPrice(menuRequest.getPrice() != null ? menuRequest.getPrice() : menu.getPrice());
        menu.setAvailableOnlyRestaurant(menuRequest.isAvailableOnlyRestaurant());
        menu.setPhotoPath(menuRequest.getPhotoPath() != null ? menuRequest.getPhotoPath() : menu.getPhotoPath());
        menu.setRestaurantId(menuRequest.getRestaurantId() != null ? menuRequest.getRestaurantId() : menu.getRestaurantId());
        MenuEntity saved = this.menuItemRepository.save(MenuMapper.toEntity(menu));
        return MenuMapper.toDomain(saved);
    }

    @Override
    public Menu updateAvailableOnlyRestaurant(Integer id, Boolean availableOnlyRestaurant) {
        Menu menu = findMenuOrThrow(id);
        menu.setAvailableOnlyRestaurant(availableOnlyRestaurant);
        MenuEntity saved = this.menuItemRepository.save(MenuMapper.toEntity(menu));
        return MenuMapper.toDomain(saved);
    }

    public Menu findMenuOrThrow(Integer id) {
        MenuEntity entity = this.menuItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Id not found: " + id));
        return MenuMapper.toDomain(entity);
    }
}
