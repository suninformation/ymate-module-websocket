### YMP-WebSocket

基于YMP框架实现对WebSocket技术的封装；

#### Maven包依赖

    <dependency>
        <groupId>net.ymate.module</groupId>
        <artifactId>ymate-module-websocket</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

#### 模块配置参数说明

    #-------------------------------------
    # module.websocket 模块初始化参数
    #-------------------------------------
    
    # 消息异步发送超时时间, 单位: 毫秒, 默认值: 0
    ymp.configs.module.websocket.async_send_timeout=
    
    # 会话默认最大空闲超时时间, 单位: 毫秒, 默认值: 0
    ymp.configs.module.websocket.default_max_session_idle_timeout=
    
    # 文本消息默认最大缓冲区大小, 默认值: 0
    ymp.configs.module.websocket.default_max_text_message_buffer_size=
    
    # 二进制消息默认最大缓冲区大小, 默认值: 0
    ymp.configs.module.websocket.default_max_binary_message_buffer_size=

#### One More Thing

YMP不仅提供便捷的Web及其它Java项目的快速开发体验，也将不断提供更多丰富的项目实践经验。

感兴趣的小伙伴儿们可以加入 官方QQ群480374360，一起交流学习，帮助YMP成长！

了解更多有关YMP框架的内容，请访问官网：http://www.ymate.net/