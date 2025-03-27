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
  console.log('Fetching lowest prices by category...');
  try {
    const response = await fetch(`${API_BASE_URL}/products/lowest-prices`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    console.log('Response status:', response.status);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log('Response data:', data);

    if (!data.success || !data.data || !data.data.categories) {
      throw new Error('Invalid response structure');
    }

    const result = data.data;
    let html = '<table class="result-table">';
    html += '<tr><th>카테고리</th><th>브랜드</th><th>가격</th></tr>';

    result.categories.forEach(category => {
      if (!category || !category.category || !category.brandPrices) {
        console.error('Invalid category data:', category);
        return;
      }
      const brands = category.brandPrices.map(
          bp => `${bp.brand} (${formatPrice(bp.price)})`).join(', ');
      html += `
                <tr>
                    <td>${category.category}</td>
                    <td>${brands}</td>
                    <td>${formatPrice(category.brandPrices[0].price)}</td>
                </tr>
            `;
    });

    html += '</table>';
    html += `<div class="total-price">총액: ${formatPrice(
        result.totalPrice)}</div>`;

    document.getElementById('lowestPricesResult').innerHTML = html;
  } catch (error) {
    console.error('Error fetching lowest prices:', error);
    document.getElementById('lowestPricesResult').innerHTML = `
            <div class="error">
                Error: ${error.message}
            </div>
        `;
  }
}

// 최저가 브랜드 세트 조회
async function findCheapestBrandTotal() {
  try {
    // Get selected categories
    const selectedCategories = [
      'TOP', 'OUTER', 'PANTS', 'SNEAKERS', 'BAG', 'HAT', 'SOCKS', 'ACCESSORY'
    ].filter(category => document.getElementById(
        `${category.toLowerCase()}Check`).checked);

    if (selectedCategories.length === 0) {
      document.getElementById('cheapestBrandResult').innerHTML = `
                <div class="error">
                    최소한 하나의 카테고리를 선택해주세요.
                </div>
            `;
      return;
    }

    console.log('Frontend: Selected categories:', selectedCategories);
    console.log('Frontend: Making API call to /products/cheapest-brand');

    const response = await fetch(`${API_BASE_URL}/products/cheapest-brand`, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({categories: selectedCategories})
    });

    console.log('Frontend: Response status:', response.status);
    console.log('Frontend: Response headers:',
        Object.fromEntries(response.headers.entries()));

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

    if (!data.success || !data.data || !Array.isArray(data.data)) {
      console.error('Frontend: Invalid data structure:', data);
      throw new Error('Invalid response structure');
    }

    const brandTotals = data.data;
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
async function findPriceRangeByCategory(category) {
  console.log('Fetching price range for category:', category);
  try {
    const response = await fetch(
        `${API_BASE_URL}/products/categories/${category}/price-range`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });
    console.log('Response status:', response.status);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log('Response data:', data);

    if (!data.success) {
      throw new Error(data.message || 'Failed to fetch price range');
    }

    return data.data;
  } catch (error) {
    console.error('Error fetching price range:', error);
    throw error;
  }
}

// 브랜드 등록
async function registerBrand() {
  const brandName = document.getElementById('brandName').value.trim();
  if (!brandName) {
    alert('브랜드 이름을 입력해주세요.');
    return;
  }

  console.log('Registering brand:', brandName);
  try {
    const response = await fetch(`${API_BASE_URL}/admin/brands`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({name: brandName})
    });
    console.log('Response status:', response.status);

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(
          errorData.message || `HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log('Response data:', data);

    if (!data.success) {
      throw new Error(data.message || 'Failed to register brand');
    }

    alert('브랜드가 성공적으로 등록되었습니다.');
    document.getElementById('brandName').value = '';
  } catch (error) {
    console.error('Error registering brand:', error);
    alert('브랜드 등록 실패: ' + error.message);
  }
}

// 상품 등록
async function registerProduct() {
  const brand = document.getElementById('productBrand').value.trim();
  const category = document.getElementById('productCategory').value;
  const price = document.getElementById('productPrice').value;

  if (!brand || !category || !price) {
    alert('모든 필드를 입력해주세요.');
    return;
  }

  if (isNaN(price) || parseInt(price) <= 0) {
    alert('올바른 가격을 입력해주세요.');
    return;
  }

  console.log('Registering product:', {brand, category, price});
  try {
    const response = await fetch(`${API_BASE_URL}/admin/products`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        brand: brand,
        category: category.toUpperCase(),
        price: parseInt(price)
      })
    });
    console.log('Response status:', response.status);

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(
          errorData.message || `HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log('Response data:', data);

    if (!data.success) {
      throw new Error(data.message || 'Failed to register product');
    }

    alert('상품이 성공적으로 등록되었습니다.');
    document.getElementById('productBrand').value = '';
    document.getElementById('productCategory').value = '';
    document.getElementById('productPrice').value = '';
    loadProducts();
  } catch (error) {
    console.error('Error registering product:', error);
    alert('상품 등록 실패: ' + error.message);
  }
}

// 카테고리별 가격 범위 조회
async function findPriceRangeByCategory() {
  let response;
  try {
    console.log('Fetching price range by category...');
    const category = document.getElementById('categorySelect').value;
    console.log('Selected category:', category);

    response = await fetch(
        `${API_BASE_URL}/products/categories/${category}/price-range`, {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          }
        });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const responseText = await response.text();
    console.log('Raw response:', responseText);

    if (!responseText) {
      throw new Error('Empty response received');
    }

    const data = JSON.parse(responseText);
    console.log('Received data:', data);

    if (!data || !data.success || !data.data || !data.data.lowestPrices
        || !data.data.highestPrices) {
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
    document.getElementById('priceRangeResult').innerHTML = `
            <div class="alert alert-danger">
                오류가 발생했습니다: ${error.message}
            </div>
        `;
  }
}

// 상품 목록 조회
async function loadProducts() {
  console.log('Loading all products...');
  try {
    const response = await fetch(`${API_BASE_URL}/admin/products`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    console.log('Response status:', response.status);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log('Response data:', data);

    if (!data.success) {
      throw new Error(data.message || 'Failed to load products');
    }

    displayProducts(data.data);
    return data.data;
  } catch (error) {
    console.error('Error loading products:', error);
    document.getElementById('productList').innerHTML = `
            <div class="error">
                상품 목록을 불러오는데 실패했습니다: ${error.message}
            </div>
        `;
    throw error;
  }
}

// 상품 목록 표시
function displayProducts(products) {
  console.log('Displaying products:', products);
  const productList = document.getElementById('productList');
  if (!productList) {
    console.error('Product list element not found');
    return;
  }

  if (!products || products.length === 0) {
    productList.innerHTML = '<div class="no-data">등록된 상품이 없습니다.</div>';
    return;
  }

  const brandFilter = document.getElementById(
      'brandFilter').value.toLowerCase();
  const categoryFilter = document.getElementById('categoryFilter').value;

  // 필터링 적용
  const filteredProducts = products.filter(product => {
    const matchesBrand = !brandFilter ||
        product.brand.name.toLowerCase().includes(brandFilter);
    const matchesCategory = !categoryFilter ||
        product.category === categoryFilter;
    return matchesBrand && matchesCategory;
  });

  if (filteredProducts.length === 0) {
    productList.innerHTML = '<div class="no-data">검색 조건에 맞는 상품이 없습니다.</div>';
    return;
  }

  let html = '<table class="table">';
  html += `
        <thead>
            <tr>
                <th>ID</th>
                <th>브랜드</th>
                <th>카테고리</th>
                <th>가격</th>
                <th>작업</th>
            </tr>
        </thead>
        <tbody>
    `;

  filteredProducts.forEach(product => {
    html += `
            <tr>
                <td>${product.id}</td>
                <td>${product.brand.name}</td>
                <td>${product.category}</td>
                <td>${formatPrice(product.price)}</td>
                <td>
                    <button class="btn btn-danger btn-sm" onclick="deleteProduct(${product.id})">
                        삭제
                    </button>
                </td>
            </tr>
        `;
  });

  html += '</tbody></table>';
  productList.innerHTML = html;
}

// 상품 삭제
async function deleteProduct(productId) {
  console.log('Deleting product with ID:', productId);
  if (!confirm('정말로 이 상품을 삭제하시겠습니까?')) {
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/admin/products/${productId}`,
        {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json'
          }
        });
    console.log('Response status:', response.status);

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(
          errorData.message || `HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log('Response data:', data);

    if (!data.success) {
      throw new Error(data.message || 'Failed to delete product');
    }

    alert('상품이 성공적으로 삭제되었습니다.');
    loadProducts(); // Refresh the product list
  } catch (error) {
    console.error('Error deleting product:', error);
    alert('상품 삭제 실패: ' + error.message);
  }
}

// 섹션 전환 함수
function switchSection(sectionId) {
  // 모든 섹션 숨기기
  document.querySelectorAll('section').forEach(section => {
    section.classList.remove('section-active');
  });

  // 모든 네비게이션 링크 비활성화
  document.querySelectorAll('.nav-link').forEach(link => {
    link.classList.remove('active');
  });

  // 선택된 섹션 표시
  document.getElementById(sectionId).classList.add('section-active');

  // 해당하는 네비게이션 링크 활성화
  const navLink = document.querySelector(`.nav-link[onclick*="${sectionId}"]`);
  if (navLink) {
    navLink.classList.add('active');
  }

  // URL 해시 업데이트
  window.location.hash = sectionId;
}

// URL 해시 변경 시 해당 섹션으로 전환
window.addEventListener('hashchange', () => {
  const hash = window.location.hash.slice(1);
  if (hash && (hash === 'customer' || hash === 'admin')) {
    switchSection(hash);
  }
});

// 페이지 로드 시 URL 해시에 따라 섹션 전환
document.addEventListener('DOMContentLoaded', () => {
  loadProducts();

  // 브랜드 필터 입력 시 자동 검색
  document.getElementById('brandFilter').addEventListener('input',
      debounce(() => {
        loadProducts();
      }, 300));

  // 카테고리 선택 시 자동 검색
  document.getElementById('categoryFilter').addEventListener('change', () => {
    loadProducts();
  });

  // URL 해시에 따라 초기 섹션 설정
  const hash = window.location.hash.slice(1);
  if (hash && (hash === 'customer' || hash === 'admin')) {
    switchSection(hash);
  } else {
    switchSection('customer'); // 기본값
  }
});

// 디바운스 함수
function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
} 
