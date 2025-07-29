package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class UpdatePasswordUseCaseImp implements UpdatePasswordUseCase{

    private final UserGateway userGateway;

    public UpdatePasswordUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public void execute(Integer id, String newPassword) {
        this.userGateway.validateSelfOrAdmin(id);
        this.userGateway.updateUserPassword(id, newPassword);
    }
}
