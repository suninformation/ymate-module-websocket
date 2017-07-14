/*
 * Copyright 2007-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ymate.module.websocket.annotation;

import net.ymate.module.websocket.support.WSServerEndpointConfigurator;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import java.lang.annotation.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/12 上午11:45
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WSServer {

    String value();

    String[] subprotocols() default {};

    Class<? extends Decoder>[] decoders() default {};

    Class<? extends Encoder>[] encoders() default {};

    Class<? extends WSServerEndpointConfigurator> configurator() default WSServerEndpointConfigurator.class;
}
