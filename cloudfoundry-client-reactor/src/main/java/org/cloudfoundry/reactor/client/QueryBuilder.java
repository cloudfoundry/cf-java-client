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

package org.cloudfoundry.reactor.client;

import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.reactor.util.AnnotationUtils;
import org.cloudfoundry.reactor.util.AnnotationUtils.AnnotatedValue;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
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
        AnnotationUtils.streamAnnotatedValues(instance, QueryParameter.class)
            .forEach(processValue(builder));
    }

    private static Consumer<AnnotatedValue<QueryParameter>> processValue(UriComponentsBuilder builder) {
        return annotatedValue -> {
            QueryParameter queryParameter = annotatedValue.getAnnotation();
            Object value = annotatedValue.getValue();
            if (value instanceof Collection) {
                processCollection(builder, queryParameter, value);
            } else {
                processValue(builder, queryParameter.value(), value.toString());
            }
        };
    }

    private static void processCollection(UriComponentsBuilder builder, QueryParameter queryParameter, Object value) {
        processValue(builder, queryParameter.value(), ((Collection<?>) value).stream()
            .map(Object::toString)
            .map(String::trim)
            .collect(Collectors.joining(queryParameter.delimiter())));
    }

    private static void processValue(UriComponentsBuilder builder, String name, String value) {
        builder.queryParam(name, value);
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
