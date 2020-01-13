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

package org.cloudfoundry.operations.spaces;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Spaces Operations API
 */
public interface Spaces {

    /**
     * Allow SSH for a specific space
     *
     * @param request the allow space ssh request
     * @return a completion indicator
     */
    Mono<Void> allowSsh(AllowSpaceSshRequest request);

    /**
     * Creates a new space
     *
     * @param request the create space request
     * @return a completion indicator
     */
    Mono<Void> create(CreateSpaceRequest request);

    /**
     * Deletes a specific space.
     *
     * @param request the delete space request
     * @return a completion indicator
     */
    Mono<Void> delete(DeleteSpaceRequest request);

    /**
     * Disallow SSH for a specific space
     *
     * @param request the disallow space ssh request
     * @return a completion indicator
     */
    Mono<Void> disallowSsh(DisallowSpaceSshRequest request);

    /**
     * Gets space information
     *
     * @param request details of space information required
     * @return the space information
     */
    Mono<SpaceDetail> get(GetSpaceRequest request);

    /**
     * Lists the spaces
     *
     * @return the spaces
     */
    Flux<SpaceSummary> list();

    /**
     * Renames a specific space
     *
     * @param request the rename space request
     * @return completion indicator
     */
    Mono<Void> rename(RenameSpaceRequest request);

    /**
     * Check if SSH is allowed for a specific space
     *
     * @param request the check space ssh allowed request
     * @return Boolean is ssh allowed on the space
     */
    Mono<Boolean> sshAllowed(SpaceSshAllowedRequest request);

}
