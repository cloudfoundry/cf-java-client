/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for building maps for Json responses
 */
public final class FluentMap {

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    public static final class Builder<K, V> {

        private final Map<K, V> entries = new HashMap<>();

        public Map<K, V> build() {
            return this.entries;
        }

        public Builder<K, V> entries(Map<K, V> entries) {
            this.entries.putAll(entries);
            return this;
        }

        public Builder<K, V> entry(K key, V value) {
            this.entries.put(key, value);
            return this;
        }

    }

}
