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

package org.cloudfoundry.operations.v2;

import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;
import org.reactivestreams.Publisher;
import reactor.fn.Function;
import reactor.rx.Stream;
import reactor.rx.Streams;

/**
 * A utility class to provide functions for handling {@link PaginatedResponse}s and those containing lists of {@link Resource}s.
 */
public final class Paginated {

    private Paginated() {
    }

    /**
     * Generate the stream of responses starting from page 1 of an initial paginated response.
     *
     * @param pagePublisher a function from integers to {@link Publisher}s of {@link PaginatedResponse}s.
     * @param <U>           the type of {@link PaginatedResponse}.
     * @return a stream of <code>U</code> objects.
     */
    public static <U extends PaginatedResponse<?>> Stream<U> requestPages(final Function<Integer, Publisher<U>> pagePublisher) {
        return Streams
                .wrap(pagePublisher.apply(1))
                .take(1)
                .flatMap(requestAdditionalPages(pagePublisher));
    }

    /**
     * Generate the stream of resources accumulated from a series of responses obtained from the page publisher.
     *
     * @param pagePublisher a function from integers to {@link Publisher}s of {@link PaginatedResponse}s.
     * @param <R>           the type of resource in the list on each {@link PaginatedResponse}.
     * @param <U>           the type of {@link PaginatedResponse}.
     * @return a stream of <code>R</code> objects.
     */
    public static <R extends Resource<?>, U extends PaginatedResponse<R>> Stream<R> requestResources(final Function<Integer, Publisher<U>> pagePublisher) {
        return requestPages(pagePublisher)
                .flatMap(Resources.<R, U>extractResources());
    }

    private static <U extends PaginatedResponse<?>> Function<U, Publisher<? extends U>> requestAdditionalPages(final Function<Integer, Publisher<U>> pagePublisher) {
        return new Function<U, Publisher<? extends U>>() {

            @Override
            public Publisher<? extends U> apply(U response) {
                Integer totalPages = response.getTotalPages();
                if (totalPages == null) {
                    throw new IllegalStateException(String.format("Page response (class %s) has no total pages set", response.getClass().getCanonicalName()));
                }

                return Streams
                        .range(2, totalPages - 1)
                        .flatMap(requestPage(pagePublisher))
                        .startWith(response);
            }

        };
    }

    private static <U extends PaginatedResponse<?>> Function<Integer, Publisher<? extends U>> requestPage(final Function<Integer, Publisher<U>> pagePublisher) {
        return new Function<Integer, Publisher<? extends U>>() {

            @Override
            public Publisher<? extends U> apply(Integer page) {
                return pagePublisher.apply(page);
            }

        };
    }
}
