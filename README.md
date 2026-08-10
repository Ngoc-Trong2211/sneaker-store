# Sneaker Store Backend

Backend cho hệ thống bán giày Sneaker Store, được xây dựng bằng Spring Boot. Dự án cung cấp REST API cho các chức năng xác thực, quản lý sản phẩm, biến thể, giỏ hàng, đơn hàng, đánh giá, mã giảm giá, thanh toán SePay, upload ảnh, dashboard và chatbot AI.

## Công Nghệ Sử Dụng

- Java 17
- Spring Boot 3.5.12
- Spring Security, OAuth2 Client, OAuth2 Resource Server
- Spring Data JPA, Hibernate
- MySQL 8
- Spring AI với Gemini/OpenAI-compatible API
- Cloudinary
- Java Mail Sender
- Thymeleaf
- Springdoc OpenAPI/Swagger UI
- Docker, Docker Compose
- Maven Wrapper

## Cấu Trúc Thư Mục

```text
sneaker-store/
+-- src/main/java/com/example/sneaker_store/
|   +-- config/           # Cấu hình security, JWT, CORS, OpenAPI, Cloudinary, SePay
|   +-- controller/       # REST controller
|   +-- dto/              # Request/response DTO
|   +-- model/            # JPA entity
|   +-- repository/       # Spring Data repository
|   +-- service/          # Interface service
|   +-- service/impl/     # Logic nghiệp vụ
|   +-- specification/    # Bộ lọc/tìm kiếm động
|   +-- util/             # Helper, enum, exception
+-- src/main/resources/
|   +-- application.yaml
|   +-- application-dev.yaml
|   +-- application-prod.yaml
|   +-- application-test.yaml
|   +-- templates/
+-- Dockerfile
+-- docker-compose.yaml
+-- pom.xml
+-- mvnw.cmd
```

## Tính Năng Chính

- Đăng ký, đăng nhập, refresh token, đăng xuất và lấy thông tin tài khoản.
- Quản lý người dùng, vai trò và quyền truy cập.
- Quản lý thương hiệu, danh mục, sản phẩm, ảnh sản phẩm, size và biến thể sản phẩm.
- Giỏ hàng, yêu thích, đặt hàng, hủy đơn hàng và theo dõi trạng thái thanh toán.
- Đánh giá sản phẩm và kiểm tra điều kiện đánh giá.
- Mã giảm giá, đợt giảm giá và thống kê dashboard.
- Upload file/ảnh với Cloudinary hoặc cấu hình upload nội bộ.
- Chatbot tư vấn sản phẩm bằng Spring AI.
- Tích hợp thanh toán SePay qua webhook.
- Tài liệu API bằng Swagger UI.

## Yêu Cầu Môi Trường

- JDK 17 trở lên
- MySQL 8
- Maven hoặc Maven Wrapper có sẵn trong repo
- Docker và Docker Compose nếu chạy bằng container

## Cấu Hình Môi Trường

Dự án có các profile:

- `dev`: chạy local, mặc định khi build bằng Maven.
- `prod`: chạy trên Docker/production.
- `test`: dùng cho kiểm thử.

Nên cấu hình các thông tin nhạy cảm bằng biến môi trường hoặc file `.env`, không commit secret lên repository.

Ví dụ file `.env`:

```env
MYSQL_ROOT_PASSWORD=your_mysql_root_password
MYSQL_DATABASE=sneaker-store

DATABASE_URL=jdbc:mysql://localhost:3307/sneaker-store
DATABASE_USERNAME=root
DATABASE_PASSWORD=your_database_password

GEMINI_KEY=your_gemini_api_key
GEMINI_MODEL=gemini-2.5-flash

APP_FRONTEND_URL=http://localhost:3000

SEPAY_WEBHOOK_SECRET=your_sepay_webhook_secret
SEPAY_WEBHOOK_API_KEY=your_sepay_webhook_api_key
SEPAY_BANK_CODE=MB
SEPAY_BANK_NAME=MBBank
SEPAY_ACCOUNT_NUMBER=your_account_number
SEPAY_ACCOUNT_HOLDER=your_account_holder
SEPAY_STORE_NAME=SneakerStore
```

Nếu chạy local bằng profile `dev`, kiểm tra lại cấu hình database trong `src/main/resources/application-dev.yaml` cho đúng với MySQL trên máy của bạn.

Nếu chạy bằng Docker Compose, đảm bảo `MYSQL_DATABASE` và tên database trong `DATABASE_URL` giống nhau.

## Chạy Ứng Dụng Local

1. Tạo database MySQL:

```sql
CREATE DATABASE `sneaker-store` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Chạy ứng dụng:

```powershell
.\mvnw.cmd spring-boot:run
```

Mặc định backend chạy tại:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Build Và Test

Chạy test:

```powershell
.\mvnw.cmd test
```

Build file JAR:

```powershell
.\mvnw.cmd clean package
```

Build bỏ qua test:

```powershell
.\mvnw.cmd -DskipTests package
```

File JAR sau khi build nằm trong thư mục `target/`.

## Chạy Bằng Docker Compose

Docker Compose trong repo khai báo các service:

- `mysql`: MySQL 8, expose cổng `3307`.
- `backend-service`: backend Spring Boot, chạy cổng nội bộ `8080`.
- `frontend`: frontend image/build từ thư mục `../fe-sneaker-store`, expose cổng `80` và `443`.

Chạy:

```powershell
docker compose up --build
```

Chạy nền:

```powershell
docker compose up -d --build
```

Dừng container:

```powershell
docker compose down
```

Nếu chỉ muốn dùng database MySQL từ Docker để chạy backend local, có thể chạy service MySQL rồi kết nối đến:

```text
jdbc:mysql://localhost:3307/sneaker-store
```

## Nhóm API Chính

Một số prefix API trong backend:

- `/auth/v1`: xác thực và tài khoản hiện tại.
- `/user/v1`: quản lý người dùng.
- `/role/v1`: quản lý vai trò.
- `/permission/v1`: quản lý quyền.
- `/brand/v1`: quản lý thương hiệu.
- `/category/v1`: quản lý danh mục.
- `/product/v1`: quản lý sản phẩm.
- `/product-variant/v1`: quản lý biến thể sản phẩm.
- `/product-image/v1`: quản lý ảnh sản phẩm.
- `/cart-item/v1`: giỏ hàng.
- `/favourite/v1`: sản phẩm yêu thích.
- `/order/v1`: đơn hàng.
- `/review/v1`: đánh giá.
- `/coupon/v1`: mã giảm giá.
- `/discount/v1`: đợt giảm giá.
- `/dashboard/v1`: thống kê và export dashboard.
- `/file/v1`: upload/xóa file.
- `/payment/v1/sepay`: phiên thanh toán và webhook SePay.
- `/chat`: chatbot tư vấn sản phẩm.

Chi tiết request/response có thể xem trong Swagger UI sau khi chạy ứng dụng.

## Lưu Ý Bảo Mật

- Không để lộ `client-secret`, mật khẩu email, Cloudinary secret, JWT secret, API key hoặc thông tin ngân hàng trong source code công khai.
- Nên đưa các giá trị nhạy cảm sang biến môi trường và cập nhật `application.yaml` để đọc từ placeholder `${...}`.
- Khi deploy production, nên đặt `spring.jpa.hibernate.ddl-auto=none` và quản lý migration bằng công cụ riêng nếu có.

## Lệnh Hữu Ích

```powershell
# Kiểm tra trạng thái container
docker compose ps

# Xem log backend
docker compose logs -f backend-service

# Xem log database
docker compose logs -f mysql

# Build Docker image backend
docker build -t sneaker-store-backend .
```
