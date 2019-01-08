/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.reactor.client;

import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.reactor.util.AnnotationUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.Exceptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A builder for Cloud Foundry queries
 */
public final class QueryBuilder {

    private QueryBuilder() {
    }

    /**
     * Augments a {@link UriComponentsBuilder} with queries based on the methods annotated with {@link QueryParameter}
     *
     * @param builder  the builder to augment
     * @param instance the instance to inspect and invoke
     */
    public static void augment(UriComponentsBuilder builder, Object instance) {
        Arrays.stream(instance.getClass().getMethods())
            .sorted(MethodNameComparator.INSTANCE)
            .forEach(processMethod(builder, instance));
    }

    private static Optional<Object> getValue(Method method, Object instance) {
        try {
            return Optional.ofNullable(method.invoke(instance));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static Consumer<QueryParameter> processAnnotation(UriComponentsBuilder builder, Method method, Object instance) {
        return queryParameter -> getValue(method, instance)
            .ifPresent(processValue(builder, queryParameter));
    }

    private static void processCollection(UriComponentsBuilder builder, QueryParameter queryParameter, Object value) {
        processValue(builder, queryParameter.value(),
            ((Collection<?>) value).stream()
                .map(o -> o.toString().trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(queryParameter.delimiter())));
    }

    private static Consumer<Method> processMethod(UriComponentsBuilder builder, Object instance) {
        return method -> AnnotationUtils.findAnnotation(method, QueryParameter.class)
            .ifPresent(processAnnotation(builder, method, instance));
    }

    private static void processValue(UriComponentsBuilder builder, String name, String value) {
        if (!value.isEmpty()) {
            builder.queryParam(name, value);
        }
    }

    private static Consumer<Object> processValue(UriComponentsBuilder builder, QueryParameter queryParameter) {
        return value -> {
            if (value instanceof Collection) {
                processCollection(builder, queryParameter, value);
            } else {
                processValue(builder, queryParameter.value(), value.toString());
            }
        };
    }

}
