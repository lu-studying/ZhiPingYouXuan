# Nginx 开发环境配置说明

## 📋 配置概述

使用 Nginx 作为反向代理，统一管理前端（Vite）和后端（Spring Boot）请求。

**访问地址：** `http://localhost`（统一入口）

---

## 🚀 快速开始

### 1. 确保服务运行

**前端服务（Vite）：**
```bash
cd Vue
npm run dev
```
前端运行在：`http://localhost:3000`

**后端服务（Spring Boot）：**
```bash
cd Java
mvn spring-boot:run
```
后端运行在：`http://localhost:8080`

---

### 2. 配置 Nginx

#### Windows 系统：

1. **找到 Nginx 配置文件**
   - 通常在：`C:\nginx\conf\nginx.conf`
   - 或者在 Nginx 安装目录下的 `conf` 文件夹

2. **备份原配置文件**
   ```bash
   copy nginx.conf nginx.conf.backup
   ```

3. **使用开发环境配置**
   - 将项目根目录下的 `nginx-dev.conf` 内容复制到 `nginx.conf`
   - 或者直接在 `nginx.conf` 的 `http` 块中添加 `nginx-dev.conf` 中的 `server` 配置

4. **检查配置语法**
   ```bash
   nginx -t
   ```

5. **启动/重启 Nginx**
   ```bash
   # 启动
   nginx
   
   # 重启（修改配置后）
   nginx -s reload
   ```

#### Linux/Mac 系统：

1. **找到 Nginx 配置文件**
   ```bash
   # 通常在以下位置之一：
   /etc/nginx/nginx.conf
   /usr/local/nginx/conf/nginx.conf
   ```

2. **备份原配置文件**
   ```bash
   sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup
   ```

3. **使用开发环境配置**
   - 将 `nginx-dev.conf` 内容添加到 `nginx.conf` 的 `http` 块中
   - 或者创建符号链接：
   ```bash
   sudo ln -s /path/to/nginx-dev.conf /etc/nginx/sites-enabled/dianping-dev.conf
   ```

4. **检查配置语法**
   ```bash
   sudo nginx -t
   ```

5. **启动/重启 Nginx**
   ```bash
   # 启动
   sudo nginx
   
   # 重启（修改配置后）
   sudo nginx -s reload
   ```

---

### 3. 访问应用

打开浏览器访问：**`http://localhost`**

- 前端页面：`http://localhost/`
- 登录页：`http://localhost/login`
- API 请求：`http://localhost/api/*`（自动代理到后端）

---

## 📁 配置文件说明

### nginx-dev.conf 配置详解

```nginx
server {
    listen 80;                    # 监听 80 端口
    server_name localhost;         # 服务器名称

    # 前端代理（Vite 开发服务器）
    location / {
        proxy_pass http://localhost:3000;  # 转发到 Vite
        # WebSocket 支持（Vite HMR 热更新）
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 后端 API 代理
    location /api {
        proxy_pass http://localhost:8080;  # 转发到 Spring Boot
    }
}
```

---

## ✅ 优势

1. **统一入口**：所有请求都通过 `http://localhost`，避免跨域问题
2. **更接近生产环境**：开发环境和生产环境配置一致
3. **便于调试**：可以在 Nginx 日志中查看所有请求
4. **支持热更新**：配置了 WebSocket，Vite HMR 正常工作

---

## 🔧 常见问题

### 1. 端口 80 被占用

**解决方案：**
- 修改 `nginx-dev.conf` 中的 `listen 80;` 为其他端口，如 `listen 8081;`
- 访问时使用：`http://localhost:8081`

### 2. Vite 热更新不工作

**检查：**
- 确保 Nginx 配置中包含 WebSocket 支持（已配置）
- 检查浏览器控制台是否有 WebSocket 连接错误

### 3. API 请求 502 Bad Gateway

**原因：** 后端服务未启动或端口不对

**解决方案：**
- 确保后端服务运行在 `http://localhost:8080`
- 检查 Nginx 配置中的 `proxy_pass` 地址是否正确

### 4. 403 Forbidden

**原因：** Nginx 权限问题

**解决方案：**
- Windows：以管理员身份运行 Nginx
- Linux：检查文件权限和 SELinux 设置

---

## 📝 日志查看

**错误日志：**
```bash
# Windows
type logs\nginx-dev-error.log

# Linux/Mac
tail -f /var/log/nginx/nginx-dev-error.log
```

**访问日志：**
```bash
# Windows
type logs\nginx-dev-access.log

# Linux/Mac
tail -f /var/log/nginx/nginx-dev-access.log
```

---

## 🎯 下一步

配置完成后：
1. 启动前端：`cd Vue && npm run dev`
2. 启动后端：`cd Java && mvn spring-boot:run`
3. 启动 Nginx：`nginx` 或 `sudo nginx`
4. 访问：`http://localhost/login`

---

## 📚 参考

- [Nginx 官方文档](https://nginx.org/en/docs/)
- [Vite 开发服务器配置](https://vitejs.dev/config/server-options.html)
- [Spring Boot CORS 配置](https://spring.io/guides/gs/rest-service-cors/)

