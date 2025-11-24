CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_username ON refresh_tokens(username);
CREATE INDEX idx_refresh_tokens_expiry_date ON refresh_tokens(expiry_date);