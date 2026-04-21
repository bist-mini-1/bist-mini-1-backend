# ⚙️ Environment Setup & Tech Stack

## ⚙️ Environment Setup
프로젝트 시작 전 아래 환경 설정을 확인해 주세요.
- **JDK:** 21
- **IDE:** STS (Spring Tool Suite)
- **Database:** Oracle DB
- **Encoding:** UTF-8

## 🔐 Environment Variables
로컬 개발 시 보안이 필요한 설정값은 환경 변수(`.env`)로 관리합니다.

### 1. `.env` 파일 설정
프로젝트 루트에 `.env` 파일을 생성하고 아래 형식을 참고하여 설정을 입력합니다. (`.env.example` 참고)

```env
# Server Configuration
SERVER_PORT=8080

# Database Configuration
DB_URL=jdbc:oracle:thin:@localhost:1521:xe
DB_USERNAME=your_username
DB_PASSWORD=your_password

# API Documentation (Swagger)
APP_TITLE=BIST Mini Project API
APP_DESCRIPTION=BIST Mini 프로젝트 1기 백엔드 API 명세서입니다.
APP_VERSION=1.0.0
```

### 2. 자동 로딩 (Dotenv)
- `me.paulschwarz:springboot3-dotenv` 라이브러리가 애플리케이션 시작 시 자동으로 `.env` 파일을 로드합니다.
- 이에 따라 `application.yml`을 직접 수정할 필요 없이 환경 변수만으로 설정을 관리할 수 있습니다.

---

## 🛠 Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.13
- **Database:** Oracle DB, MyBatis
- **Major Libraries:**
  - `Validation` (입력값 검증)
  - `Spring DevTools` (개발 편의 도구)
  - `Jackson XML` (XML 데이터 처리)
- **Documentation:** [Swagger UI (SpringDoc)](http://localhost:8080/swagger-ui.html), Postman

---

## 🚀 API Documentation & Testing
- **Swagger 접속**: 애플리케이션 실행 후 아래 주소로 접속하여 확인 가능합니다.
  - [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **인증(Bearer Auth) 설정**:
  1. Swagger UI 우측 상단의 **[Authorize]** 버튼을 클릭합니다.
  2. `Value`란에 `Bearer YOUR_JWT_TOKEN` 형식으로 입력(또는 설정에 따라 토큰만 입력)하여 인증 상태를 유지할 수 있습니다.
  3. 자물쇠 아이콘이 잠긴 API는 해당 인증 정보와 함께 요청이 전송됩니다.
