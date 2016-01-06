/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.client.spring.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

/**
 * Utilities for dealing with Collections
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Create an instance of a {@link MultiValueMap} that contains a single entry.
     *
     * @param key    the key
     * @param values the values
     * @param <K>    the key type
     * @param <V>    the value type
     * @return the map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> MultiValueMap<K, V> singletonMultiValueMap(K key, V... values) {
        MultiValueMap<K, V> map = new LinkedMultiValueMap<>(1);
        map.put(key, Arrays.asList(values));
        return map;
    }

}
