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
package net.ymate.module.websocket;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import javax.websocket.Extension;
import java.util.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/16 下午7:33
 * @version 1.0
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
            Map<String, String> _params = new LinkedHashMap<String, String>(parameters.size());
            _params.putAll(parameters);
            this.parameters = Collections.unmodifiableMap(_params);
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
        WSExtension _target = (WSExtension) obj;
        return (this.name.equals(_target.name) && this.parameters.equals(_target.parameters));
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 31 + this.parameters.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.name);
        for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
            str.append(';');
            str.append(entry.getKey());
            str.append('=');
            str.append(entry.getValue());
        }
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
                List<Parameter> _params = new ArrayList<Parameter>();
                for (final Map.Entry<String, String> entry : parameters.entrySet()) {
                    _params.add(new Parameter() {
                        @Override
                        public String getName() {
                            return entry.getKey();
                        }

                        @Override
                        public String getValue() {
                            return entry.getValue();
                        }
                    });
                }
                return _params;
            }
        };
    }
}
