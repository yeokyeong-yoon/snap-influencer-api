# Brand Coordinate API

브랜드별 상품 가격을 비교하고 관리하는 Spring Boot API 프로젝트입니다.

## 구현 범위

### 1. 카테고리별 최저가격 조회 API
- 모든 카테고리의 최저가격 브랜드와 상품 가격을 조회
- 카테고리별 최저가 상품의 총액 계산
- 응답: 카테고리, 브랜드, 가격 정보와 총액

### 2. 브랜드별 최저가격 패키지 조회 API
- 단일 브랜드로 모든 카테고리 상품 구매 시 최저가격 브랜드 조회
- 해당 브랜드의 카테고리별 상품 가격과 총액 제공
- 모든 카테고리 상품을 보유한 브랜드만 대상으로 계산

### 3. 카테고리별 가격 범위 조회 API
- 특정 카테고리의 최저가/최고가 브랜드와 가격 정보 조회
- 동일 가격대의 여러 브랜드 정보 포함

### 4. 브랜드/상품 관리 API
- 브랜드 및 상품 CRUD 기능
- 성공/실패 여부와 실패 사유 전달
- Request/Response JSON 형식 준수

## 개발 환경
- Java 17
- Spring Boot 3.2
- Gradle
- Spring Web / JPA / Validation
- H2 Database (In-memory)

## 빌드 및 실행 방법

### 프로젝트 빌드
```bash
./gradlew clean build
```

### 테스트 실행
```bash
./gradlew test
```

### 애플리케이션 실행
```bash
./gradlew bootRun
```

## API 엔드포인트

### 1. 카테고리별 최저가격 조회
- GET /categories/lowest-prices
- Response: 카테고리별 최저가 브랜드/가격 정보와 총액

### 2. 브랜드별 최저가격 패키지 조회
- GET /brands/cheapest
- Response: 최저가 브랜드의 카테고리별 가격과 총액

### 3. 카테고리별 가격 범위 조회
- GET /categories/{category}/price-range
- Response: 해당 카테고리의 최저가/최고가 브랜드와 가격

### 4. 브랜드 관리
- POST /brands: 브랜드 등록
- PUT /brands/{brandId}: 브랜드 수정
- DELETE /brands/{brandId}: 브랜드 삭제

## H2 Database 접속 정보
- Console URL: http://localhost:8080/api/h2-console
- JDBC URL: jdbc:h2:mem:branddb
- Username: sa
- Password: (empty)

## 추가 정보

### 가격 표시 형식
- 모든 가격은 천 단위 구분자(쉼표) 포함
- 예시: "10,000", "5,100"

### 에러 처리
- 유효하지 않은 요청: 400 Bad Request
- 리소스 없음: 404 Not Found
- 서버 에러: 500 Internal Server Error

### 응답 형식
- 성공 시: 요청된 데이터 직접 반환
- 실패 시: 
```json
{
    "success": false,
    "message": "실패 사유"
}
```

### 참고사항
- 모든 API는 JSON 형식으로 통신
- 카테고리는 상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리로 고정
- 가격은 정수형으로 관리되며 출력 시 포맷팅 적용