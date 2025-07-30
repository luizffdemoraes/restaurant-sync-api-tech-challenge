package br.com.fiap.postech.restaurantsync.infrastructure.config.mapper;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Role;
import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RoleEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserRequest request) {
        return new User(
                request.name(),
                request.email(),
                request.login(),
                request.password(),
                AddressMapper.toDomain(request.address())
        );
    }

    public static User toDomain(UserEntity entity) {
        User user = new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getLogin(),
                entity.getPassword(),
                AddressMapper.toDomain(entity.getAddress())
        );
        entity.getRoleEntities().forEach(roleEntity ->
                user.addRole(new Role(
                        roleEntity.getId(),
                        roleEntity.getAuthority()
                ))
        );
        return user;
    }

    public static UserResponse toResponse(User domain) {
        return new UserResponse(domain);
    }

    public static UserEntity fromDomain(User user) {
        if (user == null) {
            return null;
        }
        var address = AddressMapper.fromDomain(user.getAddress());
        var entity = new UserEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getPassword(),
                address
        );
        entity.setId(user.getId());
        entity.setLastUpdateDate(user.getLastUpdateDate());
        user.getRoles().forEach(role -> {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setId(role.getId());
            roleEntity.setAuthority(role.getAuthority());
            entity.addRole(roleEntity);
        });
        return entity;
    }


}
