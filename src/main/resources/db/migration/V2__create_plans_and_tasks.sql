CREATE TABLE weekly_plans (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,
    week_start  DATE        NOT NULL COMMENT 'Always Monday',
    memo        TEXT        NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                            ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uq_wp_user_week (user_id, week_start),
    CONSTRAINT fk_wp_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE weekly_goals (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    plan_id      BIGINT       NOT NULL,
    title        VARCHAR(200) NOT NULL,
    category     ENUM('CERT','ENGLISH','FITNESS','READING','PORTFOLIO','ETC')
                              NOT NULL DEFAULT 'ETC',
    target_count INT          NOT NULL DEFAULT 1,
    done_count   INT          NOT NULL DEFAULT 0,
    sort_order   INT          NOT NULL DEFAULT 0,
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_wg_plan (plan_id),
    CONSTRAINT fk_wg_plan FOREIGN KEY (plan_id) REFERENCES weekly_plans (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tasks (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    plan_id        BIGINT       NOT NULL,
    goal_id        BIGINT       NULL,
    title          VARCHAR(200) NOT NULL,
    scheduled_date DATE         NOT NULL,
    done           TINYINT(1)   NOT NULL DEFAULT 0,
    done_at        DATETIME(6)  NULL,
    sort_order     INT          NOT NULL DEFAULT 0,
    created_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_tasks_plan_date (plan_id, scheduled_date),
    CONSTRAINT fk_tasks_plan FOREIGN KEY (plan_id) REFERENCES weekly_plans (id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_goal FOREIGN KEY (goal_id) REFERENCES weekly_goals (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
