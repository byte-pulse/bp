# BytePulse Framework

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-green)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## 📖 项目简介

BytePulse 是一个通用的基础启动框架，基于 Spring Boot 3.4.4 和 Java 21 构建。该框架集成了企业级应用开发所需的基础功能，提供了完整的认证授权、数据访问、缓存管理、接口文档等核心能力，旨在为微服务架构提供统一的基础设施支撑。

## ✨ 核心特性

### 🔐 安全认证
- **JWT 认证**：基于 JSON Web Token 的无状态认证机制
- **Spring Security**：完整的安全框架集成，支持权限控制
- **验证码支持**：集成 Kaptcha 和 Hutool 验证码组件

### 🗄️ 数据访问
- **MyBatis Plus**：强大的 ORM 框架，支持代码生成和分页
- **动态数据源**：支持多数据源动态切换
- **Druid 连接池**：高性能数据库连接池，内置监控功能
- **PageHelper**：物理分页插件

### 💾 缓存管理
- **Redis 集成**：完整的 Redis 缓存支持
- **自定义序列化**：Redis 键前缀序列化支持
- **连接池配置**：优化的 Redis 连接池设置

### 📚 接口文档
- **OpenAPI 3.0**：基于 SpringDoc 的 API 文档生成
- **Swagger UI**：美观的交互式 API 文档界面
- **多环境支持**：支持不同环境的接口配置

### 🛠️ 工具集成
- **MinIO 对象存储**：文件上传和管理
- **Apache POI**：Excel 文件处理
- **微信支付 API v3**：微信支付集成
- **系统监控**：基于 OSHI 的系统信息监控
- **HTTP 客户端**：UniRest HTTP 客户端集成

### 🎯 框架特性
- **AOP 切面编程**：日志记录、请求限流等横切关注点
- **全局异常处理**：统一的异常处理机制
- **跨域支持**：灵活的 CORS 配置
- **日志管理**：基于 Log4j2 的日志框架
- **参数验证**：Bean Validation 集成

## 🚀 快速开始

### 环境要求
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 安装配置

1. **克隆项目**
```bash
git clone https://gitee.com/byte-pulse/bp.git
cd bp
```

2. **配置数据库**
```yaml
# application-dev.yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/byte_pulse?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

3. **配置 Redis**
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

4. **启动应用**
```bash
mvn clean package
java -jar target/bp-1.0.jar
```

5. **访问应用**
- 应用地址：http://localhost:19420
- API 文档：http://localhost:19420/swagger-ui.html
- 健康检查：http://localhost:19420/actuator/health


## 📖 API 使用示例

### 认证接口
```bash
# 用户登录
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password",
  "captcha": "1234"
}
```

### 带认证的请求
```bash
# 携带 JWT Token
GET /api/user/profile
Authorization: Bearer <your_jwt_token>
```

## 🎨 自定义注解

### @Anonymous
标记免认证接口：
```java
@Anonymous
@GetMapping("/public")
public ApiResponse<String> publicEndpoint() {
    return ApiResponse.success("Public content");
}
```

### @Log
记录操作日志：
```java
@Log(value = "用户登录")
@PostMapping("/login")
public ApiResponse<LoginResultVO> login(@RequestBody LoginDTO loginDTO) {
    // 业务逻辑
}
```

### @RequestLimit
接口请求限流：
```java
@RequestLimit(count = 5, time = 60)
@GetMapping("/limited")
public ApiResponse<String> limitedEndpoint() {
    return ApiResponse.success("Limited content");
}
```

## 🔧 工具类使用

### JWT 工具类
```java
String token = JWTUtils.generateToken(userId, username);
Claims claims = JWTUtils.parseToken(token);
boolean isValid = JWTUtils.validateToken(token);
```

### Redis 工具类
```java
RedisUtils.set("key", "value", 3600);
String value = RedisUtils.get("key");
RedisUtils.delete("key");
```

### MinIO 工具类
```java
String url = MinioUtils.uploadFile(file, "bucket-name");
MinioUtils.downloadFile("bucket-name", "object-name");
MinioUtils.deleteFile("bucket-name", "object-name");
```

## 📊 监控与管理

### Actuator 端点
- `/actuator/health` - 应用健康状态
- `/actuator/info` - 应用信息
- `/actuator/metrics` - 应用指标

### Druid 监控
- `/druid/index.html` - Druid 监控控制台

## 🔄 环境配置

支持多环境配置：
- `application-dev.yaml` - 开发环境
- `application-prod.yaml` - 生产环境
- `application-druid.yaml` - 数据源配置

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 项目主页：https://gitee.com/byte-pulse
- 问题反馈：[Issues](https://gitee.com/byte-pulse/bp/issues)

## 🙏 致谢

感谢以下开源项目的支持：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis Plus](https://baomidou.com/)
- [Druid](https://github.com/alibaba/druid)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [MinIO](https://min.io/)
- 以及所有其他优秀的开源项目！