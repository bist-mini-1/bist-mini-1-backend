# 🛠 협업 규칙 (Collaboration Rules)

### 📌 Commit Message Convention
명확한 변경 이력 관리를 위해 아래 규칙을 준수합니다.
- **Format:** `type: description`
- **Types:**
  - `feat`: 새로운 기능 추가
  - `fix`: 버그 수정
  - `docs`: 문서 수정 (README 등)
  - `style`: 코드 포맷팅, 세미콜론 누락 등 (코드 변경 없는 경우)
  - `refactor`: 코드 리팩토링
  - `test`: 테스트 코드 추가
  - `chore`: 빌드 업무 수정, 패키지 매니저 설정 등

### 📦 Common Response Format
API 응답 규격을 통일하여 프론트엔드/테스트 효율을 높입니다.
```json
{
  "status": "success | fail | error",
  "message": "응답 관련 메시지",
  "data": { ... } // 성공 시 데이터, 없으면 null
}
```

### 🚨 Global Exception Handling
- `@RestControllerAdvice`를 사용하여 프로젝트 전역의 예외를 공통 형식으로 처리합니다.
- 비즈니스 로직 예외 발생 시 `CustomException`을 정의하여 명확한 에러 메시지를 전달합니다.

### 🛡️ Transaction Management
- **Service Layer**: 비즈니스 로직을 수행하는 서비스 레이어에 `@Transactional`을 적용하여 데이터 정합성을 보장합니다.
- **Rollback Policy**: 스프링의 기본 설정에 따라 `RuntimeException` (및 `CustomException`) 발생 시 자동으로 트랜잭션이 **롤백**됩니다.
- **Performance**: 단순 조회 메서드에는 `@Transactional(readOnly = true)` 사용을 권장합니다.

---

### 🤝 Pull Request (PR) Convention
명확한 코드 리뷰와 원활한 협업을 위해 아래 PR 규칙을 준수해 주세요.

#### 📝 1. PR 제목 (Title)
- **형식**: `[type] description` (커밋 메시지 규칙과 동일)
- **예시**: `[feat] 로그인 기능 구현 및 JWT 연동`

#### 📄 2. PR 내용 (Description)
- **주요 변경 사항**: 무엇이 바뀌었는지 핵심 내용을 불렛 포인트로 요약합니다.
- **테스트 결과**: 빌드 성공 여부 및 테스트 수행 결과를 명시합니다.

#### ✅ 3. 사전 체크리스트 (Self-Checklist)
- [ ] 빌드(`.\gradlew build`)가 성공적으로 완료되었나요?
- [ ] 관련 있는 테스트 코드를 실행하고 통과했나요?
- [ ] 불필요한 주석이나 디버깅용 로그(`System.out.println` 등)를 제거했나요?
- [ ] 문서 수정(README, ENVIRONMENT 등)이 필요한 경우 반영했나요?

#### 👥 4. 리뷰 및 머지 규칙 (Review Policy)
- **리뷰어 지정**: PR 생성 시 팀원 중 최소 1명 이상을 리스너로 지정합니다.
- **승인(Approve)**: 최소 1명 이상의 승인을 받아야 머지가 가능합니다.
