CREATE TABLE records (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL,
    title         VARCHAR(200) NOT NULL,
    content       TEXT         NOT NULL,
    activity_date DATE         NOT NULL,
    category      ENUM('CERT','ENGLISH','FITNESS','READING','PORTFOLIO','ETC')
                               NOT NULL DEFAULT 'ETC',
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                               ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_records_user_date (user_id, activity_date DESC),
    CONSTRAINT fk_records_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tags (
    id      BIGINT      NOT NULL AUTO_INCREMENT,
    user_id BIGINT      NOT NULL,
    name    VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tags_user_name (user_id, name),
    CONSTRAINT fk_tags_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE record_tags (
    record_id BIGINT NOT NULL,
    tag_id    BIGINT NOT NULL,
    PRIMARY KEY (record_id, tag_id),
    CONSTRAINT fk_rt_record FOREIGN KEY (record_id) REFERENCES records (id) ON DELETE CASCADE,
    CONSTRAINT fk_rt_tag    FOREIGN KEY (tag_id)    REFERENCES tags (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE stars (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    record_id      BIGINT      NOT NULL,
    situation      TEXT        NOT NULL,
    task_desc      TEXT        NOT NULL,
    action         TEXT        NOT NULL,
    result         TEXT        NOT NULL,
    generated_text TEXT        NOT NULL,
    created_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                               ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uq_stars_record (record_id),
    CONSTRAINT fk_stars_record FOREIGN KEY (record_id) REFERENCES records (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
