# YMATE-MODULE-WEBSOCKET

[![Maven Central status](https://img.shields.io/maven-central/v/net.ymate.module/ymate-module-websocket.svg)](https://search.maven.org/artifact/net.ymate.module/ymate-module-websocket)
[![LICENSE](https://img.shields.io/github/license/suninformation/ymate-module-websocket.svg)](https://gitee.com/suninformation/ymate-module-websocket/blob/master/LICENSE)

为 YMP 框架提供对 WebSocket 技术的集成与模块封装。

#### Maven包依赖

```xml
<dependency>
    <groupId>net.ymate.module</groupId>
    <artifactId>ymate-module-websocket</artifactId>
    <version>2.0.0</version>
</dependency>
```

#### 模块配置参数说明

```properties
#-------------------------------------
# module.websocket 模块初始化参数
#-------------------------------------

# 模块是否已启用, 默认值: true
ymp.configs.module.websocket.enabled=

# 消息异步发送超时时间, 单位: 毫秒, 默认值: 0
ymp.configs.module.websocket.async_send_timeout=

# 会话默认最大空闲超时时间, 单位: 毫秒, 默认值: 0
ymp.configs.module.websocket.default_max_session_idle_timeout=

# 文本消息默认最大缓冲区大小, 默认值: 0
ymp.configs.module.websocket.default_max_text_message_buffer_size=

# 二进制消息默认最大缓冲区大小, 默认值: 0
ymp.configs.module.websocket.default_max_binary_message_buffer_size=
```

## One More Thing

YMP 不仅提供便捷的 Web 及其它 Java 项目的快速开发体验，也将不断提供更多丰富的项目实践经验。

感兴趣的小伙伴儿们可以加入官方 QQ 群：[480374360](https://qm.qq.com/cgi-bin/qm/qr?k=3KSXbRoridGeFxTVA8HZzyhwU_btZQJ2)，一起交流学习，帮助 YMP 成长！

如果喜欢 YMP，希望得到你的支持和鼓励！

![Donation Code](https://ymate.net/img/donation_code.png)

了解更多有关 YMP 框架的内容，请访问官网：[https://ymate.net](https://ymate.net)