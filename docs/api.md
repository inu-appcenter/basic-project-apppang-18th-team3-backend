# 앱팡 API 명세서

Claude 같은 LLM이 빠르게 읽고 활용할 수 있도록 CSV 원본을 구조화한 Markdown 버전입니다.

## 문서 개요

- 총 API 수: 41
- 정리 기준: `도메인 > API > Method/Path > 인증 > Query Params > Request > Success Response > Failure Response`
- 원본 기준: CSV 원본 내용을 최대한 보존했고, 메모성 주석도 함께 유지했습니다.

## 도메인 목록

- [검색](#검색) (4개)
- [인증](#인증) (7개)
- [주문](#주문) (5개)
- [찜](#찜) (3개)
- [상품](#상품) (2개)
- [회원](#회원) (3개)
- [배송지](#배송지) (4개)
- [챗봇](#챗봇) (2개)
- [리뷰](#리뷰) (2개)
- [카테고리](#카테고리) (1개)
- [장바구니](#장바구니) (4개)
- [메인페이지](#메인페이지) (3개)
- [마이페이지](#마이페이지) (1개)

## 검색

### 검색어 저장

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/search/history` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
{ "keyword": "러닝화" }
```

#### Success Response
```text
204
```

#### Failure Response
```text
400 {
"error : "검색어를 입력해주세요."
} 

400 {
"error : "검색어는 50자 이하로 입력해주세요."
} 

401 {
"error :"로그인이 필요합니다."
}
```

### 검색어 삭제

| 항목 | 내용 |
| --- | --- |
| Method | `DELETE` |
| Path | `/api/search/history` |
| 인증 | 필요 |
| Query Params | keyword |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
204
```

#### Failure Response
```text
401{
"error : "인증이 필요합니다."
}

400{
"error : "keyword는 빈 값일 수 없습니다."
}
```

### 검색어 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/search/history` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "items": [ 
  { "keyword": "러닝화", "searchedAt": "2026-06-20T10:00:00" }, 
  { "keyword": "선풍기", "searchedAt": "2026-06-19T18:00:00" } 
] 
}
```

#### Failure Response
```text
401 {
"error :"로그인이 필요합니다."
}
```

### 자동완성

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/search/suggestions` |
| 인증 | 불필요 |
| Query Params | keyword (예: 티) |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200 { "suggestions": [] }  // 매칭 없으면 빈 배열
200 { "suggestions": ["티셔츠", "티팬티", "티세트"] }  // 매칭 있으면 최대 10개
```

#### Failure Response
```text
없음
```

## 인증

### 로그아웃

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/auth/logout` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200 { "message": "로그아웃되었습니다" }
```

#### Failure Response
```text
없음
```

### 이메일 중복 확인(가입 화면 실시간)

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/auth/check-email` |
| 인증 | 불필요 |
| Query Params | email (required, string, 이메일 형식) — 예: mailto:a@a.com |

#### Request
```text
(본문 없음, 쿼리 파라미터)
```

#### Success Response
```text
200 
{ 
"available": false // false = 이미 사용 중
}
```

#### Failure Response
```text
400 
{ 
"error": "이메일 형식이 올바르지 않습니다." 
}
400
{
"error": "이메일을 입력해주세요."
}
```

### 로그인

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/auth/login` |
| 인증 | 불필요 |
| Query Params | 없음 |

#### Request
```text
{ 
"email": "mailto:a@a.com", 
"password": "abcd1234" 
}
```

#### Success Response
```text
200
{ 
"token": "eyJhbGci...", 
"user": { "user_id": 1, "name": "정태영" } 
}
```

#### Failure Response
```text
401 { "error": "이메일 또는 비밀번호가 올바르지 않습니다" }
400 { "error": "이메일과 비밀번호를 입력해주세요" }
```

### 비밀번호 재설정 본인인증

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/auth/password-reset-verify` |
| 인증 | 불필요 |
| Query Params | 없음 |

#### Request
```text
{ "email": "mailto:a@a.com", "name": "고명재", "phoneNumber": "01012345678" }
```

#### Success Response
```text
200 { 
"resetToken": "tmp_eyJ..." } 
// 재설정용 임시 토큰(만료 시간 15분)
```

#### Failure Response
```text
400 {
"error": "이메일 형식이 올바르지 않습니다"
}

400 {
"error": "휴대폰 번호 형식이 올바르지 않습니다"
}

400 {
"error": "이름은 필수입니다"
}

404 { "error": "일치하는 회원 정보를 찾을 수 없습니다" }
// 일치하면 화면의 '비밀번호 재설정 영역'을 활성화
```

### 비밀번호 재설정

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/auth/reset-password` |
| 인증 | 리셋 토큰 필요 |
| Query Params | 없음 |

#### Request
```text
{ 
"resetToken": "tmp_eyJ...", 
"newPassword": "newpass12" 
}
```

#### Success Response
```text
200 { 
"message": "비밀번호가 재설정되었습니다" 
}
```

#### Failure Response
```text
400 { 
"error": "비밀번호는 8자 이상, 영문+숫자 조합이어야 합니다" 
}
401 { // 토큰 검증이 우선됨
"error": "유효하지 않거나 만료된 요청입니다" 
}
```

### 아이디(이메일) 찾기

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/auth/find-email` |
| 인증 | 불필요 |
| Query Params | 없음 |

#### Request
```text
{ 
"name": "정태영", 
"phoneNumber": "01012345678" 
}
```

#### Success Response
```text
200
{
"emails": ["rha*@gmail.com", "kim*@naver.com"]
}
```

#### Failure Response
```text
400
{
"error": "이름을 입력해주세요"
}

400
{
"error": "전화번호 형식이 올바르지 않습니다"
}

404 
{ 
"error": "일치하는 회원 정보를 찾을 수 없습니다" 
}
```

### 회원가입

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/auth/signup` |
| 인증 | 불필요 |
| Query Params | 없음 |

#### Request
```text
{ 
"email": "mailto:a@a.com", 
"password": "abcd1234", 
"name": "홍길동", 
"phoneNumber": "01012345678", 
"agreedRequiredTerms": true, 
"agreedMarketing": false 
}
```

#### Success Response
```text
201
{ 
"user_id": 1, 
"email": "mailto:a@a.com", 
"name": "고명재" 
}
```

#### Failure Response
```text
409 
{ "error": "이미 가입된 이메일입니다" }
400 
{ "error": "올바른 이메일 형식을 입력해주세요" }
400 
{ "error": "비밀번호는 8자 이상, 영문+숫자 조합이어야 합니다" }
400 
{ "error": "필수 약관에 동의해주세요" }
```

## 주문

### 주문 상세

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/orders/{order_id}` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "order_id": 100, "order_date": "2026-06-15T14:00:00", "order_status": "진행중",
"payment": { "product_amount": 50000, "discount_amount": 8000, "shipping_fee": 0, "payment_method": "app_money", "total_price": 42000 },
"shipping": { "recipient_name": "고명재", "phone": "01012345678", "zipcode": "06241", "address": "서울시 강남구 ...", "detail_address": "101동 202호", "delivery_request": "문 앞에 놓아주세요" },
"order_items": [ { "order_item_id": 1, "product_id": 5, "name": "여름 티셔츠", "image_url": "...", "price": 15000, "quantity": 2, "delivery_type": "로켓배송", "status": "배송중", "is_available": true },
{ "order_item_id": 2, "product_id": 8, "name": "운동화", "image_url": "...", "price": 30000, "quantity": 1, "delivery_type": "일반배송", "status": "배송완료", "is_available": true } ] }
```

#### Failure Response
```text
404 { "error": "주문 정보를 불러올 수 없습니다. 다시 시도해주세요" }
```

### 주문 생성

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/orders` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
{ 
"address_id": 3, 
"items": [ { "product_id": 5, "quantity": 2 } ], "payment_method": "app_money",
"delivery_request": "문 앞에 놓아주세요"
}
```

#### Success Response
```text
201
{ 
"order_id": 100, 
"total_price": 42000, 
"status": "주문접수", 
"remaining_money": 8000
}
```

#### Failure Response
```text
400 // address_id 누락
{ "error": "배송지를 선택해주세요" } 
400 // payment_method 누락
{ "error": "결제 수단을 선택해주세요" } 
402
{ "error": "앱팡 머니 잔액이 부족합니다" }
409 
{ "error": "재고가 부족합니다" }
```

### 내 주문 목록

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/orders` |
| 인증 | 필요 |
| Query Params | ?page=1&size=10 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "page": 1, "total": 12,
"orders": [ { "order_id": 100, "order_date": "2026-06-15T14:00:00", "order_status": "진행중", // 주문 전연 마막 닫기대 상태(취소 당) — 선택
"order_items": [ { "order_item_id": 1, "product_id": 5, "name": "여름 티셔츠", "image_url": "https://.../5.jpg", "price": 15000, "quantity": 2, "delivery_type": "로켓배송", -  상품별 배송 유형
"status": "배송중", -  🆕 아이템별 배송 상태
"is_available": true },
{ "order_item_id": 2, "product_id": 8, "name": "운동화", "image_url": "https://.../8.jpg", "price": 30000, "quantity": 1, "delivery_type": "일반배송", "status": "배송완료", -  상품도 다때 상태가 다를 수 있음
"is_available": true } ] } ] }
// OrderItem 주문마다 자기 status·delivery_type 표시 (느짜 그맹화는 프론트)
// 주문 없으면 orders: [] → "주문 내역이 없습니다"
```

#### Failure Response
```text
없음
```

### 결제 예상 금액 (서버계산)

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/orders/estimate` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
{ "items": [ { "product_id": 5, "quantity": 2 } ] }
// 장바구니에서 넘어오면 cart_item_ids로 보내도 됨
```

#### Success Response
```text
200
{ "product_amount": 50000, // 총 상품 금액(정가 합)
"discount_amount": 8000, // 총 할인 금액
"shipping_fee": 0, // 배송비(정책 적용)
"total_price": 42000 // 총 결제 금액
}
```

#### Failure Response
```text
없음
```

### 주문 취소

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/orders/{order_id}/cancel` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "order_id": 100, "order_status": "취소됨", "refund_money": 42000, "remaining_money": 92000 }
```

#### Failure Response
```text
409 { "error": "이미 배송이 시작되어 취소할 수 없습니다" }
// 모든 order_item이 '주문접수' 상태일 때만 취소 가능
// 취소 시 order_items 전부 status='취소됨', 재고 복구, 머니 환불 (트랜잭션)
```

## 찜

### 찜 해제

| 항목 | 내용 |
| --- | --- |
| Method | `DELETE` |
| Path | `/api/wishlist/{product_id}` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
204 (본문 없음)
```

#### Failure Response
```text
없음
```

### 찜하기

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/wishlist` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
{ "product_id": 5 }
```

#### Success Response
```text
201 { "wishlist_id": 3, "product_id": 5 }
```

#### Failure Response
```text
409 { "error": "이미 찜한 상품입니다" }
```

### 내 찜 목록

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/wishlist` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "items": [{ "wishlist_id": 3, "product_id": 5, "name": "여름 티셔츠","price": 15000, "image_url": "https://.../5.jpg" }] }
```

#### Failure Response
```text
없음
```

## 상품

### 상품 목록 (카테고리·검색, 필터, 정렬·페이지) 공용

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/products` |
| 인증 | 불필요 |
| Query Params | [전부 선택값, 필요한 것만 조합]<br>category_id=2 카테고리 필터 (카테고리 페이지)<br>&keyword=러닝 검색어 (검색 결과 페이지)<br>&min_price=10000 최소 가격 필터<br>&max_price=50000 최대 가격 필터<br>&rocket_delivery=true 로켓배송 상품만<br>&sort=sales 정렬 기준 (sales/reviews/rating/price_desc/price_asc)<br>&page=1 페이지 번호 (기본 1)<br>&size=20 한 페이지 개수 (기본 20)<br><br>[sort 옵션]<br>sales 판매량순(기본값) / reviews 리뷰 많은순 / rating 별점순 / price_desc 높은 가격순 / price_asc 낮은 가격순 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "category_id": 2, // 카테고리 조회 시, 아니면 null
"category_name": "신발", // 카테고리 조회 시, 아니면 null
"keyword": "러닝", // 검색 시, 아니면 null
"page": 1, "size": 20, "total": 134, "has_next": true,
"items": [ { "product_id": 5, "name": "여름 티셔츠", "image_url": "https://.../5.jpg", "option_info": "100g, 1개", "price": 12000, "original_price": 15000, "discount_rate": 20, "unit_price": "100g당 12,000원", "average_rating": 4.3, "review_count": 28, "shipping_info": "무료배송 / 반품 가능", "rocket_delivery": true } ] }

[예외 처리]
상품/검색 결과 없음 → items: [], total: 0 (카테고리: "등록된 상품이 없습니다" / 검색: "검색 결과가 없습니다")
필터 결과 없음 → total: 0 → "조건에 맞는 상품이 없습니다"
마지막까지 조회 → has_next: false → "마지막 상품입니다"
정렬 기준에 상품 없음 → sort 기본값(sales)로 처리
이미지 로드 실패 → 프론트가 기본 이미지 표시
```

#### Failure Response
```text
없음
```

### 상품 상세

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/products/{product_id}` |
| 인증 | 불필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "product_id": 5, "brand": "나이키", -  브랜드 추가
"name": "여름 티셔츠",
"images": [ "https://.../5_1.jpg", "https://.../5_2.jpg" ], -  이미지 슬라이드(다중) 추가
"option_info": "100g, 1개", "price": 12000, "original_price": 15000, "discount_rate": 20, "unit_price": "100g당 12,000원", "shipping_info": "무료배송 / 반품 가능", "rocket_delivery": true, -  추가
"stock": 32, "description": "시원한 여름용 티셔츠",
"detail_images": [ "https://.../detail1.jpg" ], -  상세 설명 이미지 추가
"category_id": 2, "is_wished": false, -  🔒 찜 버튼 상태(로그인 시)
"can_write_review": false, -  🔒 구매이려 있으면 true
"review_summary": { "average_rating": 4.3, "review_count": 28, "photo_thumbnails": [ "https://.../r1.jpg" ], "top_reviews": [ { "review_id": 7, "rating": 5, "user_name": "고", "created_at": "2026-06-10T11:00:00", "content": "배송 빠르고 좋아요", "image_url": "https://.../r1.jpg" } ] } }
```

#### Failure Response
```text
404 { "error": "상품 정보를 불러올 수 없습니다" }
```

## 회원

### 내 정보 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/users/me` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
※ 토큰의 user_id 기준
```

#### Success Response
```text
200
{ "userId": 1, "email": "mailto:a@a.com", "name": "고명재", "phoneNumber": "01012345678", "appMoney": 15000, "createdAt": "..." }
```

#### Failure Response
```text
없음
```

### 내 정보 수정

| 항목 | 내용 |
| --- | --- |
| Method | `PATCH` |
| Path | `/api/users/me` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(바꿀 항목만) { "email": "mailto:new@a.com" } // 또는 name / phone
```

#### Success Response
```text
200 { "userId": 1, "email": "mailto:new@a.com", "name": "고명재", "phoneNumber": "01012345678" }
```

#### Failure Response
```text
400 { "error": "이름을 입력해주세요" } // 이름 공백
400 { "error": "올바른 이메일 형식을 입력해주세요" } // 이메일 형식
400 { "error": "올바른 휴대폰 번호를 입력해주세요" } // 휴대폰 형식
409 { "error": "이미 사용 중인 이메일입니다" } // 이메일 중복
```

### 비밀번호 변경

| 항목 | 내용 |
| --- | --- |
| Method | `PATCH` |
| Path | `/api/users/me/password` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
{ "currentPassword": "old1234", "newPassword": "new12345" }
```

#### Success Response
```text
200 { "message": "비밀번호가 변경되었습니다" }
```

#### Failure Response
```text
401: 인증 실패 { "error": "인증이 필요합니다." }
401: 현재 비밀번호 불일치 { "error": "현재 비밀번호가 올바르지 않습니다." }
400: 필드 누락 { "error": "현재/새 비밀번호를 입력해주세요." }
400: 형식 오류 { "error": "비밀번호는 8자 이상, 영문+숫자 조합이어야 합니다." }
```

## 배송지

### 배송지 삭제

| 항목 | 내용 |
| --- | --- |
| Method | `DELETE` |
| Path | `/api/addresses/{address_id}` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
204 (본문 없음)
```

#### Failure Response
```text
없음
```

### 배송지 추가

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/addresses` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
{ "recipient_name": "고명재", "phone": "01012345678", "address": "서울시 강남구 ...", "detail_address": "101동 202호", "zipcode": "06241", -  우편번호 추가
"normal_delivery_request": "문 앞에 놓아주세요", -  추가
"rocket_delivery_request": "경비실에 맡겨주세요", -  추가
"is_default": true }
```

#### Success Response
```text
201 { "address_id": 3 }
```

#### Failure Response
```text
400 { "error": "필수 정보를 입력해주세요" } // 필수값 누락
400 { "error": "올바른 휴대폰 번호를 입력해주세요" } // 형식 오류
```

### 내 배송지 목록

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/addresses` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "items": [ { "address_id": 3, "recipient_name": "고명재", "phone": "01012345678", "address": "서울시 강남구 ...", "detail_address": "101동 202호", "zipcode": "06241", "is_default": true, "normal_delivery_request": "문 앞에 놓아주세요", -  추가
"rocket_delivery_request": "경비실에 맡겨주세요" -  추가
} ] }
// 없으면 items: [] → "등록된 배송지가 없습니다"
```

#### Failure Response
```text
없음
```

### 배송지 수정

| 항목 | 내용 |
| --- | --- |
| Method | `PATCH` |
| Path | `/api/addresses/{address_id}` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(바꿀 항목만) { "detail_address": "303호", "normal_delivery_request": "직접 받을게요", "is_default": true }
```

#### Success Response
```text
200 { "address_id": 3, "detail_address": "303호", "is_default": true }
```

#### Failure Response
```text
없음
```

## 챗봇

### 이전 대화 목록

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/chat/history` |
| 인증 | 필요 |
| Query Params | session_id (예: abc123) |

#### Request
```text
(본문 없음. 로그인 시 토큰으로 사용자 식별)
```

#### Success Response
```text
200
{
"items": [
{ "role": "user", "message": "2만원 이하 커피 추천", "created_at": "2026-06-23T10:00:00" },
{ "role": "bot",  "message": "이런 상품은 어떠세요?", "created_at": "2026-06-23T10:00:01" }
]
}
// 뱄로그인은 서버 저장 없이 localStorage 사용 (챗봇 설계의도 페이지 참고)
```

#### Failure Response
```text
없음
```

### 챗봇 메세지 전송 (자연어 → 상품 추천)

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/chat` |
| 인증 | (없음) |
| Query Params | 없음 |

#### Request
```text
{ "message": "2만원 이하 로켓배송 커피 추천해줘","session_id": "abc123" }
```

#### Success Response
```text
200
{
"session_id": "abc123",
"reply": "2만원 이하 로켓배송 커피를 찾아봤어요. 이런 상품은 어떠세요?",
"products": [                          -  추천 상품 슬라이드(최대 5)
{
"product_id": 12,
"name": "콜드브루 커피 1L",
"image_url": "https://.../12.jpg",
"price": 12000,
"original_price": 15000,
"discount_rate": 20,
"reason": "2만원 이하이고 로켓배송이 가능한 커피예요"  // LLM 생성, 저장 안 함
}
]
}
```

#### Failure Response
```text
[예외 처리]
- 빈 메시지            → 400 { "error": "메시지를 입력해주세요" } (또는 프론트가 차단)
- 조건에 맞는 상품 없음  → 200 { "reply": "조건에 맞는 상품을 찾을 수 없습니다", "products": [] }
- LLM이 의도 파악 실패  → 200 { "reply": "질문을 이해하지 못했습니다. 다시 입력해주세요", "products": [] }
- 챗봇/LLM 시스템 오류  → 503 { "error": "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요" }
```

## 리뷰

### 상품 리뷰 목록

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/products/{product_id}/reviews` |
| 인증 | 불필요 |
| Query Params | ?rating=5&sort=latest&photo_only=false&page=1&size=10<br>rating = all(기본) \| 1 \| 2 \| 3 \| 4 \| 5 -  별점 필터 추가<br>sort = latest(최신순·기본) \| rating_desc \| rating_asc |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "product_name": "여름 티셔츠", -  상품명
"average_rating": 4.3, -  평균 별점
"review_count": 28, -  전체 리뷰 수(필터 무관)
"page": 1, "total": 12, // 필터 적용 후 개수
"items": [ { "review_id": 7, "user_name": "고", "rating": 5, "title": "재구매 의사 있어요", -  리뷰 제목 추가
"content": "배송 빠르고 좋아요", "created_at": "2026-06-10T11:00:00", "images": [ "https://.../r1.jpg" ] } ] }
// 리뷰 없음 → items: [], review_count: 0 → "아직 작성된 리뷰가 없습니다"
// 필터 결과 없음 → total: 0 → "선택한 조건의 리뷰가 없습니다"
```

#### Failure Response
```text
없음
```

### 리뷰 작성(구매자만)

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/products/{product_id}/reviews` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
{ "rating": 5, "title": "재구매 의사 있어요", "content": "배송 빠르고 좋아요" } -  title 추가
```

#### Success Response
```text
201 { "review_id": 7, "rating": 5, "title": "재구매 의사 있어요" }
```

#### Failure Response
```text
403 { "error": "구매한 상품만 리뷰를 작성할 수 있습니다" }
```

## 카테고리

### 대표 카테고리 목록(아이콘 추가)

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/categories` |
| 인증 | 불필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "items": [ { "category_id": 1, "name": "식품", "icon_url": "https://.../food.png" }, // 식품·생활용품·뷰티·의류잡화·가전디지털·홈인테리어
{ "category_id": 2, "name": "생활용품", "icon_url": "..." } // 출산유아·반려동물·스포츠레저·자동차용품 (10종 시드 데이터)
] }
```

#### Failure Response
```text
없음
```

## 장바구니

### 장바구니 담기 ← 상품 상세/목록 페이지에서 호출

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/cart` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
{ "product_id": 5, "quantity": 2 }
```

#### Success Response
```text
201 { "cart_item_id": 10, "product_id": 5, "quantity": 5 }
// 이버간에 3개 담갬곿어었으면 3+2 = 5 로 합산
```

#### Failure Response
```text
409 { "error": "재고가 부족합니다" } // 합산 결과가 재고를 넘으에
```

### 항목 삭제

| 항목 | 내용 |
| --- | --- |
| Method | `DELETE` |
| Path | `/api/cart/{cart_item_id}` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
204 (본문 없음)
```

#### Failure Response
```text
없음
```

### 수량 변경

| 항목 | 내용 |
| --- | --- |
| Method | `PATCH` |
| Path | `/api/cart/{cart_item_id}` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
{ "quantity": 3 }
```

#### Success Response
```text
200 { "cart_item_id": 10, "quantity": 3, "subtotal": 36000 }
```

#### Failure Response
```text
400 { "error": "수량은 1개 이상이어야 합니다" } -  검증 추가
409 { "error": "재고가 부족합니다" } -  재고 초과 시
```

### 내 장바구니 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/cart` |
| 인증 | 필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200
{ "item_count": 3, // 담기 상품 종류 수
"items": [ { "cart_item_id": 10, "product_id": 5, "name": "여름 티셔츠", "image_url": "https://.../5.jpg", -  추가
"option_info": "100g, 1개", -  추가
"price": 12000, "original_price": 15000, -  추가
"discount_rate": 20, "quantity": 2, "subtotal": 24000, -  상품별 합계(판매가×수량) 추가
"stock": 32 -  재고(수량 한도·품절 생적 용) 추가
} ] }
// 비어 있으면 items: [], item_count: 0 → "장바구니에 담았 상품이 없습니다"
```

#### Failure Response
```text
없음
```

## 메인페이지

### 메인 배너 목록

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/banners` |
| 인증 | 불필요 |
| Query Params | 없음 |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200 {"items": [{ "banner_id": 1, "image_url": "https://.../banner1.jpg","link_url": "/products/5" }]}
// 배너 이미지 로드 실패 시 기본 이미지 표시는 프론트에서 처리
```

#### Failure Response
```text
없음
```

### 추천 상품 목록

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/products/recommended` |
| 인증 | 불필요 |
| Query Params | size (예: 10) |

#### Request
```text
(본문 없음. 로그인 시 토큰으로 사용자 식별 — 선택적 🔒)
```

#### Success Response
```text
200
{ "based_on": "search_history", // search_history | popular
"items": [ { "product_id": 8, "name": "운동화", "price": 30000, "image_url": "..." } ] }
// 로그인 사용자의 search_history에서 최근 검색어를 읽어, 그 키워드로 GET /api/products를 내부 호출해 연관 상품 추천
// 검색 기록이 없거나 비로그인 → 인기 상품으로 대체 (based_on: "popular")
```

#### Failure Response
```text
없음
```

### 인기 상품 목록

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/products/popular` |
| 인증 | 불필요 |
| Query Params | size (예: 10) |

#### Request
```text
(본문 없음)
```

#### Success Response
```text
200 {"items": [{ "product_id": 5, "name": "여름 티셔츠", "price": 15000, "image_url": "https://.../5.jpg" }]}
// 판매량(order_items 집계) 또는 조회수 기준으로 계산
```

#### Failure Response
```text
없음
```

## 마이페이지

### 최근 찾던 상품

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/users/me/recent-products` |
| 인증 | 필요 |
| Query Params | (없음) |

#### Request
```text
(없음)
```

#### Success Response
```text
200 { 
"items": [ { "productId": 5, "name": "...", "imageUrl": "...", "price": 12000 } ] 
}
```

#### Failure Response
```text
401{
"error" : "인증이 필요합니다."
}
```
