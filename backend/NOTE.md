## WebSocket + RabbitMQ

### WebSocket

1. WebSocket是基于TCP的一种新的网络协议，它实现了浏览器与服务器全双工通信 - 浏览器和服务器只需要完成一次握手，两者之间就可以创建持久性的连接，并进行双向数据传输。
2. HTTP协议和WebSocket协议对比：
    - HTTP是短连接
    - WebSocket是长连接
    - HTTP通信是单向的，基于请求响应模式
    - WebSocket支持双向通信
    - HTTP和WebSocket底层都是TCP连接

3. 应用场景
    - 视频弹幕
    - 网页聊天
    - 体育实况更新
    - 股票基金报价实时更新

4. 细节
    - WebSock的核心组件
        - WebSocketHandler：负责处理 WebSocket 连接、消息和关闭事件。
        - HandshakeInterceptor：负责在握手阶段进行认证和拦截。
        - (先握手再进行连接)
    - 客户端发送WebSocket连接时，会发送ws://... ，底层实现中，浏览器会自动将 ws:// 转换为 HTTP 请求并携带 Upgrade
      头，完成握手后升级为WebSocket 协议。

### RabbitMQ

###### linux中启动rabbitmq服务

- 在/usr/local/rabbitmq_server.. /sbin中执行 ./rabbitmq-server -detached
- 查看启动状态：在sbin目录下执行./rabbitmqctl status
-

1. 生产者（Producer） ：负责生成消息并将其发送到 RabbitMQ。
2. 交换机（Exchange） ：接收来自生产者的消息，并根据路由规则将消息分发到队列。
3. 队列（Queue） ：存储消息的地方，消费者从队列中获取消息进行处理。
4. 路由键（Routing Key） ：用于定义消息如何从交换机路由到队列的规则。
5. 消费者（Consumer） ：从队列中读取消息并进行处理。
6. SimpMessagingTemplate 是 Spring 提供的 STOMP 消息发送工具，用于通过 WebSocket 推送消息。

### WebSocket + RabbitMQ

- 应用场景：一个用户有多个连接记录
    - 多设备登录场景
    - 更优雅地处理网络波动和重连
    - 提供更准确的在线状态管理
    - 增强安全性和用户体验
    - 实现跨设备状态同步

## Other

### Spring Cache

* Spring Cache是一个框架 实现了基于注解的缓存功能
* Spring Cache提供了一层抽象 底层可以切换不同的缓存实现
* 常用注解：
    * @EnableCaching：开启缓存注解功能 通常加在启动类上
    * @Cacheable：方法执行前先查询缓存中是否有数据 如果有数据 则直接返回缓存数据 没有则将方法返回值放到缓存中
    * @CachePut：将方法的返回值放到缓存中
    * @CacheEvict：将一条或多条数据从缓存中删除

### ContentType 和 headers

1. ContentType

- contentType指定对象的MIME类型，告知客户端如何处理
- 设置HTTP响应的Content-Type头：浏览器根据该值觉得如何渲染内容。
- 示例：
    - text/plain：直接显示文本内容。
    - application/pdf：在浏览器中内嵌PDF阅读器打开。
    - image/png：直接显示图片。

1. headers
    - 设置自定义HTTP头，控制客户端的行为或附加元数据。
    - 示例：.headers(Map.of("Content-Disposition", "inline"))
        - Content-Disposition：响应头字段(告诉浏览器内容是下载还是显示[即内嵌])
            - inline：内容直接显示在页面中（如文本、图片）
            - attachment：表示内容应该作为附件下载。
