# 🐳 Docker Deployment Guide - Library Management System

## 📋 Table of Contents
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Detailed Instructions](#detailed-instructions)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Production Deployment](#production-deployment)
- [Useful Commands](#useful-commands)

---

## Prerequisites

### Required Software
- **Docker Desktop** 4.0+ ([Download](https://www.docker.com/products/docker-desktop))
  - Windows: Docker Desktop for Windows
  - macOS: Docker Desktop for Mac
  - Linux: Docker Engine + Docker Compose

### System Requirements
- **RAM**: Minimum 4GB (8GB recommended)
- **Disk Space**: 2GB free space
- **Ports**: 8080 (app), 3307 (MySQL) must be available

### Verify Installation
```bash
docker --version
docker-compose --version
```

---

## Quick Start

### 1. Clone Repository (if not already done)
```bash
git clone <repository-url>
cd project1
```

### 2. Start Application
```bash
docker-compose up -d
```

### 3. Wait for Services to Start
```bash
# Check service status
docker-compose ps

# Watch logs (optional)
docker-compose logs -f app
```

### 4. Access Application
Open browser and navigate to:
```
http://localhost:8080
```

### 5. Login
Use default credentials:
- **Username**: `admin`
- **Password**: `admin123`

---

## Detailed Instructions

### Building Docker Image Manually

If you want to build the Docker image separately:

```bash
# Build the image
docker build -t library-system:latest .

# Verify image was created
docker images | grep library-system
```

### Running Containers Individually

#### 1. Create Network
```bash
docker network create library-network
```

#### 2. Start MySQL Container
```bash
docker run -d \
  --name library-mysql \
  --network library-network \
  -e MYSQL_DATABASE=library_db \
  -e MYSQL_USER=library_user \
  -e MYSQL_PASSWORD=library_password \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -p 3307:3306 \
  -v mysql-data:/var/lib/mysql \
  mysql:8.0
```

#### 3. Wait for MySQL to be Ready
```bash
# Check MySQL logs
docker logs library-mysql

# Wait until you see: "ready for connections"
```

#### 4. Start Application Container
```bash
docker run -d \
  --name library-app \
  --network library-network \
  -e DATABASE_URL=jdbc:mysql://library-mysql:3306/library_db?useSSL=false&serverTimezone=UTC \
  -e DB_USERNAME=library_user \
  -e DB_PASSWORD=library_password \
  -p 8080:8080 \
  library-system:latest
```

---

## Configuration

### Environment Variables

You can customize the deployment by creating a `.env` file:

```bash
# .env file
# Database Configuration
MYSQL_DATABASE=library_db
MYSQL_USER=library_user
MYSQL_PASSWORD=your_secure_password
MYSQL_ROOT_PASSWORD=your_root_password

# Application Configuration
APP_PORT=8080
MYSQL_PORT=3307

# JVM Configuration
JAVA_OPTS=-Xmx1g -Xms512m
```

Then use it with docker-compose:
```bash
docker-compose --env-file .env up -d
```

### Port Customization

To change the application port, edit `docker-compose.yml`:

```yaml
services:
  app:
    ports:
      - "9090:8080"  # Change 9090 to your desired port
```

### Database Credentials

**⚠️ IMPORTANT**: Change default passwords in production!

Edit `docker-compose.yml`:
```yaml
services:
  mysql-db:
    environment:
      MYSQL_PASSWORD: your_strong_password
      MYSQL_ROOT_PASSWORD: your_root_password
```

### JVM Memory Settings

For production, adjust memory based on your server:

```yaml
services:
  app:
    environment:
      JAVA_OPTS: -Xmx2g -Xms1g  # 2GB max, 1GB initial
```

---

## Troubleshooting

### Issue 1: Port Already in Use

**Error**: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solution**:
```bash
# Option 1: Stop the process using the port
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Option 2: Change the port in docker-compose.yml
ports:
  - "8081:8080"  # Use port 8081 instead
```

### Issue 2: Database Connection Failed

**Error**: Application logs show "Connection refused" or "Unknown database"

**Solution**:
```bash
# Check MySQL container is running
docker-compose ps

# Check MySQL logs
docker-compose logs mysql-db

# Restart services
docker-compose restart
```

### Issue 3: Application Won't Start

**Error**: Container exits immediately

**Solution**:
```bash
# View application logs
docker-compose logs app

# Check for common issues:
# - Database not ready (wait longer)
# - Port conflict
# - Memory issues

# Rebuild the image
docker-compose build --no-cache
docker-compose up -d
```

### Issue 4: Slow Build Time

**Solution**:
```bash
# Use BuildKit for faster builds
DOCKER_BUILDKIT=1 docker-compose build

# Or set in environment permanently
# Windows PowerShell
$env:DOCKER_BUILDKIT=1

# Linux/Mac
export DOCKER_BUILDKIT=1
```

### Issue 5: Data Loss After Restart

**Problem**: Data disappears when containers restart

**Solution**: Ensure volumes are properly configured
```bash
# Check volumes
docker volume ls

# Verify mysql-data volume exists
docker volume inspect mysql-data

# If missing, recreate with:
docker-compose up -d
```

---

## Production Deployment

### Security Best Practices

#### 1. Change Default Passwords
```yaml
environment:
  MYSQL_PASSWORD: ${MYSQL_PASSWORD}  # Use environment variables
  MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
```

#### 2. Use Secrets Management
```bash
# Create secrets file (don't commit to git!)
echo "my_secure_password" | docker secret create mysql_password -
```

#### 3. Enable HTTPS
Use a reverse proxy (nginx, traefik) with SSL certificates.

#### 4. Limit Resource Usage
```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          memory: 1G
```

### Backup and Restore

#### Backup Database
```bash
# Create backup
docker-compose exec mysql-db mysqldump \
  -u library_user \
  -plibrary_password \
  library_db > backup_$(date +%Y%m%d).sql

# Or backup the entire volume
docker run --rm \
  -v mysql-data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/mysql-backup.tar.gz /data
```

#### Restore Database
```bash
# From SQL dump
docker-compose exec -T mysql-db mysql \
  -u library_user \
  -plibrary_password \
  library_db < backup_20260115.sql

# From volume backup
docker run --rm \
  -v mysql-data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/mysql-backup.tar.gz -C /
```

### Monitoring

#### Health Checks
```bash
# Check container health
docker-compose ps

# View health check logs
docker inspect library-app --format='{{json .State.Health}}'
```

#### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f app

# Last 100 lines
docker-compose logs --tail=100 app
```

---

## Useful Commands

### Start/Stop Services

```bash
# Start all services
docker-compose up -d

# Start specific service
docker-compose up -d app

# Stop all services
docker-compose stop

# Stop and remove containers
docker-compose down

# Stop and remove containers + volumes (⚠️ DATA LOSS)
docker-compose down -v
```

### View Logs

```bash
# Follow logs (all services)
docker-compose logs -f

# Follow logs (specific service)
docker-compose logs -f app

# Last 50 lines
docker-compose logs --tail=50 app

# Logs since 1 hour ago
docker-compose logs --since 1h app
```

### Execute Commands in Containers

```bash
# Access application container shell
docker-compose exec app sh

# Access MySQL shell
docker-compose exec mysql-db mysql -u library_user -plibrary_password library_db

# Run SQL query
docker-compose exec mysql-db mysql -u library_user -plibrary_password library_db \
  -e "SELECT COUNT(*) FROM books;"
```

### Rebuild and Update

```bash
# Rebuild image after code changes
docker-compose build

# Rebuild without cache
docker-compose build --no-cache

# Pull latest base images
docker-compose pull

# Recreate containers with new image
docker-compose up -d --force-recreate
```

### Clean Up

```bash
# Remove stopped containers
docker-compose rm

# Remove unused images
docker image prune -a

# Remove unused volumes
docker volume prune

# Complete cleanup (⚠️ removes all Docker data)
docker system prune -a --volumes
```

### Inspect Resources

```bash
# View container details
docker-compose ps
docker inspect library-app

# View volume details
docker volume ls
docker volume inspect mysql-data

# View network details
docker network ls
docker network inspect project1_library-network

# View resource usage
docker stats
```

---

## Advanced Configuration

### Using Docker Compose Profiles

Create different profiles for development and production:

```yaml
# docker-compose.yml
services:
  app:
    profiles: ["dev", "prod"]
    # ... config

  adminer:  # Database admin tool
    image: adminer
    profiles: ["dev"]
    ports:
      - "8081:8080"
```

Run with profile:
```bash
# Development
docker-compose --profile dev up -d

# Production
docker-compose --profile prod up -d
```

### Multi-Stage Deployment

For CI/CD pipelines:

```bash
# Build
docker-compose build

# Test
docker-compose run --rm app mvn test

# Deploy
docker-compose up -d
```

---

## FAQ

### Q: How do I update the application?

**A**: Pull latest code and rebuild:
```bash
git pull
docker-compose build
docker-compose up -d
```

### Q: Can I use this in production?

**A**: Yes, but ensure you:
- Change all default passwords
- Use environment variables for secrets
- Set up proper backups
- Configure resource limits
- Use HTTPS with reverse proxy
- Monitor logs and health

### Q: How do I scale the application?

**A**: Use Docker Swarm or Kubernetes for horizontal scaling. This docker-compose setup is for single-server deployment.

### Q: Database is slow, how to optimize?

**A**: 
- Increase MySQL memory: `--innodb-buffer-pool-size=1G`
- Add indexes to frequently queried columns
- Monitor slow queries: `--slow-query-log=1`

---

## Support

For issues or questions:
- Check logs: `docker-compose logs`
- Review [Troubleshooting](#troubleshooting) section
- Open an issue on GitHub

---

**Last Updated**: 2026-01-15  
**Docker Version**: 24.0+  
**Docker Compose Version**: 2.0+
