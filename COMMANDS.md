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

## 🔄 2. 최신 코드 반영 (Sync with Main)

개별 브랜치(`feature/닉네임`)에서 작업할 때 `main`의 최신 내용을 가져오는 방법입니다.

```bash
# 1. 원격 저장소(GitHub)의 최신 이력 가져오기
git fetch origin main

# 2. 내 브랜치에 main의 변경 사항 합치기 (rebase 권장)
git rebase origin/main
```

---

## 🐘 3. Gradle 빌드 및 실행 (Build & Run)

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

---

## 🛠 4. 유용한 트러블슈팅 (Troubleshooting)

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

## 💣 5. 강력한 초기화 및 복구 (Hard Reset & Recovery)

> [!CAUTION]
> 아래 명령어들은 **작업 중인 내용을 영구적으로 삭제**할 수 있으므로 매우 주의해서 사용해야 합니다!

### 🔄 원격 저장소와 강제 동기화
로컬 코드가 너무 꼬여서 서버의 최신 코드와 똑같이 맞추고 싶을 때 사용합니다.
```bash
# 1. 최신 정보 가져오기
git fetch origin main

# 2. 내 브랜치를 서버의 최신 상태로 강제 초기화 (현재 브랜치 기준)
git reset --hard origin/main
```

### 🗑️ 로컬 변경 사항 강제 취소
현재 수정 중인 모든 내용을 버리고 마지막 커밋 상태로 되돌립니다.
```bash
# 파일 수정 내용을 마지막 커밋 시점으로 되돌림
git reset --hard HEAD

# 새롭게 생성한 파일(Untracked)까지 아예 삭제하고 싶을 때
git clean -fd
```

---

> [!TIP]
> 협업 시 최신 코드를 유지하기 위해 작업 전 `git pull origin main --rebase`를 생활화합시다!
