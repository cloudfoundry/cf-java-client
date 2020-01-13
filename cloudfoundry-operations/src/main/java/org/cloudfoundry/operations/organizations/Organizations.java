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

package org.cloudfoundry.operations.organizations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Organizations operations API
 */
public interface Organizations {

    /**
     * Creates a new organization.
     *
     * @param request the create organization request
     * @return completion indicator
     */
    Mono<Void> create(CreateOrganizationRequest request);

    /**
     * Deletes a specific organization
     *
     * @param request the delete organization request
     * @return completion indicator
     */
    Mono<Void> delete(DeleteOrganizationRequest request);

    /**
     * Gets an Organization's Information
     *
     * @param request the organization info request
     * @return the organizations info
     */
    Mono<OrganizationDetail> get(OrganizationInfoRequest request);

    /**
     * Lists the organizations
     *
     * @return the organizations
     */
    Flux<OrganizationSummary> list();

    /**
     * Renames a specific organization
     *
     * @param request the rename organization request
     * @return completion indicator
     */
    Mono<Void> rename(RenameOrganizationRequest request);

}
