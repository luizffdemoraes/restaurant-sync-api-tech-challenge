package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;

public class DeleteUserUseCaseImp implements DeleteUserUseCase{

    private final UserGateway userGateway;

    public DeleteUserUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public void execute(Integer id) {
        this.userGateway.validateAdmin();
        this.userGateway.findUserOrThrow(id);
        try {
            this.userGateway.deleteUserById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }
}
