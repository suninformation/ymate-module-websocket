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
package net.ymate.module.websocket;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.Extension;
import java.util.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/16 下午7:33
 * @since 1.0
 */
public class WSExtension {

    private final String name;

    private final Map<String, String> parameters;

    public WSExtension(String name) {
        this(name, null);
    }

    public WSExtension(String name, Map<String, String> parameters) {
        if (StringUtils.isBlank(name)) {
            throw new NullArgumentException("name");
        }
        this.name = name;
        if (parameters != null && !parameters.isEmpty()) {
            this.parameters = Collections.unmodifiableMap(new LinkedHashMap<>(parameters));
        } else {
            this.parameters = Collections.emptyMap();
        }
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WSExtension extension = (WSExtension) obj;
        return (this.name.equals(extension.name) && this.parameters.equals(extension.parameters));
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 31 + this.parameters.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.name);
        parameters.forEach((key, value) -> {
            str.append(';');
            str.append(key);
            str.append('=');
            str.append(value);
        });
        return str.toString();
    }

    public Extension toExtension() {
        return new Extension() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public List<Parameter> getParameters() {
                List<Parameter> params = new ArrayList<>();
                parameters.forEach((key, value) -> params.add(new Parameter() {
                    @Override
                    public String getName() {
                        return key;
                    }

                    @Override
                    public String getValue() {
                        return value;
                    }
                }));
                return params;
            }
        };
    }
}
