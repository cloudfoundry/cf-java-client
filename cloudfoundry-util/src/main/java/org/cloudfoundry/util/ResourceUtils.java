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

import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;
import reactor.core.publisher.Flux;

/**
 * Utilities for dealing with {@link Resource}s
 */
public final class ResourceUtils {

    private ResourceUtils() {
    }

    /**
     * Return the entity of a resource
     *
     * @param resource the resource
     * @param <T>      the type of the resource's entity
     * @param <R>      the resource type
     * @return the resource's entity
     */
    public static <T, R extends Resource<T>> T getEntity(R resource) {
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
    public static <R extends Resource<?>, U extends PaginatedResponse<R>> Flux<R> getResources(U response) {
        return Flux.fromIterable(response.getResources());
    }

}
