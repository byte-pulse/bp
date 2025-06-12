<div align="center">

# BytePulse BP

**An enterprise-grade Spring Boot multi-module development framework.**
Built-in authentication & authorization, API rate limiting, file storage, async logging and more —
organized as "layered modules + configuration-driven", ready to serve as a backend service scaffold.

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F.svg)](https://spring.io/projects/spring-boot)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.17-red.svg)](https://baomidou.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0%2B-DC382D.svg)](https://redis.io/)
[![MinIO](https://img.shields.io/badge/MinIO-SDK%209.0.3-C72E49.svg)](https://min.io/)

[![GitHub Stars](https://img.shields.io/github/stars/byte-pulse/bp?style=flat&color=yellow)](https://github.com/byte-pulse/bp/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/byte-pulse/bp?style=flat)](https://github.com/byte-pulse/bp/network)
[![GitHub Issues](https://img.shields.io/github/issues/byte-pulse/bp)](https://github.com/byte-pulse/bp/issues)
[![Last Commit](https://img.shields.io/github/last-commit/byte-pulse/bp)](https://github.com/byte-pulse/bp/commits/main)

**English** | [简体中文](README.zh-CN.md)

</div>

---

- Entry point: `cloud.bytepulse.BootApplication` (module `bp-app`)
- Default port: `19420`, context path: `/`
- Application name: `byte-pulse`
- Tech baseline: Java 25 · Spring Boot 4.1.1 · MyBatis-Plus 3.5.17

---

## Table of Contents

- [Core Features](#core-features)
- [Tech Stack](#tech-stack)
- [Module Architecture](#module-architecture)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Reference](#api-reference)
- [Core Mechanisms](#core-mechanisms)
  - [Unified Response Format](#unified-response-format)
  - [Authentication & Token Management](#authentication--token-management)
  - [Request Logging](#request-logging)
  - [Custom Annotations](#custom-annotations)
  - [Distributed Rate Limiting](#distributed-rate-limiting)
  - [Global Exception Handling & Error Codes](#global-exception-handling--error-codes)
  - [File Access](#file-access)
- [Database Tables](#database-tables)
- [Feature Status](#feature-status)
- [Development Guide](#development-guide)
- [License](#license)

---

## Core Features

- **Multi-module layered architecture** — six decoupled layers (`repository / service / web / security / infrastructure / common`) with dependencies flowing bottom-up.
- **JWT + Redis authentication** — stateless authentication via Spring Security; the token is opaque (the JWT carries only `userId` and `fingerprint`), while login state lives in Redis with **sliding renewal** and **single-session eviction**.
- **Captcha** — Hutool `GifCaptcha` animated GIF; the code is stored in Redis and verified once.
- **Multi-identifier login** — username / phone / email are auto-routed to the right lookup, verified with BCrypt.
- **Distributed rate limiting** — `@RequestLimit` annotation + Redis Lua atomic counting; keyed by `userId` when authenticated, otherwise by IP.
- **MinIO file access** — public and authenticated access modes, 302 redirect to presigned URLs, Content-Type detection via Tika.
- **Annotation-driven control** — `@Anonymous`, `@BpLogging`, `@Pageable`, `@RequestLimit` cover security, logging, docs and rate limiting.
- **Unified exceptions & responses** — global exception handler + semantic messages + startup-time uniqueness check for business error codes.
- **API call logging** — automatic request/response body capture, JSON masking, `traceId` and `timestamp` injected into responses, operation logs (type + description) persisted to `sys_log`.
- **Rich infrastructure** — dynamic datasource (Druid + MyBatis-Plus), prefix-aware Redis serialization, virtual threads, SpringDoc, all Actuator endpoints, RabbitMQ (with a delay-queue sample).

---

## Tech Stack

| Category       | Component                | Version        | Notes                                   |
| -------------- | ------------------------ | -------------- | --------------------------------------- |
| Language       | Java                     | 25             | `java.version=25`                       |
| Framework      | Spring Boot              | 4.1.1          | Virtual threads enabled                 |
| Security       | Spring Security          | Boot BOM       | JWT stateless auth                      |
| ORM            | MyBatis-Plus             | 3.5.17         | `mybatis-plus-spring-boot4-starter`     |
| Datasource     | dynamic-datasource       | 4.5.0          | master / slave                          |
| Pool           | Druid                    | 1.2.28         | `druid-spring-boot-4-starter` + console |
| Pagination     | PageHelper               | starter 4.1.1  | MySQL dialect                           |
| Cache          | Spring Data Redis        | Boot BOM       | Lettuce pool                            |
| Docs           | SpringDoc OpenAPI        | 3.1.0          | swagger-ui                              |
| JWT            | JJWT                     | 0.13.0         | sign / parse                            |
| Captcha        | hutool-captcha           | 5.8.44         | GIF captcha                             |
| Object storage | MinIO SDK                | 9.0.3          | upload / presigned URL                  |
| File type      | Apache Tika              | 4.0.0          | Content-Type detection                  |
| JSON           | fastjson2                | 2.0.65         | response / filter output                |
| MQ             | spring-boot-starter-amqp | Boot BOM       | includes a simple delay-queue sample    |
| HTTP           | OkHttp / UniRest         | 5.5.0 / 4.10.1 | third-party calls                       |
| Compiler       | Lombok                   | Boot BOM       | annotation processor wired manually     |

> Versions are declared centrally in the root [pom.xml](pom.xml) `dependencyManagement`; submodules do not repeat them.
> Therefore a **reactor aggregation build from the root directory is required** — a single submodule cannot be packaged standalone.

---

## Module Architecture

Root aggregator POM: `cloud.bytepulse:bp:0.0.1`, aggregating `bp-repository → bp-common → bp-infrastructure → bp-web → bp-app → bp-service → bp-security` (order does not affect aggregation; dependencies are defined by the POMs).

```
bp (aggregator POM)
├── bp-common
│   └── bp-common-core            # Shared core: annotations / response model / exceptions / error codes / utils / properties / global filters
├── bp-infrastructure
│   ├── bp-cache                  # Redis wrapper: prefix-aware serializer + object/string/collection ops
│   ├── bp-storage                # MinIO wrapper: upload/download/delete/presigned URL
│   └── bp-mq                     # RabbitMQ: Jackson JSON message converter + delay queue (DLX/TTL) sample
├── bp-repository                 # Data access: entities, BaseMapper interfaces, MyBatis config
├── bp-security                   # Auth: SecurityConfig, JwtFilter, LoginUser, permission context
├── bp-service                    # Business services: auth / file / logging
├── bp-web                        # Web layer: Controllers, rate-limit aspect, global exceptions, logging filter
└── bp-app                        # Bootstrap module: BootApplication, executable jar, all environment configs
```

Module dependencies (arrow = depends on):

```
bp-common-core (no internal deps, bottom layer)
   ▲
   ├── bp-cache ── bp-storage ── bp-mq        (bp-infrastructure)
   │        │        │
   ├── bp-repository   (depends on bp-common-core)
   │        ▲
   ├── bp-security     (depends on bp-repository + bp-cache)
   │        ▲
   ├── bp-service      (depends on cache/storage/mq/repository/security)
   │        ▲
   ├── bp-web          (depends on bp-service + starter-web/actuator)
   │        ▲
   └── bp-app          (depends on bp-web, executable)
```

Wiring notes:

- All Java classes live under `cloud.bytepulse.*` and are component-scanned by `BootApplication` (package `cloud.bytepulse`);
- Mappers are registered by `MyBatisConfig` via `@MapperScan("cloud.bytepulse.**.mapper")`; entity fields follow automatic `snake_case ↔ camelCase` mapping;
- External components (Redis / MinIO / MQ) are configured through the `bp.app.*` prefix plus per-environment YAML.

### Key Class Index

| Module         | Package                             | Description                                                                    |
| -------------- | ----------------------------------- | ------------------------------------------------------------------------------ |
| bp-common-core | `common.core.annotation`            | `Anonymous` / `BpLogging` / `Pageable` / `RequestLimit`                        |
| bp-common-core | `common.core.enums`                 | `OperateEnum` (log operation type)                                             |
| bp-common-core | `common.core.model`                 | `ApiResponse<T>` unified response, `ApiLoggingInfo`                            |
| bp-common-core | `common.core.component`             | `LoggingFilterInterface` (logging filter base class)                           |
| bp-common-core | `common.core.exception`             | `BytePulseException`, error code enums, duplicate-code checker                 |
| bp-common-core | `common.core.properties`            | `AppProperties` (prefix `bp.app`)                                              |
| bp-common-core | `common.core.constant`              | `ApiPathRegistry` anonymous / logging path registry                            |
| bp-common-core | `common.core.util`                  | `JWTUtils` / `CryptoUtils` / `ReqUtils` / `TraceIdUtils` / `JsonMaskerUtils`   |
| bp-security    | `security.config`                   | `SecurityConfig` filter chain, anonymous path collector, logging filter wiring |
| bp-security    | `security.filter`                   | `JwtFilter`                                                                    |
| bp-security    | `security`                          | `LoginUser` / `LoginUserInfo` / `Auths`                                        |
| bp-cache       | `cache.redis`                       | `RedisCache` / `RedisConfig` / `RedisPrefixSerializer`                         |
| bp-storage     | `storage.minio`                     | `MinioTemplate` / `MinioConfig`                                                |
| bp-repository  | `data.entity` / `data.mapper`       | Entities + Mappers (`SysUser` / `SysLog` / `FileMetadata`)                     |
| bp-service     | `service.auth` / `file` / `logging` | Login, file and logging services                                               |
| bp-web         | `web.aspect`                        | `RequestLimitAspect` (Redis Lua rate limiting)                                 |
| bp-web         | `web.exception.handler`             | `GlobalExceptionHandler`                                                       |
| bp-web         | `web.logging`                       | `LoggingFilter` (capture / mask / persist), `NoLoggingUrlCollector`            |
| bp-web         | `web.controller`                    | `AuthController` / `DevAuthController` / `FileController`                      |

---

## Requirements

- JDK 25+
- Maven 3.9+
- MySQL 8.0+ (default database name `bytepulse`)
- Redis 6.0+
- MinIO (optional, only needed for file upload/access)
- RabbitMQ (optional; `bp-mq` contains a simple delay-queue sample and can be left unconfigured)

---

## Getting Started

### 1. Clone the project

```bash
git clone https://github.com/byte-pulse/bp.git
cd bp
```

### 2. Initialize the database

```bash
mysql -u root -p < init.sql
```

The script creates the 4 tables required by the `bytepulse` database and seeds an `admin` user (the password is
hard-coded as a BCrypt hash in the script; to use your own password, replace the `password` column of `sys_user`
with the BCrypt value of your target password before importing).

### 3. Adjust configuration

The dev environment config lives in `bp-app/src/main/resources/application-dev.yaml`.
Point the datasource, Redis and MinIO at your own instances:

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/bytepulse?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
          username: root
          password: your-password
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

> Environment YAMLs (dev/test/prod) override the same-named defaults in `config/application-app.yaml` (for example the MinIO `bucketName`).

### 4. Build and run

```bash
# Root aggregation build (required; submodules rely on the root POM's dependency management)
mvn clean install -DskipTests

# Option 1: run in dev mode (default active=dev)
mvn spring-boot:run -pl bp-app -am

# Option 2: package then run
mvn clean package -DskipTests
java -jar bp-app/target/bp-app-0.0.1.jar
```

### 5. Access the application

| Entry         | URL                                          | Notes                               |
| ------------- | -------------------------------------------- | ----------------------------------- |
| App           | http://localhost:19420/                      | Default context `/`                 |
| Swagger UI    | http://localhost:19420/swagger-ui/index.html | Default group "(All APIs)"          |
| Druid Console | http://localhost:19420/druid/login.html      | Credentials `bytepulse / bytepulse` |
| Health Check  | http://localhost:19420/actuator/health       | All Actuator endpoints exposed      |

Swagger / Druid / Actuator paths are in the anonymous allow-list by default and can be accessed directly.

---

## Configuration

Under `bp-app/src/main/resources/`:

```
application.yaml                 # Base config: port, Jackson, Redis pool, virtual threads, multipart, springdoc
application-dev.yaml             # Dev environment: datasource/Redis/RabbitMQ/MinIO
application-test.yaml            # Test environment
application-prod.yaml            # Production environment
config/application-app.yaml      # profile=app: shared bp.app.* business defaults
config/application-druid.yaml    # profile=druid: Druid pool + console + MyBatis/pagination
```

`application.yaml` defaults to `spring.profiles.include: app,druid` and `spring.profiles.active: dev`.
The dev/test/prod environments share the same structure (the master/slave datasources both point to the local
`bytepulse` database by default, ready for read/write splitting); whichever profile is active takes effect.
Logging uses Log4j2 (`log4j2-spring.xml`) and writes to the `.logs/` directory per environment.

### `bp.app` Business Properties

| Property                            | Default                 | Description                                                              |
| ----------------------------------- | ----------------------- | ------------------------------------------------------------------------ |
| `bp.app.login.expiration-minutes`   | `30`                    | Login state TTL in Redis (minutes); `0` = never expires                  |
| `bp.app.redis.prefix`               | `bp:`                   | Redis key prefix (auto added/stripped)                                   |
| `bp.app.minio.endpoint`             | `http://127.0.0.1:9000` | MinIO endpoint                                                           |
| `bp.app.minio.access-key`           | `minioadmin`            | Access key                                                               |
| `bp.app.minio.secret-key`           | `minioadmin`            | Secret key (dev environment uses `minioadmin123`)                        |
| `bp.app.minio.bucket-name`          | `bp-file`               | Default bucket (overridden to `bytepulse` by env YAML)                   |
| `bp.app.captcha.width`              | `160`                   | Captcha width                                                            |
| `bp.app.captcha.height`             | `60`                    | Captcha height                                                           |
| `bp.app.captcha.length`             | `4`                     | Captcha character count                                                  |
| `bp.app.captcha.expiration-seconds` | `30`                    | Captcha TTL (seconds)                                                    |
| `bp.app.external-api.secret-key`    | `key`                   | Third-party API key (placeholder, see [Feature Status](#feature-status)) |
| `bp.app.external-api.iv`            | `iv`                    | AES IV (placeholder, see [Feature Status](#feature-status))              |

---

## API Reference

| Endpoint                        | Method | Description                                                 | Access                                                      |
| ------------------------------- | ------ | ----------------------------------------------------------- | ----------------------------------------------------------- |
| `/auth/captcha`                 | GET    | Get a GIF captcha (returns `captcha` Base64 and `uid`)      | Anonymous + rate-limited                                    |
| `/auth/login`                   | POST   | Username/password login (with captcha)                      | Anonymous                                                   |
| `/auth/getToken`                | POST   | Captcha-free quick login (JSON body: `username`/`password`) | Anonymous, `dev`/`test` profiles only (`DevAuthController`) |
| `/auth/check`                   | GET    | Check login state                                           | Authenticated                                               |
| `/file/access/{fileId}`         | GET    | Public file access (302 to presigned URL)                   | Anonymous                                                   |
| `/file/authentication/{fileId}` | GET    | Private file access (302 after authentication)              | Authenticated                                               |
| `/file/access/upload`           | POST   | File upload test (multipart: `files` + `bizId`)             | Anonymous                                                   |

> Endpoints annotated with `@BpLogging` are automatically recorded to `sys_log` (operation type and description —
> see [Request Logging](#request-logging)).

---

## Core Mechanisms

### Unified Response Format

All endpoints return unified JSON. For a successful response with `data`:

```json
{
  "code": 200,
  "message": "成功",
  "data": {}
}
```

- `data` / `e` / `traceId` / `timestamp` are all `@JsonInclude(NON_NULL)` — nulls are omitted;
- `traceId` / `timestamp` are injected by `LoggingFilter` before the response is written (see [Request Logging](#request-logging)) for tracing;
- On business errors, `code` is replaced with the specific business error code (such as `10001` for a captcha error) instead of `500`.

```java
return ApiResponse.success(data);          // code=200
return ApiResponse.badRequest("参数有误"); // code=400
return ApiResponse.unauthorized("未登录"); // code=401
return ApiResponse.forbidden();            // code=403
return ApiResponse.notFound();             // code=404
return ApiResponse.error("服务内部错误");   // code=500
```

### Authentication & Token Management

Login flow (`AuthServiceImpl`):

1. The client first calls `/auth/captcha` to obtain a GIF captcha and `uid`;
2. `/auth/login` submits `username/password/uid/captcha`; the captcha is verified first (fetch-and-delete, case-insensitive);
3. The password is verified via Spring Security `AuthenticationManager` + `BCryptPasswordEncoder`;
4. A random `fingerprint` (UUID) is generated and a JWT containing only `userId` and `fingerprint` is issued;
5. The user info (`LoginUser`) is serialized into Redis at `login:{userId}` with TTL = `bp.app.login.expiration-minutes`;
6. `userId / nickname / username / lastLogin / lastLoginIp / token` are returned.

Request authentication (`JwtFilter`):

- Requests to protected endpoints carry `Authorization: <token>`;
- Anonymous paths (`ApiPathRegistry` + `@Anonymous`) pass through directly;
- If `login:{userId}` is missing in Redis → `401 登录状态失效` (login state expired);
- If the JWT `fingerprint` differs from the one in Redis → `401 账号在别处登陆` (a new login overwrites the old session, implementing single-session eviction);
- If the entry is in the `NEED_RE_LOGIN` set → `401` asking to re-login (forced logout after permission/role changes);
- On success, the Redis entry is rewritten (TTL refreshed) as **sliding renewal** (TTL `0` means never expires).

> Note: `fingerprint` is a random session identifier, not a value derived from device characteristics; it is used to
> invalidate old tokens on new logins.
> The login identifier supports username / phone (`^1[3-9]\d{9}$`) / email auto-detection (`UserDetailServiceImpl`).
> In dev/test environments, `POST /auth/getToken` (body: `{"username":"xxx","password":"xxx"}`) allows captcha-free login.

### Request Logging

Endpoints annotated with `@BpLogging` have their calls fully recorded in `sys_log`. The chain is implemented by
`LoggingFilter`, a subclass of the abstract `LoggingFilterInterface` wired into the Spring Security filter chain:

- It buffers request and response bodies with `ContentCachingRequestWrapper` / `ContentCachingResponseWrapper`, and generates a `traceId` via `TraceIdUtils`;
- When the response is JSON and not a redirect, it injects `traceId` and `timestamp` (see [Unified Response Format](#unified-response-format));
- For multipart requests it does not buffer file contents — only form fields and file metadata (name/size/type); file-type responses record size/type/name;
- Request and response bodies are masked with `JsonMaskerUtils.simpleMask()` before persistence to avoid leaking sensitive fields;
- For endpoints in `ApiPathRegistry.LOGGING_API` (registered by `NoLoggingUrlCollector` scanning `@BpLogging` methods at startup), `LoggingService.asyncSaveLog` writes to `sys_log` (`operate`/`description`/`uri`/`method`/`params`/`response`/`cost`/`requestIp`/`userId`/`exception`, etc.).

```java
@PostMapping("/login")
@Anonymous
@BpLogging(value = OperateEnum.LOGIN, desc = "登录")
public ApiResponse<LoginResultVO> login(@RequestBody @Validated LoginDTO loginDTO) {
    ...
}
```

> Note: `LoggingService.asyncSaveLog` is annotated with `@Async`, and `BootApplication` enables `@EnableAsync`, so logs are written to `sys_log` **asynchronously** without blocking business threads.

### Custom Annotations

| Annotation      | Target       | Purpose                                                                                                             |
| --------------- | ------------ | ------------------------------------------------------------------------------------------------------------------- |
| `@Anonymous`    | class/method | Makes the endpoint public; the path is auto-registered into the anonymous list (`AnonymousUrlCollector` at startup) |
| `@BpLogging`    | method       | API call logging: `value` (operation type `OperateEnum`) + `desc` (description), written to `sys_log`               |
| `@Pageable`     | method       | Lets SpringDoc automatically add `pageNum` (default 1) / `pageSize` (default 10) query params                       |
| `@RequestLimit` | method       | Rate limiting: `time` (window ms, default 60000) + `count` (limit, default 5)                                       |

`OperateEnum` provides the operation types: `LOGIN / LOGOUT / QUERY / ADD / UPDATE / DELETE / EXPORT / UPLOAD` (used for log categorization).

Example:

```java
@PostMapping("/login")
@Anonymous
@BpLogging(value = OperateEnum.LOGIN, desc = "登录")
public ApiResponse<LoginResultVO> login(@RequestBody @Validated LoginDTO loginDTO) {
    ...
}
```

### Distributed Rate Limiting

`RequestLimitAspect` intercepts endpoints annotated with `@RequestLimit`:

- Rate-limit key: `rl:user:{userId|ip:{IP}}:{HTTP method}:{URI}`;
- Authenticated users are keyed by `userId`; unauthenticated ones by IP;
- It uses Redis + Lua with atomic `INCR`; the first request sets `PEXPIRE`, and exceeding the threshold returns `请求过于频繁, 请稍后重试` (HTTP 200, business code 500).

### Global Exception Handling & Error Codes

`GlobalExceptionHandler` (`@ControllerAdvice`) handles:

| Exception                                                   | HTTP    | Response `code`                  |
| ----------------------------------------------------------- | ------- | -------------------------------- |
| `BytePulseException` (business exception)                   | 500     | Business error code (e.g. 10001) |
| `NoResourceFoundException` (resource not found)             | 401     | 401                              |
| Validation exceptions (`@Validated` / `ValidUtils`)         | 400     | 400                              |
| Auth failures (`BadCredentialsException`, etc.)             | 401     | 401                              |
| Insufficient permission (`AccessDeniedException`, etc.)     | 403     | 403                              |
| Data access / MinIO / JSON parse / missing param exceptions | 400/500 | corresponding code               |
| Other runtime exceptions                                    | 500     | 500                              |

Business error codes are defined via enums + the `ErrorCode` interface:

```java
public enum AuthErrorCode implements ErrorCode {
    CAPTCHA_ERROR(10001, "验证码错误"),
    LOGIN_FAIL(10002, "登陆失败"),
    TOKEN_INVALID(10003, "Token 无效"),
    TOKEN_EXPIRED(10004, "Token 已过期");
    // ...
}
```

Throw directly from services:

```java
throw new BytePulseException(AuthErrorCode.CAPTCHA_ERROR);
```

At startup, `ErrorCodeChecker` scans all `ErrorCode` enums under the base package and aborts startup on duplicate
codes, preventing conflicts at the source.

### File Access

`FileController` + `FileServiceImpl` are built on the `file_metadata` table and MinIO presigned URLs:

- **Public files** (`access_level = 0`): `GET /file/access/{fileId}` → 302 redirect to a MinIO presigned URL (valid for 120 minutes);
- **Private files** (`access_level = 1`): first 302 to `GET /file/authentication/{fileId}` for authentication, then redirect to the presigned URL;
- Upload object path rule: `{bizType}/{yyyyMMdd}/{bizId||"_tmp"}/{fileId}.{ext}`;
- `MinioTemplate` uses tiered signing when no expiration is given: <10MB → 10 minutes, 10–100MB → 60 minutes, >100MB → 24 hours.

---

## Database Tables

[init.sql](init.sql) initializes the following 3 tables in the `bytepulse` database:

| Table           | Description   | Key columns                                                                                                                                        |
| --------------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| `sys_user`      | System users  | `username`/`password` (BCrypt)/`phone`/`email`/`status` (0 disabled, 1 enabled)/`last_login`, etc.                                                 |
| `sys_log`       | API call logs | `trace_id` (unique)/`operate`/`description`/`http_method`/`query_params`/`body_params`/`response_result`/`cost`/`request_ip`/`user_id`/`exception` |
| `file_metadata` | File metadata | `object_name` (MinIO object name)/`content_type`/`size`/`access_level` (0 public, 1 auth required)/`biz_type`/`biz_id`/`status`                    |

> `sys_log` adds `operate` (operation type) and `description` (operation description) columns, populated by `@BpLogging`.

Mappers use MyBatis-Plus `BaseMapper<T>` with automatic snake_case column ↔ camelCase property mapping.

---

## Feature Status

To keep docs consistent with the code, current status of the following features is:

| Feature                                                 | Status                                                                                                                                                                                                                       |
| ------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Auth / captcha / rate limiting / unified exceptions     | ✅ Implemented and wired                                                                                                                                                                                                     |
| File upload / access                                    | ✅ Services and MinIO wrapper implemented; access and upload (test) endpoints exposed                                                                                                                                        |
| API call logging (`sys_log`)                            | ✅ Implemented and wired: `LoggingFilter` captures request/response bodies, masks, injects `traceId`/`timestamp`, and persists to `sys_log` asynchronously (`BootApplication` enables `@EnableAsync`)                        |
| `@Pageable` doc pagination params                       | ✅ Implemented (affects SpringDoc only)                                                                                                                                                                                      |
| `@BpLogging` annotation and `OperateEnum`               | ✅ Implemented; endpoints must be explicitly annotated to be logged                                                                                                                                                          |
| `traceId` / `timestamp` response fields                 | ✅ Injected by `LoggingFilter`                                                                                                                                                                                               |
| Method-level authorization (`@PreAuthorize`)            | ✅ `@EnableMethodSecurity` enabled, usable for method authorization                                                                                                                                                          |
| Permission system (roles / permission points)           | 🚧 `LoginUser.permissions` support is ready, but the permission data source is a TODO (currently empty, so `@PreAuthorize` has no actual permission data)                                                                    |
| Third-party API signature auth (HMAC/Nonce anti-replay) | 🚧 Foundations ready: `CryptoUtils` (RSA/AES/SHA/HMAC) and the `bp.app.external-api` config placeholder; the `api_credentials` table/entity were removed and the signature-verification filter/aspect is not implemented yet |
| RabbitMQ (`bp-mq`)                                      | ✅ Includes a simple sample: `JacksonJsonMessageConverter` conversion, DLX + TTL delay queue (`RabbitConfig` / `DelayQueue`)                                                                                                 |

Before wiring up the reserved chains above, do not describe them as production-ready features in external materials.

---

## Development Guide

### Switching environments

```bash
# Dev (default)
mvn spring-boot:run -pl bp-app -am

# A specific environment (dev / test / prod)
mvn spring-boot:run -pl bp-app -am -Dspring-boot.run.profiles=prod
```

Running after packaging:

```bash
java -jar bp-app/target/bp-app-0.0.1.jar --spring.profiles.active=prod
```

### Adding a new business module

1. Put new business Controllers / aspects / exception handling in `bp-web`, and business services in `bp-service` (`service.xxx`);
2. Put entities and `BaseMapper`s for new tables in `bp-repository` (`data.entity` / `data.mapper`);
3. Add error code enums under `common.core.exception.enums`; codes must be globally unique (validated automatically at startup);
4. Annotate public endpoints with `@Anonymous`, sensitive ones with `@RequestLimit`, and those that should appear in Swagger's paginated docs with `@Pageable`;
5. Paginated queries: accept `pageNum`/`pageSize` params and call `PageUtils.startPage()` + a MyBatis-Plus query in the service.

### Default anonymous / logging paths

`/error`, `/actuator/**`, `/druid/**`, Swagger-related paths, etc. are in the anonymous list by default.
Declare business anonymous endpoints with `@Anonymous`, and endpoints needing call logging with `@BpLogging`.

---

## License

[MIT License](LICENSE)
