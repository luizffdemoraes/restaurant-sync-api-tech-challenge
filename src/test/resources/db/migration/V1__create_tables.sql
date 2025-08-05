CREATE TABLE roles
(
    id        SERIAL PRIMARY KEY,
    authority VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(255)        NOT NULL,
    email            VARCHAR(255) UNIQUE NOT NULL,
    login            VARCHAR(255) UNIQUE NOT NULL,
    password         VARCHAR(255)        NOT NULL,
    last_update_date TIMESTAMP           NOT NULL,
    street           VARCHAR(255)        NOT NULL,
    number           BIGINT              NOT NULL,
    city             VARCHAR(255)        NOT NULL,
    state            VARCHAR(255)        NOT NULL,
    zip_code         VARCHAR(255)        NOT NULL
);

CREATE TABLE user_role
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE restaurants
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    street        VARCHAR(255) NOT NULL,
    number        BIGINT       NOT NULL,
    city          VARCHAR(255) NOT NULL,
    state         VARCHAR(255) NOT NULL,
    zip_code      VARCHAR(255) NOT NULL,
    cuisine_type  VARCHAR(255) NOT NULL,
    opening_hours VARCHAR(255) NOT NULL,
    owner_id      BIGINT       NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE menu
(
    id                      SERIAL PRIMARY KEY,
    name                    VARCHAR(255)   NOT NULL,
    description             TEXT           NOT NULL,
    price                   DOUBLE PRECISION NOT NULL,
    available_in_restaurant BOOLEAN        NOT NULL,
    photo_path              VARCHAR(255),
    restaurant_id           INTEGER        NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
);