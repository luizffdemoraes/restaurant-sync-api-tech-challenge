package br.com.fiap.postech.restaurantsync.factories;


public class TestDataFactory {

    /**
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

    public static AddressEntity createAddress() {
        return new AddressEntity(
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

        RoleEntity roleEntity = createRoleClient();
        user.addRole(roleEntity);

        return user;
    }

    public static RoleEntity createRoleClient() {
        return new RoleEntity(1, "ROLE_CLIENT");
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

    public static RoleEntity invokeGetRoleForEmail(UserService userService, String email) {
        try {
            Method method = UserService.class.getDeclaredMethod("getRoleForEmail", String.class);
            method.setAccessible(true);
            return (RoleEntity) method.invoke(userService, email);
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


    public static Object invokeAuthenticated(UserService userService) throws Throwable {
        try {
            Method method = UserService.class.getDeclaredMethod("authenticated");
            method.setAccessible(true);
            return method.invoke(userService);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

      **/

}