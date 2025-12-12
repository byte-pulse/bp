# BytePulse BP Framework

> 🚀 一个功能强大、高性能的SpringBoot通用开发框架，集成了企业级应用开发的最佳实践和常用组件。

## ✨ 项目特性

- **🔥 最新技术栈**: 基于SpringBoot 3.5.8 + Java 21，享受最新技术红利
- **🔐 安全认证**: 集成Spring Security + JWT，提供完整的认证授权体系
- **💾 数据访问**: 支持MyBatis-Plus + 多数据源 + Druid连接池
- **⚡ 缓存支持**: 集成Redis，提供分布式缓存能力
- **📚 接口文档**: 集成SpringDoc OpenAPI 3.0，自动生成API文档
- **📊 日志系统**: 完整的请求日志记录，支持TraceId追踪和异常记录
- **🗄️ 文件存储**: 支持MinIO对象存储，文件上传下载一体化
- **🛠️ 工具集成**: 验证码、Excel处理、支付、监控等实用工具
- **🛡️ 限流防护**: 基于AOP的接口限流功能
- **⚠️ 异常处理**: 统一的异常处理和响应封装
- **🔧 开发工具**: 内置测试接口，支持文件上传下载测试

## 📋 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.8 | 核心框架 |
| Spring Security | - | 安全框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| Dynamic DataSource | 4.3.1 | 多数据源 |
| Druid | 1.2.23 | 数据库连接池 |
| Redis | - | 缓存中间件 |
| MySQL | - | 关系型数据库 |
| SpringDoc | 2.8.14 | API文档 |
| JWT | 0.12.6 | 身份认证 |
| MinIO | 8.5.17 | 对象存储 |
| Apache Tika | 3.2.3 | 文件类型检测 |
| Hutool | 5.8.39 | 工具类库 |

## 🛠️ 环境要求

- **JDK**: 21+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+

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

数据库包含以下表结构：
- `sys_user`: 系统用户表（默认管理员账号：admin/admin123）
- `sys_log`: 系统日志表（支持TraceId追踪和异常记录）

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
# 方式一：Maven命令启动
mvn clean install
mvn spring-boot:run

# 方式二：直接运行启动类
# 运行 AdminApplication.java 的main方法
```

### 5. 访问应用

- **应用地址**: http://localhost:19420
- **API文档**: http://localhost:19420/swagger-ui.html
- **健康检查**: http://localhost:19420/actuator/health

### 6. 默认账号
```
用户名: admin
密码: admin123
```

## 📁 项目结构

```
src/main/java/cloud/bytepulse/bp/
├── AdminApplication.java           # 启动类
├── app/                           # 应用层
│   ├── auth/                      # 认证模块
│   │   ├── controller/            # 认证控制器
│   │   ├── service/               # 认证服务层
│   │   ├── dto/                   # 数据传输对象
│   │   └── vo/                    # 视图对象
│   ├── logging/                   # 日志模块
│   │   └── service/               # 日志服务层
│   └── testapi/                   # 测试API模块
│       └── controller/            # 测试控制器
├── common/                        # 公共模块
│   └── utils/                     # 工具类集合
├── domain/                        # 领域模型
│   ├── mapper/                    # 数据访问层
│   ├── models/                    # 实体类
│   └── ApiResponse.java           # 统一响应格式
└── framework/                     # 框架层
    ├── annotation/                # 自定义注解
    │   ├── Anonymous.java         # 跳过认证注解
    │   ├── NoLogging.java         # 跳过日志注解
    │   └── RequestLimit.java      # 限流注解
    ├── aspect/                    # 切面编程
    ├── config/                    # 配置类
    │   ├── SecurityConfig.java    # 安全配置
    │   ├── RedisConfig.java       # Redis配置
    │   ├── MinioConfig.java       # MinIO配置
    │   └── OpenAPIConfig.java     # API文档配置
    ├── constant/                  # 常量定义
    ├── enums/                     # 枚举类
    ├── exception/                 # 异常处理
    │   ├── ExceptionProcessor.java # 全局异常处理器
    │   ├── BytePulseException.java # 自定义异常
    │   └── BytePulseArgumentNotValidException.java # 参数验证异常
    ├── filter/                    # 过滤器
    │   ├── JWTFilter.java         # JWT认证过滤器
    │   ├── LoggingFilter.java     # 请求日志过滤器
    │   └── GlobalCorsFilter.java  # CORS过滤器
    └── interceptor/               # 拦截器
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

### 2. JWT认证体系
- 自动生成和验证JWT Token
- 支持Token刷新机制
- 灵活的权限控制
- 无状态认证设计

### 3. 请求日志追踪
- **TraceId追踪**: 每个请求分配唯一TraceId，便于链路追踪
- **完整记录**: 请求参数、响应结果、执行时间、异常信息
- **性能监控**: 自动计算接口响应耗时
- **用户关联**: 记录请求用户信息

### 4. 接口限流
使用 `@RequestLimit` 注解实现接口限流：
```java
@RequestLimit(value = 10, period = 60) // 60秒内最多请求10次
public ResponseEntity<?> someMethod() {
    // 业务逻辑
}
```

### 5. 全局异常处理
- 统一异常响应格式
- 参数验证异常处理
- 业务异常分类处理
- 调试信息可控展示

### 6. 文件处理
- **文件上传**: 支持多文件上传，自动文件类型检测
- **文件下载**: 支持文件名编码，中文文件名正确显示
- **MinIO集成**: 对象存储支持
- **类型检测**: 基于Apache Tika的文件类型识别

## 🎯 使用示例

### 1. 创建Controller
```java
@RestController
@RequestMapping("/api/demo")
@Tag(name = "示例接口")
public class DemoController {
    
    @GetMapping("/hello")
    @Operation(summary = "问候接口")
    @Anonymous  // 跳过认证
    public ApiResponse hello() {
        return ApiResponse.success("Hello, BytePulse!");
    }
    
    @GetMapping("/user/{id}")
    @Operation(summary = "获取用户信息")
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
        String value = redisUtils.getCacheObject("key");
        
        // JWT操作
        String token = jwtUtils.generateToken(userId);
        boolean isValid = jwtUtils.validateToken(token);
    }
}
```

### 3. 文件上传下载
```java
@PostMapping("/upload")
@Operation(summary = "文件上传下载测试")
@Anonymous
public ResponseEntity<byte[]> fileUploadDownload(
    @RequestParam String name, 
    @RequestParam MultipartFile[] files) throws IOException {
    
    // 获取文件内容
    byte[] fileBytes = files[0].getBytes();
    String filename = files[0].getOriginalFilename();

    // 设置返回头
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", files[0].getContentType());
    headers.setContentDisposition(ContentDisposition.builder("attachment")
            .filename(filename, StandardCharsets.UTF_8)
            .build());

    // 返回文件内容
    return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
}
```

### 4. 自定义注解使用

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
@PostMapping("/sensitive")
@NoLogging
public ApiResponse sensitiveOperation() {
    return ApiResponse.success("敏感操作");
}
```

## 🔒 安全配置

项目已集成完整的安全配置：

1. **认证机制**: 基于JWT的无状态认证
2. **密码加密**: 使用BCrypt加密算法
3. **CORS配置**: 支持跨域请求
4. **接口保护**: 自动拦截未认证请求
5. **路径安全**: 灵活的路径权限配置

## 📊 监控与健康检查

集成了Spring Boot Actuator，提供：

- **应用健康状态**: `/actuator/health`
- **系统信息监控**: `/actuator/info`
- **性能指标统计**: `/actuator/metrics`
- **环境信息**: `/actuator/env`

## 🧪 测试接口

框架内置测试接口，方便开发和调试：

1. **文件上传下载测试**: `POST /test/fileUploadDownload`
2. **无验证码登录测试**: `POST /test/loginWithoutCaptcha`

这些接口使用 `@Anonymous` 注解，可以无需认证直接访问。

## 🔧 配置说明

### 1. 多环境配置
- `application.yaml`: 主配置文件
- `application-dev.yaml`: 开发环境配置
- `application-prod.yaml`: 生产环境配置
- `application-druid.yaml`: 数据库连接池配置

### 2. 核心配置项
```yaml
# 应用配置
server:
  port: 19420
  
# Redis配置
spring:
  data:
    redis:
      timeout: 10s
      lettuce:
        pool:
          max-active: 8
          max-idle: 8

# 管理端点
management:
  endpoints:
    web:
      exposure:
        include: "*"

# 异常处理
exception:
  processer:
    debugInfo: false  # 生产环境建议关闭
```

## 📝 开发规范

1. **代码风格**: 遵循阿里巴巴Java开发手册
2. **提交规范**: 使用约定式提交规范
3. **分支策略**: Git Flow工作流
4. **API设计**: RESTful API设计原则
5. **注释规范**: 使用Swagger注解完善API文档

## 🚀 部署指南

### 1. 本地部署
```bash
mvn clean package
java -jar target/bp-1.0.jar
```

### 2. Docker部署
```dockerfile
FROM openjdk:21-jre-slim
COPY target/bp-1.0.jar app.jar
EXPOSE 19420
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 3. 生产环境注意事项
- 修改默认账号密码
- 关闭debug信息展示
- 配置合适的JVM参数
- 设置日志级别

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- **项目地址**: https://gitee.com/byte-pulse/bp
- **问题反馈**: [Issues](https://gitee.com/byte-pulse/bp/issues)

## ⭐ 更新日志

### v1.0
- ✨ 基于SpringBoot 3.5.8构建
- 🔧 修正framework目录拼写错误
- 📊 增强日志系统，支持TraceId追踪
- 🛠️ 新增测试API模块
- ⬆️ 升级依赖版本：SpringDoc 2.8.14、Apache Tika 3.2.3
- 🗄️ 优化数据库表结构，增加异常记录

---

**BytePulse Framework** - 让Java开发更简单、更高效！🎯