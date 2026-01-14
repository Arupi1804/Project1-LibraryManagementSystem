# 📚 Hệ thống Quản lý Thư viện

Hệ thống quản lý thư viện được xây dựng bằng Spring Boot, cung cấp các chức năng quản lý sách, độc giả, mượn/trả sách và báo cáo thống kê.

## 🚀 Tính năng chính

### 1. Quản lý Sách
- ✅ Thêm, sửa, xóa thông tin sách
- ✅ Tìm kiếm theo tiêu đề, ISBN, tác giả, thể loại
- ✅ Quản lý bản sao vật lý của sách
- ✅ Theo dõi trạng thái: Có sẵn, Đang mượn, Hỏng, Mất

### 2. Quản lý Độc giả
- ✅ Đăng ký độc giả mới
- ✅ Cập nhật thông tin độc giả
- ✅ Theo dõi số sách đang mượn
- ✅ Quản lý trạng thái: Hoạt động, Bị khóa

### 3. Quản lý Mượn/Trả
- ✅ Tạo phiếu mượn (tối đa 3 cuốn/độc giả)
- ✅ Trả sách từng cuốn hoặc toàn bộ
- ✅ Tự động phát hiện quá hạn
- ✅ Tính phạt tự động (5,000 VND/ngày)
- ✅ Xử lý sách hỏng/mất
- ✅ Thanh toán tiền phạt

### 4. Báo cáo & Thống kê
- ✅ Tổng quan: Tổng sách, độc giả, phiếu mượn, quá hạn
- ✅ Thống kê sách: Top 10 mượn nhiều, chưa mượn, hỏng/mất
- ✅ Thống kê độc giả: Top hoạt động, danh sách đen
- ✅ Thống kê tài chính: Tiền phạt đã thu, đang nợ

### 5. Quản lý Người dùng (ADMIN)
- ✅ Tạo tài khoản mới
- ✅ Xem chi tiết tài khoản
- ✅ Đổi mật khẩu
- ✅ Khoá/Mở khoá tài khoản
- ✅ Phân quyền: Admin, Librarian

## 🛠️ Công nghệ sử dụng

### Backend
- **Framework:** Spring Boot 3.4.1
- **Database:** MySQL 8.0
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security 6
- **Password Encryption:** BCrypt

### Frontend
- **Template Engine:** Thymeleaf
- **CSS:** Custom CSS
- **JavaScript:** Vanilla JS

### Build Tool
- **Maven** 3.9+

## 📋 Yêu cầu hệ thống

- **Java:** JDK 17 hoặc cao hơn
- **MySQL:** 8.0 hoặc cao hơn
- **Maven:** 3.9 hoặc cao hơn
- **IDE:** IntelliJ IDEA / Eclipse / VS Code (khuyến nghị)

## ⚙️ Cài đặt và Chạy

### 1. Clone repository
```bash
git clone <repository-url>
cd project1
```

### 2. Cấu hình Database

Tạo database MySQL:
```sql
CREATE DATABASE library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Cập nhật file `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build và chạy

**Sử dụng Maven:**
```bash
mvn clean install
mvn spring-boot:run
```

**Hoặc chạy file JAR:**
```bash
mvn clean package
java -jar target/project1-0.0.1-SNAPSHOT.jar
```

### 4. Truy cập ứng dụng

Mở trình duyệt và truy cập:
```
http://localhost:8080
```

## 👤 Tài khoản mặc định

Hệ thống tự động tạo 2 tài khoản mặc định:

| Username   | Password | Vai trò    |
|------------|----------|------------|
| admin      | admin123 | ADMIN      |
| librarian  | lib123   | LIBRARIAN  |

## 📁 Cấu trúc dự án

```
project1/
├── src/
│   ├── main/
│   │   ├── java/com/hust/project1/
│   │   │   ├── config/          # Cấu hình (Security, Data Initializer, Scheduled Tasks)
│   │   │   ├── controller/      # Controllers (MVC)
│   │   │   ├── entity/          # Entities (JPA)
│   │   │   ├── repository/      # Repositories (Data Access)
│   │   │   ├── service/         # Business Logic
│   │   │   └── Project1Application.java
│   │   └── resources/
│   │       ├── static/css/      # CSS files
│   │       ├── templates/       # Thymeleaf templates
│   │       │   ├── fragments/   # Reusable fragments (navbar, sidebar)
│   │       │   ├── *.html       # Page templates
│   │       └── application.properties
│   └── test/                    # Unit tests
├── pom.xml                      # Maven dependencies
└── README.md
```

## 🗄️ Database Schema

### Entities chính:

1. **User** - Người dùng hệ thống
2. **Book** - Thông tin sách (đầu sách)
3. **BookCopy** - Bản sao vật lý
4. **Member** - Độc giả
5. **BorrowRecord** - Phiếu mượn
6. **BorrowRecordDetail** - Chi tiết phiếu mượn

### Relationships:
- Book (1) → (N) BookCopy
- Member (1) → (N) BorrowRecord
- BorrowRecord (1) → (N) BorrowRecordDetail
- BookCopy (1) → (N) BorrowRecordDetail

## 🔐 Phân quyền

### ADMIN
- ✅ Toàn quyền quản lý hệ thống
- ✅ Quản lý người dùng
- ✅ Xem tất cả báo cáo
- ✅ Quản lý sách, độc giả, mượn/trả

### LIBRARIAN
- ✅ Quản lý sách, độc giả
- ✅ Quản lý mượn/trả
- ✅ Xem báo cáo
- ❌ Không quản lý người dùng

## 📊 Business Rules

### Mượn sách
- Mỗi độc giả tối đa **3 cuốn** cùng lúc
- Thời hạn mượn: **14 ngày**
- Tự động phát hiện quá hạn mỗi giờ (8h-20h) và mỗi ngày (00:00)

### Tiền phạt
- **Trễ hạn:** 5,000 VND/ngày/cuốn
- **Sách hỏng:** 50,000 VND/cuốn
- **Sách mất:** 100,000 VND/cuốn

### Trạng thái sách
- **AVAILABLE:** Có sẵn để mượn
- **BORROWED:** Đang được mượn
- **DAMAGED:** Bị hỏng
- **LOST:** Bị mất

## 🔄 Scheduled Tasks

Hệ thống tự động chạy các tác vụ:

1. **Cập nhật quá hạn:**
   - Mỗi ngày lúc 00:00
   - Mỗi giờ từ 8h-20h
   - Tự động đổi status: ACTIVE → OVERDUE
   - Tính phạt tự động

## 🧪 Testing

Chạy tests:
```bash
mvn test
```

## 📝 API Endpoints

### Public
- `GET /login` - Trang đăng nhập
- `POST /login` - Xử lý đăng nhập

### Authenticated (ADMIN + LIBRARIAN)
- `GET /` - Dashboard
- `GET /books` - Danh sách sách
- `GET /book-copies` - Danh sách bản sao
- `GET /members` - Danh sách độc giả
- `GET /borrow-records` - Danh sách mượn/trả
- `GET /reports` - Báo cáo & Thống kê

### ADMIN Only
- `GET /users` - Quản lý người dùng
- `POST /users` - Tạo người dùng
- `GET /users/{id}` - Chi tiết người dùng
- `POST /users/{id}/change-password` - Đổi mật khẩu

## 🐛 Troubleshooting

### Lỗi kết nối Database
```
Error: Access denied for user 'root'@'localhost'
```
**Giải pháp:** Kiểm tra username/password trong `application.properties`

### Port 8080 đã được sử dụng
```
Error: Port 8080 is already in use
```
**Giải pháp:** Đổi port trong `application.properties`:
```properties
server.port=8081
```

### Lỗi encoding tiếng Việt
**Giải pháp:** Đảm bảo database sử dụng `utf8mb4`:
```sql
ALTER DATABASE library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 📚 Tài liệu tham khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Thymeleaf](https://www.thymeleaf.org/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

## 👥 Đóng góp

Mọi đóng góp đều được chào đón! Vui lòng:
1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 📄 License

Dự án này được phát triển cho mục đích học tập.

## 📧 Liên hệ

- **Email:** cuong.nh235285@sis.hust.edu.vn
- **GitHub:** [Arupi1804](https://github.com/Arupi1804)

---

**Phát triển bởi:** [Arupi1804](https://github.com/Arupi1804)  
**Năm:** 2026  
**Version:** 1.0.0
