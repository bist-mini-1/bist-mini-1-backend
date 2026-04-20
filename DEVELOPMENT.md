# 📋 개발 프로세스 가이드

### 1️⃣ 주제 선정
- [ ] 팀원 간 협의를 통한 프로젝트 대주제 및 목표 설정

### 2️⃣ 기능 추출 (4 Parts)
- [ ] 주제별 4개의 독립적인 파트로 기능 분할
- [ ] 각 파트별 요구사항 정의 및 독립성 확보

### 3️⃣ 데이터베이스 스키마 정의
- [ ] 공통 도메인 분석 및 ERD 설계
- [ ] 테이블 명세서 및 제약 조건 정의

### 4️⃣ 아키텍처 설계 및 피드백
- [ ] **계층형 아키텍처(Layered Architecture)** 설계
  - `Controller` - `Service` - `DAO`
- [ ] 설계 완료 후 팀 피드백 및 코드 구조 동기화

### 5️⃣ 브랜치 전략 및 작업 방식
- [ ] `main` 브랜치 보호 (직접 Push 지양)
- [ ] `feature/[본인닉네임]` 브랜치 생성 및 작업 후 PR 진행
- [ ] 코드 리뷰 완료 후 Merge

---

## 💡 개발 모드 전환 (Mock vs Real DB)
프로젝트의 DB 연동 여부에 따라 아래와 같이 설정을 전환할 수 있습니다.

### 1️⃣ Mock 모드 (현재 활성화 상태)
DB 서버가 준비되지 않았을 때, 비즈니스 로직과 API 규격을 먼저 개발하기 위한 모드입니다.
- **설정 내용**:
    - `BackendApplication.java`: `DataSourceAutoConfiguration` 등 DB 관련 자동 설정 제외 (`exclude` 옵션)
    - `Service`: `@Transactional` 주석 처리
- **특징**: 실제 DB에 접속하지 않으며 서비스 레이어에서 Mock 데이터를 반환합니다.

### 2️⃣ 실제 DB 연동 모드 전환 방법
실제 데이터베이스와 연동하여 작업을 진행하려면 아래 설정을 복구해야 합니다.
1. **`BackendApplication.java`**: `@SpringBootApplication` 어노테이션의 `exclude` 옵션 전체 삭제
2. **`Service`**: 주석 처리된 `@Transactional` (또는 관련 DB 호출 코드) 주석 해제
3. **`application.yml`**: [README.md](./README.md) 가이드에 따라 실제 DB 계정 정보 입력

### 6️⃣ API 테스트 및 검증
- **Swagger / Postman**을 적극 활용하여 기능 검증
- 의존적인 파트가 존재할 경우 **Mock API**를 활용하여 독립 테스트 수행

### 7️⃣ 병합 (Merge)
- 개발 및 테스트가 완료된 하위 브랜치는 `main` 브랜치로 Pull Request 및 Merge 진행
