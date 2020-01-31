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

package org.cloudfoundry.reactor.client.v2;

import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.reactor.util.AnnotationUtils;
import org.cloudfoundry.reactor.util.AnnotationUtils.AnnotatedValue;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
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
        AnnotationUtils.streamAnnotatedValues(instance, FilterParameter.class)
            .forEach(processValue(builder));
    }

    private static void processCollection(UriComponentsBuilder builder, FilterParameter filterParameter, Object value) {
        List<String> collection = ((Collection<?>) value).stream()
            .map(Object::toString)
            .map(String::trim)
            .collect(Collectors.toList());

        if (collection.size() == 1) {
            processValue(builder, filterParameter.value(), filterParameter.operation(), collection.get(0));
        } else if (collection.size() > 1) {
            processValue(builder, filterParameter.value(), filterParameter.collectionOperation(), collection);
        }
    }

    private static Consumer<AnnotatedValue<FilterParameter>> processValue(UriComponentsBuilder builder) {
        return annotatedValue -> {
            FilterParameter filterParameter = annotatedValue.getAnnotation();
            Object value = annotatedValue.getValue();
            if (value instanceof Collection) {
                processCollection(builder, filterParameter, value);
            } else {
                processValue(builder, filterParameter.value(), filterParameter.operation(), value.toString()
                    .trim());
            }
        };
    }

    private static void processValue(UriComponentsBuilder builder, String name, FilterParameter.Operation operation,
                                     Collection<String> collection) {
        String value = String.join(",", collection);
        if (!value.isEmpty()) {
            processValue(builder, name, operation, value);
        }
    }

    private static void processValue(UriComponentsBuilder builder, String name, FilterParameter.Operation operation, String value) {
        builder.queryParam("q", String.format("%s%s%s", name, operation, value));
    }

}
