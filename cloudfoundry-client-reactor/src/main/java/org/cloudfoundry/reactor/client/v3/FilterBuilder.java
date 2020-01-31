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

package org.cloudfoundry.reactor.client.v3;

import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.reactor.util.AnnotationUtils;
import org.cloudfoundry.reactor.util.AnnotationUtils.AnnotatedValue;
import org.cloudfoundry.reactor.util.UriQueryParameter;
import org.cloudfoundry.reactor.util.UriQueryParameterBuilder;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class FilterBuilder implements UriQueryParameterBuilder {

    public Stream<UriQueryParameter> build(Object instance) {
        return AnnotationUtils.streamAnnotatedValues(instance, FilterParameter.class)
            .map(FilterBuilder::processValue)
            .filter(Objects::nonNull);
    }

    private static UriQueryParameter processCollection(String name, Object value) {
        return processValue(name, ((Collection<?>) value).stream()
            .map(Object::toString)
            .map(String::trim)
            .collect(Collectors.toList()));
    }

    private static UriQueryParameter processValue(AnnotatedValue<FilterParameter> annotatedValue) {
        FilterParameter filterParameter = annotatedValue.getAnnotation();
        Object value = annotatedValue.getValue();
        if (value instanceof Collection) {
            return processCollection(filterParameter.value(), value);
        } else {
            return processValue(filterParameter.value(), value.toString());
        }
    }

    private static UriQueryParameter processValue(String name, Collection<String> collection) {
        String value = String.join(",", collection);
        if (!value.isEmpty()) {
            return processValue(name, value);
        }
        return null;
    }

    private static UriQueryParameter processValue(String name, String value) {
        return UriQueryParameter.of(name, value);
    }

}
