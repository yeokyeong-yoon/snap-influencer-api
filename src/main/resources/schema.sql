-- 브랜드 테이블을 생성합니다. 이 테이블은 각 브랜드의 고유 ID와 이름을 저장합니다.
CREATE TABLE IF NOT EXISTS brand
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 제품 테이블을 생성합니다. 이 테이블은 각 제품의 고유 ID, 브랜드 ID, 카테고리 및 가격을 저장합니다.
-- 브랜드 ID는 브랜드 테이블의 ID를 참조하여, 각 제품이 어떤 브랜드에 속하는지를 나타냅니다.
CREATE TABLE IF NOT EXISTS product
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id BIGINT      NOT NULL,
    category VARCHAR(50) NOT NULL,
    price    INT         NOT NULL,
    FOREIGN KEY (brand_id) REFERENCES brand (id)
); 
