# Iungo 后端 API 文档（总索引）

> **浏览器阅读（8080）**：[http://localhost:8080/api.html](http://localhost:8080/api.html)（左侧切换分册，默认渲染总索引）  
> **完整分册（每接口含成功/失败 JSON 示例）**：源码目录 [docs/api/README.md](docs/api/README.md)；构建后静态资源目录 `src/main/resources/static/docs/api/`

---

## 快速约定

- **基地址**：`http://localhost:8080`  
- **业务前缀**：`/api`  
- **开发 Token**：`Authorization: Bearer dev-token:<username>`  
- **统一返回**（多数）：`{ "code", "msg?", "data?" }`  
- **帖子相关写接口**：`PostController` 解析 Token 时要求请求头以 **`Bearer `** 开头（见分册 `06-帖子.md`）。  
- **博客互动状态**：`GET /api/blog/interact/status` → 字段在 **`data` 内**。  
- **帖子点赞状态**：`GET /api/post/like/status` → `liked`、`likeCount` 在 **根级**。

### 博客 `open_scope`

| 值 | 含义 |
|----|------|
| 0 | 草稿 |
| 1 | 仅自己 |
| 2 | 仅粉丝可见 |
| 3 | 仅互关可见 |
| 4 | 公开（公开流需 `status=1`） |

更多字段说明见 [docs/api/00-约定.md](docs/api/00-约定.md)。

---

## 文档分册一览

| 分册 | 路径前缀 |
|------|----------|
| [01-用户与认证](docs/api/01-用户与认证.md) | `/api/user` |
| [02-个人中心与互动基础](docs/api/02-个人中心与互动基础.md) | `/api/user/me`、`/api/follow`、`/api/dm`、`/api/sub`、`/api/collection` |
| [03-首页站点与文件](docs/api/03-首页站点与文件.md) | `/api/home`、`/api/site`、`/api/admin/site`、`/api/file` |
| [04-博客](docs/api/04-博客.md) | `/api/blog` |
| [05-博客互动](docs/api/05-博客互动.md) | `/api/blog/interact` |
| [06-帖子](docs/api/06-帖子.md) | `/api/post` |
| [07-贴吧与禁言](docs/api/07-贴吧与禁言.md) | `/api/post/category`、`/api/post/category/mute` |
| [08-管理端](docs/api/08-管理端.md) | `/api/admin/user`、`/api/admin/role`、`/api/admin/blog`、`/api/admin/blog-type` |

---

*接口以 `controller` 源码为准；分册示例与常见 `msg` 已按当前实现整理。*
