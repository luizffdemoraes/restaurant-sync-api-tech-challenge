package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FindAllPagedUsersUseCase {
    Page<UserResponse> execute(PageRequest pageRequest);
}
