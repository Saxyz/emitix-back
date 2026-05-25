-- ============================================================
-- V8: Crear tabla password_reset_tokens (recuperación contraseña)
--     DDL v3.2: TIMESTAMPTZ en lugar de TIMESTAMP
-- ============================================================

-- Tabla para tokens de recuperación de contraseña (OTP)
CREATE TABLE password_reset_tokens
(
    id         UUID         NOT NULL DEFAULT gen_random_uuid(),
    user_email VARCHAR(100) NOT NULL,
    otp_hash   VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ  NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id)
);

CREATE INDEX idx_prt_email      ON password_reset_tokens (user_email);
CREATE INDEX idx_prt_expires_at ON password_reset_tokens (expires_at);
