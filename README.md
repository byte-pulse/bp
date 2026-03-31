# BytePulse BP

企业级 Spring Boot 通用开发框架，集成认证授权、日志追踪、文件存储、第三方接口认证等核心能力。

## 核心特性

- **JWT 认证授权** - 无状态 Token 认证，支持登录状态管理与 Token 刷新
- **第三方接口认证** - HMAC-SHA256 签名 + Nonce 防重放攻击
- **请求日志追踪** - TraceId 链路追踪，完整记录请求响应
- **MinIO 文件存储** - 对象存储服务，支持公开/私有访问控制
- **接口限流防护** - 基于 Redis + Lua 的分布式限流
- **统一异常处理** - 全局异常捕获，标准化错误响应
- **API 文档自动生成** - SpringDoc OpenAPI 3.0

## 技术栈

| 技术            | 版本   | 说明         |
| --------------- | ------ | ------------ |
| Spring Boot     | 3.5.12 | 核心框架     |
| Spring Security | -      | 安全框架     |
| MyBatis-Plus    | 3.5.16 | ORM 框架     |
| Druid           | 1.2.28 | 数据库连接池 |
| Redis           | -      | 分布式缓存   |
| SpringDoc       | 2.8.16 | API 文档     |
| JJWT            | 0.13.0 | JWT 认证     |
| MinIO           | 9.0.0  | 对象存储     |
| Apache Tika     | 3.3.0  | 文件类型检测 |
| Kaptcha         | 2.3.2  | 图形验证码   |

## 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- MinIO (可选，用于文件存储)

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

初始化脚本将创建以下数据表：

| 表名            | 说明                                     |
| --------------- | ---------------------------------------- |
| sys_user        | 系统用户表，默认管理员: admin / admin123 |
| sys_log         | 系统日志表，记录 TraceId 追踪信息        |
| api_credentials | API 凭证表，用于第三方接口认证           |
| file_metadata   | 文件元数据表                             |

### 3. 修改配置

编辑 `src/main/resources/application-dev.yaml`：

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/bytepulse?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
          username: your_username
          password: your_password
  data:
    redis:
      host: localhost
      port: 6379

app:
  minio:
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: bytepulse
```

### 4. 启动项目

```bash
mvn clean install -DskipTests
mvn spring-boot:run -Pdev
```

### 5. 访问应用

- 应用地址: http://localhost:19420
- API 文档: http://localhost:19420/swagger-ui.html
- 健康检查: http://localhost:19420/actuator/health

## 项目结构

```
src/main/java/cloud/bytepulse/bp/
├── BPApplication.java              # 启动类
├── app/                            # 应用层
│   ├── auth/                       # 认证模块
│   │   ├── controller/             # 认证控制器
│   │   ├── dto/                    # 数据传输对象
│   │   ├── service/                # 认证服务
│   │   └── vo/                     # 视图对象
│   ├── file/                       # 文件模块
│   │   ├── controller/             # 文件控制器
│   │   └── service/                # 文件服务
│   ├── logging/                    # 日志模块
│   └── scheduler/                  # 定时任务
├── common/                         # 公共模块
│   ├── annotation/                 # 自定义注解
│   │   ├── Anonymous.java          # 匿名访问注解
│   │   ├── ExternalApi.java        # 第三方接口注解
│   │   ├── NoLogging.java          # 跳过日志注解
│   │   └── RequestLimit.java       # 接口限流注解
│   ├── constant/                   # 常量定义
│   ├── enums/                      # 枚举类
│   │   └── errorcode/              # 错误码枚举
│   └── util/                       # 工具类
│       ├── json/                   # JSON 工具
│       ├── AuthUtils.java          # 认证工具
│       ├── CryptoUtils.java        # 加密工具
│       ├── DateUtils.java          # 日期工具
│       ├── JWTUtils.java           # JWT 工具
│       ├── MinioUtils.java         # MinIO 工具
│       ├── RedisUtils.java         # Redis 工具
│       └── TraceIdUtils.java       # TraceId 工具
├── domain/                         # 领域模型
│   ├── entity/                     # 实体类
│   ├── mapper/                     # 数据访问层
│   └── ApiResponse.java            # 统一响应格式
└── framework/                      # 框架层
    ├── aspect/                     # 切面编程
    ├── config/                     # 配置类
    ├── exception/                  # 异常处理
    ├── filter/                     # 过滤器
    │   ├── ExternalApiFilter.java  # 第三方接口认证过滤器
    │   ├── JWTFilter.java          # JWT 认证过滤器
    │   └── LoggingFilter.java      # 日志过滤器
    ├── http/                       # HTTP 相关
    │   └── wrapper/                # 请求响应包装器
    ├── lifecycle/                  # 生命周期
    ├── properties/                 # 配置属性
    └── runner/                     # 启动运行器
```

## 核心功能

### 统一响应格式

所有接口返回统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "traceId": "a1b2c3d4e5f6",
  "timestamp": 1712345678901
}
```

### JWT 认证

#### 认证接口

| 接口           | 方法 | 说明                   | 权限              |
| -------------- | ---- | ---------------------- | ----------------- |
| /auth/captcha  | GET  | 获取验证码             | 匿名              |
| /auth/login    | POST | 用户登录               | 匿名              |
| /auth/check    | GET  | 检查登录状态           | 需登录            |
| /auth/getToken | POST | 开发环境快速获取 Token | 匿名 (仅开发环境) |

#### 登录流程

1. 调用 `/auth/captcha` 获取验证码
2. 提交用户名、密码、验证码到 `/auth/login`
3. 获取 JWT Token，后续请求在 Header 中携带 `Authorization: <token>`

#### Token 管理

- Token 存储于 Redis，支持分布式部署
- 支持配置登录过期时间
- 支持设备指纹校验，防止多设备登录
- 支持强制重新登录机制

### 第三方接口认证

使用 `@ExternalApi` 注解标记第三方接口：

```java
@PostMapping("/external")
@Operation(summary = "第三方接口")
@ExternalApi
public ApiResponse<OrderVO> externalApi(@RequestBody OrderDTO orderDTO) {
    return ApiResponse.success(orderVO);
}
```

#### 请求头要求

| Header      | 说明                |
| ----------- | ------------------- |
| X-App-Key   | 应用标识            |
| X-Timestamp | 请求时间戳 (毫秒)   |
| X-Nonce     | 随机字符串 (防重放) |
| X-Signature | HMAC-SHA256 签名    |

#### 签名算法

```
body_hash = SHA256(body) -> Base64
sign_content = uri + "\n" + app_key + "\n" + timestamp + "\n" + nonce + "\n" + body_hash
signature = HMAC-SHA256(sign_content, api_secret)
```

#### Python 调用示例

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
    mac = hmac.new(secret.encode("utf-8"), data.encode("utf-8"), hashlib.sha256)
    return mac.hexdigest()

def call_external_api():
    url = "http://localhost:19420/test/external"
    uri = "/test/external"
    app_key = "your_app_key"
    api_secret = "your_api_secret"
    body = '{"orderId":123,"amount":99.9}'

    timestamp = str(int(time.time() * 1000))
    nonce = uuid.uuid4().hex
    body_hash = sha256_base64(body)

    sign_content = f"{uri}\n{app_key}\n{timestamp}\n{nonce}\n{body_hash}"
    signature = sign_hmac_sha256_hex(sign_content, api_secret)

    headers = {
        "Content-Type": "application/json",
        "X-App-Key": app_key,
        "X-Timestamp": timestamp,
        "X-Nonce": nonce,
        "X-Signature": signature
    }

    response = requests.post(url, data=body, headers=headers)
    print(response.json())

if __name__ == "__main__":
    call_external_api()
```

完整示例见 [example/external_api/external_api_demo.py](example/external_api/external_api_demo.py)

### 接口限流

使用 `@RequestLimit` 注解限制接口访问频率：

```java
@GetMapping("/sensitive")
@RequestLimit(count = 10, time = 60000)  // 60秒内最多10次
public ApiResponse<Void> sensitiveApi() {
    return ApiResponse.success();
}
```

参数说明：

- `count`: 时间窗口内最大请求次数
- `time`: 时间窗口大小 (毫秒)

限流基于 Redis + Lua 脚本实现，保证原子性。已登录用户按 userId 限流，未登录用户按 IP 限流。

### 文件存储

基于 MinIO 的文件存储服务，支持：

- 文件上传下载
- 公开访问 (无需登录)
- 私有访问 (需登录)
- 文件元数据管理

#### 文件访问接口

| 接口                   | 方法 | 说明         | 权限   |
| ---------------------- | ---- | ------------ | ------ |
| /file/access/{fileId}  | GET  | 公开文件访问 | 匿名   |
| /file/private/{fileId} | GET  | 私有文件访问 | 需登录 |

### 请求日志追踪

框架自动记录所有 API 请求日志，包含：

- TraceId 链路追踪标识
- 请求 URI、方法、参数
- 请求/响应体
- 请求耗时
- 异常信息

使用 `@NoLogging` 注解可跳过日志记录：

```java
@GetMapping("/health")
@NoLogging
public ApiResponse<Void> health() {
    return ApiResponse.success();
}
```

### 自定义注解

| 注解          | 说明                           |
| ------------- | ------------------------------ |
| @Anonymous    | 允许匿名访问，无需登录         |
| @ExternalApi  | 标记为第三方接口，使用签名认证 |
| @NoLogging    | 跳过请求日志记录               |
| @RequestLimit | 接口限流                       |
| @Pageable     | 分页参数封装                   |

### 异常处理

框架提供统一异常处理，支持：

- 业务异常 (BytePulseException)
- 参数校验异常
- 权限异常
- 数据库异常
- 其他运行时异常

业务异常示例：

```java
if (user == null) {
    throw new BytePulseException(AuthErrorCode.USER_NOT_FOUND);
}
```

错误码枚举：

```java
public enum AuthErrorCode implements ErrorCode {
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误");
    // ...
}
```

## 配置说明

### 应用配置 (application.yaml)

```yaml
server:
  port: 19420

spring:
  profiles:
    active: dev
  jackson:
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss
  servlet:
    multipart:
      max-file-size: 1024MB
      max-request-size: 4096MB
  threads:
    virtual:
      enabled: true # 启用虚拟线程
```

### 业务配置 (application-dev.yaml)

```yaml
app:
  login:
    expiration-minutes: 300 # 登录过期时间 (分钟), 0 表示永不过期
  redis:
    prefix: 'bp:' # Redis 键前缀
  minio:
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: bytepulse
  external-api:
    secretKey: your_secret_key # 第三方接口密钥加密密钥
    iv: your_iv # AES 加密 IV
  captcha:
    expiration-seconds: 30 # 验证码过期时间
```

## 开发指南

### 环境切换

```bash
# 开发环境
mvn spring-boot:run -Pdev

# 生产环境
mvn spring-boot:run -Pprod
```

### 构建部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/bp-1.0.jar --spring.profiles.active=prod
```

## 许可证

[MIT License](LICENSE)
