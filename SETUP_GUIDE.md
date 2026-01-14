# 🚀 Hướng Dẫn Cài Đặt & Chạy Dự Án - Máy Mới

> **Hệ thống Quản Lý Thư Viện - Library Management System**

Tài liệu này hướng dẫn chi tiết cách cài đặt và chạy dự án trên một máy tính mới theo **2 phương pháp**:
1. **Phương pháp 1**: Chạy trực tiếp với Maven (Development)
2. **Phương pháp 2**: Chạy với Docker (Recommended - Dễ nhất)

---

## 📋 Mục Lục

- [Phương Pháp 1: Chạy với Maven](#phương-pháp-1-chạy-với-maven)
  - [Yêu cầu hệ thống](#yêu-cầu-hệ-thống-maven)
  - [Các bước cài đặt](#các-bước-cài-đặt-maven)
  - [Khởi chạy ứng dụng](#khởi-chạy-ứng-dụng-maven)
- [Phương Pháp 2: Chạy với Docker](#phương-pháp-2-chạy-với-docker)
  - [Yêu cầu hệ thống](#yêu-cầu-hệ-thống-docker)
  - [Các bước cài đặt](#các-bước-cài-đặt-docker)
  - [Khởi chạy ứng dụng](#khởi-chạy-ứng-dụng-docker)
- [Truy cập ứng dụng](#truy-cập-ứng-dụng)
- [Troubleshooting](#troubleshooting)

---

# Phương Pháp 1: Chạy với Maven

## Yêu cầu Hệ Thống (Maven)

### Phần mềm cần cài đặt

| Phần mềm | Phiên bản | Link tải |
|----------|-----------|----------|
| **Java JDK** | 21 hoặc cao hơn | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) hoặc [OpenJDK](https://adoptium.net/) |
| **Maven** | 3.9+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| **MySQL** | 8.0+ | [MySQL Community Server](https://dev.mysql.com/downloads/mysql/) |
| **Git** | Latest | [Git SCM](https://git-scm.com/downloads) |
| **IDE** (Optional) | - | [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) hoặc [VS Code](https://code.visualstudio.com/) |

### Kiểm tra cài đặt

Sau khi cài đặt, mở **Command Prompt** hoặc **Terminal** và kiểm tra:

```bash
# Kiểm tra Java
java -version
# Expected: java version "21.x.x"

# Kiểm tra Maven
mvn -version
# Expected: Apache Maven 3.9.x

# Kiểm tra MySQL
mysql --version
# Expected: mysql Ver 8.0.x

# Kiểm tra Git
git --version
# Expected: git version 2.x.x
```

---

## Các Bước Cài Đặt (Maven)

### Bước 1: Clone Repository

```bash
# Clone dự án từ GitHub
git clone https://github.com/Arupi1804/Project1-LibraryManagementSystem.git

# Di chuyển vào thư mục dự án
cd Project1-LibraryManagementSystem
```

### Bước 2: Cài Đặt MySQL Database

#### 2.1. Khởi động MySQL Server

**Windows:**
```bash
# Khởi động MySQL Service
net start MySQL80
```

**macOS/Linux:**
```bash
# Khởi động MySQL
sudo systemctl start mysql
# hoặc
sudo service mysql start
```

#### 2.2. Tạo Database

Mở **MySQL Command Line** hoặc **MySQL Workbench** và chạy:

```sql
-- Tạo database
CREATE DATABASE library_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Kiểm tra database đã tạo
SHOW DATABASES;
```

#### 2.3. Tạo User (Optional - Recommended)

```sql
-- Tạo user riêng cho ứng dụng
CREATE USER 'library_user'@'localhost' IDENTIFIED BY 'your_password';

-- Cấp quyền
GRANT ALL PRIVILEGES ON library_db.* TO 'library_user'@'localhost';

-- Áp dụng thay đổi
FLUSH PRIVILEGES;
```

### Bước 3: Cấu Hình Application

Mở file `src/main/resources/application.properties` và cập nhật:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=library_user
spring.datasource.password=your_password

# Hoặc dùng root (không khuyến nghị)
# spring.datasource.username=root
# spring.datasource.password=your_root_password
```

> **Lưu ý**: File này đã có sẵn cấu hình với environment variables. Bạn có thể:
> - **Option 1**: Sửa trực tiếp file (như trên)
> - **Option 2**: Giữ nguyên và set environment variables

**Option 2 - Set Environment Variables:**

**Windows (PowerShell):**
```powershell
$env:DB_USERNAME="library_user"
$env:DB_PASSWORD="your_password"
```

**macOS/Linux:**
```bash
export DB_USERNAME="library_user"
export DB_PASSWORD="your_password"
```

### Bước 4: Build Project

```bash
# Clean và build project
mvn clean install

# Hoặc build mà không chạy tests
mvn clean install -DskipTests
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 2-3 minutes
```

---

## Khởi Chạy Ứng Dụng (Maven)

### Cách 1: Chạy với Maven

```bash
mvn spring-boot:run
```

### Cách 2: Chạy JAR file

```bash
# Build JAR
mvn clean package

# Chạy JAR
java -jar target/project1-0.0.1-SNAPSHOT.jar
```

### Cách 3: Chạy từ IDE

**IntelliJ IDEA:**
1. Mở project trong IntelliJ
2. Tìm file `Project1Application.java`
3. Click chuột phải → **Run 'Project1Application'**

**VS Code:**
1. Mở project trong VS Code
2. Cài extension "Spring Boot Extension Pack"
3. Press F5 hoặc Run → Start Debugging

### Kiểm tra ứng dụng đã chạy

Xem console output, tìm dòng:
```
Started Project1Application in X.XXX seconds
```

---

# Phương Pháp 2: Chạy với Docker

> ⭐ **Khuyến nghị**: Phương pháp này đơn giản nhất, không cần cài MySQL hay cấu hình phức tạp!

## Yêu cầu Hệ Thống (Docker)

### Phần mềm cần cài đặt

| Phần mềm | Link tải |
|----------|----------|
| **Docker Desktop** | [Windows](https://docs.docker.com/desktop/install/windows-install/) \| [Mac](https://docs.docker.com/desktop/install/mac-install/) \| [Linux](https://docs.docker.com/desktop/install/linux-install/) |
| **Git** | [Git SCM](https://git-scm.com/downloads) |

### Yêu cầu phần cứng

- **RAM**: Tối thiểu 4GB (Khuyến nghị 8GB)
- **Disk**: 5GB trống
- **CPU**: 2 cores trở lên

### Kiểm tra cài đặt

```bash
# Kiểm tra Docker
docker --version
# Expected: Docker version 20.x.x hoặc cao hơn

# Kiểm tra Docker Compose
docker-compose --version
# Expected: Docker Compose version v2.x.x

# Kiểm tra Docker đang chạy
docker ps
# Expected: Hiển thị danh sách containers (có thể rỗng)
```

---

## Các Bước Cài Đặt (Docker)

### Bước 1: Clone Repository

```bash
# Clone dự án từ GitHub
git clone https://github.com/Arupi1804/Project1-LibraryManagementSystem.git

# Di chuyển vào thư mục dự án
cd Project1-LibraryManagementSystem
```

### Bước 2: Kiểm tra các file Docker

Đảm bảo các file sau tồn tại:
- ✅ `Dockerfile`
- ✅ `docker-compose.yml`
- ✅ `.dockerignore`

```bash
# Kiểm tra files
ls -la | grep -E "Dockerfile|docker-compose"

# Windows PowerShell
Get-ChildItem | Where-Object {$_.Name -match "Dockerfile|docker-compose"}
```

---

## Khởi Chạy Ứng Dụng (Docker)

### Bước 1: Build và Start Containers

```bash
# Khởi động tất cả services (MySQL + Application)
docker-compose up -d
```

**Quá trình:**
1. ⏳ Build Docker image (~5-10 phút lần đầu)
2. ⏳ Pull MySQL image (~1-2 phút)
3. ⏳ Tạo network và volumes
4. ✅ Start MySQL container
5. ✅ Wait for MySQL health check
6. ✅ Start Application container

### Bước 2: Kiểm tra trạng thái

```bash
# Xem trạng thái containers
docker-compose ps
```

**Expected output:**
```
NAME            IMAGE           STATUS          PORTS
library-mysql   mysql:8.0       Up (healthy)    0.0.0.0:3307->3306/tcp
library-app     project1-app    Up (healthy)    0.0.0.0:8080->8080/tcp
```

### Bước 3: Xem logs (Optional)

```bash
# Xem logs của application
docker-compose logs -f app

# Đợi thấy dòng: "Started Project1Application"
# Nhấn Ctrl+C để thoát
```

### Bước 4: Verify ứng dụng đã chạy

```bash
# Test HTTP endpoint
curl http://localhost:8080/login

# Windows PowerShell
Invoke-WebRequest -Uri http://localhost:8080/login -UseBasicParsing
```

**Expected**: HTTP Status Code 200

---

## Các Lệnh Docker Hữu Ích

### Quản lý Containers

```bash
# Dừng containers
docker-compose stop

# Khởi động lại
docker-compose start

# Khởi động lại (rebuild nếu có thay đổi code)
docker-compose up -d --build

# Xóa containers (giữ lại data)
docker-compose down

# Xóa containers VÀ data (⚠️ mất dữ liệu)
docker-compose down -v
```

### Xem Logs

```bash
# Logs tất cả services
docker-compose logs

# Logs real-time
docker-compose logs -f

# Logs của app only
docker-compose logs -f app

# Logs 50 dòng cuối
docker-compose logs --tail=50 app
```

### Debug

```bash
# Truy cập shell của app container
docker-compose exec app sh

# Truy cập MySQL
docker-compose exec mysql-db mysql -u library_user -plibrary_password library_db

# Kiểm tra resource usage
docker stats
```

---

# Truy Cập Ứng Dụng

## URL và Credentials

**URL:** http://localhost:8080

**Tài khoản mặc định:**

| Username | Password | Vai trò |
|----------|----------|---------|
| `admin` | `admin123` | ADMIN (Toàn quyền) |
| `librarian` | `lib123` | LIBRARIAN (Hạn chế) |

## Các trang chính

- **Dashboard**: http://localhost:8080/dashboard
- **Quản lý Sách**: http://localhost:8080/books
- **Quản lý Độc giả**: http://localhost:8080/members
- **Quản lý Mượn/Trả**: http://localhost:8080/borrow-records
- **Quản lý Người dùng**: http://localhost:8080/users (Admin only)
- **Báo cáo**: http://localhost:8080/reports

---

# Troubleshooting

## Vấn đề chung

### ❌ Port đã được sử dụng

**Lỗi**: `Port 8080 is already in use`

**Giải pháp:**

**Option 1 - Tìm và kill process:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux
lsof -ti:8080 | xargs kill -9
```

**Option 2 - Đổi port:**

Sửa `application.properties`:
```properties
server.port=8081
```

Hoặc với Docker, sửa `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"
```

---

## Vấn đề với Maven

### ❌ Maven build failed

**Lỗi**: `Failed to execute goal`

**Giải pháp:**
```bash
# Clean Maven cache
mvn clean

# Rebuild
mvn clean install -U

# Skip tests nếu test fail
mvn clean install -DskipTests
```

### ❌ Database connection failed

**Lỗi**: `Access denied for user`

**Giải pháp:**
1. Kiểm tra MySQL đang chạy
2. Kiểm tra username/password trong `application.properties`
3. Kiểm tra database `library_db` đã tạo chưa
4. Thử connect bằng MySQL Workbench để verify credentials

### ❌ Java version mismatch

**Lỗi**: `Unsupported class file major version`

**Giải pháp:**
```bash
# Kiểm tra Java version
java -version

# Phải là Java 21 trở lên
# Nếu không, tải và cài Java 21
```

---

## Vấn đề với Docker

### ❌ Docker daemon not running

**Lỗi**: `Cannot connect to the Docker daemon`

**Giải pháp:**
- Mở Docker Desktop
- Đợi Docker khởi động hoàn toàn (icon màu xanh)

### ❌ Container unhealthy

**Lỗi**: Container status shows "unhealthy"

**Giải pháp:**
```bash
# Xem logs để tìm lỗi
docker-compose logs app

# Restart containers
docker-compose restart

# Rebuild nếu cần
docker-compose up -d --build
```

### ❌ MySQL connection timeout

**Lỗi**: `Communications link failure`

**Giải pháp:**
```bash
# Kiểm tra MySQL container
docker-compose logs mysql-db

# Đợi MySQL khởi động hoàn toàn (~30 giây)
# Restart app container
docker-compose restart app
```

### ❌ Out of disk space

**Lỗi**: `no space left on device`

**Giải pháp:**
```bash
# Xóa unused images
docker image prune -a

# Xóa unused volumes
docker volume prune

# Xóa tất cả unused data
docker system prune -a --volumes
```

---

## So Sánh 2 Phương Pháp

| Tiêu chí | Maven | Docker |
|----------|-------|--------|
| **Độ khó** | Trung bình | Dễ |
| **Thời gian setup** | 30-60 phút | 10-15 phút |
| **Yêu cầu cài đặt** | Java, Maven, MySQL | Chỉ Docker |
| **Phù hợp cho** | Development, Debug | Production, Testing |
| **Tốc độ khởi động** | Nhanh (~30s) | Trung bình (~60s) |
| **Dễ dàng cleanup** | Khó | Dễ (1 lệnh) |
| **Portable** | Không | Có |
| **Khuyến nghị** | Developers | Everyone |

---

## 📚 Tài Liệu Tham Khảo

### Tài liệu dự án
- [README.md](README.md) - Tổng quan dự án
- [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) - Hướng dẫn Docker chi tiết
- [DATABASE_DESIGN.md](DATABASE_DESIGN.md) - Thiết kế database

### Tài liệu công nghệ
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

## 💡 Tips & Best Practices

### Cho Development (Maven)
1. ✅ Sử dụng IDE để debug dễ dàng
2. ✅ Enable hot reload với Spring DevTools
3. ✅ Sử dụng MySQL Workbench để quản lý database
4. ✅ Commit code thường xuyên

### Cho Production (Docker)
1. ✅ Đổi tất cả passwords mặc định
2. ✅ Sử dụng environment variables cho secrets
3. ✅ Setup backup tự động cho database
4. ✅ Monitor logs và resource usage
5. ✅ Sử dụng reverse proxy (nginx) cho HTTPS

---

## 🆘 Hỗ Trợ

Nếu gặp vấn đề:
1. Kiểm tra [Troubleshooting](#troubleshooting) section
2. Xem logs: `docker-compose logs` hoặc console output
3. Tìm kiếm lỗi trên Google/Stack Overflow
4. Mở issue trên GitHub repository

---

**Chúc bạn setup thành công! 🎉**

**Last Updated**: 2026-01-15  
**Version**: 1.0.0
