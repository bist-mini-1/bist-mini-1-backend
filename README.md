# 🚀 BIST Mini Project 1 - Backend

## 👥 Team Members

| Name | Role / Part | GitHub |
| :--- | :--- | :--- |
| **전명준** | Member | [@baming320](https://github.com/baming320) |
| **김지환** | Member | [@pileuszu](https://github.com/pileuszu) |
| **방대혁** | Member | [@daehyuk1231](https://github.com/daehyuk1231) |
| **신동원** | Member | [@shindw2001](https://github.com/shindw2001) |

---

## 📖 Documentation
프로젝트의 효율적인 협업과 가이드라인은 아래 문서를 참고해 주세요.

- [📋 개발 프로세스 가이드 (DEVELOPMENT.md)](./DEVELOPMENT.md)
- [🛠 협업 규칙 및 컨벤션 (CONVENTIONS.md)](./CONVENTIONS.md)
- [⚙️ 환경 설정 및 기술 스택 (ENVIRONMENT.md)](./ENVIRONMENT.md)
- [⚡ 갓 명령어 가이드 (COMMANDS.md)](./COMMANDS.md)

---

## 💻 로컬 환경 설정 안내 (Local Setup Guide)
팀원들이 각자의 로컬에서 프로젝트를 실행할 때, 본인의 환경에 맞춰 아래 설정들을 수정해야 합니다.

### 1️⃣ 환경 변수 설정 (.env)
- **과정**: 프로젝트 루트의 `.env.example` 파일을 복사하여 `.env` 파일을 생성합니다.
- **설명**: 생성한 `.env` 파일에 본인의 Oracle DB 계정 정보(URL, Username, Password)와 서버 포트를 설정합니다.
- **특이사항**: `springboot3-dotenv` 라이브러리가 자동으로 `.env` 파일을 읽어 프로젝트에 반영하므로, `application.yml`을 직접 수정하지 않아도 됩니다.

### 2️⃣ JDK 경로 설정 (필수)
- **파일명**: `gradle.properties`
- **대상**: `org.gradle.java.home`
- **설명**: 본인의 PC에 JDK 21이 설치된 절대 경로로 수정해 주세요. (빌드 오류 방지)

### 3️⃣ IDE 설정 확인 (STS 기준)
- **설정**: `Annotation Processing` 활성화 (Lombok 사용을 위함)
- **동기화**: 설정 변경 후 반드시 프로젝트 우클릭 → `Gradle` → `Refresh Gradle Project` 수행