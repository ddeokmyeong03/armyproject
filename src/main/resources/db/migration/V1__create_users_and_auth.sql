SET time_zone = '+09:00';

CREATE TABLE users (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    email               VARCHAR(100)    NOT NULL,
    password_hash       VARCHAR(255)    NOT NULL,
    nickname            VARCHAR(50)     NOT NULL,
    discharge_date      DATE            NOT NULL,
    daily_minutes       INT             NOT NULL DEFAULT 60,
    goal_priorities     JSON            NOT NULL,
    current_streak      INT             NOT NULL DEFAULT 0,
    max_streak          INT             NOT NULL DEFAULT 0,
    last_completed_date DATE            NULL,
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refresh_tokens (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    token_hash  VARCHAR(255) NOT NULL,
    expires_at  DATETIME(6)  NOT NULL,
    revoked     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uq_rt_token_hash (token_hash),
    KEY idx_rt_user_id (user_id),
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
