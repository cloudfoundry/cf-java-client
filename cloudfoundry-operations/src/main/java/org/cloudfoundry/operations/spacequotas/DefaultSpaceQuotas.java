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
import org.cloudfoundry.operations.util.Exceptions;
import org.cloudfoundry.operations.util.Function2;
import org.cloudfoundry.operations.util.Predicate2;
import org.cloudfoundry.operations.util.Tuples;
import org.cloudfoundry.operations.util.Validators;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;

public final class DefaultSpaceQuotas implements SpaceQuotas {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> organizationId;

    public DefaultSpaceQuotas(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
    }

    @Override
    public Mono<SpaceQuota> get(final GetSpaceQuotaRequest getSpaceQuotaRequest) {
        return Stream
                .from(Validators
                        .validate(getSpaceQuotaRequest)
                        .and(this.organizationId)
                        .flatMap(requestSpaceQuotaDefinitionsWithContext(this.cloudFoundryClient)))
                .filter(equalRequestAndDefinitionName())
                .single()
                .map(extractQuotaDefinition())
                .map(toSpaceQuota())
                .otherwise(Exceptions.<SpaceQuota>convert(String.format("Space Quota %s does not exist", getSpaceQuotaRequest.getName())));
    }

    @Override
    public Publisher<SpaceQuota> list() {
        return this.organizationId
                .flatMap(requestSpaceQuotaDefinitions(this.cloudFoundryClient))
                .map(toSpaceQuota());
    }

    private static Predicate<Tuple2<SpaceQuotaDefinitionResource, GetSpaceQuotaRequest>> equalRequestAndDefinitionName() {
        return Tuples.predicate(new Predicate2<SpaceQuotaDefinitionResource, GetSpaceQuotaRequest>() {

            @Override
            public boolean test(SpaceQuotaDefinitionResource resource, GetSpaceQuotaRequest request) {
                return request.getName().equals(Resources.getEntity(resource).getName());
            }

        });
    }

    private static Function<Tuple2<SpaceQuotaDefinitionResource, GetSpaceQuotaRequest>, SpaceQuotaDefinitionResource> extractQuotaDefinition() {
        return Tuples.function(new Function2<SpaceQuotaDefinitionResource, GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>() {

            @Override
            public SpaceQuotaDefinitionResource apply(SpaceQuotaDefinitionResource spaceQuotaDefinitionResource, GetSpaceQuotaRequest getSpaceQuotaRequest) {
                return spaceQuotaDefinitionResource;
            }

        });
    }

    private static Stream<SpaceQuotaDefinitionResource> fromSpaceQuotaDefinitionResourceStream(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return Paginated
                .requestResources(new Function<Integer, Mono<ListOrganizationSpaceQuotaDefinitionsResponse>>() {

                    @Override
                    public Mono<ListOrganizationSpaceQuotaDefinitionsResponse> apply(Integer page) {
                        ListOrganizationSpaceQuotaDefinitionsRequest request = ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                                .organizationId(organizationId)
                                .page(page)
                                .build();

                        return cloudFoundryClient.organizations().listSpaceQuotaDefinitions(request);
                    }

                });
    }

    private static Function<String, Stream<SpaceQuotaDefinitionResource>> requestSpaceQuotaDefinitions(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Stream<SpaceQuotaDefinitionResource>>() {

            @Override
            public Stream<SpaceQuotaDefinitionResource> apply(String organizationId) {
                return fromSpaceQuotaDefinitionResourceStream(cloudFoundryClient, organizationId);
            }

        };
    }

    private static Function<Tuple2<GetSpaceQuotaRequest, String>, Stream<Tuple2<SpaceQuotaDefinitionResource, GetSpaceQuotaRequest>>> requestSpaceQuotaDefinitionsWithContext(
            final CloudFoundryClient cloudFoundryClient) {

        return Tuples.function(new Function2<GetSpaceQuotaRequest, String, Stream<Tuple2<SpaceQuotaDefinitionResource, GetSpaceQuotaRequest>>>() {

            @Override
            public Stream<Tuple2<SpaceQuotaDefinitionResource, GetSpaceQuotaRequest>> apply(GetSpaceQuotaRequest request, String organizationId) {
                return fromSpaceQuotaDefinitionResourceStream(cloudFoundryClient, organizationId)
                        .zipWith(Mono.just(request));
            }

        });
    }

    private static Function<SpaceQuotaDefinitionResource, SpaceQuota> toSpaceQuota() {
        return new Function<SpaceQuotaDefinitionResource, SpaceQuota>() {

            @Override
            public SpaceQuota apply(SpaceQuotaDefinitionResource resource) {
                SpaceQuotaDefinitionEntity entity = Resources.getEntity(resource);

                return SpaceQuota.builder()
                        .id(Resources.getId(resource))
                        .instanceMemoryLimit(entity.getInstanceMemoryLimit())
                        .name(entity.getName())
                        .organizationId(entity.getOrganizationId())
                        .paidServicePlans(entity.getNonBasicServicesAllowed())
                        .totalMemoryLimit(entity.getMemoryLimit())
                        .totalRoutes(entity.getTotalRoutes())
                        .totalServiceInstances(entity.getTotalServices())
                        .build();
            }

        };
    }

}
