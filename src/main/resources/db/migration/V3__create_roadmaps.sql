CREATE TABLE roadmaps (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    title          VARCHAR(200) NOT NULL,
    description    TEXT         NULL,
    category       ENUM('CERT','ENGLISH','FITNESS','READING','PORTFOLIO','ETC') NOT NULL,
    duration_weeks INT          NOT NULL,
    is_official    TINYINT(1)   NOT NULL DEFAULT 1,
    created_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE roadmap_weeks (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    roadmap_id  BIGINT       NOT NULL,
    week_number INT          NOT NULL,
    goal_title  VARCHAR(200) NOT NULL,
    task_titles JSON         NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_rw_roadmap_week (roadmap_id, week_number),
    CONSTRAINT fk_rw_roadmap FOREIGN KEY (roadmap_id) REFERENCES roadmaps (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roadmaps (
    id           BIGINT    NOT NULL AUTO_INCREMENT,
    user_id      BIGINT    NOT NULL,
    roadmap_id   BIGINT    NOT NULL,
    status       ENUM('ACTIVE','PAUSED','DONE') NOT NULL DEFAULT 'ACTIVE',
    started_at   DATE      NOT NULL,
    current_week INT       NOT NULL DEFAULT 1,
    created_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                            ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_ur_user_status (user_id, status),
    CONSTRAINT fk_ur_user    FOREIGN KEY (user_id)    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_roadmap FOREIGN KEY (roadmap_id) REFERENCES roadmaps (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
