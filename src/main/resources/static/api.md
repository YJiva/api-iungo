## APi-iungo API 文档（较全面版）

- **后端基地址**：`http://localhost:8080`
- **接口统一前缀**：`/api`
- **认证方式（开发态）**：请求头携带

```http
Authorization: Bearer dev-token:<username>
```

> 说明：当前项目使用开发态 token（不是 JWT）。`<username>` 对应数据库 `user.username`。

---

## 统一返回结构（约定）

多数接口返回：

```json
{
  "code": 200,
  "msg": "可选提示",
  "data": {}
}
```

部分老接口可能只有 `code/data`，含义一致。

---

## 一、用户与认证 `/api/user/*`

### 1) 发送邮箱验证码

- **POST** ` /api/user/send-email-code?email=xxx@qq.com `
- **说明**：发送 6 位验证码，有效期 5 分钟（内存存储）。
- **响应**：`code=200` 成功，`code=500` 失败

### 2) 账号密码登录

- **POST** ` /api/user/login/password `
- **Body(JSON)**：

```json
{ "username": "admin", "password": "123456" }
```

- **成功响应**：`data.token` 为 `dev-token:<username>`；`data.user` 为用户对象

### 3) 邮箱验证码登录

- **POST** ` /api/user/login/email `
- **Body(JSON)**：

```json
{ "email": "xxx@qq.com", "code": "123456" }
```

### 4) 获取当前登录用户

- **GET** ` /api/user/me `
- **Header**：需要 `Authorization`
- **响应**：`code=200 data=User`；未登录 `code=401`

### 5) 获取当前用户角色信息

- **GET** ` /api/user/role-info `
- **Header**：需要 `Authorization`
- **响应 data**：`{ roleId, name, description }`

### 6) 更新个人资料

- **POST** ` /api/user/update `
- **Header**：需要 `Authorization`
- **Body(JSON)**：User（必须包含 `id` 且等于当前登录用户）
- **注意**：`password` 字段不传不会更新（避免 NOT NULL 报错）

### 7) 修改密码

- **POST** ` /api/user/change-password `
- **Header**：需要 `Authorization`
- **Body(JSON)**：

```json
{ "oldPassword": "123456", "newPassword": "654321" }
```

### 8) 邀请注册

- **POST** ` /api/user/register `
- **Body(JSON)**（示例）：

```json
{
  "username": "tom",
  "password": "123456",
  "email": "tom@example.com",
  "emailCode": "123456",
  "nickname": "Tom",
  "gender": 1,
  "inviteCode": "ADMIN123"
}
```

### 9) 查询邀请树（全部）

- **GET** ` /api/user/invite-tree?userId=1 `
- **响应**：`data.tree`（树结构数组）

### 10) 查询邀请树（与某用户直接相关）

- **GET** ` /api/user/invite-tree/close?userId=1 `

### 11) 获取/生成邀请码（固定邀请码）

- **POST** ` /api/user/generate-invite-code?userId=1 `
- **响应**：`data.code`

---

## 二、文件上传 `/api/file/*`

### 1) 上传头像

- **POST** ` /api/file/upload-avatar `
- **Content-Type**：`multipart/form-data`
- **字段**：`file`
- **限制**：图片类型；≤ 2MB
- **响应**：`data.url` 为相对路径（如 `/upload/avatars/20260317/xxx.png`）

### 2) 富文本图片上传（博客/帖子编辑器共用）

- **POST** ` /api/file/upload-editor-image `
- **Content-Type**：`multipart/form-data`
- **字段**：`file`
- **限制**：图片类型；≤ 5MB
- **响应**：`data.url` 为相对路径（如 `/upload/editor/20260317/xxx.png`）

---

## 三、博客 `/api/blog/*`

### 1) 保存博客（新建/更新）

- **POST** ` /api/blog/save `
- **Body(JSON)**（示例）：

```json
{
  "id": null,
  "userId": 1,
  "title": "标题",
  "content": "<p>HTML 富文本内容</p>",
  "tags": "1,2,3",
  "status": 1,
  "top": 0,
  "openScope": 2
}
```

> `tags` 当前约定为 **逗号分隔的 blog_type.id**（例如 `1,2,3`）。前端负责解析并展示名称。

### 2) 按开放范围列出博客

- **GET** ` /api/blog/list-by-scope?userId=1&scope=2 `
- **scope 说明**：
  - `0`：只看自己的博客
  - `1`：自己的 + 公共博客
  - `2`：仅公共博客

### 3) 博客详情（自增阅读次数）

- **GET** ` /api/blog/detail/{id} `
- **说明**：每次访问会将 `blog.read` +1
- **响应 data**：当前实现会返回：
  - `data.blog`：博客对象
  - `data.tags`：后端解析的标签列表（可选，前端也可以不用它）

> 推荐前端统一使用：`GET /api/blog/types` + 解析 `blog.tags` 自行关联 `name/show`。

### 4) blog_type 列表（前端解析 tags 用）

- **GET** ` /api/blog/types `
- **响应 data**：`[{ id, name, show, description }]`

### 5) 查询已使用过的 tags 字段（去重字符串）

- **GET** ` /api/blog/tags `
- **说明**：返回 `blog.tags` 字段去重后的字符串列表（可能是 `"1,2,3"` 这种整体字符串）

---

## 四、帖子与互动 `/api/post/*`

### 1) 创建帖子（需登录）

- **POST** ` /api/post/create `
- **Header**：`Authorization: Bearer dev-token:<username>`
- **Body(JSON)**：Post（后端会用 token 自动填充 `authorId`）

### 2) 帖子列表

- **GET** ` /api/post/list?offset=0&limit=10 `

### 3) 帖子详情

- **GET** ` /api/post/detail?id=123 `

### 4) 点赞帖子

- **POST** ` /api/post/like?id=123 `

### 5) 收藏/取消收藏帖子（toggle）

- **POST** ` /api/post/favorite?id=123 `
- **Header**：需要登录
- **响应**：
  - `favorited`: 当前是否已收藏
  - `count`: 当前收藏数

### 6) 添加评论（并发通知）

- **POST** ` /api/post/comment/add `
- **Header**：需要登录
- **Body(JSON)**：

```json
{ "postId": 123, "content": "评论内容" }
```

### 7) 评论列表

- **GET** ` /api/post/comment/list?postId=123 `

---

## 五、通知 `/api/sub/*`

### 1) 通知列表

- **GET** ` /api/sub/list `
- **Header**：需要登录

### 2) 标记单条通知已读

- **POST** ` /api/sub/read?id=1 `
- **Header**：需要登录

### 3) 全部标记已读

- **POST** ` /api/sub/read-all `
- **Header**：需要登录
- **响应**：包含 `count`

---

## 六、关注 `/api/follow/*`

### 1) 关注/取关（toggle）

- **POST** ` /api/follow/toggle?targetId=2 `
- **Header**：需要登录

### 2) 是否已关注

- **GET** ` /api/follow/status?targetId=2 `
- **Header**：需要登录

### 3) 我关注的人

- **GET** ` /api/follow/following `
- **可选**：`/api/follow/following?userId=1`

### 4) 关注我的人

- **GET** ` /api/follow/followers `
- **可选**：`/api/follow/followers?userId=1`

---

## 七、收藏汇总 `/api/collection/*`

### 1) 我的收藏

- **GET** ` /api/collection/list `
- **Header**：需要登录

---

## 八、首页统计 `/api/home/*`

### 1) 用户名列表（调试用）

- **GET** ` /api/home/usernames `

### 2) 按用户名查询用户（调试用）

- **GET** ` /api/home/user?username=admin `

### 3) 全站统计

- **GET** ` /api/home/stats `
- **响应 data**：`{ totalUsers, totalPosts, totalBlogs, totalComments, totalFavorites }`

---

## 九、管理员接口（需 ADMIN）

> ADMIN 判定：当前登录用户 `role_id == 2`。

### 1) 用户管理 `/api/admin/user/*`

- **GET** ` /api/admin/user/list `
- **POST** ` /api/admin/user/update-status?userId=1&status=0|1 `
- **POST** ` /api/admin/user/update-role?userId=1&roleId=1|2 `

### 2) 角色管理 `/api/admin/role/*`

- **GET** ` /api/admin/role/list `
- **POST** ` /api/admin/role/create `（Body=Role）
- **POST** ` /api/admin/role/update `（Body=Role，必须有 id）
- **POST** ` /api/admin/role/delete?id=1 `

### 3) 博客分类管理 `/api/admin/blog-type/*`

- **GET** ` /api/admin/blog-type/list `
- **POST** ` /api/admin/blog-type/create `（Body=BlogType）
- **POST** ` /api/admin/blog-type/update `（Body=BlogType，必须有 id）
- **POST** ` /api/admin/blog-type/delete?id=1 `

### 4) 博客管理 `/api/admin/blog/*`

- **GET** ` /api/admin/blog/list `
- **POST** ` /api/admin/blog/save `（Body：含 `tagNames: string[]`，后端会自动创建 blog_type 并写入 blog.tags 为 id 列表）
- **POST** ` /api/admin/blog/delete?id=1 `

