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

package org.cloudfoundry.networking.v1.tags;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Tags API
 */
public interface Tags {

    /**
     * Makes the <a href="https://github.com/cloudfoundry-incubator/cf-networking-release/blob/develop/docs/API.md#get-networkingv1externaltags">List Tags</a> request
     *
     * @param request the List Tags request
     * @return the response to the List Tags request
     */
    Mono<ListTagsResponse> list(ListTagsRequest request);

}
