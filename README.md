# 🚀 BIST Mini Project 1 - Backend Server

BIST 미니 프로젝트 1기 백엔드 서버 저장소입니다.  
사용자들이 마크다운 기반의 자유로운 글 작성과 소셜 상호작용(댓글, 대댓글, 좋아요, 북마크, 팔로우), 그리고 실시간 채팅 및 알림, 게이미피케이션 요소(활동 잔디 및 성장형 캐릭터)를 경험할 수 있도록 든든하고 견고한 API 서버를 제공합니다.

---

## 👥 팀원 소개 (Team Members)

| 이름 | 역할 / 파트 | GitHub |
| :---: | :---: | :---: |
| **전명준** | Member | [@baming320](https://github.com/baming320) |
| **김지환** | Member | [@pileuszu](https://github.com/pileuszu) |
| **방대혁** | Member | [@daehyuk1231](https://github.com/daehyuk1231) |
| **신동원** | Member | [@shindw2001](https://github.com/shindw2001) |

---

## 🛠️ 기술 스택 (Tech Stack)

백엔드 서버는 현대적이고 안정적인 엔터프라이즈급 백엔드 개발 표준 기술들을 채택하였습니다.

*   **Language**: Java 21 (JDK 21)
*   **Framework**: Spring Boot 3.5.13
*   **Database**: Oracle Database
*   **Data Access Layer**: MyBatis 3.0.4
*   **Security & Authentication**: Spring Security, JWT (JSON Web Token) (jjwt 0.12.6)
*   **Real-time Communication**: Spring Boot Starter WebSocket (SockJS & STOMP), Server-Sent Events (SSE)
*   **API Documentation**: Swagger (springdoc-openapi-starter-webmvc-ui 2.8.5)
*   **Utilities & Development**: Lombok, Jackson Dataformat XML, Dotenv (springboot3-dotenv, dotenv-java)

---

## 🏗️ 아키텍처 및 폴더 구조 (Project Architecture)

이 프로젝트는 **도메인 중심의 계층형 아키텍처(Domain-Driven Layered Architecture)**를 준수하여 설계되었습니다.  
각 핵심 비즈니스 도메인은 패키지별로 완전히 격리되어 있으며, 그 아래에 `controller`, `service`, `dao` (MyBatis Mapper), `dto`, `entity`를 자급자족 형태로 구현하여 높은 응집도와 낮은 결합도를 자랑합니다.

### 📂 디렉토리 구조 (Directory Layout)
```text
src/main/java/com/bist/mini/
├── BackendApplication.java       # 애플리케이션 메인 진입점
├── common/                       # 공통 모듈 및 크로스-컷팅 컨서언
│   ├── annotation/               # 공통 커스텀 어노테이션 (@LoginMember 등)
│   ├── config/                   # CORS, Security, WebMvc, Swagger, WebSocket 설정 클래스
│   ├── enums/                    # 공통 에러 코드 및 상태값
│   ├── exception/                # @RestControllerAdvice 기반 전역 예외 처리
│   ├── jwt/                      # JWT 토큰 생성, 파싱 및 검증 공급자 (JwtProvider)
│   └── resolver/                 # HTTP/STOMP 통합 로그인 멤버 아규먼트 리졸버
└── [domain]/                     # 격리된 도메인별 패키지 구성 (동일 구조 반복)
    ├── controller/               # API 컨트롤러 계층
    ├── service/                  # 비즈니스 로직 및 트랜잭션 계층
    ├── dao/                      # MyBatis Mapper 인터페이스 계층
    ├── dto/                      # 계층 간 및 클라이언트 전송용 DTO
    ├── entity/                   # 도메인 데이터 모델 계층
    ├── features.md               # 해당 도메인의 상세 기능 요구사항
    └── schema.md                 # 해당 도메인의 SQL 스키마
```

---

## 🗄️ 데이터베이스 스키마 및 설계 (Database Schema)

이 서버는 관계형 데이터베이스로 **Oracle DB**를 사용하며, 총 13개의 테이블로 고도의 관계를 구축하고 있습니다. 전체 스키마에 대한 상세 내용은 [SCHEMA.md](./docs/SCHEMA.md) 및 각 도메인 내의 `schema.md`에서 확인하실 수 있습니다.

### 📊 주요 테이블 일람
1.  **`members`** (회원): 사용자의 기본 인증 정보(아이디, 패스워드, 이메일, 닉네임) 및 프로필 데이터(자기소개, 프로필 이미지) 관리.
2.  **`posts`** (게시글): 마크다운 기반 콘텐츠 본문(`CLOB`), 조회수, 좋아요수, 댓글수, 임시저장(`is_temp`) 및 공개 여부 관리.
3.  **`comments`** (댓글): 게시글 댓글 및 `parent_id`를 통한 계층형 무제한 대댓글(Recomment) 기능 지원.
4.  **`tags` & `post_tags`** (태그): 해시태그 목록 및 게시글과의 다대다(N:M) 연동 테이블.
5.  **`post_likes`** (좋아요): 게시글에 대한 유저별 좋아요 고유 기록.
6.  **`bookmarks`** (북마크): 사용자가 나중에 읽기 위해 보관해 둔 게시글 스크랩 정보.
7.  **`attachments`** (첨부파일): **데이터베이스 BLOB 기반 파일 저장소**. 파일 원본명, 크기, MIME타입, 확장자 및 바이너리 데이터를 직접 DB에 저장.
8.  **`follows`** (팔로우): 회원 간 팔로워-팔로잉 단방향 및 쌍방향 소셜 관계 형성.
9.  **`notifications`** (알림): 댓글 작성, 좋아요, 팔로우 등 이벤트 시 발송되는 실시간 알림 이력 관리.
10. **`chat_rooms`, `chat_room_members`, `chat_messages`** (채팅): WebSocket 기반 실시간 1:1 및 그룹 채팅방, 참여 회원 목록, CLOB 타입 채팅 메시지 이력 보존.

---

## ✨ 백엔드 핵심 기능 및 특징 (Key Features & Engineering)

### 🔐 1. 안전하고 통합된 인증 시스템 (JWT & Security)
*   **Spring Security & JWT**: 무상태(Stateless) API에 최적화된 JWT 기반 인증을 제공합니다.
*   **통합 아규먼트 리졸버**: 커스텀 `@LoginMember` 어노테이션과 `LoginMemberArgumentResolver`를 설계하여, **일반 REST API(HTTP) 요청**과 **실시간 WebSocket STOMP 메시지 송수신** 양쪽에서 동일한 방식으로 현재 로그인된 회원 ID를 즉시 주입받을 수 있는 혁신적인 컨트롤러 인터페이스를 제공합니다.

### 💾 2. 데이터베이스 BLOB 기반의 파일 서버 (File Storage)
*   외부 클라우드 스토리지(S3 등)나 로컬 디스크 파일 경로 대신, **Oracle DB의 BLOB(Binary Large Object) 타입 컬럼**을 활용하여 파일(썸네일 이미지, 본문 삽입 이미지, 다운로드용 일반 문서 등)을 직접 저장하고 바이너리 스트리밍 방식으로 실시간 다운로드 및 조회 기능을 보장합니다.
*   파일 메타데이터(용량, 확장자, MIME타입 검증)의 일치성을 DB 레벨에서 완벽하게 제어합니다.

### 💬 3. 실시간 소통 및 양방향 통신 (WebSocket & SSE)
*   **실시간 채팅**: `Spring WebSocket`과 `STOMP` 프로토콜을 사용해 유저 간 1:1 대화방을 실시간으로 중계합니다.
*   **실시간 알림**: Server-Sent Events(SSE) 기술을 적용해 로그인한 사용자가 서비스 이용 중에 댓글, 좋아요, 팔로우 등의 이벤트가 발생할 경우 외부 통신을 통해 화면을 새로고침하지 않고도 실시간 알림 팝업을 받을 수 있도록 백엔드 이벤트를 브로드캐스팅합니다.

### 📊 4. 게이미피케이션 및 활동 통계 API
*   사용자의 활동 점수(게시글 작성, 댓글 작성 등)에 연동되는 동적 캐릭터/아바타 변경 데이터와 일자별 기여 이력을 집계하여 마이페이지에 **활동 잔디(Contribution Grass)**를 시각화할 수 있는 강력한 통계 및 조회 API를 제공합니다.

---

## 📖 문서 가이드 (Documentation Map)

원활한 백엔드 개발 참여를 위해 아래 순서대로 문서를 정독해 주세요. 모든 문서는 `docs/` 디렉토리에 위치해 있습니다.

1.  **[⚙️ 환경 설정 (ENVIRONMENT.md)](./docs/ENVIRONMENT.md)**: 가장 먼저 확인해야 할 기술 스택 및 로컬 설정 가이드입니다.
2.  **[📋 개발 프로세스 (DEVELOPMENT.md)](./docs/DEVELOPMENT.md)**: 브랜치 전략(Git Flow), Mock 모드 ↔ Real DB 연동 모드 전환 가이드가 담겨 있습니다.
3.  **[🛠 협업 규칙 (CONVENTIONS.md)](./docs/CONVENTIONS.md)**: 커밋 메시지, PR 룰, 공통 API 응답 규격(`ApiResponse`), DTO/Entity 사용 가이드 등 팀 약속을 정의합니다.
4.  **[✨ 기능 명세 (FEATURES.md)](./docs/FEATURES.md)**: 백엔드가 구현하는 주요 기능 명세와 예외 조건들을 기술합니다.
5.  **[🗄️ 스키마 명세 (SCHEMA.md)](./docs/SCHEMA.md)**: DB 테이블 관계와 SQL 컬럼 사양을 명시합니다.
6.  **[⚡ 명령어 가이드 (COMMANDS.md)](./docs/COMMANDS.md)**: 로컬 실행, 빌드, 트러블슈팅(포트 충돌 해결 등) 및 복구 명령어 모음입니다.

---

## 💻 빠른 시작 (Getting Started)

### 1️⃣ 환경 변수 설정
프로젝트 루트 경로에 `.env` 파일을 생성하고 아래 예시를 기반으로 알맞은 환경 변수를 채워 넣습니다 (상세 설명은 [ENVIRONMENT.md](./docs/ENVIRONMENT.md)를 참고해 주세요).

```env
SERVER_PORT=8080
DB_URL=jdbc:oracle:thin:@localhost:1521:xe
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
APP_TITLE=BIST Mini Project API
JWT_SECRET=your_super_secure_and_very_long_jwt_secret_key_1234567890
JWT_EXPIRATION=86400000
```

### 2️⃣ 의존성 설치 및 컴파일 확인 (Windows)
```powershell
.\gradlew clean compileJava
```

### 3️⃣ 애플리케이션 로컬 서버 실행
```powershell
.\gradlew bootRun
```

### 4️⃣ API 문서 접속 및 인증 (Swagger)
*   **Swagger UI 주소**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
*   JWT 토큰 테스트 시 Swagger UI 상단의 **[Authorize]** 버튼을 사용해 토큰 정보를 입력할 수 있습니다.

---

> [!TIP]
> 백엔드 개발 도중 예외가 발생하거나 포트 충돌, 의존성 불일치가 일어날 경우 [COMMANDS.md](./docs/COMMANDS.md)의 **트러블슈팅 섹션**을 활용해 보세요!