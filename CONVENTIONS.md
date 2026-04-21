# 🛠 협업 규칙 & 컨벤션 (Conventions)

팀의 일관성 있는 코드 품질과 효율적인 협업을 위한 약속입니다.

---

## 📌 1. Git 컨벤션

### 커밋 메시지 (Commit Message)
- **형식**: `type: description`
- **종류**:
  - `feat`: 새로운 기능 추가
  - `fix`: 버그 수정
  - `docs`: 문서 수정
  - `style`: 코드 포맷팅 (로직 변경 없음)
  - `refactor`: 코드 리팩토링
  - `chore`: 빌드 업무, 패키지 설정 등

### Pull Request (PR)
- **제목**: `[type] description` (커밋 규칙과 동일)
- **수행**: 최소 1명 이상의 승인(Approve) 후 머지 진행

---

## 📦 2. 공통 응답 규격 (API Response)

프론트엔드와 통신 시 아래의 규정된 형식을 반드시 준수합니다.

```json
{
  "status": "success | fail | error",
  "message": "응답 관련 메시지",
  "data": { ... } // 성공 시 데이터, 없으면 null
}
```

---

## 🚨 3. 예외 처리 및 트랜잭션

### 글로벌 예외 처리
- `@RestControllerAdvice`를 통해 전역 예외를 공통 형식으로 처리합니다.
- 비즈니스 로직 예외는 `CustomException`과 `ErrorCode`를 활용합니다.

### 트랜잭션 관리 (Transaction)
- **Service Layer**: 비즈니스 로직 수행 시 `@Transactional`을 적용합니다.
- **ReadOnly**: 단순 조회 메서드에는 `@Transactional(readOnly = true)` 사용을 권장합니다.
- **Rollback**: `RuntimeException` 발생 시 스프링 기본 설정에 따라 자동 롤백됩니다.

---

## 🛡️ 4. 코드 스타일 가이드
- **Encoding**: 모든 소스 코드는 `UTF-8`을 기본으로 합니다.
- **Lombok**: 적극 활용하여 코드 다이어트(`@Getter`, `@Builder` 등)를 진행합니다.
- **Logging**: `System.out.println` 대신 로그 라이브러리 사용을 지향합니다.
