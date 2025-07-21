package br.com.fiap.postech.restaurantsync.domain.usecases.user;

public interface UpdatePasswordUseCase {
    void execute(Integer id, String password);
}
