package br.com.fiap.postech.restaurantsync.entities;

import br.com.fiap.postech.restaurantsync.dtos.requests.UserRequest;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(name = "last_update_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate;

    @Embedded
    private Address address;

    public User() {
        // Construtor padrão necessário para o JPA
    }

    public User(String name, String email, String login, String password, Address address) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.lastUpdateDate = new Date();
        this.address = address;
    }

    public User(UserRequest userRequest) {
        this(
                userRequest.name(),
                userRequest.email(),
                userRequest.login(),
                userRequest.password(),
                new Address(userRequest.address())
        );
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}