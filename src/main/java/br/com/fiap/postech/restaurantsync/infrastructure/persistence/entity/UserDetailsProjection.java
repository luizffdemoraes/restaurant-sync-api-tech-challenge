package br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity;

public interface UserDetailsProjection {
    String getUsername();
    String getPassword();
    Integer getRoleId();
    String getAuthority();
}