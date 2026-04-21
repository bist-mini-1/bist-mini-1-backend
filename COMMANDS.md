# ⚡ 명령어 가이드 (Cheat Sheet)

실제 개발 시 자주 사용하게 될 명령어들을 카테고리별로 모아 놓았습니다.

---

## 🐣 1. Git 기초 흐름 (Git Flow)

처음 시작하는 분들을 위한 가장 기본적인 명령어 순서입니다.

```bash
# 1. 상태 확인
git status

# 2. 변경사항 담기
git add .

# 3. 로컬 기록 남기 (컨벤션 준수!)
git commit -m "feat: 설명 추가"

# 4. 서버에 동기화
git push origin main
```

---

## 🐘 2. Gradle 빌드 및 실행 (Build & Run)

모든 명령어는 프로젝트 루트에서 실행해야 합니다.

### ⚙️ 빌드
```powershell
# 빌드 폴더 초기화 및 컴파일 확인
.\gradlew clean compileJava

# 테스트 코드를 제외하고 빌드 (실행 파일 생성)
.\gradlew clean build -x test
```

### 🚀 실행 및 테스트
```powershell
# 애플리케이션 즉시 실행
.\gradlew bootRun

# 모든 단위 테스트 실행
.\gradlew test
```

---

## 🛠 3. 유용한 트러블슈팅 (Troubleshooting)

### 🚫 포트 충돌 해결 (8080 포트 종료)
포트 8080이 이미 사용 중이라는 에러 발생 시 아래 명령어로 강제 종료합니다.
```powershell
# PID 확인
netstat -ano | findstr :8080

# 프로세스 종료 (확인된 PID가 1234인 경우)
taskkill /F /PID 1234
```

### 📦 의존성 캐시 새로고침
라이브러리가 제대로 인지되지 않을 때 사용합니다.
```powershell
.\gradlew build --refresh-dependencies
```

---

> [!TIP]
> 협업 시 최신 코드를 유지하기 위해 작업 전 `git pull origin main --rebase`를 생활화합시다!
