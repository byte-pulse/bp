# BytePulse BP Framework

一个功能强大、高性能的SpringBoot通用开发框架，集成了企业级应用开发的最佳实践和常用组件。

## 🚀 项目特性

- **SpringBoot 3.4.4**: 基于最新SpringBoot版本，支持Java 21
- **安全认证**: 集成Spring Security + JWT，提供完整的认证授权体系
- **数据访问**: 支持MyBatis-Plus + 多数据源 + Druid连接池
- **缓存支持**: 集成Redis，提供分布式缓存能力
- **接口文档**: 集成SpringDoc OpenAPI 3.0，自动生成API文档
- **日志系统**: 完整的请求日志记录和TraceId追踪
- **文件存储**: 支持MinIO对象存储
- **工具集成**: 验证码、Excel处理、支付、监控等实用工具
- **限流防护**: 基于AOP的接口限流功能
- **全局异常**: 统一的异常处理和响应封装

## 📋 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.4.4 | 核心框架 |
| Spring Security | - | 安全框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| Dynamic DataSource | 4.3.1 | 多数据源 |
| Druid | 1.2.23 | 数据库连接池 |
| Redis | - | 缓存中间件 |
| MySQL | - | 关系型数据库 |
| SpringDoc | 2.8.6 | API文档 |
| JWT | 0.12.6 | 身份认证 |
| MinIO | 8.5.17 | 对象存储 |
| Hutool | 5.8.39 | 工具类库 |

## 🛠️ 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone https://gitee.com/byte-pulse/bp.git
cd bp
```

### 2. 数据库初始化
执行项目根目录下的 `init.sql` 文件：
```bash
mysql -u root -p < init.sql
```

### 3. 配置文件
根据环境修改配置文件：

**开发环境配置** (`application-dev.yaml`):
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

### 4. 启动项目
```bash
mvn clean install
mvn spring-boot:run
```

或者直接运行 `AdminApplication.java` 的main方法。

### 5. 访问应用

- **应用地址**: http://localhost:19420
- **API文档**: http://localhost:19420/swagger-ui.html
- **健康检查**: http://localhost:19420/actuator/health

### 6. 默认账号
```
用户名: admin
密码: admin123
```

## 🔧 核心功能

### 1. 统一响应格式
所有API接口都返回统一的JSON格式：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "request_id": "trace-id",
  "timestamp": "2024-01-01 12:00:00"
}
```

### 2. JWT认证
- 自动生成和验证JWT Token
- 支持Token刷新机制
- 灵活的权限控制

### 3. 接口限流
使用 `@RequestLimit` 注解实现接口限流：
```java
@RequestLimit(value = 10, period = 60) // 60秒内最多请求10次
public ResponseEntity<?> someMethod() {
    // 业务逻辑
}
```

### 4. 请求日志
自动记录所有HTTP请求的详细信息，包括：
- 请求参数
- 响应结果
- 执行时间
- 用户信息

### 5. 异常处理
全局异常处理机制，统一错误响应格式。

## 🎯 使用示例

### 1. 创建Controller
```java
@RestController
@RequestMapping("/api/demo")
public class DemoController {
    
    @GetMapping("/hello")
    public ApiResponse hello() {
        return ApiResponse.success("Hello, BytePulse!");
    }
    
    @GetMapping("/user/{id}")
    public ApiResponse getUser(@PathVariable Long id) {
        // 获取用户信息
        return ApiResponse.success(userService.getById(id));
    }
}
```

### 2. 使用工具类
```java
@Service
public class DemoService {
    
    @Autowired
    private RedisUtils redisUtils;
    
    @Autowired
    private JWTUtils jwtUtils;
    
    public void demoMethod() {
        // Redis操作
        redisUtils.set("key", "value", 3600);
        
        // JWT操作
        String token = jwtUtils.generateToken(userId);
    }
}
```

### 3. 自定义注解使用

**跳过认证**：
```java
@GetMapping("/public")
@Anonymous
public ApiResponse publicApi() {
    return ApiResponse.success("公开接口");
}
```

**跳过日志记录**：
```java
@PostMapping("/upload")
@NoLogging
public ApiResponse upload() {
    return ApiResponse.success("文件上传");
}
```

## 🔒 安全配置

项目已集成完整的安全配置：

1. **认证机制**: 基于JWT的无状态认证
2. **密码加密**: 使用BCrypt加密算法
3. **CORS配置**: 支持跨域请求
4. **接口保护**: 自动拦截未认证请求

## 📊 监控与健康检查

集成了Spring Boot Actuator，提供：

- 应用健康状态检查
- 系统信息监控
- 性能指标统计

访问地址：`http://localhost:19420/actuator`

## 🧪 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 📝 开发规范

1. **代码风格**: 遵循阿里巴巴Java开发手册
2. **提交规范**: 使用约定式提交规范
3. **分支策略**: Git Flow工作流
4. **API设计**: RESTful API设计原则

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目地址: https://gitee.com/byte-pulse/bp
- 问题反馈: [Issues](https://gitee.com/byte-pulse/bp/issues)

## ⭐ Star History

如果这个项目对您有帮助，请给我们一个 Star！

---

**BytePulse Framework** - 让Java开发更简单、更高效！