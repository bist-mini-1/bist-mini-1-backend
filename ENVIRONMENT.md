# ⚙️ Environment Setup & Tech Stack

## ⚙️ Environment Setup
프로젝트 시작 전 아래 환경 설정을 확인해 주세요.
- **JDK:** 21
- **IDE:** STS (Spring Tool Suite)
- **Database:** Oracle DB
- **Encoding:** UTF-8

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
