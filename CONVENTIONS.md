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
