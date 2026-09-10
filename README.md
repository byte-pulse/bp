# BytePulse BP

> 企业级 Spring Boot 多模块开发框架。内置认证授权、接口限流、文件存储、异步日志等能力，
> 采用「按层分模块 + 配置驱动」的组织方式，适合作为后端服务脚手架。

- 启动类：`cloud.bytepulse.BootApplication`（模块 `bp-app`）
- 默认端口：`19420`，上下文路径：`/`
- 应用名：`byte-pulse`
- 技术基线：Java 25 · Spring Boot 4.1.1 · MyBatis-Plus 3.5.17

---

## 目录

- [核心特性](#核心特性)
- [技术栈](#技术栈)
- [模块架构](#模块架构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [配置文件](#配置文件)
- [接口一览](#接口一览)
- [核心机制](#核心机制)
  - [统一响应格式](#统一响应格式)
  - [认证与 Token 管理](#认证与-token-管理)
  - [接口日志记录](#接口日志记录)
  - [自定义注解体系](#自定义注解体系)
  - [分布式限流](#分布式限流)
  - [全局异常处理与错误码](#全局异常处理与错误码)
  - [文件访问](#文件访问)
- [数据库表](#数据库表)
- [功能状态说明](#功能状态说明)
- [开发指南](#开发指南)
- [许可证](#许可证)

---

## 核心特性

- **多模块分层架构** - `repository / service / web / security / infrastructure / common` 六层解耦，依赖方向自底向上。
- **JWT + Redis 认证** - Spring Security 无状态认证；Token 明文（JWT 仅携带 `userId` 与 `fingerprint`），登录态存 Redis 并支持**滑动续期**与**单会话互踢**。
- **图形验证码** - Hutool `GifCaptcha` 动态 GIF，校验码存 Redis，一次一验。
- **多方式账号登录** - 用户名 / 手机号 / 邮箱三种标识自动路由查询，BCrypt 校验。
- **分布式接口限流** - `@RequestLimit` 注解 + Redis Lua 原子计数，已登录按 `userId`、未登录按 IP。
- **MinIO 文件访问** - 公开/鉴权两种访问模式，预签名 URL 302 跳转，Tika 探测 Content-Type。
- **灵活注解控制** - `@Anonymous`、`@BpLogging`、`@Pageable`、`@RequestLimit` 覆盖安全、日志、文档、限流。
- **统一异常与响应** - 全局异常处理器 + 中文语义化响应 + 业务错误码唯一性启动校验。
- **接口调用日志** - 请求/响应体自动捕获、对 JSON 做脱敏、向响应注入 `traceId` 与 `timestamp`，操作日志（含类型与描述）落库 `sys_log`。
- **丰富基础设施** - 动态数据源(Druid + MyBatis-Plus)、Redis 带前缀序列化、虚拟线程、SpringDoc、Actuator 全端点、RabbitMQ（含延迟队列案例）。

---

## 技术栈

| 分类     | 组件                     | 版本           | 说明                                   |
| -------- | ------------------------ | -------------- | -------------------------------------- |
| 语言     | Java                     | 25             | `java.version=25`                      |
| 框架     | Spring Boot              | 4.1.1          | 含虚拟线程                             |
| 安全     | Spring Security          | Boot BOM 管理  | JWT 无状态认证                         |
| ORM      | MyBatis-Plus             | 3.5.17         | `mybatis-plus-spring-boot4-starter`    |
| 多数据源 | dynamic-datasource       | 4.5.0          | master/slave                           |
| 连接池   | Druid                    | 1.2.28         | `druid-spring-boot-4-starter` + 监控台 |
| 分页     | PageHelper               | starter 4.1.1  | MySQL 方言                             |
| 缓存     | Spring Data Redis        | Boot BOM 管理  | Lettuce 连接池                         |
| 文档     | SpringDoc OpenAPI        | 3.1.0          | swagger-ui                             |
| JWT      | JJWT                     | 0.13.0         | 签名/解析                              |
| 验证码   | hutool-captcha           | 5.8.44         | GIF 验证码                             |
| 对象存储 | MinIO SDK                | 9.0.3          | 文件上传/预签名                        |
| 文件类型 | Apache Tika              | 4.0.0          | Content-Type 探测                      |
| JSON     | fastjson2                | 2.0.65         | 响应/过滤链输出                        |
| MQ       | spring-boot-starter-amqp | Boot BOM 管理  | 含延迟队列简单案例                     |
| HTTP     | OkHttp / UniRest         | 5.5.0 / 4.10.1 | 第三方调用                             |
| 编译     | Lombok                   | Boot BOM 管理  | 注解处理器手动装配                     |

> 版本统一由根 [pom.xml](pom.xml) 的 `dependencyManagement` 声明，各子模块不重复写版本号；
> 因此**必须从根目录进行 reactor 聚合构建**，单个子模块无法独立打包。

---

## 模块架构

根聚合 POM：`cloud.bytepulse:bp:0.0.1`，聚合顺序 `bp-repository → bp-common → bp-infrastructure → bp-web → bp-app → bp-service → bp-security`（顺序对聚合无影响，依赖由 POM 声明决定）。

```
bp (聚合 POM)
├── bp-common
│   └── bp-common-core            # 公共核心：注解/响应模型/异常/错误码/工具类/配置属性/全局过滤器
├── bp-infrastructure
│   ├── bp-cache                  # Redis 封装：带前缀序列化 + 对象/字符串/集合操作
│   ├── bp-storage                # MinIO 封装：上传/下载/删除/预签名 URL
│   └── bp-mq                     # RabbitMQ：Jackson JSON 消息转换器 + 延迟队列（死信/TTL）案例
├── bp-repository                 # 数据访问：实体、BaseMapper 接口、MyBatis 配置
├── bp-security                   # 认证授权：SecurityConfig、JwtFilter、LoginUser、权限上下文
├── bp-service                    # 业务服务：auth / file / logging
├── bp-web                        # Web 层：Controller、切面限流、全局异常、日志过滤器
└── bp-app                        # 启动模块：BootApplication、可执行 jar、全部环境配置
```

模块依赖关系（箭头 = 依赖）：

```
bp-common-core (无内部依赖，最底层)
   ▲
   ├── bp-cache ── bp-storage ── bp-mq        (bp-infrastructure)
   │        │        │
   ├── bp-repository   (依赖 bp-common-core)
   │        ▲
   ├── bp-security     (依赖 bp-repository + bp-cache)
   │        ▲
   ├── bp-service      (依赖 cache/storage/mq/repository/security)
   │        ▲
   ├── bp-web          (依赖 bp-service + starter-web/actuator)
   │        ▲
   └── bp-app          (依赖 bp-web，可执行)
```

组件装配说明：

- 各模块 Java 类均位于 `cloud.bytepulse.*` 包，由 `BootApplication`（包 `cloud.bytepulse`）统一组件扫描；
- Mapper 由 `MyBatisConfig` 的 `@MapperScan("cloud.bytepulse.**.mapper")` 装配，实体字段遵循「下划线 ↔ 驼峰」自动映射；
- Redis / MinIO / MQ 等外部组件配置统一由 `bp.app.*` 前缀 + 各环境 YAML 驱动。

### 关键类索引

| 模块           | 包                                  | 说明                                                                            |
| -------------- | ----------------------------------- | ------------------------------------------------------------------------------- |
| bp-common-core | `common.core.annotation`            | `Anonymous` / `BpLogging` / `Pageable` / `RequestLimit`                         |
| bp-common-core | `common.core.enums`                 | `OperateEnum`（日志操作类型枚举）                                               |
| bp-common-core | `common.core.model`                 | `ApiResponse<T>` 统一响应、`ApiLoggingInfo`                                     |
| bp-common-core | `common.core.component`             | `LoggingFilterInterface`（日志过滤器抽象基类）                                  |
| bp-common-core | `common.core.exception`             | `BytePulseException`、错误码枚举、重复错误码校验器                              |
| bp-common-core | `common.core.properties`            | `AppProperties`（前缀 `bp.app`）                                                |
| bp-common-core | `common.core.constant`              | `ApiPathRegistry` 匿名 / 日志接口注册表                                         |
| bp-common-core | `common.core.util`                  | `JWTUtils` / `CryptoUtils` / `ReqUtils` / `TraceIdUtils` / `JsonMaskerUtils` 等 |
| bp-security    | `security.config`                   | `SecurityConfig` 过滤链、匿名路径收集器、日志过滤器接入                         |
| bp-security    | `security.filter`                   | `JwtFilter`                                                                     |
| bp-security    | `security`                          | `LoginUser` / `LoginUserInfo` / `Auths`                                         |
| bp-cache       | `cache.redis`                       | `RedisCache` / `RedisConfig` / `RedisPrefixSerializer`                          |
| bp-storage     | `storage.minio`                     | `MinioTemplate` / `MinioConfig`                                                 |
| bp-repository  | `data.entity` / `data.mapper`       | 实体 + Mapper（`SysUser` / `SysLog` / `FileMetadata`）                          |
| bp-service     | `service.auth` / `file` / `logging` | 登录、文件、日志服务                                                            |
| bp-web         | `web.aspect`                        | `RequestLimitAspect`（Redis Lua 限流）                                          |
| bp-web         | `web.exception.handler`             | `GlobalExceptionHandler`                                                        |
| bp-web         | `web.logging`                       | `LoggingFilter`（捕获/脱敏/落库）、`NoLoggingUrlCollector`                      |
| bp-web         | `web.controller`                    | `AuthController` / `DevAuthController` / `FileController`                       |

---

## 环境要求

- JDK 25+
- Maven 3.9+
- MySQL 8.0+（数据库名默认 `bytepulse`）
- Redis 6.0+
- MinIO（可选，仅在文件上传/访问时需要）
- RabbitMQ（可选，`bp-mq` 含延迟队列简单案例，不配置不影响其它功能）

---

## 快速开始

### 1. 克隆项目

```bash
git clone https://gitee.com/byte-pulse/bp.git
cd bp
```

### 2. 初始化数据库

```bash
mysql -u root -p < init.sql
```

脚本创建 `bytepulse` 库所需的 4 张表，并内置管理员 `admin`（密码以 BCrypt 哈希固化在脚本中；
如需自定义密码，请在导入前将 `sys_user` 的 `password` 字段替换为目标密码的 BCrypt 值）。

### 3. 修改配置

开发环境配置位于 `bp-app/src/main/resources/application-dev.yaml`，
修改数据源、Redis、MinIO 指向即可：

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/bytepulse?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
          username: root
          password: 你的密码
          driverClassName: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0

bp:
  app:
    minio:
      endpoint: http://127.0.0.1:9000
      accessKey: minioadmin
      secretKey: minioadmin
      bucketName: bytepulse
```

> 环境 YAML（dev/test/prod）会覆盖 `config/application-app.yaml` 中的同名默认项（例如 MinIO `bucketName`）。

### 4. 构建并启动

```bash
# 根目录聚合构建（必须，子模块依赖根 POM 的依赖管理）
mvn clean install -DskipTests

# 方式一：开发模式运行（默认 active=dev）
mvn spring-boot:run -pl bp-app -am

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar bp-app/target/bp-app-0.0.1.jar
```

### 5. 访问应用

| 入口         | 地址                                         | 备注                         |
| ------------ | -------------------------------------------- | ---------------------------- |
| 应用         | http://localhost:19420/                      | 默认上下文 `/`               |
| Swagger UI   | http://localhost:19420/swagger-ui/index.html | 默认分组「(全部接口)」       |
| Druid 监控台 | http://localhost:19420/druid/login.html      | 账密 `bytepulse / bytepulse` |
| 健康检查     | http://localhost:19420/actuator/health       | Actuator 全端点已暴露        |

Swagger / Druid / Actuator 路径默认在匿名放行名单内，可直接访问。

---

## 配置文件

`bp-app/src/main/resources/`：

```
application.yaml                 # 基础配置：端口、Jackson、Redis 池、虚拟线程、multipart、springdoc
application-dev.yaml             # 开发环境：数据源/Redis/RabbitMQ/MinIO
application-test.yaml            # 测试环境
application-prod.yaml            # 生产环境
config/application-app.yaml      # profile=app：bp.app.* 公共业务默认值
config/application-druid.yaml    # profile=druid：Druid 连接池 + 监控台 + MyBatis/分页
```

`application.yaml` 默认 `spring.profiles.include: app,druid`、`spring.profiles.active: dev`；
dev/test/prod 三套环境结构一致（数据源默认 master/slave 均指向本地 `bytepulse` 库，供读写分离扩展），
激活哪个 profile 即生效哪份数据源配置。日志框架为 Log4j2（`log4j2-spring.xml`），按环境输出到 `.logs/` 目录。

### `bp.app` 业务配置项

| 配置项                              | 默认值                  | 说明                                                    |
| ----------------------------------- | ----------------------- | ------------------------------------------------------- |
| `bp.app.login.expiration-minutes`   | `30`                    | 登录态 Redis 有效期(分钟)，`0` = 永久                   |
| `bp.app.redis.prefix`               | `bp:`                   | Redis 键前缀（写读自动补/去）                           |
| `bp.app.minio.endpoint`             | `http://127.0.0.1:9000` | MinIO 地址                                              |
| `bp.app.minio.access-key`           | `minioadmin`            | 访问密钥                                                |
| `bp.app.minio.secret-key`           | `minioadmin`            | 私钥（dev 环境为 `minioadmin123`）                      |
| `bp.app.minio.bucket-name`          | `bp-file`               | 默认桶（环境 YAML 覆盖为 `bytepulse`）                  |
| `bp.app.captcha.width`              | `160`                   | 验证码宽度                                              |
| `bp.app.captcha.height`             | `60`                    | 验证码高度                                              |
| `bp.app.captcha.length`             | `4`                     | 验证码字符数                                            |
| `bp.app.captcha.expiration-seconds` | `30`                    | 验证码有效期(秒)                                        |
| `bp.app.external-api.secret-key`    | `key`                   | 第三方接口密钥（占位，见[功能状态说明](#功能状态说明)） |
| `bp.app.external-api.iv`            | `iv`                    | AES IV（占位，见[功能状态说明](#功能状态说明)）         |

---

## 接口一览

| 接口                            | 方法 | 说明                                                 | 权限                                                 |
| ------------------------------- | ---- | ---------------------------------------------------- | ---------------------------------------------------- |
| `/auth/captcha`                 | GET  | 获取 GIF 验证码（返回 `captcha` Base64 与 `uid`）    | 匿名 + 限流                                          |
| `/auth/login`                   | POST | 用户名密码登录（携带验证码）                         | 匿名                                                 |
| `/auth/getToken`                | POST | 免验证码快速登录（JSON body：`username`/`password`） | 匿名，仅 `dev`/`test` profile（`DevAuthController`） |
| `/auth/check`                   | GET  | 检查登录态                                           | 需登录                                               |
| `/file/access/{fileId}`         | GET  | 公开文件访问（302 到预签名 URL）                     | 匿名                                                 |
| `/file/authentication/{fileId}` | GET  | 私有文件访问（登录鉴权后 302）                       | 需登录                                               |
| `/file/access/upload`           | POST | 文件上传测试（multipart：`files` + `bizId`）         | 匿名                                                 |

> 接口标注 `@BpLogging` 后，其调用会自动记录到 `sys_log`（操作类型与描述见「接口日志记录」）。

---

## 核心机制

### 统一响应格式

所有接口返回统一 JSON。成功响应 `data` 存在时：

```json
{
  "code": 200,
  "message": "成功",
  "data": {}
}
```

- `data` / `e` / `traceId` / `timestamp` 字段均为 `@JsonInclude(NON_NULL)`，空值不输出；
- `traceId` / `timestamp` 由 `LoggingFilter` 在响应写回前注入（见「接口日志记录」），用于链路追踪；
- 业务异常时 `code` 会被替换为具体业务错误码（如验证码错误 `10001`）而非 `500`。

```java
return ApiResponse.success(data);          // code=200
return ApiResponse.badRequest("参数有误"); // code=400
return ApiResponse.unauthorized("未登录"); // code=401
return ApiResponse.forbidden();            // code=403
return ApiResponse.notFound();             // code=404
return ApiResponse.error("服务内部错误");   // code=500
```

### 认证与 Token 管理

登录流程（`AuthServiceImpl`）：

1. 客户端先调 `/auth/captcha` 获取 GIF 验证码与 `uid`；
2. `/auth/login` 提交 `username/password/uid/captcha`，先校验验证码（取用即删、大小写不敏感）；
3. 通过 Spring Security `AuthenticationManager` + `BCryptPasswordEncoder` 校验密码；
4. 生成随机 `fingerprint`（UUID），签发仅含 `userId`、`fingerprint` 的 JWT；
5. 用户信息（`LoginUser`）序列化写入 Redis `login:{userId}`，TTL = `bp.app.login.expiration-minutes`；
6. 返回 `userId / nickname / username / lastLogin / lastLoginIp / token`。

请求认证（`JwtFilter`）：

- 携带 `Authorization: <token>` 访问受保护接口；
- 匿名路径（`ApiPathRegistry` + `@Anonymous`）直接放行；
- Redis 中无 `login:{userId}` → `401 登录状态失效`；
- JWT 内 `fingerprint` 与 Redis 中不一致 → `401 账号在别处登陆`（新登录会覆盖旧登录态，实现单会话互踢）；
- 命中 `NEED_RE_LOGIN` 集合 → `401` 提示重新登录（权限/角色调整后强制下线）；
- 认证通过后按过期时间**滑动续期**回写 Redis（TTL 配置为 `0` 表示永久不过期）。

> 说明：`fingerprint` 是登录会话的随机指纹而非基于设备特征计算，用于「新登录使旧 Token 失效」。
> 登录账号支持用户名 / 手机号（`^1[3-9]\d{9}$`）/ 邮箱三种标识自动识别（`UserDetailServiceImpl`）。
> 开发/测试环境可用 `POST /auth/getToken`（body：`{"username":"xxx","password":"xxx"}`，免验证码登录）。

### 接口日志记录

标注 `@BpLogging` 的接口，其调用会被完整记录到 `sys_log`。链路由集成进 Spring Security 过滤链的抽象过滤器 `LoggingFilterInterface` 的子类 `LoggingFilter` 实现：

- 用 `ContentCachingRequestWrapper` / `ContentCachingResponseWrapper` 缓存请求与响应体，基于 `TraceIdUtils` 生成 `traceId`；
- 响应为 JSON 且非重定向时，向其注入 `traceId` 与 `timestamp`（见「统一响应格式」）；
- multipart 请求不缓存文件内容，仅记录表单字段与文件元信息（文件名/大小/类型）；文件类响应记录大小/类型/文件名；
- 请求体与响应体在入库前经 `JsonMaskerUtils.simpleMask()` 脱敏，避免敏感字段泄露；
- 命中 `ApiPathRegistry.LOGGING_API`（由 `NoLoggingUrlCollector` 启动时扫描 `@BpLogging` 方法注册）的接口，调用 `LoggingService.asyncSaveLog` 写入 `sys_log`（`operate`/`description`/`uri`/`method`/`params`/`response`/`cost`/`requestIp`/`userId`/`exception` 等）。

```java
@PostMapping("/login")
@Anonymous
@BpLogging(value = OperateEnum.LOGIN, desc = "登录")
public ApiResponse<LoginResultVO> login(@RequestBody @Validated LoginDTO loginDTO) {
    ...
}
```

> 说明：`LoggingService.asyncSaveLog` 标注了 `@Async`，且 `BootApplication` 已开启 `@EnableAsync`，因此日志是**异步**写入 `sys_log` 的，不阻塞业务线程。

### 自定义注解体系

| 注解            | 位置    | 作用                                                                             |
| --------------- | ------- | -------------------------------------------------------------------------------- |
| `@Anonymous`    | 类/方法 | 接口免登录，路径自动并入匿名名单（`AnonymousUrlCollector` 启动扫描注册）         |
| `@BpLogging`    | 方法    | 接口调用日志记录：`value`(操作类型 `OperateEnum`) + `desc`(描述)，写入 `sys_log` |
| `@Pageable`     | 方法    | 让 SpringDoc 自动为接口补充 `pageNum`(默认1)/`pageSize`(默认10) 查询参数         |
| `@RequestLimit` | 方法    | 接口限流：`time`(窗口ms，默认60000) + `count`(次数，默认5)                       |

`OperateEnum` 提供操作类型：`LOGIN / LOGOUT / QUERY / ADD / UPDATE / DELETE / EXPORT / UPLOAD`（用于日志分类展示）。

示例：

```java
@PostMapping("/login")
@Anonymous
@BpLogging(value = OperateEnum.LOGIN, desc = "登录")
public ApiResponse<LoginResultVO> login(@RequestBody @Validated LoginDTO loginDTO) {
    ...
}
```

### 分布式限流

`RequestLimitAspect` 拦截标注 `@RequestLimit` 的接口：

- 限流键：`rl:user:{userId|ip:{IP}}:{HTTP方法}:{URI}`；
- 已登录用户按 `userId`，未登录按 IP；
- 基于 Redis + Lua 原子 `INCR`，首次请求设置 `PEXPIRE`，超过阈值返回 `请求过于频繁, 请稍后重试`（HTTP 200，业务码 500）。

### 全局异常处理与错误码

`GlobalExceptionHandler`（`@ControllerAdvice`）统一处理：

| 异常                                                 | HTTP    | 响应 `code`            |
| ---------------------------------------------------- | ------- | ---------------------- |
| `BytePulseException`（业务异常）                     | 500     | 业务错误码（如 10001） |
| `NoResourceFoundException`（资源不存在）             | 401     | 401                    |
| 参数校验异常（`@Validated` / `ValidUtils`）          | 400     | 400                    |
| `BadCredentialsException` 等认证失败                 | 401     | 401                    |
| `AccessDeniedException` 等权限不足                   | 403     | 403                    |
| 数据访问异常 / MinIO 异常 / JSON 解析异常 / 缺失参数 | 400/500 | 对应码                 |
| 其他运行时异常                                       | 500     | 500                    |

业务错误码通过枚举 + `ErrorCode` 接口定义：

```java
public enum AuthErrorCode implements ErrorCode {
    CAPTCHA_ERROR(10001, "验证码错误"),
    LOGIN_FAIL(10002, "登陆失败"),
    TOKEN_INVALID(10003, "Token 无效"),
    TOKEN_EXPIRED(10004, "Token 已过期");
    // ...
}
```

服务中直接抛出即可：

```java
throw new BytePulseException(AuthErrorCode.CAPTCHA_ERROR);
```

`ErrorCodeChecker` 在应用启动时扫描基础包下所有 `ErrorCode` 枚举，发现重复错误码会直接终止启动，从源头避免错误码冲突。

### 文件访问

`FileController` + `FileServiceImpl` 基于 `file_metadata` 表与 MinIO 预签名 URL：

- **公开文件**（`access_level = 0`）：`GET /file/access/{fileId}` → 302 跳转 MinIO 预签名 URL（120 分钟有效）；
- **私有文件**（`access_level = 1`）：先 302 到 `GET /file/authentication/{fileId}` 完成登录鉴权，再跳预签名 URL；
- 上传对象路径规则：`{bizType}/{yyyyMMdd}/{bizId||"_tmp"}/{fileId}.{ext}`；
- `MinioTemplate` 提供无过期时间参数时的分级签名策略：<10MB 10 分钟、10–100MB 60 分钟、>100MB 24 小时。

---

## 数据库表

[init.sql](init.sql) 初始化 `bytepulse` 库以下 3 张表：

| 表              | 说明         | 关键字段                                                                                                                                        |
| --------------- | ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| `sys_user`      | 系统用户     | `username`/`password`(BCrypt)/`phone`/`email`/`status`(0禁用 1启用)/`last_login` 等                                                             |
| `sys_log`       | 系统接口日志 | `trace_id`(唯一)/`operate`/`description`/`http_method`/`query_params`/`body_params`/`response_result`/`cost`/`request_ip`/`user_id`/`exception` |
| `file_metadata` | 文件元数据   | `object_name`(MinIO 对象名)/`content_type`/`size`/`access_level`(0公开 1需登录)/`biz_type`/`biz_id`/`status`                                    |

> `sys_log` 新增了 `operate`（操作类型）与 `description`（操作描述）两列，由 `@BpLogging` 填充。

Mapper 使用 MyBatis-Plus `BaseMapper<T>`，实体下划线列 ↔ 驼峰属性自动映射。

---

## 功能状态说明

为保证文档与代码一致，以下功能当前状态如下：

| 功能                                    | 现状                                                                                                                                                |
| --------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| 认证 / 验证码 / 限流 / 统一异常         | ✅ 已实现并接线                                                                                                                                     |
| 文件上传 / 访问                         | ✅ 服务与 MinIO 封装已实现；已暴露访问与上传（测试）接口                                                                                            |
| 接口调用日志（`sys_log`）               | ✅ 已实现并接线：`LoggingFilter` 捕获请求/响应体、脱敏、注入 `traceId`/`timestamp`、异步落库 `sys_log`（`BootApplication` 已开启 `@EnableAsync`）   |
| `@Pageable` 文档分页参数                | ✅ 已实现（作用于 SpringDoc 文档）                                                                                                                  |
| `@BpLogging` 注解与 `OperateEnum`       | ✅ 已实现；接口需显式标注后才记录日志                                                                                                               |
| `traceId` / `timestamp` 响应字段        | ✅ 已由 `LoggingFilter` 注入                                                                                                                        |
| 方法级授权（`@PreAuthorize`）           | ✅ 已开启 `@EnableMethodSecurity`，可用于方法鉴权                                                                                                   |
| 权限体系（角色/权限点）                 | 🚧 `LoginUser.permissions` 支撑已就绪，但权限数据来源为 TODO（当前为空，`@PreAuthorize` 暂无实际权限点数据）                                        |
| 第三方接口签名认证（HMAC/Nonce 防重放） | 🚧 基础能力已就绪：`CryptoUtils`(RSA/AES/SHA/HMAC)、`bp.app.external-api` 配置占位；`api_credentials` 表与实体已被移除，签名校验过滤器/切面尚未实现 |
| RabbitMQ（`bp-mq`）                     | ✅ 含简单案例：`JacksonJsonMessageConverter` 消息转换、死信 + TTL 延迟队列（`RabbitConfig` / `DelayQueue`）                                         |

接入上述预留链路前，请勿在对外描述中将其声明为已上线能力。

---

## 开发指南

### 环境切换

```bash
# 开发（默认）
mvn spring-boot:run -pl bp-app -am

# 指定环境（dev / test / prod）
mvn spring-boot:run -pl bp-app -am -Dspring-boot.run.profiles=prod
```

打包后运行：

```bash
java -jar bp-app/target/bp-app-0.0.1.jar --spring.profiles.active=prod
```

### 新增业务模块步骤

1. 新业务 Controller / 切面 / 异常处理放 `bp-web`，业务服务放 `bp-service`（`service.xxx`）；
2. 新表对应实体与 `BaseMapper` 放 `bp-repository`（`data.entity` / `data.mapper`）；
3. 错误码在 `common.core.exception.enums` 新增枚举，注意错误码全局唯一（启动期自动校验）；
4. 匿名接口加 `@Anonymous`，敏感接口用 `@RequestLimit`，希望出现在 Swagger 分页文档加 `@Pageable`；
5. 分页查询：接口参数 `pageNum`/`pageSize`，业务内调用 `PageUtils.startPage()` + MyBatis-Plus 查询。

### 默认放行的匿名 / 日志接口路径

`/error`、`/actuator/**`、`/druid/**`、Swagger 相关路径等默认在匿名名单内；
业务匿名接口统一用 `@Anonymous` 声明，需要记录调用日志的接口统一用 `@BpLogging` 标注。

---

## 许可证

[MIT License](LICENSE)
