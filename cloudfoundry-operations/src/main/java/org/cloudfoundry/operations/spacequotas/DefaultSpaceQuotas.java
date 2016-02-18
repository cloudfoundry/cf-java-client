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

package org.cloudfoundry.operations.spacequotas;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import org.cloudfoundry.util.tuple.Function2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.rx.Stream;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultSpaceQuotas implements SpaceQuotas {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> organizationId;

    public DefaultSpaceQuotas(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
    }

    @Override
    public Mono<SpaceQuota> get(GetSpaceQuotaRequest getSpaceQuotaRequest) {
        return ValidationUtils
            .validate(getSpaceQuotaRequest)
            .and(this.organizationId)
            .then(function(new Function2<GetSpaceQuotaRequest, String, Mono<SpaceQuotaDefinitionResource>>() {

                @Override
                public Mono<SpaceQuotaDefinitionResource> apply(GetSpaceQuotaRequest request, String organizationId) {
                    return getSpaceQuotaDefinition(DefaultSpaceQuotas.this.cloudFoundryClient, organizationId, request.getName());
                }

            }))
            .map(new Function<SpaceQuotaDefinitionResource, SpaceQuota>() {

                @Override
                public SpaceQuota apply(SpaceQuotaDefinitionResource resource) {
                    return toSpaceQuota(resource);
                }

            });
    }

    @Override
    public Publisher<SpaceQuota> list() {
        return this.organizationId
            .flatMap(new Function<String, Stream<SpaceQuotaDefinitionResource>>() {

                @Override
                public Stream<SpaceQuotaDefinitionResource> apply(String organizationId) {
                    return requestSpaceQuotaDefinitions(DefaultSpaceQuotas.this.cloudFoundryClient, organizationId);
                }

            })
            .map(new Function<SpaceQuotaDefinitionResource, SpaceQuota>() {

                @Override
                public SpaceQuota apply(SpaceQuotaDefinitionResource resource) {
                    return toSpaceQuota(resource);
                }

            });
    }

    private static Mono<SpaceQuotaDefinitionResource> getSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, String organizationId, final String name) {
        return requestSpaceQuotaDefinitions(cloudFoundryClient, organizationId)
            .filter(new Predicate<SpaceQuotaDefinitionResource>() {

                @Override
                public boolean test(SpaceQuotaDefinitionResource resource) {
                    return name.equals(ResourceUtils.getEntity(resource).getName());
                }

            })
            .single()
            .otherwise(ExceptionUtils.<SpaceQuotaDefinitionResource>convert("Space Quota %s does not exist", name));
    }

    private static Stream<SpaceQuotaDefinitionResource> requestSpaceQuotaDefinitions(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationSpaceQuotaDefinitionsResponse>>() {

                @Override
                public Mono<ListOrganizationSpaceQuotaDefinitionsResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                            .organizationId(organizationId)
                            .page(page)
                            .build());
                }

            });
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
