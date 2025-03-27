// API 기본 URL
const API_BASE_URL = 'http://localhost:8080/api';

// 에러 처리 함수
async function handleError(error, response) {
    console.error('Error:', error);
    let errorMessage = error.message;
    
    if (response) {
        console.error('Response status:', response.status);
        console.error('Response headers:', response.headers);
        try {
            const errorBody = await response.text();
            console.error('Response body:', errorBody);
            if (errorBody) {
                try {
                    const errorJson = JSON.parse(errorBody);
                    errorMessage = errorJson.message || errorMessage;
                } catch (e) {
                    errorMessage = errorBody;
                }
            }
        } catch (e) {
            console.error('Error reading response:', e);
        }
    }
    
    alert('오류가 발생했습니다: ' + errorMessage);
}

// 숫자 포맷팅 함수
function formatPrice(price) {
    return new Intl.NumberFormat('ko-KR').format(price) + '원';
}

// 카테고리별 최저가 브랜드 조회
async function findLowestPricesByCategory() {
    let response;
    try {
        console.log('Frontend: Calling API - GET /products/lowest-prices');
        response = await fetch(`${API_BASE_URL}/products/lowest-prices`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        });
        console.log('Frontend: Received response status:', response.status);
        console.log('Frontend: Response headers:', Object.fromEntries(response.headers.entries()));
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const responseText = await response.text();
        console.log('Frontend: Raw response:', responseText);
        
        if (!responseText) {
            throw new Error('Empty response received');
        }
        
        const data = JSON.parse(responseText);
        console.log('Frontend: Parsed data:', data);
        
        if (!data || !data.success || !data.data || !data.data.categories) {
            console.error('Frontend: Invalid data structure:', data);
            throw new Error('Invalid response data structure');
        }
        
        let html = '<table class="result-table">';
        html += '<tr><th>카테고리</th><th>브랜드</th><th>가격</th></tr>';
        
        data.data.categories.forEach(category => {
            console.log('Frontend: Processing category:', category);
            if (!category || !category.category || !category.brandPrices) {
                console.error('Frontend: Invalid category data:', category);
                return;
            }
            const brands = category.brandPrices.map(bp => `${bp.brand} (${formatPrice(bp.price)})`).join(', ');
            html += `
                <tr>
                    <td>${category.category}</td>
                    <td>${brands}</td>
                    <td>${formatPrice(category.brandPrices[0].price)}</td>
                </tr>
            `;
        });
        
        html += '</table>';
        html += `<div class="total-price">총액: ${formatPrice(data.data.totalPrice)}</div>`;
        
        document.getElementById('lowestPricesResult').innerHTML = html;
    } catch (error) {
        console.error('Frontend: Error in findLowestPricesByCategory:', error);
        handleError(error, response);
    }
}

// 최저가 브랜드 세트 조회
async function findCheapestBrandTotal() {
    try {
        // Get selected categories
        const selectedCategories = [
            'TOP', 'OUTER', 'PANTS', 'SNEAKERS', 'BAG', 'HAT', 'SOCKS', 'ACCESSORY'
        ].filter(category => document.getElementById(`${category.toLowerCase()}Check`).checked);

        if (selectedCategories.length === 0) {
            document.getElementById('cheapestBrandResult').innerHTML = `
                <div class="error">
                    최소한 하나의 카테고리를 선택해주세요.
                </div>
            `;
            return;
        }

        console.log('Frontend: Selected categories:', selectedCategories);
        console.log('Frontend: Making API call to /brands/cheapest');
        
        const response = await fetch(`${API_BASE_URL}/brands/cheapest`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ categories: selectedCategories })
        });

        console.log('Frontend: Response status:', response.status);
        console.log('Frontend: Response headers:', Object.fromEntries(response.headers.entries()));

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const responseText = await response.text();
        console.log('Frontend: Raw response:', responseText);

        if (!responseText) {
            throw new Error('Empty response received');
        }

        const data = JSON.parse(responseText);
        console.log('Frontend: Parsed response:', data);

        if (!data.success || !data.data || !data.data.cheapestBrands) {
            console.error('Frontend: Invalid data structure:', data);
            throw new Error('Invalid response structure');
        }

        const brandTotals = data.data.cheapestBrands;
        console.log('Frontend: Brand totals:', brandTotals);

        if (brandTotals.length === 0) {
            document.getElementById('cheapestBrandResult').innerHTML = `
                <div class="error">
                    선택한 카테고리의 모든 상품을 보유한 브랜드가 없습니다.
                </div>
            `;
            return;
        }

        let html = '<h3>최저가 브랜드 세트</h3>';
        brandTotals.forEach(brandTotal => {
            html += `
                <div class="brand-total">
                    <h4>브랜드: ${brandTotal.brand}</h4>
                    <p>총액: ${formatPrice(brandTotal.total)}</p>
                    <h5>카테고리별 가격:</h5>
                    <ul>
                        ${brandTotal.categoryPrices.map(cp => `
                            <li>${cp.category}: ${formatPrice(cp.price)}</li>
                        `).join('')}
                    </ul>
                </div>
            `;
        });

        document.getElementById('cheapestBrandResult').innerHTML = html;
    } catch (error) {
        console.error('Frontend: Error in findCheapestBrandTotal:', error);
        document.getElementById('cheapestBrandResult').innerHTML = `
            <div class="error">
                Error: ${error.message}
            </div>
        `;
    }
}

// 카테고리별 가격 범위 조회
async function findPriceRangeByCategory() {
    try {
        console.log('Fetching price range by category...');
        const category = document.getElementById('categorySelect').value;
        console.log('Selected category:', category);
        
        const response = await fetch(`${API_BASE_URL}/categories/${category}/price-range`);
        const data = await response.json();
        console.log('Received data:', data);
        
        if (!data || !data.success || !data.data || !data.data.lowestPrices || !data.data.highestPrices) {
            console.error('Invalid data structure:', data);
            throw new Error('Invalid response data structure');
        }
        
        let html = '<div class="card">';
        html += '<div class="card-body">';
        
        // 최저가 브랜드들
        html += '<h6 class="card-subtitle mb-2">최저가 브랜드</h6>';
        html += '<table class="result-table mb-4">';
        html += '<tr><th>브랜드</th><th>가격</th></tr>';
        data.data.lowestPrices.forEach(price => {
            console.log('Processing lowest price:', price);
            html += `
                <tr>
                    <td>${price.brand}</td>
                    <td>${formatPrice(price.price)}</td>
                </tr>`;
        });
        html += '</table>';
        
        // 최고가 브랜드들
        html += '<h6 class="card-subtitle mb-2">최고가 브랜드</h6>';
        html += '<table class="result-table">';
        html += '<tr><th>브랜드</th><th>가격</th></tr>';
        data.data.highestPrices.forEach(price => {
            console.log('Processing highest price:', price);
            html += `
                <tr>
                    <td>${price.brand}</td>
                    <td>${formatPrice(price.price)}</td>
                </tr>`;
        });
        html += '</table>';
        
        html += '</div></div>';
        
        document.getElementById('priceRangeResult').innerHTML = html;
    } catch (error) {
        console.error('Error in findPriceRangeByCategory:', error);
        handleError(error, response);
    }
}

// 브랜드 등록
async function registerBrand() {
    try {
        const brandName = document.getElementById('brandName').value;
        if (!brandName) {
            alert('브랜드 이름을 입력해주세요.');
            return;
        }
        
        const response = await fetch(`${API_BASE_URL}/admin/brands`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ name: brandName })
        });
        
        if (response.ok) {
            alert('브랜드가 등록되었습니다.');
            document.getElementById('brandName').value = '';
        } else {
            const error = await response.json();
            throw new Error(error.message);
        }
    } catch (error) {
        handleError(error, response);
    }
}

// 상품 등록
async function registerProduct() {
    try {
        const brand = document.getElementById('productBrand').value;
        const category = document.getElementById('productCategory').value;
        const price = parseInt(document.getElementById('productPrice').value);
        
        if (!brand || !category || !price) {
            alert('모든 필드를 입력해주세요.');
            return;
        }
        
        const response = await fetch(`${API_BASE_URL}/admin/products`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                brand: brand,
                category: category,
                price: price
            })
        });
        
        if (response.ok) {
            alert('상품이 등록되었습니다.');
            document.getElementById('productBrand').value = '';
            document.getElementById('productPrice').value = '';
            loadProducts();
        } else {
            const error = await response.json();
            throw new Error(error.message);
        }
    } catch (error) {
        handleError(error, response);
    }
}

// 상품 목록 조회
async function loadProducts() {
    let response;
    try {
        console.log('Frontend: Calling API - GET /admin/products');
        response = await fetch(`${API_BASE_URL}/admin/products`);
        console.log('Frontend: Received response status:', response.status);
        console.log('Frontend: Response headers:', Object.fromEntries(response.headers.entries()));
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const responseText = await response.text();
        console.log('Frontend: Raw response:', responseText);
        
        if (!responseText) {
            throw new Error('Empty response received');
        }
        
        const data = JSON.parse(responseText);
        console.log('Frontend: Parsed data:', data);
        
        if (!data || !data.success || !data.data) {
            console.error('Frontend: Invalid data structure:', data);
            throw new Error('Invalid response data structure');
        }

        const products = data.data;
        console.log('Frontend: Processing products:', products);
        
        let html = '<div class="list-group">';
        products.forEach(product => {
            html += `
                <div class="product-item mb-2 p-3 border rounded">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <strong>${product.brand}</strong> - ${product.category}
                            <br>
                            <span class="text-primary">${formatPrice(product.price)}</span>
                        </div>
                        <div>
                            <button class="btn btn-sm btn-danger" onclick="deleteProduct(${product.id})">삭제</button>
                        </div>
                    </div>
                </div>`;
        });
        html += '</div>';
        
        document.getElementById('productList').innerHTML = html;
    } catch (error) {
        console.error('Frontend: Error in loadProducts:', error);
        handleError(error, response);
    }
}

// 상품 삭제
async function deleteProduct(productId) {
    try {
        if (!confirm('정말 이 상품을 삭제하시겠습니까?')) {
            return;
        }
        
        const response = await fetch(`${API_BASE_URL}/admin/products/${productId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            alert('상품이 삭제되었습니다.');
            loadProducts();
        } else {
            const error = await response.json();
            throw new Error(error.message);
        }
    } catch (error) {
        handleError(error, response);
    }
}

// 페이지 로드 시 상품 목록 조회
document.addEventListener('DOMContentLoaded', () => {
    loadProducts();
}); 