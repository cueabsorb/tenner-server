# Tennis Server（来嘞 iRallyIn）

> iRallyIn 运动社交平台后端服务 —— 面向网球爱好者的社交、约球、球场发现与装备管理平台。

---

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21 |
| 框架 | Spring Boot | 3.3.6 |
| 安全 | Spring Security + JWT (jjwt) | 0.12.6 |
| ORM | MyBatis-Plus | 3.5.9 |
| 数据库 | MySQL（PolarDB MySQL 8.0+）+ ShardingSphere-JDBC | 5.5.0 |
| NoSQL | Redis (Jedis)、MongoDB | — |
| 数据库迁移 | Flyway | — |
| API 文档 | SpringDoc OpenAPI (Swagger) | 2.6.0 |
| 邮件 | Spring Boot Mail + Thymeleaf | — |
| 构建工具 | Maven（多模块） | — |
| 工具库 | Lombok | 1.18.38 |
| HTML 处理 | Jsoup | 1.16.1 |
| 管理后台 UI | Vite + TypeScript（SPA） | — |
| 测试 | Spring Boot Test、H2、Mockito | — |
| 部署 | WAR 包 → 外部 Tomcat | — |

---

## 工程结构

```
tennis-server/                          # Maven 父 POM（聚合项目）
├── common/                             # 通用基础模块（工具、安全、异常）
├── data/                               # 数据访问层模块（DAO、Domain、Mapper）
├── core/                               # 核心业务逻辑模块（Service 层）
├── web/                                # Web/PC 端应用（管理后台 + PC 认证）
├── appgateway/                         # 移动端 API 网关
├── test/                               # 集成/单元测试
├── pom.xml                             # 父 POM
├── irallyin-variables.yml              # 本地配置变量（密钥等）
├── irallyin-variables.properties       # 同上（properties 格式）
└── aliyun-irallyin-variables.properties # 阿里云部署变量（gitignored）
```

### 模块依赖关系

```
common ← data ← core ← web
                        ← appgateway
test → web（传递依赖所有模块）
```

---

## 各模块详细说明

### 1. `common` — 通用基础模块

> **职责**：提供全局共享的基础设施，包括安全认证、异常处理、统一响应格式、缓存键管理等。

```
common/src/main/java/com/irallyin/server/common/
├── cache/
│   └── RedisKeys.java               # Redis Key 统一管理（验证码、Nonce、关注数缓存）
├── config/
│   └── WebMvcConfig.java             # Web MVC 全局配置
├── exception/
│   ├── BusinessException.java        # 业务异常
│   └── GlobalExceptionHandler.java   # 全局异常处理器
├── response/
│   ├── ApiResponse.java              # 统一 API 响应包装
│   └── PageResult.java               # 分页结果封装
└── security/
    ├── SecurityConfig.java           # Spring Security 配置（无状态 Session、JWT 过滤器链）
    ├── JwtTokenProvider.java         # JWT Token 生成与解析
    ├── JwtAuthenticationFilter.java  # JWT 认证过滤器
    ├── RequestSignatureFilter.java   # 请求签名验证过滤器（防重放）
    ├── CachedBodyHttpServletRequest.java # 请求体缓存（支持多次读取）
    ├── NonceCache.java               # Nonce 去重缓存
    └── SignatureProperties.java      # 签名配置属性
```

**核心功能**：
- **JWT 认证体系**：无状态 Session，所有请求通过 `JwtAuthenticationFilter` 校验 Access Token
- **请求签名防重放**：`RequestSignatureFilter` + Redis Nonce 缓存，防止请求重放攻击
- **统一异常处理**：`GlobalExceptionHandler` 捕获业务异常并返回标准化错误响应
- **Redis Key 集中管理**：所有业务 Redis Key 通过 `RedisKeys` 类统一生成，避免散落

---

### 2. `data` — 数据访问层模块

> **职责**：封装所有数据库交互，包括实体定义、DAO 操作、MyBatis Mapper 映射。

```
data/src/main/java/com/irallyin/server/data/
├── config/
│   ├── MyBatisPlusConfig.java        # MyBatis-Plus 配置（分页插件等）
│   └── RedisConfig.java              # Redis 连接配置
├── dao/                              # 48 个 DAO 类（每个对应一张表）
├── domain/                           # 48 个 DO 实体类（Data Object）
└── mapper/                           # 7 个 Mapper 接口（复杂查询 XML）
data/src/main/resources/
├── db/migration/                     # 17 个 Flyway 迁移脚本
└── mapper/                           # 51 个 MyBatis XML 映射文件
    ├── admin/
    ├── dao/
    └── profile/
```

**数据库设计（多 Schema 架构）**：

| Schema | 用途 | 核心表 |
|--------|------|--------|
| `ir_auth` | 认证与账户 | users, linked_accounts, refresh_tokens, verification_codes, login_audit_log |
| `ir_profile` | 用户资料与网球档案 | player_skill_profiles, tennis_profiles, playing_habits, habit_courts, courts, rackets, equipment_bags, cities, areas, tennis_stars, racket_catalog |
| `ir_privacy` | 隐私与安全 | privacy_settings, resource_privacy, block_relations, user_reports, account_deletion_requests |
| `ir_social` | 社交动态 | feed_posts, feed_comments, feed_likes, feed_media |
| `ir_activity` | 约球活动 | play_sessions, play_participants, player_relationship_edges |
| `ir_review` | 评价系统 | player_reviews, player_review_aggregates, review_risk_logs |
| `ir_club` | 俱乐部 | clubs, club_members, club_join_requests, club_events |

> 高增长表（feed_posts、feed_comments、feed_likes、play_sessions、login_audit_log）采用 **按日期范围分区** 策略。

---

### 3. `core` — 核心业务逻辑模块

> **职责**：实现所有业务逻辑，被 `web` 和 `appgateway` 两个应用模块共同依赖。

```
core/src/main/java/com/irallyin/server/core/
├── admin/                            # 管理后台业务
│   ├── dto/                          # Admin DTO
│   └── service/
│       ├── AdminAuthService.java      # 管理员登录认证
│       ├── AdminCourtReviewService.java # 球场审核工作流
│       ├── AdminProperties.java       # 管理员配置属性
│       └── AdminTokenService.java     # 管理员 Token 服务
├── auth/                             # 认证业务
│   ├── dto/                          # Auth DTO（登录、注册、验证码等）
│   ├── service/
│   │   ├── AuthService.java           # 核心认证服务（Google OAuth、JWT、登录审计）
│   │   ├── EmailRegistrationService.java # 邮箱验证码注册/登录
│   │   ├── GatewayPhoneLoginService.java # 运营商网关一键登录
│   │   ├── GoogleOAuthClient.java     # Google OAuth 客户端
│   │   ├── VerificationCodeService.java # 通用验证码服务
│   │   ├── EmailVerificationSender.java # 验证码邮件发送
│   │   ├── PhoneNumberProvider.java   # 手机号获取接口
│   │   └── AliyunPhoneNumberProvider.java # 阿里云手机号解析
│   └── verification/                 # 验证码存储（内存/Redis）
├── mail/                             # 邮件服务
│   └── template/
│       ├── EmailTemplateService.java  # Thymeleaf 邮件模板渲染
│       └── CssInliner.java           # CSS 内联处理（兼容邮件客户端）
└── profile/                          # 用户资料业务
    ├── dto/                          # 24 个 Profile DTO
    └── service/
        ├── MobileProfileService.java  # 移动端资料管理（核心服务，覆盖所有资料操作）
        └── ProfileContentSafetyService.java # 内容安全审核（敏感词 + 外部审核 API）
```

**核心业务服务详解**：

| 服务 | 说明 |
|------|------|
| **AuthService** | Google OAuth 登录、JWT Token 签发、Refresh Token 轮换、用户创建/查找、登录审计日志 |
| **EmailRegistrationService** | 邮箱验证码发送 → 验证 → 注册/登录（支持新用户自动创建） |
| **GatewayPhoneLoginService** | 运营商网关一键手机号登录（阿里云号码认证服务） |
| **AdminCourtReviewService** | 球场审核工作流：查看待审核 → 编辑 → 通过/拒绝；处理球场信息变更请求 |
| **MobileProfileService** | 综合移动端资料管理：个人资料编辑、球场搜索/收藏/提交、球拍目录/装备包管理、活动记录、用户关注、用户搜索 |
| **ProfileContentSafetyService** | 内容安全审核：本地敏感词检测 + 可选外部审核 API，用于所有用户输入内容 |

---

### 4. `web` — Web/PC 端应用模块

> **职责**：PC 端 Web 应用入口，提供管理后台 SPA 和 PC 端 API。

```
web/src/main/java/com/irallyin/server/web/
├── IRallyInWebApplication.java       # Spring Boot 启动类
├── config/
│   └── OpenApiConfig.java            # Swagger/OpenAPI 配置
├── controller/
│   ├── AuthController.java           # PC 端认证接口（Google 登录、Token 刷新、验证码）
│   └── admin/
│       ├── AdminAuthController.java  # 管理员登录
│       ├── AdminCourtReviewController.java # 球场审核管理
│       └── AdminSpaController.java   # 管理 SPA 路由转发
└── security/
    └── AdminAuthenticationFilter.java # 管理员认证过滤器

web/src/main/resources/
├── application.yml                   # 主配置
├── application-dev.yml               # 开发环境（直连 MySQL、Redis、MongoDB）
├── application-prod.yml              # 生产环境（ShardingSphere、Tair、OSS）
├── shardingsphere-dev.yaml           # ShardingSphere 开发配置
├── shardingsphere-prod.yaml          # ShardingSphere 生产配置
├── logback-spring.xml                # 日志配置
└── static/admin/                     # 管理后台构建产物（SPA）
web/src/main/webapp/admin-ui/         # 管理后台前端源码（Vite + TypeScript）
```

**API 端点一览**：

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login/google` | Google OAuth 登录 |
| POST | `/auth/refresh` | 刷新 JWT Token |
| POST | `/auth/verification-code` | 发送验证码（邮箱/手机） |
| POST | `/auth/verification-code/verify` | 验证验证码 |
| GET | `/auth/me` | 获取当前认证用户 |
| POST | `/api/admin/auth/login` | 管理员登录 |
| GET | `/api/admin/courts` | 按状态查看球场列表（默认：待审核） |
| PATCH | `/api/admin/courts/{courtId}/review` | 审核球场（通过/拒绝） |

---

### 5. `appgateway` — 移动端 API 网关模块

> **职责**：移动端 App 的 API 入口，运行在独立端口（8081），上下文路径 `/api`。

```
appgateway/src/main/java/com/irallyin/server/appgateway/
├── IRallyInAppGatewayApplication.java # Spring Boot 启动类
└── controller/
    ├── AppAuthController.java         # App Token 刷新
    ├── MobileAuthController.java      # 移动端认证（邮箱注册、运营商网关登录）
    ├── MobileProfileController.java   # 移动端资料管理（核心控制器，30+ 端点）
    └── MobileAdminCourtController.java # 移动端球场审核（管理员用）
```

**API 端点一览**：

#### 认证相关

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/mobile/auth/verification-code` | 发送邮箱验证码 |
| POST | `/mobile/auth/register` | 邮箱 + 验证码注册/登录 |
| POST | `/mobile/auth/phone/gateway-login` | 运营商网关一键登录 |
| POST | `/auth/refresh` | 刷新 App JWT Token |

#### 资料管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/mobile/profile/me` | 获取个人资料 |
| POST | `/mobile/profile/name` | 修改昵称 |
| POST | `/mobile/profile/avatar` | 修改头像 |
| POST | `/mobile/profile/intro` | 修改个人简介 |
| POST | `/mobile/profile/tags` | 修改实力标签 |
| POST | `/mobile/profile/gender` | 修改性别 |
| POST | `/mobile/profile/birthday` | 修改生日 |
| POST | `/mobile/profile/region` | 修改地区 |
| POST | `/mobile/profile/tennis-level` | 修改 NTRP 等级 |
| POST | `/mobile/profile/tennis-identity` | 修改球员身份 |
| POST | `/mobile/profile/dominant-hand` | 修改惯用手 |
| POST | `/mobile/profile/play-preference` | 修改单双打偏好 |
| POST | `/mobile/profile/real-name-visibility` | 切换真名显示 |

#### 球场相关

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/mobile/profile/habit-courts/search` | 搜索常去球场 |
| POST | `/mobile/profile/habit-courts` | 添加常去球场 |
| DELETE | `/mobile/profile/habit-courts/{courtId}` | 移除常去球场 |
| GET | `/mobile/profile/courts/{courtId}` | 获取球场详情 |
| POST | `/mobile/profile/courts/{courtId}/likes` | 点赞球场 |
| POST | `/mobile/profile/courts/submissions` | 提交新球场（待审核） |
| POST | `/mobile/profile/courts/{courtId}/change-requests` | 提交球场信息修改请求 |

#### 球拍与装备

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/mobile/profile/racket-catalog` | 获取球拍目录 |
| GET | `/mobile/profile/racket-catalog/{catalogId}/player-usages` | 获取职业选手球拍使用 |
| POST | `/mobile/profile/equipment/rackets` | 添加球拍到装备包 |
| POST | `/mobile/profile/admin/racket-catalog` | 管理员创建球拍目录 |

#### 活动与社交

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/mobile/profile/activity-records` | 获取活动记录 |
| POST | `/mobile/profile/activity-records` | 创建活动/打球记录 |
| GET | `/mobile/profile/users/search` | 搜索用户 |
| POST | `/mobile/profile/users/{targetUserId}/follow` | 关注/取关用户 |

#### 移动端球场审核（管理员）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/mobile/profile/admin/courts/pending` | 查看待审核球场 |
| POST | `/mobile/profile/admin/courts/{courtId}/draft` | 编辑待审核球场信息 |
| POST | `/mobile/profile/admin/courts/{courtId}/review` | 审核通过/拒绝 |

---

### 6. `test` — 测试模块

> **职责**：集成测试和单元测试。

```
test/src/test/java/com/irallyin/server/test/
├── EmailIntegrationTest.java         # 邮件集成测试
├── dao/                              # 47 个 DAO 测试类
│   └── AbstractDaoIntegrationTest.java # DAO 测试基类
└── service/
    └── EmailRegistrationServiceTest.java # 邮箱注册服务测试
```

- 使用 **H2 内存数据库** 进行 DAO 集成测试
- 使用 **Mockito** 进行 Service 单元测试

---

## 核心架构设计

### 认证体系

```
┌──────────────┐    ┌───────────────────┐    ┌──────────────────┐
│  Google OAuth │    │  Email + 验证码    │    │  运营商网关一键登录 │
│   (PC/Mobile) │    │    (Mobile)       │    │    (Mobile)      │
└──────┬───────┘    └────────┬──────────┘    └────────┬─────────┘
       │                     │                        │
       └─────────────────────┼────────────────────────┘
                             ▼
                   ┌──────────────────┐
                   │   AuthService    │
                   └────────┬─────────┘
                            ▼
              ┌─────────────────────────────┐
              │  JWT Access Token (短期)     │
              │  + Refresh Token (SHA-256)   │
              │    存储在 MySQL ir_auth 库    │
              └─────────────────────────────┘
```

- **Access Token**：JWT 格式，短期有效，无状态校验
- **Refresh Token**：SHA-256 哈希存储，支持 Token 轮换
- **请求签名**：防重放攻击，Nonce + 时间戳 + Redis 去重

### 安全过滤链

```
HTTP Request
    │
    ▼
RequestSignatureFilter    ← 验证请求签名（防重放）
    │
    ▼
JwtAuthenticationFilter   ← 校验 JWT Access Token
    │
    ▼
Controller / Service      ← 业务逻辑处理
```

### 数据库架构

```
MySQL（PolarDB）
├── ir_auth       ← 认证与账户（用户、关联账号、Token、验证码、登录日志）
├── ir_profile    ← 用户资料（网球档案、习惯、球场、装备、城市区域）
├── ir_privacy    ← 隐私设置（隐私、屏蔽、举报、注销）
├── ir_social     ← 社交动态（帖子、评论、点赞、媒体）
├── ir_activity   ← 约球活动（打球记录、参与者、关系边）
├── ir_review     ← 评价系统（球员评价、评价聚合、风险日志）
└── ir_club       ← 俱乐部（俱乐部、成员、加入请求、活动）

Redis
├── 验证码缓存（5 分钟 TTL）
├── Nonce 去重缓存
└── 用户关注数缓存

MongoDB
└── （扩展存储）
```

---

## 部署架构

项目部署为 **两个独立的 WAR 包**，运行在外部 Tomcat 上：

| 应用 | WAR 包名 | 用途 | 端口 |
|------|---------|------|------|
| **web** | `tennisweb.war` | PC 端 Web + 管理后台 SPA + Swagger UI | 由外部配置 |
| **appgateway** | `tennis.war` | 移动端 App API 网关 | 8081 |

- **开发环境**：直连 MySQL、Redis、MongoDB
- **生产环境**：通过 ShardingSphere-JDBC 代理数据库访问，使用阿里云 Tair（Redis 兼容）、OSS

---

## 快速开始

### 前置条件

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis
- MongoDB（可选）

### 配置

1. 复制配置变量文件：
   ```bash
   cp irallyin-variables.properties.example irallyin-variables.properties
   ```
2. 填写数据库、Redis、OAuth 等配置项

### 构建

```bash
mvn clean package -DskipTests
```

### 运行

```bash
# Web 应用
java -jar web/target/tennisweb.war

# App Gateway
java -jar appgateway/target/tennis.war
```

### API 文档

启动 Web 应用后访问 Swagger UI：

```
http://localhost:{port}/swagger-ui.html
```

---

## Flyway 数据库迁移

迁移脚本位于 `data/src/main/resources/db/migration/`，按版本递增：

| 版本范围 | 内容 |
|---------|------|
| `V1.0.0.*` | 基础 Schema 创建（auth、profile、privacy） |
| `V1.1.0.*` | 社交模块（social、activity、review） |
| `V1.2.0.*` | 俱乐部模块 |
| `V1.3.0.*` | 移动端功能扩展（资料编辑、球场审核、球拍目录、点赞等） |

---

## 项目名称

**来嘞 (iRallyIn)** — "Rally" 是网球回合之意，寓意持续对打、连接球友。
