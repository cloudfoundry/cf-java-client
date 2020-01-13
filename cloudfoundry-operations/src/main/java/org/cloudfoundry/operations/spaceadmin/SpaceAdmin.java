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

package org.cloudfoundry.operations.spaceadmin;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Space Admin API
 */
public interface SpaceAdmin {

    /**
     * Gets a space quota
     *
     * @param request the Get Space Quota request
     * @return the space quota
     */
    Mono<SpaceQuota> get(GetSpaceQuotaRequest request);

    /**
     * Lists the space quotas
     *
     * @return the space quotas
     */
    Flux<SpaceQuota> listQuotas();

}