package br.com.fiap.postech.restaurantsync.factories;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Address;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.entities.Role;
import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.usecases.user.CreateUserUseCase;
import br.com.fiap.postech.restaurantsync.application.dtos.requests.AddressRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.AddressResponse;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.UserDetailsProjection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class TestDataFactory {
    public static UserRequest createUserRequest() {
        return new UserRequest(
                "John Doe",
                "johndoe@example.com",
                "johndoe",
                "senha123",
                createAddressRequest()
        );
    }

    public static AddressRequest createAddressRequest() {
        return new AddressRequest(
                "Rua Exemplo",
                587L,
                "São Paulo",
                "SP",
                "12345-678"
        );
    }

    public static UserResponse createUserResponse() {
        return new UserResponse(
                1,
                "John Doe",
                "johndoe@example.com",
                "johndoe",
                new Date(),
                createAddressResponse()
        );
    }

    public static AddressResponse createAddressResponse() {
        return new AddressResponse(
                "Rua Exemplo",
                587L,
                "São Paulo",
                "SP",
                "12345-678"
        );
    }

    public static Address createAddress() {
        return new Address(
                "Main Street",
                123L,
                "Sample City",
                "Sample State",
                "12345-678"
        );
    }

    public static User createUser() {
        User user = new User(
                "Test User",
                "user@test.com",
                "testLogin",
                "testPassword",
                createAddress()
        );
        user.setId(1);
        user.setLastUpdateDate(new Date());

        Role role = createRoleClient();
        user.addRole(role);

        return user;
    }

    public static Restaurant createRestaurant(String name, Integer id) {
        return new Restaurant(
                id,
                name,
                createAddress(),
                "Italian",
                "12:00-23:00",
                1
        );
    }

    public static Role createRoleClient() {
        return new Role(1, "ROLE_CLIENT");
    }

    public static UserDetailsProjection createUserDetailsProjection() {
        return new UserDetailsProjection() {
            @Override
            public String getUsername() {
                return "user@test.com";
            }

            @Override
            public String getPassword() {
                return "hashedPassword";
            }

            @Override
            public Integer getRoleId() {
                return 1;
            }

            @Override
            public String getAuthority() {
                return "ROLE_CLIENT";
            }
        };
    }

    public static RestaurantRequest createRestaurantRequest() {
        return new RestaurantRequest(
                "Gourmet Bistro",
                new AddressRequest(
                        "Avenida Paulista",
                        1000L,
                        "São Paulo",
                        "SP",
                        "01310-100"
                ),
                "French",
                "10:00-22:00",
                1
        );
    }

    public static RestaurantResponse createRestaurantResponse() {
        Restaurant restaurant = new Restaurant(
                1,
                "Gourmet Bistro",
                new Address("Avenida Paulista", 1000L, "São Paulo", "SP", "01310-100"),
                "French",
                "10:00-22:00",
                1
        );
        return new RestaurantResponse(restaurant);
    }

    public static Role invokeGetRoleForEmail(CreateUserUseCase createUserUseCase, String email) {
        try {
            Method method = CreateUserUseCase.class.getDeclaredMethod("getRoleForEmail", String.class);
            method.setAccessible(true);
            return (Role) method.invoke(createUserUseCase, email);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Object invokeAuthenticated(CreateUserUseCase createUserUseCase) throws Throwable {
        try {
            Method method = CreateUserUseCase.class.getDeclaredMethod("authenticated");
            method.setAccessible(true);
            return method.invoke(createUserUseCase);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
