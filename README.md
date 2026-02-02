# BytePulse BP Framework

> 企业级SpringBoot通用开发框架，集成认证授权、日志追踪、文件存储、第三方接口等核心功能

## 特性

- 基于SpringBoot 3.5.8 + Java 21最新技术栈
- JWT认证授权体系，支持Token刷新
- 第三方接口API密钥认证（HMAC-SHA256签名 + Nonce防重放）
- 完整的请求日志追踪（TraceId链路追踪）
- MinIO对象存储，支持文件上传下载
- 接口限流防护（基于AOP）
- 统一异常处理，支持调试信息控制
- SpringDoc OpenAPI 3.0自动生成API文档
- Redis分布式缓存
- MyBatis-Plus + Druid连接池
- 验证码系统（Kaptcha + Hutool）

## 技术栈

| 技术            | 版本   | 说明         |
| --------------- | ------ | ------------ |
| Spring Boot     | 3.5.8  | 核心框架     |
| Spring Security | -      | 安全框架     |
| MyBatis-Plus    | 3.5.5  | ORM框架      |
| Druid           | 1.2.23 | 数据库连接池 |
| Redis           | -      | 缓存中间件   |
| SpringDoc       | 2.8.14 | API文档      |
| JWT             | 0.12.6 | 身份认证     |
| MinIO           | 8.5.17 | 对象存储     |
| Apache Tika     | 3.2.3  | 文件类型检测 |
| Kaptcha         | 2.3.2  | 图形验证码   |

## 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## 快速开始

### 1. 克隆项目

```bash
git clone https://gitee.com/byte-pulse/bp.git
cd bp
```

### 2. 数据库初始化

```bash
mysql -u root -p < init.sql
```

数据库包含以下表：

- `sys_user`: 系统用户表（默认管理员：admin/admin123）
- `sys_log`: 系统日志表（TraceId追踪）
- `api_credentials`: API凭证表（第三方接口认证）
- `file_metadata`: 文件元数据表

### 3. 配置文件

修改 `application-dev.yaml` 配置数据库和Redis：

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

### 5. 访问应用

- 应用地址: http://localhost:19420
- API文档: http://localhost:19420/swagger-ui.html
- 健康检查: http://localhost:19420/actuator/health

## 项目结构

```
src/main/java/cloud/bytepulse/bp/
├── AdminApplication.java           # 启动类
├── app/                           # 应用层
│   ├── auth/                      # 认证模块
│   │   ├── controller/            # 认证控制器
│   │   └── dto/                   # 数据传输对象
│   ├── file/                      # 文件模块
│   │   ├── controller/            # 文件控制器
│   │   └── service/               # 文件服务
│   ├── logging/                   # 日志模块
│   ├── scheduler/                 # 定时任务
│   └── testapi/                   # 测试API模块
├── common/                        # 公共模块
│   └── util/                      # 工具类集合
│       ├── json/                  # JSON工具
│       ├── minio/                 # MinIO工具
│       ├── AuthUtils.java         # 认证工具
│       ├── CryptoUtils.java       # 加密工具
│       ├── DateUtils.java         # 日期工具
│       ├── FileMetaUtils.java     # 文件元数据工具
│       ├── JWTUtils.java          # JWT工具
│       ├── RedisUtils.java        # Redis工具
│       ├── TraceIdUtils.java      # TraceId工具
│       └── ...
├── domain/                        # 领域模型
│   ├── entity/                    # 实体类
│   ├── mapper/                    # 数据访问层
│   └── ApiResponse.java           # 统一响应格式
└── framework/                     # 框架层
    ├── annotation/                # 自定义注解
    ├── aspect/                    # 切面编程
    ├── config/                    # 配置类
    ├── constant/                  # 常量定义
    ├── exception/                 # 异常处理
    └── filter/                    # 过滤器
```

## 核心功能

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "traceId": "trace-id",
  "timestamp": 1234567890
}
```

### JWT认证

- 自动生成和验证JWT Token
- 支持Token刷新机制
- 无状态认证设计
- 支持调试模式快速获取Token

**认证接口：**

- `GET /auth/captcha` - 获取验证码
- `POST /auth/login` - 登录
- `GET /auth/check` - 检查登录状态

### 第三方接口认证

使用 `@ExternalApi` 注解标记第三方接口：

```java
@PostMapping("/external")
@Operation(summary = "第三方接口")
@ExternalApi
public ApiResponse externalApi(OrderDTO orderDTO) {
    return ApiResponse.success(orderDTO);
}
```

**请求头要求：**

```
X-App-Key: your_app_key
X-Timestamp: timestamp
X-Nonce: random_string
X-Signature: hmac_sha256_signature
```

**签名计算：**

```
body_hash = SHA256(body)
sign_content = uri + "\n" + app_key + "\n" + timestamp + "\n" + nonce + "\n" + body_hash
signature = HMAC-SHA256(sign_content, api_secret)
```

**Python调用示例：**

```python
import time
import uuid
import hmac
import hashlib
import requests
import base64

def sha256_base64(data: str) -> str:
    hash_bytes = hashlib.sha256(data.encode("utf-8")).digest()
    return base64.b64encode(hash_bytes).decode("utf-8")

def sign_hmac_sha256_hex(data: str, secret: str) -> str:
    mac = hmac.new(secret.encode("utf-8"), data.encode("utf-8"), digestmod=hashlib.sha256)
    return mac.hexdigest()

def call_external_api():
    url = "http://127.0.0.1:19420/test/external"
    uri = "/test/external"
    app_key = "your_app_key"
    api_secret = "your_api_secret"
    body = '{"orderId":123,"amount":99.9}'
    timestamp = str(int(time.time() * 1000))
    nonce = uuid.uuid4().hex
    body_hash = sha256_base64(body)
    sign_content = "\n".join([uri, app_key, timestamp, nonce, body_hash])
    signature = sign_hmac_sha256_hex(sign_content, api_secret)
    headers = {
        "Content-Type": "application/json",
        "X-App-Key": app_key,
        "X-Timestamp": timestamp,
        "X-Nonce": nonce,
        "X-Signature": signature
    }
    resp = requests.post(url, data=body, headers=headers)
    print(resp.text)

call_external_api()
```

### 请求日志追踪

- TraceId链路追踪
- 完整记录请求参数、响应结果、执行时间
- 性能监控，自动计算接口响应耗时
- 用户关联，记录请求用户信息

### 接口限流

使用 `@RequestLimit` 注解实现接口限流：

```java
@RequestLimit(count = 10, time = 60000)
public ResponseEntity<?> someMethod() {
}
```

### 文件处理

- 文件上传下载
- MinIO对象存储
- Apache Tika文件类型识别
- 支持公开和私有文件访问

**文件接口：**

- `GET /file/access/{fileId}` - 公开文件访问
- `GET /file/private/{fileId}` - 私有文件访问（需要认证）

### 验证码系统

- 图形验证码（Kaptcha）
- Hutool验证码
- 无验证码调试接口
- Redis存储验证码

## 自定义注解

- `@Anonymous`: 跳过认证，允许匿名访问
- `@ExternalApi`: 第三方接口认证，使用API密钥
- `@NoLogging`: 跳过日志记录
- `@RequestLimit`: 接口限流，防止接口被恶意调用

## 使用示例

### 创建Controller

```java
@RestController
@RequestMapping("/api/demo")
@Tag(name = "示例接口")
public class DemoController {

    @GetMapping("/hello")
    @Operation(summary = "问候接口")
    @Anonymous
    public ApiResponse hello() {
        return ApiResponse.success("Hello, BytePulse!");
    }
}
```

### 使用工具类

```java
@Service
public class DemoService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private JWTUtils jwtUtils;

    public void demoMethod() {
        redisUtils.set("key", "value", 3600);
        String token = jwtUtils.generateToken(userId);
    }
}
```

### 文件上传

```java
@Autowired
private FileMetaUtils fileMetaUtils;

public void uploadFile(MultipartFile file) throws Exception {
    fileMetaUtils.uploadFile("bizType", "bizId", file, false, false);
}
```

## 配置说明

### 多环境配置

- `application.yaml`: 主配置文件
- `application-dev.yaml`: 开发环境
- `application-prod.yaml`: 生产环境

### 核心配置项

```yaml
server:
  port: 19420

spring:
  data:
    redis:
      timeout: 10s

api:
  external:
    secretKey: your_secret_key_for_aes_encryption
    iv: your_initialization_vector_for_aes

login:
  expiration: 30
```

### 异常处理配置

```yaml
exception:
  processer:
    debugInfo: false
```

- 开发环境: 设置为`true`，返回详细错误信息
- 生产环境: 设置为`false`，只返回通用错误信息

## 部署指南

### 本地部署

```bash
mvn clean package
java -jar target/bp-1.0.jar
```

### Docker部署

```dockerfile
FROM openjdk:21-jre-slim
COPY target/bp-1.0.jar app.jar
EXPOSE 19420
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 生产环境注意事项

- 修改默认账号密码
- 关闭debug信息展示
- 配置合适的JVM参数
- 启用HTTPS
- 配置防火墙规则

## 许可证

MIT License

## 项目地址

https://gitee.com/byte-pulse/bp
