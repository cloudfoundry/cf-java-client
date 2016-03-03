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

package org.cloudfoundry.spring.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.cloudfoundry.RequestHeader;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestHeaderBuilder {

    /**
     * Populates a {@link HttpHeaders} with headers based on the methods annotated with {@link RequestHeader}
     *
     * @param headers  the headers to populate
     * @param instance the instance to inspect and invoke
     */
    public static void populate(HttpHeaders headers, Object instance) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(instance.getClass());
        Arrays.sort(methods, MethodNameComparator.INSTANCE);

        for (Method method : methods) {
            RequestHeader requestHeader = AnnotationUtils.getAnnotation(method, RequestHeader.class);
            if (requestHeader == null) {
                continue;
            }

            ReflectionUtils.makeAccessible(method);
            Object value = ReflectionUtils.invokeMethod(method, instance);

            if (value != null) {
                if (Iterable.class.isInstance(value)) {
                    for (Iterator<?> it = Iterable.class.cast(value).iterator(); it.hasNext(); ) {
                        Object subValue = it.next();
                        if (subValue != null) {
                            headers.add(requestHeader.value(), subValue.toString());
                        }
                    }
                } else {
                    headers.add(requestHeader.value(), value.toString());
                }
            }
        }
    }

}
