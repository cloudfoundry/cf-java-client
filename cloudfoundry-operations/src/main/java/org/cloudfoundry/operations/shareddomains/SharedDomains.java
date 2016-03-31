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

package org.cloudfoundry.operations.shareddomains;

import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import reactor.core.publisher.Mono;

/**
 * Interface for Cloud Foundry Shared Domains Operations
 */
public interface SharedDomains {

    /**
     * Create a shared domain
     *
     * @param request The Create Shared Domain request
     * @return a completion indicator
     */
    Mono<Void> create(CreateSharedDomainRequest request);

}
