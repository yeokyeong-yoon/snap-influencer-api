# Snap Influencer API

Spring Boot API 프로젝트입니다.

## 기술 스택
- Java 17
- Spring Boot 3.2
- Gradle
- Spring Web / JPA / Validation
- H2 Database

## 주요 기능
- 브랜드별 상품 가격 비교
- 카테고리별 최저가/최고가 상품 조회
- 브랜드 및 상품 관리 (CRUD)

## API Endpoints
- GET /brands/cheapest - 브랜드별 최저가 상품 조회
- GET /categories/price-range - 카테고리별 가격 범위 조회
- POST /brands - 브랜드 등록
- PUT /brands/{id} - 브랜드 수정
- DELETE /brands/{id} - 브랜드 삭제

## 실행 방법
1. Gradle build
```./gradlew build```

2. 애플리케이션 실행
```./gradlew bootRun```

3. API 접근
```http://localhost:8080/api```

## H2 Console
- URL: http://localhost:8080/api/h2-console
- JDBC URL: jdbc:h2:mem:branddb
- Username: sa
- Password: (empty)