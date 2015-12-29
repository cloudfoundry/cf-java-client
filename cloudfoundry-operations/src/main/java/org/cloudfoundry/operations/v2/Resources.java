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
import reactor.rx.Stream;
import reactor.rx.Streams;

public final class Resources {

    private Resources() {
    }

    /**
     * Returns a function to extract the id of a resource
     *
     * @return the function to extract the id of a resource
     */
    public static Function<Resource<?>, String> extractId() {
        return new Function<Resource<?>, String>() {

            @Override
            public String apply(Resource<?> resource) {
                return getId(resource);
            }

        };
    }

    /**
     * Returns a function to extract the resources from a response
     *
     * @param <R> the resource type
     * @param <U> the response type
     * @return the function to extract the resources from the response
     */
    public static <R extends Resource<?>, U extends PaginatedResponse<R>> Function<U, Publisher<R>> extractResources() {
        return new Function<U, Publisher<R>>() {

            @Override
            public Publisher<R> apply(U pageResponse) {
                return getResources(pageResponse);
            }

        };
    }

    /**
     * Return the entity of a resource
     *
     * @param resource the resource
     * @param <T>      the type of the resource's entity
     * @return the resource's entity
     */
    public static <T> T getEntity(Resource<T> resource) {
        return resource.getEntity();
    }

    /**
     * Returns the id of a resource
     *
     * @param resource the resource
     * @return the id of the resource
     */
    public static String getId(Resource<?> resource) {
        return resource.getMetadata().getId();
    }

    /**
     * Return a stream of resources from a response
     *
     * @param response the response
     * @param <R>      the resource type
     * @param <U>      the response type
     * @return a stream of resources from the response
     */
    public static <R extends Resource<?>, U extends PaginatedResponse<R>> Stream<R> getResources(U response) {
        return Streams.from(response.getResources());
    }

}
