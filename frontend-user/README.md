# music-website

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
pnpm install
```

### Compile and Hot-Reload for Development

```sh
pnpm dev
```

### Type-Check, Compile and Minify for Production

```sh
pnpm build
```

### WebSocket 通信：

```javascript
// 心跳
{
  type: 'heartbeat'
}

// 37 用户发送消息：
{
  type: 'message', to
:
  35, sender
:
  37, content
:
  '35用户你好', messageType
:
  'TEXT', messageId
:
  '16位唯一数字'
}
// 服务器接收到消息向 35 用户发送消息：
{
  type: 'message', to
:
  35, sender
:
  37, content
:
  '35用户你好', messageType
:
  'TEXT'，messageId：'58'
}
-- >

// 37 用户发送图片消息：
{ type: 'message', to: 35, sender: 37, content: 'https://tupian.jpg', messageType: 'IMAGE', messageId: '16位唯一数字' }
服务器接收到图片消息向
35
用户发送消息：
{
  type: 'message', to
:
  35, sender
:
  37, content
:
  'https://tupian.jpg', messageType
:
  'IMAGE'，messageId：'59'
}

// 37 用户撤回消息：
{
  type: 'recall', to
:
  35, sender
:
  37, messageId：'58'
}
// 服务器接收到消息向 35 用户发送撤回消息（成功时）：
{
  type: 'recall', to
:
  35, sender
:
  37, messageId：'58'
}
// 服务器接收到消息向 35 用户发送撤回消息（失败时）：
{
  type: 'recall_error', to
:
  35, sender
:
  37, message：'消息已超过 2 分钟，无法撤回'
}

// 35 用户上线，服务器向全体在线用户发送消息
{
  'type'
:
  'status', 'userId'
:
  35, 'online'
:
  true
}

// 37 用户断开连接时，服务器向全体在线用户发送消息
{
  'type'
:
  'status', 'userId'
:
  37, 'online'
:
  false
}

// 激活 35 用户聊天
{
  type: 'chat_active', otherUserId
:
  35
}
// 35 用户离开
{
  type: 'chat_inactive'
}
```
