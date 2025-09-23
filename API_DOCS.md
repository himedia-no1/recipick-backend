# Recipick API 문서

## 🚀 Swagger UI 접근 방법

애플리케이션 실행 후 다음 URL로 접속하여 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 📋 API 그룹별 정리

### 1. 🔐 인증 (Authentication)
- **POST** `/api/auth/token` - 토큰 갱신
- **PATCH** `/api/users/me/initial` - 초기 사용자 정보 설정
- **POST** `/api/auth/logout` - 로그아웃

### 2. 👤 사용자 (User)
- **GET** `/api/users/me` - 현재 사용자 정보 조회
- **PATCH** `/api/users/me/profile` - 프로필 업데이트
- **DELETE** `/api/users/me` - 사용자 삭제
- **GET** `/api/users/me/stats` - 사용자 통계
- **GET** `/api/users/me/cooking-stats` - 요리 통계
- **GET** `/api/users/me/rating-stats` - 평점 통계

### 3. 🧊 냉장고 (Fridge)
- **GET** `/api/fridges` - 냉장고 목록 조회
- **POST** `/api/fridges` - 냉장고 생성
- **GET** `/api/fridges/{fridgeId}` - 냉장고 상세 조회
- **PUT** `/api/fridges/{fridgeId}` - 냉장고 수정
- **DELETE** `/api/fridges/{fridgeId}` - 냉장고 삭제

### 4. 🍳 레시피 (Recipe)
- **GET** `/api/recipes` - 레시피 목록 조회
- **GET** `/api/recipes/recommendations` - 추천 레시피 조회
- **GET** `/api/recipes/{recipeId}` - 레시피 상세 조회
- **POST** `/api/recipes/{recipeId}/ratings` - 레시피 평점 등록
- **GET** `/api/users/me/favorite-recipes` - 즐겨찾기 레시피 조회
- **PUT** `/api/users/me/favorite-recipes/{recipeId}` - 즐겨찾기 추가
- **DELETE** `/api/users/me/favorite-recipes/{recipeId}` - 즐겨찾기 제거

### 5. 🔔 알림 (Notification)
- **GET** `/api/users/me/notifications` - 알림 목록 조회
- **GET** `/api/users/me/notifications/count` - 읽지 않은 알림 개수
- **PUT** `/api/users/me/notifications/{notificationId}/read` - 알림 읽음 처리
- **PUT** `/api/users/me/notifications/read-all` - 모든 알림 읽음 처리

### 6. 📁 파일 (File)
- **POST** `/api/files/upload/profile` - 프로필 이미지 업로드
- **POST** `/api/files/upload/recipe` - 레시피 이미지 업로드
- **POST** `/api/files/upload/ingredient` - 식재료 이미지 업로드

## 🔑 인증 방법

JWT 토큰을 사용한 Bearer 인증 방식:

1. OAuth2를 통해 로그인 (Google, Kakao, Naver)
2. 발급받은 액세스 토큰을 헤더에 포함:
   ```
   Authorization: Bearer {your-access-token}
   ```

## 🏃‍♂️ 실행 방법

1. 데이터베이스 준비 (PostgreSQL)
2. MinIO 서버 실행
3. application-local.properties에서 설정 확인
4. 애플리케이션 실행:
   ```bash
   ./gradlew :apps:user:bootRun --args='--spring.profiles.active=local'
   ```

## 📝 주요 기능

- ✅ **완전한 CRUD API** - 모든 리소스에 대한 생성, 조회, 수정, 삭제
- ✅ **스마트 레시피 추천** - 보유 식재료 기반 추천 알고리즘
- ✅ **실시간 알림 시스템** - 유통기한, 레시피 추천 등
- ✅ **파일 업로드** - MinIO 기반 이미지 관리
- ✅ **사용자 통계** - 요리 기록, 평점 분석
- ✅ **자동화된 스케줄링** - 유통기한 체크 및 알림

모든 API는 표준화된 응답 형식을 따르며, 에러 처리 및 페이지네이션이 포함되어 있습니다.