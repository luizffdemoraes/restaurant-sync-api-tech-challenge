package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;

public class DeleteMenuUseCaseImp implements DeleteMenuUseCase{

    private final MenuGateway menuGateway;
    private final UserGateway userGateway;

    public DeleteMenuUseCaseImp(MenuGateway menuGateway, UserGateway userGateway) {
        this.menuGateway = menuGateway;
        this.userGateway = userGateway;
    }

    @Override
    public void execute(Integer id) {
        this.userGateway.validateAdmin();
        this.menuGateway.findMenuOrThrow(id);
        try {
            this.menuGateway.deleteMenuById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }
}
