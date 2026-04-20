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
  - `Controller` - `Service` - `Repository(DAO)`
- [ ] 설계 완료 후 팀 피드백 및 코드 구조 동기화

### 5️⃣ 브랜치 전략 및 작업 방식
- `main` 브랜치를 기준으로 기능별 `feature` 브랜치를 파서 작업합니다.
- **Branch Naming Convention:** `feature/[본인닉네임]`
  - *Example:* `feature/baming320`, `feature/pileuszu`

### 6️⃣ API 테스트 및 검증
- **Swagger / Postman**을 적극 활용하여 기능 검증
- 의존적인 파트가 존재할 경우 **Mock API**를 활용하여 독립 테스트 수행

### 7️⃣ 병합 (Merge)
- 개발 및 테스트가 완료된 하위 브랜치는 `main` 브랜치로 Pull Request 및 Merge 진행
