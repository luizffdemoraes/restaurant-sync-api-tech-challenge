package br.com.fiap.postech.restaurantsync.domain.entities;


import br.com.fiap.postech.restaurantsync.application.dtos.requests.UserRequest;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User {
    private Integer id;
    private String name;
    private String email;
    private String login;
    private String password;
    private Date lastUpdateDate;
    private Address address;
    private Set<Role> roles = new HashSet<>();

    public User(String name, String email, String login, String password, Address address) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.address = address;
        this.lastUpdateDate = new Date();
    }

    public User(UserRequest request) {
        this.name = request.name();
        this.email = request.email();
        this.login = request.login();
        this.password = request.password();
        this.lastUpdateDate = new Date();
        this.address = new Address(
                request.address().street(),
                request.address().number(),
                request.address().city(),
                request.address().state(),
                request.address().zipCode()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getLogin() { return login; }

    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Date getLastUpdateDate() { return lastUpdateDate; }

    public void setLastUpdateDate(Date lastUpdateDate) { this.lastUpdateDate = lastUpdateDate; }

    public Address getAddress() { return address; }

    public void setAddress(Address address) { this.address = address; }

    public Set<Role> getRoles() { return roles; }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(role -> role.getAuthority().equals(roleName));
    }
}
