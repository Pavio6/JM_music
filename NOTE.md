### Spring Cache

* Spring Cache是一个框架 实现了基于注解的缓存功能
* Spring Cache提供了一层抽象 底层可以切换不同的缓存实现
* 常用注解：
    * @EnableCaching：开启缓存注解功能 通常加在启动类上
    * @Cacheable：方法执行前先查询缓存中是否有数据 如果有数据 则直接返回缓存数据 没有则将方法返回值放到缓存中
    * @CachePut：将方法的返回值放到缓存中
    * @CacheEvict：将一条或多条数据从缓存中删除

### WebSocket

- WebSocket是基于TCP的一种新的网络协议，它实现了浏览器与服务器全双工通信 - 浏览器和服务器只需要完成一次握手，两者之间就可以创建
  持久性的连接，并进行双向数据传输。
- HTTP协议和WebSocket协议对比：
    - HTTP是短连接
    - WebSocket是长连接
    - HTTP通信是单向的，基于请求响应模式
    - WebSocket支持双向通信
    - HTTP和WebSocket底层都是TCP连接

##### 应用场景

- 视频弹幕
- 网页聊天
- 体育实况更新
- 股票基金报价实时更新

#### DETAIL

- WebSock的核心组件
    - WebSocketHandler：负责处理 WebSocket 连接、消息和关闭事件。
    - HandshakeInterceptor：负责在握手阶段进行认证和拦截。
    - (先握手再进行连接)

##### *

- 客户端发送WebSocket连接时，会发送ws://... ，底层实现中，浏览器会自动将 ws:// 转换为 HTTP 请求并携带 Upgrade 头，完成握手后升级为
  WebSocket 协议。

#### RabbitMQ

###### 交换机(Exchange)

- 交换机是消息的路由中心，负责接收生产者发送的消息，并根据特定的规则将消息路由到一个或多个队列中。交换机本身并不存储消息，它只是根据消息的路由键（Routing
  Key）和绑定规则（Binding）来决定消息的去向。

###### 队列(Queue)

- 队列是消息的存储容器，用于存储消息，直到消费者从队列中取出并处理消息。队列是消息的最终目的地。
### WebSocket + RabbitMQ
- 一个用户有多个连接记录
  - 多设备登录场景
  - 更优雅地处理网络波动和重连
  - 提供更准确的在线状态管理
  - 增强安全性和用户体验
  - 实现跨设备状态同步