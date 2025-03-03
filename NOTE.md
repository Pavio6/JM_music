### Spring Cache
* Spring Cache是一个框架 实现了基于注解的缓存功能
* Spring Cache提供了一层抽象 底层可以切换不同的缓存实现
* 常用注解：
  * @EnableCaching：开启缓存注解功能 通常加在启动类上
  * @Cacheable：方法执行前先查询缓存中是否有数据 如果有数据 则直接返回缓存数据 没有则将方法返回值放到缓存中
  * @CachePut：将方法的返回值放到缓存中
  * @CacheEvict：将一条或多条数据从缓存中删除
