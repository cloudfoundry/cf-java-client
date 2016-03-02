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

package org.cloudfoundry.util;

import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * A utility class to provide functions for handling {@link PaginatedResponse}s and those containing lists of {@link Resource}s.
 */
public final class PaginationUtils {

    private PaginationUtils() {
    }

    /**
     * Generate the stream of responses starting from page 1 of an initial paginated response.
     *
     * @param pageSupplier a function from integers to {@link Mono}s of {@link PaginatedResponse}s.
     * @param <U>          the type of {@link PaginatedResponse}.
     * @return a stream of <code>U</code> objects.
     */
    public static <U extends PaginatedResponse<?>> Flux<U> requestPages(Function<Integer, Mono<U>> pageSupplier) {
        return pageSupplier
            .apply(1)
            .flatMap(requestAdditionalPages(pageSupplier));
    }

    /**
     * Generate the stream of resources accumulated from a series of responses obtained from the page supplier.
     *
     * @param pageSupplier a function from integers to {@link Mono}s of {@link PaginatedResponse}s.
     * @param <R>          the type of resource in the list on each {@link PaginatedResponse}.
     * @param <U>          the type of {@link PaginatedResponse}.
     * @return a stream of <code>R</code> objects.
     */
    public static <R extends Resource<?>, U extends PaginatedResponse<R>> Flux<R> requestResources(Function<Integer, Mono<U>> pageSupplier) {
        return requestPages(pageSupplier)
            .flatMap(ResourceUtils::getResources);
    }

    private static <U extends PaginatedResponse<?>> Function<U, Flux<U>> requestAdditionalPages(Function<Integer, Mono<U>> pageSupplier) {
        return response -> {
            Integer totalPages = response.getTotalPages();
            if (totalPages == null) {
                throw new IllegalStateException(String.format("Page response (class %s) has no total pages set", response.getClass().getCanonicalName()));
            }

            return Flux
                .range(2, totalPages - 1)
                .flatMap(pageSupplier::apply)
                .startWith(response);
        };
    }

}
