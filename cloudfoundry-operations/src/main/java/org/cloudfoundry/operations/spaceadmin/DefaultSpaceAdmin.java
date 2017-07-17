/*
 * Copyright 2013-2017 the original author or authors.
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

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultSpaceAdmin implements SpaceAdmin {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<String> organizationId;

    public DefaultSpaceAdmin(Mono<CloudFoundryClient> cloudFoundryClient, Mono<String> organizationId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
    }

    @Override
    public Mono<SpaceQuota> get(GetSpaceQuotaRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> getSpaceQuotaDefinition(cloudFoundryClient, organizationId, request.getName())))
            .map(DefaultSpaceAdmin::toSpaceQuota)
            .transform(OperationsLogging.log("Get Space Quota"))
            .checkpoint();
    }

    @Override
    public Flux<SpaceQuota> listQuotas() {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMapMany(function(DefaultSpaceAdmin::requestSpaceQuotaDefinitions))
            .map(DefaultSpaceAdmin::toSpaceQuota)
            .transform(OperationsLogging.log("List Space Quota"))
            .checkpoint();
    }

    private static Mono<SpaceQuotaDefinitionResource> getSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, String organizationId, String name) {
        return requestSpaceQuotaDefinitions(cloudFoundryClient, organizationId)
            .filter(resource -> name.equals(ResourceUtils.getEntity(resource).getName()))
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Space Quota %s does not exist", name));
    }

    private static Flux<SpaceQuotaDefinitionResource> requestSpaceQuotaDefinitions(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static SpaceQuota toSpaceQuota(SpaceQuotaDefinitionResource resource) {
        SpaceQuotaDefinitionEntity entity = ResourceUtils.getEntity(resource);

        return SpaceQuota.builder()
            .id(ResourceUtils.getId(resource))
            .instanceMemoryLimit(entity.getInstanceMemoryLimit())
            .name(entity.getName())
            .organizationId(entity.getOrganizationId())
            .paidServicePlans(entity.getNonBasicServicesAllowed())
            .totalMemoryLimit(entity.getMemoryLimit())
            .totalRoutes(entity.getTotalRoutes())
            .totalServiceInstances(entity.getTotalServices())
            .build();
    }

}