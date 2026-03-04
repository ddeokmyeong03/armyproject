# millog.kr

> 대한민국 군 장병을 위한 자기계발 관리 서비스

군 복무 중 자격증·어학·체력·독서·포트폴리오 등 자기계발 활동을 체계적으로 계획하고 기록합니다.
주간 플랜 → 오늘 할 일 → 활동 일지 → 로드맵의 흐름으로 전역까지의 성장을 추적합니다.

**Live** → [https://millog.netlify.app]
---

## 주요 기능

| 기능 | 설명 |
|---|---|
| **대시보드** | 전역 D-day · 연속 달성 스트릭 · 오늘 할 일 · 주간 목표 달성률 · 최근 활동일지 |
| **주간 플랜** | 주차별 목표 설정(카테고리·목표 횟수) · 날짜별 할 일 스케줄링 · 완료 체크 |
| **활동 일지** | 활동 기록 CRUD · 카테고리 & 커스텀 태그 · STAR 형식 자기소개서 초안 |
| **로드맵** | 공식 템플릿(정보처리기사·TOEIC·체력 등) · 주차별 진행 추적 · 개인 로드맵 등록 |
| **설정** | 닉네임·전역일·일일 목표 시간 수정 · 커스텀 태그 관리 |

### 카테고리

`자격증` `영어` `체력` `독서` `포트폴리오` `기타`

### 기본 제공 로드맵 템플릿
- 정보처리기사 합격 12주
- TOEIC 900 달성 12주
- 체력 단련 12주
- 독서 마스터 12주
- 개발 포트폴리오 12주

---

## 기술 스택

### Frontend
| 분류 | 기술 |
|---|---|
| Framework | React 18 + Vite |
| Styling | TailwindCSS (군복 올리브 그린 테마) |
| 상태 관리 | Zustand (인증) + TanStack Query (서버 상태) |
| HTTP | Axios (401 자동 토큰 갱신) |

### Backend
| 분류 | 기술 |
|---|---|
| Framework | Spring Boot 3.2 / Java 21 |
| 보안 | Spring Security · JWT (Access 15분 / Refresh 7일) |
| ORM | Spring Data JPA / Hibernate |
| DB 마이그레이션 | Flyway |
| API 문서 | SpringDoc OpenAPI (개발환경 전용) |

### Infrastructure
| 역할 | 서비스 |
|---|---|
| 프론트엔드 호스팅 | Netlify |
| 백엔드 실행 | Google Cloud Run (europe-west1) |
| 데이터베이스 | Google Cloud SQL — MySQL 8 (us-central1) |
| CI/CD | Developer Connect (main 브랜치 push → 자동 재배포) |

---

## 아키텍처

```
Browser
  │
  ├─ Static (HTML/JS/CSS)
  │   └─ Netlify CDN
  │
  └─ API 요청 (HTTPS)
      └─ Google Cloud Run  ──[Cloud SQL Connector]──  Google Cloud SQL
             Spring Boot 3.2                              MySQL 8
```

---

## 로컬 개발 환경

### 사전 요구사항
- Java 21+
- Maven 3.9+
- Node.js 20+
- MySQL 8 (로컬) 또는 Google Cloud SQL

### 백엔드 실행
```bash
# DB 스키마는 Flyway가 자동 생성
./mvnw spring-boot:run
# → http://localhost:8080
```

### 프론트엔드 실행
```bash
cd frontend
npm install
npm run dev
# → http://localhost:5173  (Vite proxy: /api → localhost:8080)
```

### 환경변수 (`application.yml` 기본값 사용, 프로덕션은 환경변수 필요)

| 변수명 | 설명 |
|---|---|
| `SPRING_DATASOURCE_URL` | Cloud SQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | DB 사용자명 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 |
| `JWT_SECRET` | JWT 서명 키 (Base64) |
| `VITE_API_BASE_URL` | 프론트엔드 → 백엔드 API URL |

---

## DB 스키마 (Flyway 마이그레이션)

```
V1 — users, refresh_tokens
V2 — weekly_plans, weekly_goals, tasks
V3 — roadmaps, roadmap_weeks, user_roadmaps, user_roadmap_weeks
V4 — records, tags, record_tags, stars
V5 — 공식 로드맵 템플릿 시드 데이터
```

---

## API 엔드포인트

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/api/v1/auth/register` | 회원가입 |
| POST | `/api/v1/auth/login` | 로그인 |
| POST | `/api/v1/auth/refresh` | 토큰 갱신 |
| GET/POST | `/api/v1/plans` | 주간 플랜 조회/생성 |
| GET/POST/PATCH | `/api/v1/plans/{id}/goals` | 목표 관리 |
| GET/POST/PATCH | `/api/v1/plans/{id}/tasks` | 할 일 관리 |
| GET/POST/PUT/DELETE | `/api/v1/records` | 활동일지 CRUD |
| GET/POST | `/api/v1/roadmaps` | 로드맵 목록/등록 |
| GET | `/api/v1/dashboard` | 대시보드 데이터 |
| GET/PATCH | `/api/v1/user/me` | 프로필 조회/수정 |
| GET/POST/DELETE | `/api/v1/tags` | 태그 관리 |

---

## 프로젝트 구조

```
armyproject/
├── Dockerfile                  # Cloud Run 빌드 (멀티스테이지)
├── netlify.toml                # Netlify 빌드 설정
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/armydev/selfdev/
│       │   ├── config/         # SecurityConfig, JwtConfig, JacksonConfig
│       │   ├── security/       # JwtAuthenticationFilter, JwtProvider
│       │   ├── domain/
│       │   │   ├── auth/       # 인증 (register/login/refresh)
│       │   │   ├── user/       # 프로필
│       │   │   ├── plan/       # 주간 플랜
│       │   │   ├── goal/       # 주간 목표
│       │   │   ├── task/       # 할 일
│       │   │   ├── record/     # 활동일지 + 태그
│       │   │   ├── roadmap/    # 로드맵
│       │   │   ├── star/       # STAR 자기소개서
│       │   │   └── dashboard/  # 대시보드 집계
│       │   └── common/         # ApiResponse, GlobalExceptionHandler
│       └── resources/
│           ├── application.yml
│           ├── application-prod.yml
│           └── db/migration/   # Flyway SQL (V1~V5)
└── frontend/
    ├── public/_redirects       # Netlify SPA 라우팅
    ├── src/
    │   ├── api/                # client.js + 도메인별 API 함수
    │   ├── hooks/              # TanStack Query 커스텀 훅
    │   ├── pages/              # 11개 페이지
    │   ├── components/         # Layout, Modal, Badge, Spinner
    │   ├── store/authStore.js  # Zustand 인증 상태
    │   └── utils/              # 날짜·카테고리·에러 유틸
    └── vite.config.js
```
