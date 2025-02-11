CREATE TABLE refresh_tokens (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    ip_address VARCHAR(45),  -- IPv4 и IPv6 адреса
    user_agent TEXT,         -- Информация о браузере/устройстве
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);
