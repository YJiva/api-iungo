## 接口总览

后端基地址默认：`http://localhost:8080`  
所有业务接口统一前缀：`/api`。  
统一返回结构（部分老接口仅使用 `code` + `data`，但含义一致）：

```json
{
  "code": 200,
  "msg": "可选提示",
  "data": { }
}
```

登录后接口统一通过请求头携带「开发环境 token」：

```http
Authorization: Bearer dev-token:<username>
```

---

## 一、用户与认证模块 `/api/user/*`

### 1. 发送邮箱验证码

- **URL**: `POST /api/user/send-email-code?email=xxx@qq.com`
- **说明**: 给指定邮箱发送 6 位验证码，有效期 5 分钟，用于注册 / 邮箱登录。
- **请求头**: 无
- **请求体**: 无
- **响应**:
  - `code = 200` 成功
  - `code = 500` 发送失败

---

### 2. 账号密码登录

- **URL**: `POST /api/user/login/password`
- **说明**: 使用用户名 + 密码登录，返回开发环境 token + 用户信息。
- **请求头**: 无
- **请求体(JSON)**:

```json
{
  "username": "test",
  "password": "123456"
}
```

- **成功响应示例**:

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "dev-token:test",
    "user": { /* User 对象 */ }
  }
}
```

---

### 3. 邮箱验证码登录

- **URL**: `POST /api/user/login/email`
- **说明**: 使用邮箱 + 验证码登录，流程与密码登录类似。
- **请求体(JSON)**:

```json
{
  "email": "xxx@qq.com",
  "code": "123456"
}
```

---

### 4. 获取当前登录用户信息

- **URL**: `GET /api/user/me`
- **说明**: 根据 `Authorization` 头中的 `dev-token:<username>` 返回当前登录用户信息。
- **请求头**:

```http
Authorization: Bearer dev-token:<username>
```

- **响应**:
  - `code = 200` 时，`data` 为 `User` 对象
  - `code = 401` 未登录

---

### 5. 更新个人资料

- **URL**: `POST /api/user/update`
- **说明**: 更新当前登录用户的基本资料（昵称、头像、简介等）。
- **请求头**: 需登录
- **请求体(JSON)**: 传 `User` 对象，至少需要 `id` 匹配当前登录用户 ID。

```json
{
  "id": 1,
  "username": "test",
  "email": "xxx@qq.com",
  "nickname": "昵称",
  "avatar": "/upload/avatars/20260315/xxx.png",
  "bio": "个人简介"
}
```

---

### 6. 修改密码

- **URL**: `POST /api/user/change-password`
- **说明**: 修改当前登录用户密码。
- **请求体(JSON)**:

```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

---

### 7. 邀请注册

- **URL**: `POST /api/user/register`
- **说明**: 通过邀请码 + 邮箱验证码完成新用户注册。
- **请求体(JSON)**:

```json
{
  "username": "test",
  "password": "123456",
  "email": "xxx@qq.com",
  "emailCode": "123456",
  "nickname": "昵称",
  "inviteCode": "ABCDEFGH"
}
```

---

### 8. 查询邀请树

- **URL**: `GET /api/user/invite-tree?userId=1`
- **说明**: 获取指定用户作为根的多级邀请树。

---

### 9. 生成邀请码

- **URL**: `POST /api/user/generate-invite-code?userId=1`
- **说明**: 为指定用户生成一个新的邀请码（带过期时间）。

---

## 二、帖子与互动 `/api/post/*`

### 1. 创建帖子

- **URL**: `POST /api/post/create`
- **说明**: 登录后创建一个帖子，作者 ID 自动根据 token 解析。
- **请求头**: 登录必需
- **请求体(JSON)**: `Post` 对象（如标题、内容等）。

---

### 2. 帖子列表

- **URL**: `GET /api/post/list?offset=0&limit=10`
- **说明**: 分页获取最近帖子列表。

---

### 3. 帖子详情

- **URL**: `GET /api/post/detail?id=123`

---

### 4. 点赞帖子

- **URL**: `POST /api/post/like?id=123`
- **说明**: 对指定帖子点赞（简单计数）。

---

### 5. 收藏 / 取消收藏帖子

- **URL**: `POST /api/post/favorite?id=123`
- **说明**: 登录用户对帖子进行收藏或取消收藏（后端自动切换）。
- **响应字段**:
  - `favorited`: 是否收藏中
  - `count`: 当前收藏总数

---

### 6. 添加评论

- **URL**: `POST /api/post/comment/add`
- **说明**: 登录用户对帖子发表评论，同时给帖子作者发送一条通知。
- **请求体(JSON)**:

```json
{
  "postId": 123,
  "content": "评论内容"
}
```

---

### 7. 评论列表

- **URL**: `GET /api/post/comment/list?postId=123`

---

## 三、通知模块 `/api/sub/*`

### 1. 通知列表

- **URL**: `GET /api/sub/list`
- **说明**: 获取当前登录用户的通知列表（如帖子被评论等）。

---

### 2. 标记单条通知为已读

- **URL**: `POST /api/sub/read?id=123`

---

### 3. 全部标记为已读

- **URL**: `POST /api/sub/read-all`

---

## 四、关注关系 `/api/follow/*`

### 1. 关注 / 取消关注用户

- **URL**: `POST /api/follow/toggle?targetId=2`
- **说明**: 当前登录用户对目标用户进行关注/取关。

---

### 2. 是否已关注

- **URL**: `GET /api/follow/status?targetId=2`
- **说明**: 查询当前登录用户是否已关注指定用户。

---

### 3. 我关注的人列表

- **URL**: `GET /api/follow/following`
- **可选**: `GET /api/follow/following?userId=1` 查询指定用户关注的人。

---

### 4. 关注我的人列表

- **URL**: `GET /api/follow/followers`
- **可选**: `GET /api/follow/followers?userId=1`

---

## 五、收藏汇总 `/api/collection/*`

### 1. 我的收藏列表

- **URL**: `GET /api/collection/list`
- **说明**: 获取当前登录用户收藏的内容列表（帖子、博客等聚合）。

---

## 六、首页与统计 `/api/home/*`

### 1. 用户名列表（调试用）

- **URL**: `GET /api/home/usernames`

### 2. 按用户名查用户信息（调试用）

- **URL**: `GET /api/home/user?username=test`

### 3. 全站统计信息

- **URL**: `GET /api/home/stats`
- **返回字段**（在 `data` 中）：
  - `totalUsers`
  - `totalPosts`
  - `totalBlogs`
  - `totalComments`
  - `totalFavorites`

---

## 七、文件上传 `/api/file/*`

### 1. 上传头像

- **URL**: `POST /api/file/upload-avatar`
- **说明**: 上传用户头像图片，返回相对路径 URL，可保存到 `user.avatar`。
- **请求体**: `multipart/form-data`，字段名 `file`。
- **校验**:
  - 类型必须是 `image/*`
  - 大小 ≤ 2MB

---

### 2. 富文本编辑器图片上传

- **URL**: `POST /api/file/upload-editor-image`
- **说明**: 富文本中的插入图片统一调用该接口，返回图片可访问 URL。
- **请求体**: `multipart/form-data`，字段名 `file`。
- **校验**:
  - 类型必须是 `image/*`
  - 大小 ≤ 5MB

---

## 八、博客模块 `/api/blog/*`

### 1. 保存博客（新建 / 编辑）

- **URL**: `POST /api/blog/save`
- **说明**: 保存博客内容，`id` 为空时为新建，否则为更新。
- **请求体(JSON)**（示例）：

```json
{
  "id": null,
  "userId": 1,
  "title": "博客标题",
  "content": "<p>富文本 HTML 内容</p>",
  "tags": "Java,Spring",
  "categoryId": 1,
  "openScope": 2
}
```

---

### 2. 按开放范围列出博客

- **URL**: `GET /api/blog/list-by-scope?userId=1&scope=1`
- **说明**:
  - `scope=0`: 只看自己的博客
  - `scope=1`: 自己 + 公共博客
  - `scope=2`: 仅公共博客

---

### 3. 查询所有已使用标签

- **URL**: `GET /api/blog/tags`
- **说明**: 返回数据库中已使用标签字段的去重集合，前端可用来提供下拉选择。

---

### 4. 博客历史版本恢复（预留）

- **URL**: `POST /api/blog/restore/{blogId}/{versionId}`
- **说明**: 当前无版本表，此接口暂返回失败，为后续扩展预留。

---

## 九、管理员用户管理 `/api/admin/user/*`

> 要求当前登录用户 `roleId == 2`（ADMIN）。

### 1. 用户列表

- **URL**: `GET /api/admin/user/list`

### 2. 修改用户状态（启用/禁用）

- **URL**: `POST /api/admin/user/update-status?userId=1&status=0|1`

### 3. 修改用户角色

- **URL**: `POST /api/admin/user/update-role?userId=1&roleId=1|2`

