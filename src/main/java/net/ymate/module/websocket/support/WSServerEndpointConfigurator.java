/*
 * Copyright 2007-2021 the original author or authors.
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
package net.ymate.module.websocket.support;

import net.ymate.module.websocket.IWSHandshakeModifier;
import net.ymate.module.websocket.IWebSocket;
import net.ymate.module.websocket.WSServerListener;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/12 下午5:22
 * @since 1.0
 */
public class WSServerEndpointConfigurator extends ServerEndpointConfig.Configurator implements ServerEndpointConfig {

    private final IWebSocket owner;

    private final String path;

    private final Class<? extends WSServerListener> endpointClass;

    private List<Class<? extends Encoder>> encoders = new ArrayList<>();

    private List<Class<? extends Decoder>> decoders = new ArrayList<>();

    private List<String> subprotocols = new ArrayList<>();

    private List<Extension> extensions = new ArrayList<>();

    private final Map<String, Object> userProperties = new HashMap<>();

    private IWSHandshakeModifier handshakeModifier;

    public WSServerEndpointConfigurator(IWebSocket owner, String path, Class<? extends WSServerListener> endpointClass) {
        if (owner == null) {
            throw new NullArgumentException("owner");
        }
        if (StringUtils.isBlank(path)) {
            throw new NullArgumentException("path");
        }
        if (endpointClass == null) {
            throw new NullArgumentException("endpointClass");
        }
        this.owner = owner;
        this.path = path;
        this.endpointClass = endpointClass;
    }

    @Override
    public Class<?> getEndpointClass() {
        return endpointClass;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public List<String> getSubprotocols() {
        return subprotocols;
    }

    public void setSubprotocols(List<String> subprotocols) {
        this.subprotocols = subprotocols;
    }

    @Override
    public List<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public Configurator getConfigurator() {
        return this;
    }

    @Override
    public List<Class<? extends Encoder>> getEncoders() {
        return encoders;
    }

    public void setEncoders(List<Class<? extends Encoder>> encoders) {
        this.encoders = encoders;
    }

    @Override
    public List<Class<? extends Decoder>> getDecoders() {
        return decoders;
    }

    public void setDecoders(List<Class<? extends Decoder>> decoders) {
        this.decoders = decoders;
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(Map<String, Object> userProperties) {
        this.userProperties.clear();
        this.userProperties.putAll(userProperties);
    }

    public IWSHandshakeModifier getHandshakeModifier() {
        return handshakeModifier;
    }

    public void setHandshakeModifier(IWSHandshakeModifier handshakeModifier) {
        this.handshakeModifier = handshakeModifier;
    }

    @Override
    public final <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return (T) owner.getOwner().getBeanFactory().getBean(clazz);
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        if (handshakeModifier != null) {
            handshakeModifier.modifyHandshake(sec, request, response);
        }
    }

    @Override
    public boolean checkOrigin(String originHeaderValue) {
        return super.checkOrigin(originHeaderValue);
    }

    @Override
    public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
        return super.getNegotiatedSubprotocol(supported, requested);
    }

    @Override
    public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
        return super.getNegotiatedExtensions(installed, requested);
    }
}
