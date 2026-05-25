CREATE TYPE user_role AS ENUM ('admin', 'accountant', 'viewer');

CREATE TABLE users
(
    id         UUID         NOT NULL DEFAULT gen_random_uuid(),
    username   VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    full_name  VARCHAR(150) NOT NULL,
    phone      VARCHAR(20),
    role       user_role    NOT NULL DEFAULT 'viewer',
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_username ON users (username);
