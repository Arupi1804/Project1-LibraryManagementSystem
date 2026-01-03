# 🚂 Hướng dẫn Deploy lên Railway

## 📋 Checklist chuẩn bị

- [x] Đã tạo file `system.properties` (Java 17)
- [x] Đã cập nhật `application.properties` hỗ trợ environment variables
- [ ] Code đã push lên GitHub
- [ ] Đã đăng ký Railway với GitHub Student Pack

---

## 🚀 Các bước Deploy

### **Bước 1: Push code lên GitHub**

```bash
# Khởi tạo Git (nếu chưa có)
git init

# Add tất cả files
git add .

# Commit
git commit -m "Prepare for Railway deployment"

# Tạo repository trên GitHub, sau đó:
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/library-management.git
git push -u origin main
```

---

### **Bước 2: Đăng ký Railway**

1. Truy cập: **https://railway.app**
2. Click **Login with GitHub**
3. Authorize Railway
4. Verify **GitHub Student Developer Pack** để nhận credit miễn phí

---

### **Bước 3: Tạo Project mới**

1. Click **New Project**
2. Chọn **Deploy from GitHub repo**
3. Chọn repository `library-management`
4. Railway sẽ tự động detect Spring Boot project

---

### **Bước 4: Thêm MySQL Database**

1. Trong project, click **New** → **Database** → **Add MySQL**
2. Railway tự động tạo MySQL instance
3. Đợi database khởi động (khoảng 1-2 phút)

---

### **Bước 5: Configure Environment Variables**

1. Click vào **service** (Spring Boot app)
2. Vào tab **Variables**
3. Thêm các biến sau:

```
DATABASE_URL=mysql://root:PASSWORD@HOST:PORT/railway
DB_USERNAME=root
DB_PASSWORD=<password-from-railway>
PORT=8080
SHOW_SQL=false
```

**Lấy thông tin database:**
- Click vào **MySQL** service
- Tab **Variables** → Copy `MYSQL_URL`, `MYSQL_ROOT_PASSWORD`
- Format lại thành `DATABASE_URL` như trên

**Hoặc dùng Railway CLI:**
```bash
# Install Railway CLI
npm i -g @railway/cli

# Login
railway login

# Link project
railway link

# Get database URL
railway variables
```

---

### **Bước 6: Deploy**

1. Railway tự động build & deploy khi có code mới
2. Xem logs: Tab **Deployments** → Click vào deployment mới nhất
3. Đợi build hoàn thành (3-5 phút)

---

### **Bước 7: Lấy Public URL**

1. Vào **Settings** của service
2. Scroll xuống **Networking**
3. Click **Generate Domain**
4. Railway tạo URL: `https://your-app.up.railway.app`

---

### **Bước 8: Test ứng dụng**

1. Truy cập URL vừa tạo
2. Login với tài khoản mặc định:
   - Username: `admin`
   - Password: `admin123`

---

## 🔧 Troubleshooting

### **Lỗi: Application failed to start**

**Kiểm tra logs:**
```bash
railway logs
```

**Nguyên nhân thường gặp:**
1. Database URL sai format
2. MySQL chưa sẵn sàng
3. Port conflict

**Giải pháp:**
- Verify environment variables
- Restart deployment
- Check MySQL status

---

### **Lỗi: Database connection failed**

**Kiểm tra:**
1. MySQL service đã running chưa?
2. `DATABASE_URL` đúng format?
3. Password có special characters? (cần encode)

**Format đúng:**
```
mysql://username:password@host:port/database
```

---

### **Lỗi: Build failed**

**Nguyên nhân:**
- Java version không khớp
- Maven dependencies lỗi

**Giải pháp:**
1. Verify `system.properties` có `java.runtime.version=17`
2. Check `pom.xml` dependencies
3. Clean build local: `mvn clean install`

---

## 📊 Monitor Application

### **Xem Logs:**
```bash
railway logs --follow
```

### **Xem Metrics:**
- Railway Dashboard → Service → Metrics
- CPU, Memory, Network usage

### **Database Management:**
- Railway Dashboard → MySQL → Connect
- Hoặc dùng MySQL Workbench với credentials từ Railway

---

## 🔄 Auto-Deploy

Railway tự động deploy khi:
1. Push code mới lên GitHub
2. Merge Pull Request
3. Update environment variables

**Disable auto-deploy:**
- Settings → Deployments → Turn off auto-deploy

---

## 💰 Cost Management

**GitHub Student Pack:**
- $5/month credit miễn phí
- Đủ cho 1 app nhỏ + MySQL

**Monitor usage:**
- Dashboard → Usage
- Set up billing alerts

---

## 🌐 Custom Domain (Optional)

1. Mua domain (Namecheap, GoDaddy, etc.)
2. Railway Settings → Networking → Custom Domain
3. Add domain: `library.yourdomain.com`
4. Update DNS records theo hướng dẫn Railway

---

## 📝 Deployment Checklist

- [ ] Code pushed to GitHub
- [ ] Railway project created
- [ ] MySQL database added
- [ ] Environment variables configured
- [ ] Application deployed successfully
- [ ] Public URL generated
- [ ] Tested login & basic features
- [ ] Logs checked for errors

---

## 🎯 Next Steps

1. **Setup CI/CD:** GitHub Actions for automated testing
2. **Add SSL:** Railway provides free SSL
3. **Monitoring:** Setup error tracking (Sentry)
4. **Backup:** Regular database backups
5. **Documentation:** Update README with production URL

---

## 📞 Support

**Railway Documentation:** https://docs.railway.app
**Community:** https://discord.gg/railway
**Status:** https://status.railway.app

---

**Deployment Date:** 2024-01-04  
**Version:** 1.0.0  
**Platform:** Railway
