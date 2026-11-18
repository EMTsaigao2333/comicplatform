# 漫阅 · 原创漫画在线阅读分享平台

一个前后端分离的原创漫画在线阅读与分享平台，参考哔哩哔哩漫画的 UI 风格设计。平台支持漫画浏览、搜索、阅读、收藏、发布与管理，区分读者、作者、管理员三种角色，适合作为 Java Web 课程设计或毕业设计项目。

- 后端：[comicplatform_springbootweb](comicplatform_springbootweb)（Spring Boot + MyBatis-Plus + MySQL）
- 前端：[comicplatform_vue/comicplatform](comicplatform_vue/comicplatform)（Vue 3 + Vue Router + Pinia + Vite）

---

## 功能特性

### 读者端

| 模块 | 功能 |
|---|---|
| 首页 | 公告栏、分类筛选、漫画搜索、热门漫画展示 |
| 分类页 | 按题材、地区、连载进度、收费方式多维度筛选 |
| 漫画详情 | 封面、简介、标签、章节列表、开始阅读、评论与评分 |
| 章节阅读 | 滚动 / 翻页两种阅读模式、键盘快捷键、上一章 / 下一章导航 |
| 书架 | 收藏管理、按分类查看、继续阅读、取消收藏 |
| 登录注册 | 支持读者、作者两种身份注册，登录状态本地持久化 |

### 作者端

| 模块 | 功能 |
|---|---|
| 发布漫画 | 填写封面、标题、作者、分类、地区、简介、关键词 |
| 编辑漫画 | 修改已发布漫画的基本信息 |
| 章节发布 | 添加 / 删除内容页、设置章节发布状态、调整章节顺序 |
| 作品管理 | 查看自己的漫画列表，编辑或删除作品 |

### 管理员端

| 模块 | 功能 |
|---|---|
| 漫画管理 | 按标题 / 作者搜索，标记违规，下架 / 上架漫画 |
| 分类管理 | 分类的新增、修改、删除、启用 / 停用 |
| 公告管理 | 首页公告的发布、编辑、上线 / 下线、删除 |
| 评论管理 | 评论搜索、编辑、屏蔽 / 恢复、删除 |

---

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|---|---|---|
| Java | 17 | 开发语言 |
| Spring Boot | 2.6.13 | Web 框架，提供 RESTful API |
| MyBatis-Plus | 3.5.3.1 | ORM，Lambda 条件构造、分页插件 |
| MySQL | 8.0 | 关系型数据库 |
| Druid | 1.2.16 | 数据库连接池 |
| Lombok | - | 简化实体类样板代码 |
| Maven | 3.6+ | 依赖管理与构建 |

### 前端

| 技术 | 版本 | 说明 |
|---|---|---|
| Vue | 3.x（Composition API） | 组件化开发 |
| Vue Router | 4.x / 5.x | 前端路由与权限守卫 |
| Pinia | 3.x | 用户登录状态、角色管理 |
| Axios | 1.x | HTTP 请求 |
| Vite | 8.x | 开发服务器、接口代理与打包 |
| Node.js | ^22.18.0 或 >=24.12.0 | 前端运行环境 |

---

## 项目结构

```
web/
├── comicplatform_springbootweb/          # 后端项目
│   ├── src/main/java/com/example/comicplatform_springbootweb/
│   │   ├── ComicplatformSpringbootwebApplication.java  # 启动类
│   │   ├── common/          # Result 统一返回封装
│   │   ├── config/          # MyBatis-Plus 分页、CORS 跨域配置
│   │   ├── controller/      # REST API 控制器
│   │   ├── entity/          # 数据库实体
│   │   ├── mapper/          # MyBatis-Plus Mapper
│   │   ├── service/         # 业务接口及实现
│   │   └── vo/              # 视图对象（如 BookshelfVO）
│   ├── src/main/resources/application.properties  # 端口、数据库等配置
│   ├── sundry/              # 数据库脚本（升级脚本等）
│   ├── 项目介绍.md / 技术总结.md                   # 项目文档
│   └── pom.xml
├── comicplatform_vue/
│   └── comicplatform/       # 前端项目
│       ├── src/
│       │   ├── router/      # 路由表与权限守卫
│       │   ├── stores/      # Pinia 用户状态
│       │   ├── utils/       # 图片占位工具
│       │   └── views/       # 页面组件（首页/详情/阅读/后台等）
│       ├── public/images/   # 本地封面与占位图
│       ├── vite.config.js   # 5173 端口与 /api 代理
│       └── package.json
├── comicplatformdb.sql      # 数据库初始化脚本（8 张业务表 + 2 个视图）
└── wechatTS/                # 独立的微信小程序示例，与漫阅平台无关
```

---

## 快速开始

### 环境要求

- JDK 17
- Maven 3.6+
- MySQL 8.0（默认端口 3306）
- Node.js ^22.18.0 或 >=24.12.0、npm

### 1. 初始化数据库

使用命令行或 Navicat / DataGrip 导入根目录下的 `comicplatformdb.sql`，脚本会自动创建 `comicplatformdb` 库、8 张业务表、`v_bookshelf` / `v_comic` 视图及演示数据。

```bash
mysql -u root -p < comicplatformdb.sql
```

> 如果已存在旧版数据库，可执行 `comicplatform_springbootweb/sundry/升级脚本_新增3张表.sql` 补齐分类、公告、评论表。

### 2. 启动后端

修改 [application.properties](comicplatform_springbootweb/src/main/resources/application.properties) 中的数据库账号密码：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/comicplatformdb?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的数据库密码
```

然后任选一种方式启动：

```bash
# 方式一：Maven 直接运行
cd comicplatform_springbootweb
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package
java -jar target/comicplatform_springbootweb-0.0.1-SNAPSHOT.jar
```

也可以在 IDEA 中打开项目，运行 `ComicplatformSpringbootwebApplication.java`。

启动成功后访问 `http://localhost:8080/api/comic/page`，返回 JSON 数据即表示后端正常。

### 3. 启动前端

```bash
cd comicplatform_vue/comicplatform
npm install
npm run dev
```

浏览器访问 `http://localhost:5173`。开发环境下 Vite 已将 `/api` 代理到 `http://localhost:8080`，无需额外配置跨域。

### 4. 测试账号

| 账号 | 密码 | 角色 |
|---|---|---|
| admin | 123456 | 管理员 |
| 李四 | 123456 | 作者 |

读者账号可在注册页自行注册（注册时选择「读者」或「作者」）。

---

## API 概览

所有接口以 `/api` 为统一前缀，返回统一结构 `{ code, msg, data }`（`code = 200` 表示成功）。

| 模块 | 前缀 | 主要接口 |
|---|---|---|
| 用户 | `/api/user` | `POST /register`、`POST /login`、`PUT`、`GET /{id}`、`DELETE /{id}`、`PUT /{id}/password` |
| 漫画 | `/api/comic` | `POST`、`PUT /{id}`、`DELETE /{id}`、`GET /{id}`、`GET /page`、`GET /search`、`GET /category/{category}`、`GET /author`、`PUT /{id}/violation`、`PUT /{id}/status` |
| 章节 | `/api/chapter` | `POST`、`PUT /{id}`、`DELETE /{id}`、`GET /{id}`、`GET /comic/{comicId}`、`PUT /{id}/status` |
| 章节内容 | `/api/chapter-content` | `POST`、`PUT /{id}`、`DELETE /{id}`、`GET /{id}`、`GET /chapter/{chapterId}`、`PUT /{id}/status` |
| 书架 | `/api/bookshelf` | `POST`、`DELETE /{id}`、`DELETE`（按用户 + 漫画）、`GET /user/{userId}`、`GET /check` |
| 分类 | `/api/category` | `GET`、`GET /enabled`、`GET /{id}`、`POST`、`PUT /{id}`、`DELETE /{id}` |
| 公告 | `/api/notice` | `GET`、`GET /published`、`GET /{id}`、`POST`、`PUT /{id}`、`DELETE /{id}` |
| 评论 | `/api/comment` | `GET`、`GET /comic/{comicId}`、`GET /rating/{comicId}`、`GET /{id}`、`POST`、`PUT /{id}`、`PUT /{id}/status`、`DELETE /{id}` |

---

## 数据库设计

| 表名 | 说明 | 主要字段 |
|---|---|---|
| `t_user` | 用户表 | userid、username、password、usertype、phone、email |
| `t_comic` | 漫画表 | comicid、comictitle、author、category、intro、country、coverimg、keywords、status、isviolation |
| `t_chapter` | 章节表 | chapterid、comicid、chapterno、chaptertitle、content（JSON 图片数组）、releasedate、status |
| `t_chaptercontent` | 章节内容表（扩展保留） | contentid、chapterid、imgurl、displayorder、status |
| `t_bookshelf` | 书架表 | bookshelfid、userid、comicid、collecttime |
| `t_category` | 分类表 | categoryid、categoryname、description、sortorder、status |
| `t_notice` | 公告表 | noticeid、title、content、noticetype、publisher、publishdate、status |
| `t_comment` | 评论表 | commentid、comicid、userid、username、content、rating、status |
| `v_bookshelf` | 书架视图 | 联表查询书架、用户与漫画信息，统计章节数与最后更新时间 |
| `v_comic` | 漫画视图 | 联表查询漫画、章节、章节内容与作者信息 |

章节图片当前以 `t_chapter.content` 中的 JSON 数组存储，一次查询即可拿到全部页面；`t_chaptercontent` 作为可扩展的独立内容表保留。

---

## 关键实现说明

- **统一返回**：后端通过 `Result<T>` 封装 `code / msg / data`，Controller 一行 `Result.success(data)` 即可返回。
- **分层结构**：Controller → Service → Mapper → Entity，多表数据使用 VO（如 `BookshelfVO`）组装后返回前端。
- **鉴权方式**：登录 / 注册接口返回用户信息与 `role` 字段，前端 Pinia 持久化到 `localStorage`；路由守卫中 `/author/*` 需登录、`/admin/*` 需管理员身份。
- **接口代理**：前端统一以 `/api` 开头请求，开发环境由 Vite 代理到 8080，部署时可交由 Nginx 按路径转发。
- **图片策略**：开发阶段使用 `public/images/` 下的本地占位图，封面与章节页统一由 `src/utils/image.js` 管理，后续可无缝替换为真实图片。
- **状态枚举**：漫画 `status`（已发布 / 已下架）、`isviolation`（0 / 1）、章节 `status`（发布 / 草稿）、评论 `status`（正常 / 已屏蔽）、分类与公告的启用 / 停用。

---

## 常见问题

**1. 后端启动报端口被占用**
8080 端口被其他程序占用，修改 `application.properties` 中的 `server.port`，同时同步修改 `vite.config.js` 的代理 `target`。

**2. 数据库连接失败**
确认 MySQL 已启动、已导入 `comicplatformdb.sql`，并检查 `application.properties` 中的账号、密码、时区参数是否正确。

**3. 前端请求接口 404 或跨域**
确认后端已启动在 8080 端口、前端通过 `npm run dev` 启动，请求路径以 `/api` 开头。后端已对 `/api/**` 开启 CORS 配置。

**4. 登录提示账号或密码错误**
确认使用 `comicplatformdb.sql` 中的测试账号（admin / 123456）。注意登录、注册、加入书架等接口的字段名区分大小写。

**5. 下架的漫画仍然出现在列表**
列表接口已过滤 `status = '已下架'` 的数据；若使用旧版代码，请检查分页查询是否包含该过滤条件。

---

## 说明

- 本项目用于课程设计与学习交流，用户密码为明文存储，未接入 token / JWT 鉴权，请勿直接用于生产环境。
- 更多设计细节见 [项目介绍.md](comicplatform_springbootweb/项目介绍.md) 与 [技术总结.md](comicplatform_springbootweb/技术总结.md)。
