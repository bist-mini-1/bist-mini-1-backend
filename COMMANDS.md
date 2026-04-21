# ⚡ BIST Mini Project "갓" 명령어 가이드

개발 생산성을 높여주는 필수 명령어 모음집입니다. 복사해서 바로 사용하세요!

---

## 🔐 1. 환경 설정 (Environment Setup)

프로젝트 초기 설정 또는 설정 변경 시 사용합니다.

### ✅ .env 파일 생성
```bash
# .env.example을 복사하여 .env 파일 생성 (Windows PowerShell)
cp .env.example .env
```

### 🔄 IDE 설정 동기화
- **STS/Eclipse**: 프로젝트 우클릭 -> `Gradle` -> `Refresh Gradle Project`
- **VS Code**: `Command Palette` (Ctrl+Shift+P) -> `Java: Clean Language Server Workspace`

---

## 🐘 2. Gradle 빌드 및 실행 (Build & Run)

Gradle Wrapper(`gradlew`)를 사용하여 프로젝트를 관리합니다.

### 🛠 빌드 및 클린
```bash
# 빌드 폴더 초기화 및 컴파일
.\gradlew clean compileJava

# 테스트 없이 전체 빌드 (jar 생성)
.\gradlew clean build -x test
```

### 🚀 애플리케이션 실행
```bash
# 로컬에서 바로 실행
.\gradlew bootRun
```

### 🧪 테스트 실행
```bash
# 모든 테스트 실행
.\gradlew test

# 특정 테스트 클래스만 실행
.\gradlew test --tests "com.bist.mini.service.*"
```

---

## 🐣 3. 초보자를 위한 Git 기초 (Beginner's Git Flow)

처음 Git을 사용하는 분들을 위한 가장 기본적인 명령어 흐름입니다.

### 🔍 1단계: 변경사항 확인 (Status)
```bash
# 어떤 파일이 수정되었는지 확인
git status
```

### ➕ 2단계: 파일 담기 (Add)
```bash
# 수정한 모든 파일을 커밋 대기열에 담기
git add .

# 특정 파일만 담고 싶을 때
git add src/main/java/com/bist/mini/Sample.java
```

### ✍️ 3단계: 기록 남기 (Commit)
```bash
# 내 PC에 변경 사항을 기록 (메시지 필수!)
git commit -m "feat: 설명 추가"
```

### 🚀 4단계: 서버에 올리기 (Push)
```bash
# 내가 기록한 내용을 GitHub 서버(remote)에 반영
git push origin main
```

---

## 🐙 4. Git 협업 규칙 (Git Convention)

컨벤션에 맞는 깔끔한 커밋을 위해 사용합니다.

### 📝 커밋 메시지 템플릿
```bash
# 형식: type: description
git commit -m "feat: 새로운 로그인 기능 추가"
git commit -m "fix: DB 연결 타임아웃 오류 수정"
git commit -m "docs: API 명세서 업데이트"
git commit -m "refactor: 코드 구조 개선"
```

### 📥 최신 코드 반영 (Sync)
```bash
# main 브랜치의 최신 내용을 가져와서 내 코드 위에 얹기
git pull origin main --rebase
```

---

## 🛠 4. 유용한 꿀팁 (Helpful Tips)

### 🚫 8080 포트가 이미 사용 중일 때 (Port Kill)
```powershell
# 8080 포트를 사용하는 프로세스 ID(PID) 확인
netstat -ano | findstr :8080

# 확인된 PID(예: 1234) 종료
taskkill /F /PID 1234
```

### 📦 의존성 캐시 강제 새로고침
```bash
# 의존성 문제가 해결되지 않을 때 사용
.\gradlew build --refresh-dependencies
```

---

> [!TIP]
> 명령어 실행 시 권한 오류가 발생하면 **터미널을 관리자 권한으로 실행**해 보세요.
