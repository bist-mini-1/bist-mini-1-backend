# ⚙️ Environment Setup & Tech Stack

프로젝트를 시작하기 위해 필요한 시스템 환경과 초기 설정 방법입니다.

---

## 🛠 Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.13
- **Database:** Oracle DB, MyBatis
- **Major Libraries:**
  - `springboot3-dotenv`: 환경 변수(`.env`) 자동 로딩
  - `Validation`: 입력값 검증
  - `SpringDevTools`: 개발 편의 도구
  - `Swagger (SpringDoc)`: API 문서화

---

## 💻 로컬 환경 설정 (Local Setup)

팀원들이 각자의 로컬에서 프로젝트를 실행하기 위한 필수 설정입니다.

### 1️⃣ 환경 변수 설정 (.env)
- **과정**: 프로젝트 루트의 `.env.example` 파일을 복사하여 `.env` 파일을 생성합니다.
- **설명**: 생성한 `.env` 파일에 본인의 Oracle DB 계정 정보와 서버 포트를 입력합니다.
- **특이사항**: 라이브러리가 자동으로 `.env`를 읽으므로 `application.yml`은 수정할 필요가 없습니다.

### 2️⃣ JDK 경로 설정 (필수)
- **파일명**: `gradle.properties`
- **대상**: `org.gradle.java.home`
- **설명**: 본인의 PC에 설치된 JDK 21의 절대 경로로 수정해 주세요.

### 3️⃣ IDE 설정 확인 (STS/Eclipse 기준)
- **Annotation Processing**: 활성화 (Lombok 사용을 위함)
- **Gradle Refresh**: 설정 변경 후 프로젝트 우클릭 -> `Gradle` -> `Refresh Gradle Project` 수행

---

## 🔐 환경 변수 상세 (.env)
로컬 개발 시 보안이 필요한 설정값은 환경 변수로 관리하며, 아래 변수들이 사용됩니다.

| Variable | Description | Default Example |
| :--- | :--- | :--- |
| `SERVER_PORT` | 애플리케이션 실행 포트 | `8080` |
| `DB_URL` | Oracle DB 접속 URL | `jdbc:oracle:thin:@localhost:1521:xe` |
| `DB_USERNAME` | DB 계정명 | `your_username` |
| `DB_PASSWORD` | DB 비밀번호 | `your_password` |
| `APP_TITLE` | Swagger 문서 제목 | `BIST Mini Project API` |

---

## 🚀 API Documentation
- **Swagger 접속**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **인증 설정**: Swagger UI 상단의 **[Authorize]** 버튼을 통해 JWT 토큰을 입력할 수 있습니다.
