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

package org.cloudfoundry.operations.organizationadmin;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Organization Admin Operations API
 */
public interface OrganizationAdmin {

    /**
     * Create an organization quota
     *
     * @param request The Create Quota request
     * @return the organization quota
     */
    Mono<OrganizationQuota> createQuota(CreateQuotaRequest request);

    /**
     * Delete an organization quota
     *
     * @param request The Delete Quota request
     * @return a completion indicator
     */
    Mono<Void> deleteQuota(DeleteQuotaRequest request);

    /**
     * Get the organization quota
     *
     * @param request The Get Quota request
     * @return the organization quota
     */
    Mono<OrganizationQuota> getQuota(GetQuotaRequest request);

    /**
     * Lists the organization quotas
     *
     * @return the organization quotas
     */
    Flux<OrganizationQuota> listQuotas();

    /**
     * Set the organization quota
     *
     * @param request The Set Quota request
     * @return a completion indicator
     */
    Mono<Void> setQuota(SetQuotaRequest request);

    /**
     * Update an organization quota
     *
     * @param request The Update Quota request
     * @return the organization quota
     */
    Mono<OrganizationQuota> updateQuota(UpdateQuotaRequest request);

}
