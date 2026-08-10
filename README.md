# Sneaker Store Backend

Backend cho he thong ban giay Sneaker Store, duoc xay dung bang Spring Boot. Du an cung cap REST API cho cac chuc nang xac thuc, quan ly san pham, bien the, gio hang, don hang, danh gia, ma giam gia, thanh toan SePay, upload anh, dashboard va chatbot AI.

## Cong nghe su dung

- Java 17
- Spring Boot 3.5.12
- Spring Security, OAuth2 Client, OAuth2 Resource Server
- Spring Data JPA, Hibernate
- MySQL 8
- Spring AI voi Gemini/OpenAI-compatible API
- Cloudinary
- Java Mail Sender
- Thymeleaf
- Springdoc OpenAPI/Swagger UI
- Docker, Docker Compose
- Maven Wrapper

## Cau truc thu muc

```text
sneaker-store/
+-- src/main/java/com/example/sneaker_store/
|   +-- config/           # Cau hinh security, JWT, CORS, OpenAPI, Cloudinary, SePay
|   +-- controller/       # REST controller
|   +-- dto/              # Request/response DTO
|   +-- model/            # JPA entity
|   +-- repository/       # Spring Data repository
|   +-- service/          # Interface service
|   +-- service/impl/     # Logic nghiep vu
|   +-- specification/    # Bo loc/tim kiem dong
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

## Tinh nang chinh

- Dang ky, dang nhap, refresh token, dang xuat va lay thong tin tai khoan.
- Quan ly nguoi dung, vai tro va quyen truy cap.
- Quan ly thuong hieu, danh muc, san pham, anh san pham, size va bien the san pham.
- Gio hang, yeu thich, dat hang, huy don hang va theo doi trang thai thanh toan.
- Danh gia san pham va kiem tra dieu kien danh gia.
- Ma giam gia, dot giam gia va thong ke dashboard.
- Upload file/anh voi Cloudinary hoac cau hinh upload noi bo.
- Chatbot tu van san pham bang Spring AI.
- Tich hop thanh toan SePay qua webhook.
- Tai lieu API bang Swagger UI.

## Yeu cau moi truong

- JDK 17 tro len
- MySQL 8
- Maven hoac Maven Wrapper co san trong repo
- Docker va Docker Compose neu chay bang container

## Cau hinh moi truong

Du an co cac profile:

- `dev`: chay local, mac dinh khi build bang Maven.
- `prod`: chay tren Docker/production.
- `test`: dung cho kiem thu.

Nen cau hinh cac thong tin nhay cam bang bien moi truong hoac file `.env`, khong commit secret len repository.

Vi du file `.env`:

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

Neu chay local bang profile `dev`, kiem tra lai cau hinh database trong `src/main/resources/application-dev.yaml` cho dung voi MySQL tren may cua ban.

Neu chay bang Docker Compose, dam bao `MYSQL_DATABASE` va ten database trong `DATABASE_URL` giong nhau.

## Chay ung dung local

1. Tao database MySQL:

```sql
CREATE DATABASE `sneaker-store` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Chay ung dung:

```powershell
.\mvnw.cmd spring-boot:run
```

Mac dinh backend chay tai:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Build va test

Chay test:

```powershell
.\mvnw.cmd test
```

Build file JAR:

```powershell
.\mvnw.cmd clean package
```

Build bo qua test:

```powershell
.\mvnw.cmd -DskipTests package
```

File JAR sau khi build nam trong thu muc `target/`.

## Chay bang Docker Compose

Docker Compose trong repo khai bao cac service:

- `mysql`: MySQL 8, expose cong `3307`.
- `backend-service`: backend Spring Boot, chay cong noi bo `8080`.
- `frontend`: frontend image/build tu thu muc `../fe-sneaker-store`, expose cong `80` va `443`.

Chay:

```powershell
docker compose up --build
```

Chay nen:

```powershell
docker compose up -d --build
```

Dung container:

```powershell
docker compose down
```

Neu chi muon dung database MySQL tu Docker de chay backend local, co the chay service MySQL roi ket noi den:

```text
jdbc:mysql://localhost:3307/sneaker-store
```

## Nhom API chinh

Mot so prefix API trong backend:

- `/auth/v1`: xac thuc va tai khoan hien tai.
- `/user/v1`: quan ly nguoi dung.
- `/role/v1`: quan ly vai tro.
- `/permission/v1`: quan ly quyen.
- `/brand/v1`: quan ly thuong hieu.
- `/category/v1`: quan ly danh muc.
- `/product/v1`: quan ly san pham.
- `/product-variant/v1`: quan ly bien the san pham.
- `/product-image/v1`: quan ly anh san pham.
- `/cart-item/v1`: gio hang.
- `/favourite/v1`: san pham yeu thich.
- `/order/v1`: don hang.
- `/review/v1`: danh gia.
- `/coupon/v1`: ma giam gia.
- `/discount/v1`: dot giam gia.
- `/dashboard/v1`: thong ke va export dashboard.
- `/file/v1`: upload/xoa file.
- `/payment/v1/sepay`: phien thanh toan va webhook SePay.
- `/chat`: chatbot tu van san pham.

Chi tiet request/response co the xem trong Swagger UI sau khi chay ung dung.

## Luu y bao mat

- Khong de lo `client-secret`, mat khau email, Cloudinary secret, JWT secret, API key hoac thong tin ngan hang trong source code cong khai.
- Nen dua cac gia tri nhay cam sang bien moi truong va cap nhat `application.yaml` de doc tu placeholder `${...}`.
- Khi deploy production, nen dat `spring.jpa.hibernate.ddl-auto=none` va quan ly migration bang cong cu rieng neu co.

## Lenh huu ich

```powershell
# Kiem tra trang thai container
docker compose ps

# Xem log backend
docker compose logs -f backend-service

# Xem log database
docker compose logs -f mysql

# Build Docker image backend
docker build -t sneaker-store-backend .
```
