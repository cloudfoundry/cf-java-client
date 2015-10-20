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
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.util.function.Function;
import java.util.function.Supplier;

abstract class AbstractOperations {

    protected final <T extends PaginatedRequest<T>, U extends PaginatedResponse> Stream<U> paginate(
            Supplier<T> requestProvider, Function<T, Publisher<U>> operationExecutor) {

        return Streams.just(Streams.wrap(operationExecutor.apply(requestProvider.get().withPage(1))))
                .concatMap(responseStream -> responseStream
                        .take(1)
                        .concatMap(response -> Streams.range(2, response.getTotalPages() - 1)
                                        .flatMap(page -> operationExecutor.apply(requestProvider.get().withPage(page)))
                                        .startWith(response)
                        ));
    }

}
