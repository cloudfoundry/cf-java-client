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

package org.cloudfoundry.reactor.util;

import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpOutbound;

/**
 * A provider that adds the {@code Authorization} header to requests
 */
public interface AuthorizationProvider {

    /**
     * Adds the {@code Authorization} header to an {@link HttpOutbound}
     *
     * @param outbound the {@link HttpOutbound} to modify
     * @return an uncompleted {@link HttpOutbound}
     */
    Mono<HttpOutbound> addAuthorization(HttpOutbound outbound);

}
