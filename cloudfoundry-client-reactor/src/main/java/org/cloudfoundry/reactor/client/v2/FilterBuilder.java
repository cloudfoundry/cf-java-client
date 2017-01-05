/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.client.v2;

import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.reactor.client.MethodNameComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A builder for Cloud Foundry V2 filters
 */
final class FilterBuilder {

    private FilterBuilder() {
    }

    /**
     * Augments a {@link UriComponentsBuilder} with queries based on the methods annotated with {@link FilterParameter}
     *
     * @param builder  the builder to augment
     * @param instance the instance to inspect and invoke
     */
    public static void augment(UriComponentsBuilder builder, Object instance) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(instance.getClass());
        Arrays.sort(methods, MethodNameComparator.INSTANCE);

        for (Method method : methods) {
            for (Annotation annotation : AnnotationUtils.getAnnotations(method)) {
                if (AnnotationUtils.isAnnotationMetaPresent(annotation.getClass(), FilterParameter.class)) {
                    Object value = getValue(method, instance);

                    if (value != null) {
                        FilterParameter filterParameter = AnnotationUtils.getAnnotation(annotation, FilterParameter.class);

                        Object name = AnnotationUtils.getValue(annotation);
                        String operation = filterParameter.operator();

                        builder.queryParam("q", String.format("%s%s%s", name, operation, value));
                    }

                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Object getValue(Method method, Object instance) {
        ReflectionUtils.makeAccessible(method);
        Object value = ReflectionUtils.invokeMethod(method, instance);

        if (!(value instanceof Collection)) {
            return value;
        }

        List<?> collection = (List<?>) ((Collection) value).stream()
            .filter(o -> !ObjectUtils.isEmpty(o))
            .collect(Collectors.toList());

        if (collection.isEmpty()) {
            return null;
        }

        return StringUtils.collectionToCommaDelimitedString(collection);
    }

}
