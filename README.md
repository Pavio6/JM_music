# JM Music

JM Music 是一个前后端分离的在线音乐平台项目，包含用户端、后台管理端和 Spring Boot 后端服务。项目支持歌曲、歌手、专辑、歌单、MV、评论、收藏、关注、私信、反馈和后台内容管理等功能，适合作为 Java 毕业设计或音乐社区类 Web 项目参考。

## 项目结构

```text
jm_music
├── backend              # Spring Boot 后端服务
├── frontend-user        # 用户端 Vue 应用
└── frontend-management  # 后台管理端 Vue 应用
```

## 技术栈

- 后端：Java 17、Spring Boot 3、Spring Security、MyBatis-Plus、MySQL、Redis、MinIO、WebSocket
- 用户端：Vue 3、Vite、TypeScript、Pinia、Vue Router、Vue I18n、Howler、UnoCSS
- 管理端：Vue 3、Vite、TypeScript、Element Plus、Vue Router、Axios

## 主要功能

- 用户注册、登录、个人资料、隐私设置
- 歌曲、歌手、专辑、歌单、MV 浏览与搜索
- 音乐播放、播放队列、歌词、播放记录
- 收藏、关注、评论、私信和用户反馈
- 后台用户、歌曲、歌手、专辑、歌单、轮播图、反馈和数据看板管理
- 文件上传与对象存储，支持图片、音频、歌词和 MV 资源

## 本地运行

### 1. 后端服务

先准备 MySQL、Redis 和 MinIO，并根据本地环境修改 `backend/src/main/resources/application-dev.yml` 中的数据库、Redis 和 MinIO 配置。

```bash
cd backend
mvn spring-boot:run
```

默认开发环境端口为 `8082`。

### 2. 用户端

```bash
cd frontend-user
pnpm install
pnpm dev
```

如需配置接口地址，可在用户端环境变量中设置 `VITE_APP_API_BASEURL`。

### 3. 管理端

```bash
cd frontend-management
npm install
npm run dev
```

## 构建

```bash
# 后端打包
cd backend
mvn clean package

# 用户端构建
cd frontend-user
pnpm build

# 管理端构建
cd frontend-management
npm run build
```

## 说明

项目中的环境配置需要按实际部署环境调整。生产部署时建议使用独立的生产配置文件，并妥善管理数据库、Redis、MinIO 等服务的账号和密钥。
