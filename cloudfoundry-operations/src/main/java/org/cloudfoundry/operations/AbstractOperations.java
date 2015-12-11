/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.v2.PaginatedRequest;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.reactivestreams.Publisher;
import reactor.fn.Function;
import reactor.rx.Stream;
import reactor.rx.Streams;


abstract class AbstractOperations {

    protected final <T extends PaginatedRequest, U extends PaginatedResponse<?>> Stream<U> paginate(
            final Function<Integer, T> requestProvider, final Function<T, Publisher<U>> operationExecutor) {

        return Streams.just(Streams.wrap(operationExecutor.apply(requestProvider.apply(1))))
                .concatMap(new Function<Stream<U>, Publisher<? extends U>>() {

                    @Override
                    public Publisher<? extends U> apply(Stream<U> responseStream) {
                        return responseStream
                                .take(1)
                                .concatMap(new Function<U, Publisher<? extends U>>() {

                                               @Override
                                               public Publisher<? extends U> apply(U response) {
                                                   return Streams.range(2, response.getTotalPages() - 1)
                                                           .flatMap(new Function<Integer, Publisher<? extends U>>() {

                                                               @Override
                                                               public Publisher<? extends U> apply(Integer page) {
                                                                   return operationExecutor.apply(requestProvider
                                                                           .apply(page));
                                                               }

                                                           })
                                                           .startWith(response);
                                               }

                                           }
                                );
                    }

                });
    }

}
