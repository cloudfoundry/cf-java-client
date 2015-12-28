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

package org.cloudfoundry.operations.v2;

import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;
import org.reactivestreams.Publisher;
import reactor.fn.Function;
import reactor.rx.Streams;

public final class Resources {

    private Resources() {
    }

    /**
     * Generate the stream of resources accumulated from a series of responses.
     *
     * @param <R>           the type of resource in the list on each {@link PaginatedResponse}.
     * @param <U>           the type of {@link PaginatedResponse}.
     * @return a stream of <code>R</code> objects.
     */
    public static <R extends Resource<?>, U extends PaginatedResponse<R>> Function<U, Publisher<R>> extractResources() {
        return new Function<U, Publisher<R>>() {

            @Override
            public Publisher<R> apply(U pageResponse) {
                return Streams.from(pageResponse.getResources());
            }

        };
    }

}
