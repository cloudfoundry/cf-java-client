/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.v3.mapper;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.CloudFoundryClient;
import org.immutables.value.Value;
import org.cloudfoundry.client.v3.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v3.spaces.SpaceResource;
import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.ExceptionUtils;
import reactor.core.publisher.Mono;
import java.util.NoSuchElementException;

public final class MapperUtils {

    /**
     * Retrieves space id by given space name
     */
    public static Mono<String> getSpaceIdByName(CloudFoundryClient cloudFoundryClient, String organizationId,
            String spaceName) {
        return getSpace(cloudFoundryClient, organizationId, spaceName).flatMap(space -> Mono.just(space.getId()));
    }

    /**
     * Retrieves domainb id by given domain name
     */
    public static Mono<String> getDomainIdByName(CloudFoundryClient cloudFoundryClient, String organizationId,
            String domainName) {
        return null; // TODO
    }

    private static Mono<SpaceResource> getSpace(CloudFoundryClient cloudFoundryClient, String organizationId,
            String spaceName) {
        return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.spacesV3()
                .list(ListSpacesRequest.builder()
                        .organizationId(organizationId)
                        .name(spaceName)
                        .page(page)
                        .build()))
                .single()
                .onErrorResume(NoSuchElementException.class,
                        t -> ExceptionUtils.illegalArgument(
                                "Space %s does not exist",
                                spaceName));

    }

}
