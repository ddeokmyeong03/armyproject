# Self-Development Manager — MVP Design Document

> Korean military conscript soldiers' self-development tracking web application.
> Stack: Spring Boot 3.2 / Java 21 / MySQL 8 / JWT / Docker Compose

---

## A. System Design Overview

### Component Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        Docker Compose Host                       │
│                                                                  │
│  ┌──────────────────────────────┐   ┌──────────────────────────┐│
│  │        app (Spring Boot)     │   │      db (MySQL 8)        ││
│  │                              │   │                          ││
│  │  ┌────────────────────────┐  │   │  ┌────────────────────┐  ││
│  │  │  SecurityFilterChain   │  │   │  │  users             │  ││
│  │  │  JwtAuthFilter         │  │   │  │  refresh_tokens    │  ││
│  │  └──────────┬─────────────┘  │   │  │  weekly_plans      │  ││
│  │             │                │   │  │  weekly_goals      │  ││
│  │  ┌──────────▼─────────────┐  │   │  │  tasks             │  ││
│  │  │  Controller Layer      │  │   │  │  roadmaps          │  ││
│  │  │  /api/v1/*             │  │   │  │  user_roadmaps     │  ││
│  │  └──────────┬─────────────┘  │   │  │  records           │  ││
│  │             │                │   │  │  record_tags       │  ││
│  │  ┌──────────▼─────────────┐  │   │  │  tags              │  ││
│  │  │  Service Layer         │  │   │  │  stars             │  ││
│  │  │  (domain logic)        │  │   │  └────────────────────┘  ││
│  │  └──────────┬─────────────┘  │   │                          ││
│  │             │                │   │  port: 3306 (internal)   ││
│  │  ┌──────────▼─────────────┐  │   └──────────────────────────┘│
│  │  │  Repository Layer      │◄─┼──────────────────────────────┤│
│  │  │  (Spring Data JPA)     │  │        JDBC / HikariCP        ││
│  │  └────────────────────────┘  │                               ││
│  │                              │                               ││
│  │  port: 8080                  │                               ││
│  └──────────────────────────────┘                               │
│                    ▲                                             │
└────────────────────┼─────────────────────────────────────────── ┘
                     │ HTTP REST /api/v1
              ┌──────┴───────┐
              │  React SPA   │  (future — out of MVP scope)
              │  (browser)   │
              └──────────────┘
```

### Data Flow: Login

```
Client                    JwtAuthFilter         AuthController        AuthService          DB
  │                            │                     │                    │                │
  │── POST /api/v1/auth/login ─►│                     │                    │                │
  │   {email, password}        │── permitAll ──────►│                    │                │
  │                            │                     │── authenticate() ─►│                │
  │                            │                     │                    │── findByEmail ─►│
  │                            │                     │                    │◄── User entity ─│
  │                            │                     │                    │── BCrypt verify │
  │                            │                     │                    │── generate AT  │
  │                            │                     │                    │── generate RT  │
  │                            │                     │                    │── save RT ─────►│
  │                            │                     │◄── TokenPair ──────│                │
  │◄── 200 {accessToken,       │                     │                    │                │
  │         refreshToken} ─────│                     │                    │                │
```

### Data Flow: Create Weekly Plan

```
Client                JwtAuthFilter        PlanController       PlanService            DB
  │                        │                    │                   │                   │
  │─ POST /api/v1/plans ──►│                    │                   │                   │
  │  Authorization: Bearer │                    │                   │                   │
  │                        │── validate JWT ───►│                   │                   │
  │                        │   set SecurityCtx  │                   │                   │
  │                        │                    │── createPlan() ──►│                   │
  │                        │                    │                   │── check duplicate │
  │                        │                    │                   │   (week_start) ──►│
  │                        │                    │                   │── save WeeklyPlan►│
  │                        │                    │                   │── create default  │
  │                        │                    │                   │   WeeklyGoals ───►│
  │                        │                    │◄── PlanResponse ──│                   │
  │◄── 201 {plan + goals} ─│                    │                   │                   │
```

### Data Flow: Complete Task

```
Client              JwtAuthFilter     TaskController      TaskService          StreakService       DB
  │                      │                 │                  │                     │              │
  │─ PATCH /tasks/{id} ─►│                 │                  │                     │              │
  │  {completed: true}   │── validate ────►│                  │                     │              │
  │                      │                 │── toggleTask() ──►│                     │              │
  │                      │                 │                  │── find Task ────────────────────────►│
  │                      │                 │                  │── update done=true ─────────────────►│
  │                      │                 │                  │── updateStreak() ──►│              │
  │                      │                 │                  │                     │── load user ─►│
  │                      │                 │                  │                     │── apply rules│
  │                      │                 │                  │                     │── save user ─►│
  │                      │                 │◄── TaskResponse ──│                     │              │
  │◄── 200 {task, streak}│                 │                  │                     │              │
```

### Data Flow: Create Record → STAR

```
Client           JwtAuthFilter    RecordController    RecordService       StarService          DB
  │                   │                │                  │                   │                │
  │─ POST /records ──►│                │                  │                   │                │
  │  {title,content,  │── validate ───►│                  │                   │                │
  │   tagIds}         │                │── createRecord() ►│                   │                │
  │                   │                │                  │── save Record ────────────────────►│
  │                   │                │                  │── link Tags ──────────────────────►│
  │◄── 201 Record ────│                │                  │                   │                │
  │                   │                │                  │                   │                │
  │─ POST /records    │                │                  │                   │                │
  │  /{id}/star ─────►│── validate ───►│                  │                   │                │
  │                   │                │── createStar() ──►│                   │                │
  │                   │                │                  │── generateText() ─►│                │
  │                   │                │                  │   (template-based) │                │
  │                   │                │                  │── save Star ──────────────────────►│
  │◄── 201 Star ──────│                │                  │                   │                │
```

---

## B. Database Design

### ERD (Text Form)

```
users (1) ──< refresh_tokens
users (1) ──< weekly_plans
users (1) ──< user_roadmaps
users (1) ──< records

weekly_plans (1) ──< weekly_goals
weekly_plans (1) ──< tasks

tasks (N) >── weekly_goals (1)   [nullable: task may belong to a goal]

roadmaps (1) ──< roadmap_weeks
roadmaps (1) ──< user_roadmaps

records (1) ──  stars
records (N) >──< tags  [via record_tags]
```

### Tables & Columns

#### users
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| email | VARCHAR(100) | UNIQUE, NOT NULL |
| password_hash | VARCHAR(255) | NOT NULL |
| nickname | VARCHAR(50) | NOT NULL |
| discharge_date | DATE | NOT NULL |
| daily_minutes | INT | NOT NULL DEFAULT 60 |
| goal_priorities | JSON | NOT NULL (array of enum strings) |
| current_streak | INT | NOT NULL DEFAULT 0 |
| max_streak | INT | NOT NULL DEFAULT 0 |
| last_completed_date | DATE | NULL |
| created_at | DATETIME(6) | NOT NULL DEFAULT CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) |

#### refresh_tokens
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id, NOT NULL |
| token_hash | VARCHAR(255) | UNIQUE, NOT NULL |
| expires_at | DATETIME(6) | NOT NULL |
| revoked | TINYINT(1) | NOT NULL DEFAULT 0 |
| created_at | DATETIME(6) | NOT NULL DEFAULT CURRENT_TIMESTAMP(6) |

#### weekly_plans
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id, NOT NULL |
| week_start | DATE | NOT NULL (always Monday) |
| memo | TEXT | NULL |
| created_at | DATETIME(6) | NOT NULL DEFAULT CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) |
| UNIQUE | (user_id, week_start) | |

#### weekly_goals
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| plan_id | BIGINT | FK → weekly_plans.id, NOT NULL |
| title | VARCHAR(200) | NOT NULL |
| category | ENUM('CERT','ENGLISH','FITNESS','READING','PORTFOLIO','ETC') | NOT NULL DEFAULT 'ETC' |
| target_count | INT | NOT NULL DEFAULT 1 |
| done_count | INT | NOT NULL DEFAULT 0 |
| sort_order | INT | NOT NULL DEFAULT 0 |
| created_at | DATETIME(6) | NOT NULL DEFAULT CURRENT_TIMESTAMP(6) |

#### tasks
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| plan_id | BIGINT | FK → weekly_plans.id, NOT NULL |
| goal_id | BIGINT | FK → weekly_goals.id, NULL |
| title | VARCHAR(200) | NOT NULL |
| scheduled_date | DATE | NOT NULL |
| done | TINYINT(1) | NOT NULL DEFAULT 0 |
| done_at | DATETIME(6) | NULL |
| sort_order | INT | NOT NULL DEFAULT 0 |
| created_at | DATETIME(6) | NOT NULL DEFAULT CURRENT_TIMESTAMP(6) |

#### roadmaps
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| title | VARCHAR(200) | NOT NULL |
| description | TEXT | NULL |
| category | ENUM('CERT','ENGLISH','FITNESS','READING','PORTFOLIO','ETC') | NOT NULL |
| duration_weeks | INT | NOT NULL |
| is_official | TINYINT(1) | NOT NULL DEFAULT 1 |
| created_at | DATETIME(6) | NOT NULL DEFAULT CURRENT_TIMESTAMP(6) |

#### roadmap_weeks
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| roadmap_id | BIGINT | FK → roadmaps.id, NOT NULL |
| week_number | INT | NOT NULL (1-based) |
| goal_title | VARCHAR(200) | NOT NULL |
| task_titles | JSON | NOT NULL (array of strings) |
| UNIQUE | (roadmap_id, week_number) | |

#### user_roadmaps
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id, NOT NULL |
| roadmap_id | BIGINT | FK → roadmaps.id, NOT NULL |
| status | ENUM('ACTIVE','PAUSED','DONE') | NOT NULL DEFAULT 'ACTIVE' |
| started_at | DATE | NOT NULL |
| current_week | INT | NOT NULL DEFAULT 1 |
| created_at | DATETIME(6) | NOT NULL DEFAULT CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) |

#### records
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id, NOT NULL |
| title | VARCHAR(200) | NOT NULL |
| content | TEXT | NOT NULL |
| activity_date | DATE | NOT NULL |
| category | ENUM('CERT','ENGLISH','FITNESS','READING','PORTFOLIO','ETC') | NOT NULL DEFAULT 'ETC' |
| created_at | DATETIME(6) | NOT NULL DEFAULT CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) |

#### tags
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id, NOT NULL |
| name | VARCHAR(50) | NOT NULL |
| UNIQUE | (user_id, name) | |

#### record_tags
| Column | Type | Constraints |
|---|---|---|
| record_id | BIGINT | FK → records.id, NOT NULL |
| tag_id | BIGINT | FK → tags.id, NOT NULL |
| PK | (record_id, tag_id) | |

#### stars
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT |
| record_id | BIGINT | FK → records.id, UNIQUE, NOT NULL |
| situation | TEXT | NOT NULL |
| task_desc | TEXT | NOT NULL |
| action | TEXT | NOT NULL |
| result | TEXT | NOT NULL |
| generated_text | TEXT | NOT NULL |
| created_at | DATETIME(6) | NOT NULL DEFAULT CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) |

### Indexing Strategy (Top 8 Queries)

| # | Query | Index |
|---|---|---|
| 1 | Dashboard: load today's tasks for user | `idx_tasks_plan_date` on `tasks(plan_id, scheduled_date)` |
| 2 | Dashboard / Planner: find plan by user + week | `UNIQUE(user_id, week_start)` on `weekly_plans` |
| 3 | Streak update: find user by id (write path) | PK on `users.id` (default) |
| 4 | Records list by user ordered by date | `idx_records_user_date` on `records(user_id, activity_date DESC)` |
| 5 | Tags lookup by user | `UNIQUE(user_id, name)` on `tags` |
| 6 | Refresh token validation | `UNIQUE(token_hash)` on `refresh_tokens` |
| 7 | Active roadmap for user | `idx_user_roadmaps_user_status` on `user_roadmaps(user_id, status)` |
| 8 | Weekly goals by plan | `idx_weekly_goals_plan` on `weekly_goals(plan_id)` |

### MySQL DDL

```sql
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
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    plan_id     BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    category    ENUM('CERT','ENGLISH','FITNESS','READING','PORTFOLIO','ETC')
                             NOT NULL DEFAULT 'ETC',
    target_count INT         NOT NULL DEFAULT 1,
    done_count   INT         NOT NULL DEFAULT 0,
    sort_order   INT         NOT NULL DEFAULT 0,
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
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
```

---

## C. API Design

### Endpoints List

#### Auth
| Method | Path | Description |
|---|---|---|
| POST | /api/v1/auth/register | 회원가입 (온보딩 포함) |
| POST | /api/v1/auth/login | 로그인 |
| POST | /api/v1/auth/refresh | 액세스 토큰 재발급 |
| POST | /api/v1/auth/logout | 로그아웃 (refresh token 폐기) |

#### Me (현재 사용자)
| Method | Path | Description |
|---|---|---|
| GET | /api/v1/me | 내 프로필 조회 |
| PATCH | /api/v1/me | 프로필 수정 (nickname, daily_minutes, goal_priorities, discharge_date) |
| GET | /api/v1/me/streak | 스트릭 정보 조회 |

#### Dashboard
| Method | Path | Description |
|---|---|---|
| GET | /api/v1/dashboard | 대시보드 전체 조회 (D-day, 주간완료율, 스트릭, 오늘 tasks, 주간 goals, 최근 records) |

#### Plans (Weekly Plans)
| Method | Path | Description |
|---|---|---|
| GET | /api/v1/plans | 주간계획 목록 (query: year, week 또는 default=이번주) |
| POST | /api/v1/plans | 주간계획 생성 |
| GET | /api/v1/plans/{planId} | 주간계획 상세 (goals + tasks 포함) |
| PATCH | /api/v1/plans/{planId} | 주간계획 수정 (memo) |
| DELETE | /api/v1/plans/{planId} | 주간계획 삭제 |

#### Weekly Goals
| Method | Path | Description |
|---|---|---|
| GET | /api/v1/plans/{planId}/goals | 목표 목록 |
| POST | /api/v1/plans/{planId}/goals | 목표 생성 |
| PATCH | /api/v1/plans/{planId}/goals/{goalId} | 목표 수정 |
| DELETE | /api/v1/plans/{planId}/goals/{goalId} | 목표 삭제 |

#### Tasks
| Method | Path | Description |
|---|---|---|
| GET | /api/v1/plans/{planId}/tasks | 태스크 목록 (query: date 필터 선택) |
| POST | /api/v1/plans/{planId}/tasks | 태스크 생성 |
| PATCH | /api/v1/plans/{planId}/tasks/{taskId} | 태스크 수정 |
| DELETE | /api/v1/plans/{planId}/tasks/{taskId} | 태스크 삭제 |
| PATCH | /api/v1/plans/{planId}/tasks/{taskId}/toggle | 완료 토글 (스트릭 연동) |

#### Roadmaps
| Method | Path | Description |
|---|---|---|
| GET | /api/v1/roadmaps | 공식 로드맵 목록 (query: category) |
| GET | /api/v1/roadmaps/{roadmapId} | 로드맵 상세 (weeks 포함) |
| GET | /api/v1/me/roadmaps | 내 로드맵 목록 |
| POST | /api/v1/me/roadmaps | 로드맵 적용 (→ 이번 주 goals+tasks 자동 생성) |
| PATCH | /api/v1/me/roadmaps/{userRoadmapId} | 상태 변경 (PAUSED/DONE) |

#### Records
| Method | Path | Description |
|---|---|---|
| GET | /api/v1/records | 기록 목록 (query: page, size, category, tag) |
| POST | /api/v1/records | 기록 생성 |
| GET | /api/v1/records/{recordId} | 기록 상세 |
| PATCH | /api/v1/records/{recordId} | 기록 수정 |
| DELETE | /api/v1/records/{recordId} | 기록 삭제 |
| POST | /api/v1/records/{recordId}/star | STAR 생성 (템플릿 기반) |
| GET | /api/v1/records/{recordId}/star | STAR 조회 |
| PATCH | /api/v1/records/{recordId}/star | STAR 수정 |

#### Tags
| Method | Path | Description |
|---|---|---|
| GET | /api/v1/tags | 내 태그 목록 |
| POST | /api/v1/tags | 태그 생성 |
| DELETE | /api/v1/tags/{tagId} | 태그 삭제 |

---

### Request / Response JSON Examples (10개)

#### 1. POST /api/v1/auth/register
```json
// Request
{
  "email": "kim@army.mil",
  "password": "Soldier1!",
  "nickname": "김병사",
  "discharge_date": "2026-06-30",
  "daily_minutes": 90,
  "goal_priorities": ["CERT", "ENGLISH", "FITNESS"]
}

// Response 201
{
  "data": {
    "access_token": "eyJhbGci...",
    "refresh_token": "dGhpcyBp...",
    "token_type": "Bearer",
    "expires_in": 900
  }
}
```

#### 2. POST /api/v1/auth/login
```json
// Request
{
  "email": "kim@army.mil",
  "password": "Soldier1!"
}

// Response 200
{
  "data": {
    "access_token": "eyJhbGci...",
    "refresh_token": "dGhpcyBp...",
    "token_type": "Bearer",
    "expires_in": 900
  }
}
```

#### 3. GET /api/v1/dashboard
```json
// Response 200
{
  "data": {
    "d_day": 120,
    "discharge_date": "2026-06-30",
    "streak": {
      "current": 5,
      "max": 14
    },
    "weekly_completion_rate": 0.67,
    "today_tasks": [
      { "id": 1, "title": "영어 단어 50개", "done": false, "scheduled_date": "2026-03-02" },
      { "id": 2, "title": "팔굽혀펴기 30개", "done": true, "scheduled_date": "2026-03-02" }
    ],
    "weekly_goals": [
      { "id": 1, "title": "TOEIC 파트5 완성", "category": "ENGLISH", "target_count": 3, "done_count": 2 },
      { "id": 2, "title": "정보처리기사 1회독", "category": "CERT", "target_count": 5, "done_count": 3 }
    ],
    "recent_records": [
      { "id": 10, "title": "영어 공부 후기", "activity_date": "2026-03-01", "category": "ENGLISH" }
    ]
  }
}
```

#### 4. POST /api/v1/plans
```json
// Request
{
  "week_start": "2026-03-02",
  "memo": "이번 주 집중 주간"
}

// Response 201
{
  "data": {
    "id": 5,
    "week_start": "2026-03-02",
    "week_end": "2026-03-08",
    "memo": "이번 주 집중 주간",
    "goals": [],
    "created_at": "2026-03-02T09:00:00"
  }
}
```

#### 5. POST /api/v1/plans/{planId}/goals
```json
// Request
{
  "title": "TOEIC 파트5 완성",
  "category": "ENGLISH",
  "target_count": 3
}

// Response 201
{
  "data": {
    "id": 12,
    "plan_id": 5,
    "title": "TOEIC 파트5 완성",
    "category": "ENGLISH",
    "target_count": 3,
    "done_count": 0,
    "sort_order": 0
  }
}
```

#### 6. POST /api/v1/plans/{planId}/tasks
```json
// Request
{
  "title": "영어 단어 50개",
  "scheduled_date": "2026-03-02",
  "goal_id": 12
}

// Response 201
{
  "data": {
    "id": 30,
    "plan_id": 5,
    "goal_id": 12,
    "title": "영어 단어 50개",
    "scheduled_date": "2026-03-02",
    "done": false,
    "done_at": null
  }
}
```

#### 7. PATCH /api/v1/plans/{planId}/tasks/{taskId}/toggle
```json
// Request
{
  "done": true
}

// Response 200
{
  "data": {
    "task": {
      "id": 30,
      "title": "영어 단어 50개",
      "done": true,
      "done_at": "2026-03-02T14:30:00"
    },
    "streak": {
      "current": 6,
      "max": 14
    }
  }
}
```

#### 8. POST /api/v1/records
```json
// Request
{
  "title": "정보처리기사 1회독 완료",
  "content": "오늘 정처기 필기 이론 1회독을 마쳤다. 특히 데이터베이스 파트가 어려웠다.",
  "activity_date": "2026-03-02",
  "category": "CERT",
  "tag_ids": [1, 3]
}

// Response 201
{
  "data": {
    "id": 10,
    "title": "정보처리기사 1회독 완료",
    "content": "오늘 정처기 필기 이론 1회독을 마쳤다. 특히 데이터베이스 파트가 어려웠다.",
    "activity_date": "2026-03-02",
    "category": "CERT",
    "tags": [
      { "id": 1, "name": "자격증" },
      { "id": 3, "name": "정처기" }
    ],
    "has_star": false
  }
}
```

#### 9. POST /api/v1/records/{recordId}/star
```json
// Request
{
  "situation": "정보처리기사 준비를 시작한 첫 날",
  "task_desc": "1회독 완료 및 약점 파악",
  "action": "교재 전체를 읽고 DB 파트는 따로 노트 정리",
  "result": "전체 흐름 파악 완료, DB 추가 학습 계획 수립"
}

// Response 201
{
  "data": {
    "id": 7,
    "record_id": 10,
    "situation": "정보처리기사 준비를 시작한 첫 날",
    "task_desc": "1회독 완료 및 약점 파악",
    "action": "교재 전체를 읽고 DB 파트는 따로 노트 정리",
    "result": "전체 흐름 파악 완료, DB 추가 학습 계획 수립",
    "generated_text": "정보처리기사 준비를 시작한 첫 날이라는 상황에서, 1회독 완료 및 약점 파악이라는 과제를 맡아, 교재 전체를 읽고 DB 파트는 따로 노트 정리하는 행동을 취했습니다. 그 결과, 전체 흐름 파악 완료, DB 추가 학습 계획 수립이라는 성과를 이루었습니다.",
    "created_at": "2026-03-02T15:00:00"
  }
}
```

#### 10. POST /api/v1/me/roadmaps
```json
// Request
{
  "roadmap_id": 2,
  "started_at": "2026-03-02",
  "seed_current_week": true
}

// Response 201
{
  "data": {
    "id": 3,
    "roadmap_id": 2,
    "roadmap_title": "TOEIC 900 달성 로드맵",
    "status": "ACTIVE",
    "started_at": "2026-03-02",
    "current_week": 1,
    "total_weeks": 12,
    "seeded_goal": {
      "id": 15,
      "title": "TOEIC 파트5 어휘/문법 기초",
      "category": "ENGLISH",
      "target_count": 5
    },
    "seeded_tasks_count": 5
  }
}
```

---

### Error Model (Standard Response Format)

모든 응답은 다음 구조를 따릅니다:

```json
// 성공
{
  "data": { ... }
}

// 성공 (목록)
{
  "data": [ ... ],
  "meta": {
    "page": 0,
    "size": 20,
    "total_elements": 42,
    "total_pages": 3
  }
}

// 에러
{
  "error": {
    "code": "PLAN_ALREADY_EXISTS",
    "message": "해당 주에 이미 플랜이 존재합니다.",
    "status": 409,
    "timestamp": "2026-03-02T09:00:00",
    "path": "/api/v1/plans"
  }
}
```

#### 에러 코드 목록

| HTTP | Code | 상황 |
|---|---|---|
| 400 | VALIDATION_ERROR | Bean Validation 실패 (필드별 상세 포함) |
| 401 | UNAUTHORIZED | 토큰 없음 / 만료 |
| 401 | TOKEN_EXPIRED | 액세스 토큰 만료 |
| 401 | REFRESH_TOKEN_INVALID | 리프레시 토큰 무효 / 폐기 |
| 403 | FORBIDDEN | 다른 사용자 리소스 접근 |
| 404 | RESOURCE_NOT_FOUND | 엔티티 없음 |
| 409 | PLAN_ALREADY_EXISTS | 동일 주 플랜 중복 |
| 409 | STAR_ALREADY_EXISTS | 해당 기록에 STAR 중복 |
| 409 | EMAIL_ALREADY_EXISTS | 이메일 중복 |
| 500 | INTERNAL_ERROR | 서버 내부 오류 |

---

## D. Backend Project Plan

### Package / Module Structure

```
com.armydev.selfdev
├── SelfDevApplication.java
│
├── config/
│   ├── SecurityConfig.java          # SecurityFilterChain, CORS
│   ├── JwtConfig.java               # JWT secret, expiry properties
│   ├── JacksonConfig.java           # LocalDate/Time serialization
│   └── WebMvcConfig.java            # (필요시) CORS 추가 설정
│
├── security/
│   ├── JwtTokenProvider.java        # 토큰 생성/검증
│   ├── JwtAuthenticationFilter.java # OncePerRequestFilter
│   ├── UserDetailsServiceImpl.java  # loadUserByUsername
│   └── SecurityUser.java            # UserDetails wrapper
│
├── domain/
│   ├── user/
│   │   ├── User.java                # @Entity
│   │   ├── UserRepository.java
│   │   ├── UserService.java
│   │   └── UserController.java      # /api/v1/me
│   │
│   ├── auth/
│   │   ├── RefreshToken.java        # @Entity
│   │   ├── RefreshTokenRepository.java
│   │   ├── AuthService.java
│   │   ├── AuthController.java      # /api/v1/auth
│   │   └── dto/
│   │       ├── RegisterRequest.java
│   │       ├── LoginRequest.java
│   │       └── TokenResponse.java
│   │
│   ├── plan/
│   │   ├── WeeklyPlan.java          # @Entity
│   │   ├── WeeklyPlanRepository.java
│   │   ├── WeeklyPlanService.java
│   │   ├── WeeklyPlanController.java # /api/v1/plans
│   │   └── dto/
│   │
│   ├── goal/
│   │   ├── WeeklyGoal.java          # @Entity
│   │   ├── WeeklyGoalRepository.java
│   │   ├── WeeklyGoalService.java
│   │   └── WeeklyGoalController.java # /api/v1/plans/{id}/goals
│   │
│   ├── task/
│   │   ├── Task.java                # @Entity
│   │   ├── TaskRepository.java
│   │   ├── TaskService.java
│   │   ├── TaskController.java      # /api/v1/plans/{id}/tasks
│   │   └── StreakService.java       # 스트릭 업데이트 로직
│   │
│   ├── roadmap/
│   │   ├── Roadmap.java             # @Entity
│   │   ├── RoadmapWeek.java         # @Entity
│   │   ├── UserRoadmap.java         # @Entity
│   │   ├── RoadmapRepository.java
│   │   ├── RoadmapWeekRepository.java
│   │   ├── UserRoadmapRepository.java
│   │   ├── RoadmapService.java
│   │   └── RoadmapController.java   # /api/v1/roadmaps, /api/v1/me/roadmaps
│   │
│   ├── record/
│   │   ├── Record.java              # @Entity
│   │   ├── Tag.java                 # @Entity
│   │   ├── RecordTag.java           # @Entity (join)
│   │   ├── RecordRepository.java
│   │   ├── TagRepository.java
│   │   ├── RecordService.java
│   │   ├── RecordController.java    # /api/v1/records
│   │   └── TagController.java       # /api/v1/tags
│   │
│   └── star/
│       ├── Star.java                # @Entity
│       ├── StarRepository.java
│       ├── StarService.java         # generateText() 템플릿 로직
│       └── dto/
│
├── dashboard/
│   ├── DashboardService.java        # 여러 도메인 집계
│   └── DashboardController.java     # /api/v1/dashboard
│
├── common/
│   ├── exception/
│   │   ├── BusinessException.java
│   │   ├── ErrorCode.java           # enum
│   │   └── GlobalExceptionHandler.java # @RestControllerAdvice
│   ├── response/
│   │   ├── ApiResponse.java         # 표준 응답 래퍼
│   │   └── PageResponse.java
│   └── util/
│       └── DateUtil.java            # getMondayOfWeek() 등
```

### Security Setup

#### JWT Filter Chain
```
Request
  → JwtAuthenticationFilter (OncePerRequestFilter)
      → Header: "Authorization: Bearer {token}"
      → JwtTokenProvider.validateToken()
      → SecurityContextHolder.setAuthentication()
  → SecurityFilterChain
      → permitAll: POST /auth/register, POST /auth/login, POST /auth/refresh
      → authenticated: 나머지 모든 /api/v1/**
```

#### Refresh Token Strategy
- Access Token: 15분, stateless (JWT claims에 userId, email 포함)
- Refresh Token: 7일, DB 저장 (`refresh_tokens` 테이블)
- 갱신 시: 기존 RT 폐기(`revoked=1`) → 새 RT 발급 (Rotation)
- 로그아웃 시: RT 즉시 폐기

#### BCrypt
```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

### Validation Rules (Bean Validation)

```java
// RegisterRequest
@Email String email;
@NotBlank @Size(min=8, max=100) String password;  // 영문+숫자+특수문자 1개 이상 (커스텀)
@NotBlank @Size(max=50) String nickname;
@NotNull @Future LocalDate discharge_date;
@Min(10) @Max(480) Integer daily_minutes;
@NotEmpty @Size(max=6) List<@NotNull GoalCategory> goal_priorities;

// TaskRequest
@NotBlank @Size(max=200) String title;
@NotNull LocalDate scheduled_date;

// RecordRequest
@NotBlank @Size(max=200) String title;
@NotBlank @Size(max=5000) String content;
@NotNull @PastOrPresent LocalDate activity_date;
```

### Streak Update Logic

```java
// StreakService.updateStreak(User user, LocalDate today)
LocalDate last = user.getLastCompletedDate();

if (last == null) {
    user.setCurrentStreak(1);
} else if (last.equals(today)) {
    // 오늘 이미 처리됨 → 변경 없음
    return;
} else if (last.equals(today.minusDays(1))) {
    user.setCurrentStreak(user.getCurrentStreak() + 1);
} else {
    user.setCurrentStreak(1);
}

user.setLastCompletedDate(today);
if (user.getCurrentStreak() > user.getMaxStreak()) {
    user.setMaxStreak(user.getCurrentStreak());
}
```

### Weekly Completion Rate Logic

```java
// DashboardService.getWeeklyCompletionRate(Long userId, LocalDate weekStart)
List<WeeklyGoal> goals = goalRepo.findByPlanUserIdAndPlanWeekStart(userId, weekStart);

if (goals.isEmpty()) {
    // fallback: task 기반
    long total = taskRepo.countByPlanUserIdAndScheduledDateBetween(userId, weekStart, weekEnd);
    long done  = taskRepo.countByPlanUserIdAndScheduledDateBetweenAndDoneTrue(...);
    return total == 0 ? 0.0 : Math.min(1.0, (double) done / total);
}

double rate = goals.stream()
    .mapToDouble(g -> Math.min(1.0, (double) g.getDoneCount() / g.getTargetCount()))
    .average()
    .orElse(0.0);
return rate;
```

### Migration Approach (Flyway)

```
src/main/resources/db/migration/
├── V1__create_users_and_auth.sql
├── V2__create_plans_and_tasks.sql
├── V3__create_roadmaps.sql
├── V4__create_records_and_stars.sql
└── V5__seed_roadmap_templates.sql
```

`application.yml`:
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: false
    validate-on-migrate: true
```

### Logging & Exception Handling

```java
// GlobalExceptionHandler (@RestControllerAdvice)
@ExceptionHandler(BusinessException.class)
ResponseEntity<ApiResponse<?>> handleBusiness(BusinessException e) { ... }

@ExceptionHandler(MethodArgumentNotValidException.class)
ResponseEntity<ApiResponse<?>> handleValidation(...) { ... }

@ExceptionHandler(Exception.class)
ResponseEntity<ApiResponse<?>> handleUnexpected(...) { ... }
```

로깅: `@Slf4j` + Logback (콘솔 + 파일 롤링). MDC에 `userId`, `requestId` 포함.

---

## E. Implementation Sprint Plan

### Sprint 1 — Foundation & Auth (1주)

| Task | Complexity | Acceptance Criteria |
|---|---|---|
| Spring Boot 3.2 프로젝트 초기화 (pom.xml, application.yml) | S | 앱 구동 성공, /actuator/health → 200 |
| Docker Compose 설정 (app + MySQL 8) | S | docker-compose up 후 앱-DB 연결 정상 |
| Flyway V1~V4 마이그레이션 작성 | M | 전체 DDL 적용, schema_version 기록 |
| User 엔티티 + Repository | S | 저장/조회 단위 테스트 통과 |
| JWT 토큰 Provider 구현 | M | 생성/검증/만료 단위 테스트 통과 |
| SecurityConfig + JwtAuthFilter | M | 인증 없는 요청 401, 유효 토큰 통과 |
| POST /auth/register | M | 사용자 생성, 토큰 반환, 중복 이메일 409 |
| POST /auth/login | S | 토큰 반환, 잘못된 비밀번호 401 |
| POST /auth/refresh + logout | M | RT rotation, 폐기된 RT 401 |
| GET/PATCH /me | S | 프로필 조회/수정 |
| GlobalExceptionHandler 기본 구조 | S | ValidationError, BusinessException 형식 일치 |

**Definition of Done**: 모든 Auth API MockMvc 테스트 통과. Docker Compose에서 통합 실행 성공.

---

### Sprint 2 — Core Features (2주)

| Task | Complexity | Acceptance Criteria |
|---|---|---|
| WeeklyPlan 엔티티 + CRUD API | M | 주간계획 생성/조회/수정/삭제, 중복 주 409 |
| DateUtil.getMondayOfWeek() | S | 임의 날짜 → 해당 주 월요일 변환 정확 |
| WeeklyGoal 엔티티 + CRUD API | M | 목표 생성/수정/삭제, plan 소유권 검증 |
| Task 엔티티 + CRUD API | M | 태스크 생성/수정/삭제, date 범위 검증 |
| PATCH /tasks/{id}/toggle + StreakService | L | 완료 시 스트릭 업데이트, done_count 연동 |
| Roadmap + RoadmapWeek 엔티티 | S | 로드맵 조회 API, seed 데이터 포함 |
| UserRoadmap + 적용 API | M | 로드맵 적용, seed_current_week=true 시 goal+task 자동 생성 |
| GET /dashboard | L | D-day, 주간완료율, 스트릭, 오늘태스크, 주간목표, 최근기록 |
| Flyway V5 로드맵 시드 데이터 | M | 공식 로드맵 3개 이상 삽입 |

**Definition of Done**: Dashboard API 통합 테스트 통과. Streak 단위 테스트 5케이스 통과.

---

### Sprint 3 — Records, STAR, Polish (1주)

| Task | Complexity | Acceptance Criteria |
|---|---|---|
| Record + Tag + RecordTag 엔티티 | M | 기록 CRUD, 태그 N:M 정상 동작 |
| GET /records 페이징 + 필터 | M | category, tag 필터, page/size 동작 |
| Star 엔티티 + StarService.generateText() | M | 템플릿 기반 generated_text 생성, 중복 STAR 409 |
| POST/GET/PATCH /records/{id}/star | M | STAR CRUD, record 소유권 검증 |
| GET /tags + POST /tags + DELETE /tags | S | 태그 관리 API |
| Bean Validation 전수 점검 | S | 모든 Request DTO 검증 누락 없음 |
| MDC 로깅 (userId, requestId) | S | 로그에 컨텍스트 정보 포함 |
| OpenAPI (springdoc-openapi) 문서화 | M | /swagger-ui.html 접근 가능, 전 API 문서화 |
| 전체 통합 테스트 보완 | M | 주요 플로우 E2E 테스트 통과 |

**Definition of Done**: 전체 API Swagger 문서 완성. Docker Compose 단일 명령으로 완전한 MVP 실행.

---

## F. Test Plan

### Unit Tests — Domain Logic

#### StreakService

| Test Case | Input | Expected |
|---|---|---|
| 첫 완료 (last=null) | lastCompleted=null, today=03-02 | current=1, max=1 |
| 연속 완료 (어제) | lastCompleted=03-01, current=5, max=10, today=03-02 | current=6, max=10 |
| 최대치 갱신 | lastCompleted=03-01, current=10, max=10, today=03-02 | current=11, max=11 |
| 오늘 중복 완료 | lastCompleted=03-02, current=5, today=03-02 | current=5 (불변) |
| 연속 끊김 | lastCompleted=02-28, current=5, today=03-02 | current=1 |

```java
@ExtendWith(MockitoExtension.class)
class StreakServiceTest {
    @InjectMocks StreakService streakService;

    @Test
    void 첫번째_완료시_스트릭1() {
        User user = User.builder().currentStreak(0).maxStreak(0).build();
        streakService.updateStreak(user, LocalDate.of(2026, 3, 2));
        assertThat(user.getCurrentStreak()).isEqualTo(1);
        assertThat(user.getMaxStreak()).isEqualTo(1);
    }

    @Test
    void 연속완료시_스트릭증가() {
        User user = User.builder().currentStreak(5).maxStreak(10)
                        .lastCompletedDate(LocalDate.of(2026, 3, 1)).build();
        streakService.updateStreak(user, LocalDate.of(2026, 3, 2));
        assertThat(user.getCurrentStreak()).isEqualTo(6);
    }
    // ... 추가 케이스
}
```

#### DashboardService — Weekly Completion Rate

| Test Case | Input | Expected |
|---|---|---|
| 목표 2개, 각 done/target=2/3, 3/3 | goals=[{2,3},{3,3}] | rate = (0.667 + 1.0) / 2 = 0.833 |
| 목표 없음, task 4개 중 2개 완료 | goals=[], tasks=[done×2, total×4] | rate = 0.5 |
| 목표 없음 + task 없음 | goals=[], tasks=[] | rate = 0.0 |
| done_count > target_count (cap) | goals=[{5,3}] | rate = 1.0 (cap) |

#### StarService — generateText

| Test Case | Input | Expected |
|---|---|---|
| 정상 STAR 4개 필드 | S/T/A/R 모두 채움 | generated_text에 4개 내용 포함 |
| 이미 STAR 존재 | record_id 중복 | STAR_ALREADY_EXISTS 예외 |

---

### Integration Tests — API (MockMvc)

```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Test
    void 회원가입_성공() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(APPLICATION_JSON)
                .content("""
                    {"email":"test@test.com","password":"Test1234!","nickname":"테스터",
                     "discharge_date":"2026-12-31","daily_minutes":60,
                     "goal_priorities":["CERT"]}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.access_token").isNotEmpty());
    }

    @Test
    void 중복이메일_409() throws Exception { ... }

    @Test
    void 만료토큰_401() throws Exception { ... }
}

class TaskIntegrationTest {

    @Test
    void 태스크완료_스트릭업데이트() throws Exception {
        // given: 사용자 생성, 플랜 생성, 태스크 생성
        // when: PATCH /toggle {done:true}
        // then: streak.current == 1 (첫 완료)
    }
}
```

#### Testcontainers (선택사항 — Sprint 3)
```java
@Testcontainers
class RecordRepositoryTest {
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("selfdev_test");
    // Flyway 마이그레이션 → 실제 MySQL로 Repository 테스트
}
```

---

## G. Risks & Mitigations

### 1. 데이터 프라이버시 및 군 보안 정책

| Risk | Impact | Mitigation |
|---|---|---|
| 병사 개인 학습 데이터 유출 | High | 군 내부망 전용 배포. HTTPS 필수 (Let's Encrypt 또는 내부 CA). DB 볼륨 암호화 검토. |
| 이메일/닉네임으로 신원 식별 | Medium | email은 군 ID 체계 사용 시 최소화. 닉네임 익명화 옵션 고려. |
| discharge_date로 전역일 정보 노출 | Medium | API 응답에서 discharge_date 직접 노출 최소화 (D-day 숫자만 반환). |

### 2. 병사 사용 환경 제약

| Risk | Impact | Mitigation |
|---|---|---|
| 군 네트워크 제한 (외부 CDN 차단) | High | 프론트엔드 번들을 내부 서버에서 서빙. 외부 의존 제거. |
| 공용 PC/태블릿 사용 | Medium | 액세스 토큰 만료 15분 유지. 로그아웃 강제화 UI 제공. |
| 모바일 환경 다양성 | Low | MVP는 반응형 웹으로 제한. 네이티브 앱 제외. |

### 3. 데이터 무결성 및 오남용

| Risk | Impact | Mitigation |
|---|---|---|
| 태스크 소급 완료 처리 (스트릭 조작) | Medium | `done_at` 서버 타임스탬프 기록. scheduled_date +1일 이후 완료 불가 규칙 추가 (MVP에서 논의). |
| 동일 주 플랜 중복 생성 시도 | Low | DB UNIQUE(user_id, week_start) + 409 처리. |
| 다른 사용자 리소스 접근 | High | 모든 Service에서 `userId == resource.userId` 검증 + 403 반환. |

### 4. 일정 리스크 및 스코프 관리

| Risk | Impact | Mitigation |
|---|---|---|
| Sprint 2 Dashboard 집계 쿼리 복잡도 | Medium | 쿼리 최적화 전에 N+1 문제 체크. JOIN 최소화, 필요시 JPQL 직접 작성. |
| Roadmap 시드 데이터 준비 지연 | Low | 개발팀이 임시 시드 데이터 3개 이상 작성. 실제 콘텐츠는 운영팀에서 추후 추가. |
| 스코프 크리프 (알림, 채팅 등 추가 요청) | High | MVP 범위 문서화 고정. 추가 기능은 v2 백로그로 분리. |
| 테스트 환경 부재 (군 망 내) | Medium | Docker Compose로 로컬 재현 100% 가능하도록 설계. CI는 GitHub Actions 또는 내부 Jenkins. |

---

## Quick Reference

### D-day 계산
```java
long dDay = ChronoUnit.DAYS.between(LocalDate.now(ZoneId.of("Asia/Seoul")), user.getDischargeDate());
// 전역일 = 오늘이면 0, 전역 후 음수
```

### 이번 주 월요일 계산
```java
LocalDate monday = LocalDate.now(ZoneId.of("Asia/Seoul"))
    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
```

### STAR 템플릿 생성
```java
String generated = String.format(
    "%s이라는 상황에서, %s이라는 과제를 맡아, %s하는 행동을 취했습니다. 그 결과, %s이라는 성과를 이루었습니다.",
    situation, taskDesc, action, result
);
```

---

## Next Step

> Do you want me to generate the Spring Boot code skeleton now (entities/repositories/controllers + Docker Compose + Flyway migrations + OpenAPI)?
