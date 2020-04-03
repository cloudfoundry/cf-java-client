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
import org.cloudfoundry.reactor.util.UriQueryParameter;
import org.cloudfoundry.reactor.util.UriQueryParameterBuilder;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A builder for Cloud Foundry queries
 */
public final class QueryBuilder implements UriQueryParameterBuilder {

    public Stream<UriQueryParameter> build(Object instance) {
        return AnnotationUtils.streamAnnotatedValues(instance, QueryParameter.class)
            .map(QueryBuilder::processValue)
            .filter(Objects::nonNull);
    }

    private static UriQueryParameter processCollection(QueryParameter queryParameter, Object value) {
        return processValue(queryParameter.value(), ((Collection<?>) value).stream()
            .map(Object::toString)
            .map(String::trim)
            .collect(Collectors.joining(queryParameter.delimiter())));
    }

    private static UriQueryParameter processValue(AnnotatedValue<QueryParameter> annotatedValue) {
        QueryParameter queryParameter = annotatedValue.getAnnotation();
        Object value = annotatedValue.getValue();
        if (value instanceof Collection) {
            return processCollection(queryParameter, value);
        } else {
            return processValue(queryParameter.value(), value.toString());
        }
    }

    private static UriQueryParameter processValue(String name, String value) {
        return UriQueryParameter.of(name, value);
    }

}
