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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

/**
 * A utility class to provide functions for handling PaginatedResponse and those containing lists of Resources.
 */
public final class PaginationUtils {

    private PaginationUtils() {
    }

    /**
     * Generate the stream of resources accumulated from a series of responses obtained from the page supplier.
     *
     * @param pageSupplier a function from integers to {@link Mono}s of {@link org.cloudfoundry.client.v2.PaginatedResponse}s.
     * @param <T>          the type of resource in the list on each {@link org.cloudfoundry.client.v2.PaginatedResponse}.
     * @param <U>          the type of {@link org.cloudfoundry.client.v2.PaginatedResponse}.
     * @return a stream of <code>T</code> objects.
     */
    public static <T extends org.cloudfoundry.client.v2.Resource<?>, U extends org.cloudfoundry.client.v2.PaginatedResponse<T>> Flux<T> requestClientV2Resources(
        Function<Integer, Mono<U>> pageSupplier) {

        return pageSupplier
            .apply(1)
            .flatMapMany(requestClientV2AdditionalPages(pageSupplier))
            .flatMap(ResourceUtils::getResources);
    }

    /**
     * Generate the stream of resources accumulated from a series of responses obtained from the page supplier.
     *
     * @param pageSupplier a function from integers to {@link Mono}s of {@link org.cloudfoundry.client.v3.PaginatedResponse}s.
     * @param <T>          the type of resource in the list on each {@link org.cloudfoundry.client.v3.PaginatedResponse}.
     * @param <U>          the type of {@link org.cloudfoundry.client.v3.PaginatedResponse}.
     * @return a stream of <code>T</code> objects.
     */
    @SuppressWarnings("rawtypes")
    public static <T, U extends org.cloudfoundry.client.v3.PaginatedResponse<T>> Flux<T> requestClientV3Resources(Function<Integer, Mono<U>> pageSupplier) {
        return pageSupplier
            .apply(1)
            .flatMapMany(requestClientV3AdditionalPages(pageSupplier))
            .flatMapIterable(org.cloudfoundry.client.v3.PaginatedResponse::getResources);
    }

    /**
     * Generate the stream of resources accumulated from a series of responses obtained from the page supplier.
     *
     * @param pageSupplier a function from integers to {@link Mono}s of {@link org.cloudfoundry.uaa.PaginatedResponse}s.
     * @param <T>          the type of resource in the list on each {@link org.cloudfoundry.uaa.PaginatedResponse}.
     * @param <U>          the type of {@link org.cloudfoundry.uaa.PaginatedResponse}.
     * @return a stream of <code>T</code> objects.
     */
    @SuppressWarnings("rawtypes")
    public static <T, U extends org.cloudfoundry.uaa.PaginatedResponse<T>> Flux<T> requestUaaResources(Function<Integer, Mono<U>> pageSupplier) {
        return pageSupplier
            .apply(1)
            .flatMapMany(requestUaaAdditionalPages(pageSupplier))
            .flatMapIterable(org.cloudfoundry.uaa.PaginatedResponse::getResources);
    }

    private static <T> Function<T, Flux<T>> requestAdditionalPages(Function<Integer, Mono<T>> pageSupplier, Function<T, Integer> totalPagesSupplier) {
        return response -> {
            Integer totalPages = Optional.ofNullable(totalPagesSupplier.apply(response)).orElse(1);

            return Flux
                .range(2, totalPages - 1)
                .flatMap(pageSupplier)
                .startWith(response)
                .buffer()
                .flatMapIterable(d -> d);
        };
    }

    private static <T extends org.cloudfoundry.client.v2.PaginatedResponse<?>> Function<T, Flux<T>> requestClientV2AdditionalPages(Function<Integer, Mono<T>> pageSupplier) {
        return requestAdditionalPages(pageSupplier, response -> response.getTotalPages());
    }

    private static <T extends org.cloudfoundry.client.v3.PaginatedResponse<?>> Function<T, Flux<T>> requestClientV3AdditionalPages(Function<Integer, Mono<T>> pageSupplier) {
        return requestAdditionalPages(pageSupplier, response -> response.getPagination().getTotalPages());
    }

    private static <T extends org.cloudfoundry.uaa.PaginatedResponse<?>> Function<T, Flux<T>> requestUaaAdditionalPages(Function<Integer, Mono<T>> pageSupplier) {
        return response -> {
            Integer totalPages = (response.getTotalResults() - 1) / response.getItemsPerPage() + 1;

            return Flux
                .range(1, totalPages - 1)
                .map(page -> 1 + (page * response.getItemsPerPage()))
                .flatMap(pageSupplier)
                .startWith(response)
                .buffer()
                .flatMapIterable(d -> d);
        };
    }

}
