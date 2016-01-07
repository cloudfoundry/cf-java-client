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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.Mono;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;

import java.util.NoSuchElementException;

final class DefaultSpaceQuotas implements SpaceQuotas {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> organizationId;

    DefaultSpaceQuotas(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId) {
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
                .otherwise(convertException(String.format("Space Quota %s does not exist", getSpaceQuotaRequest.getName())));
    }

    @Override
    public Publisher<SpaceQuota> list() {
        return this.organizationId
                .flatMap(requestSpaceQuotaDefinitions(this.cloudFoundryClient))
                .map(toSpaceQuota());
    }

    private static Function<SpaceQuotaDefinitionResource, Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>> combineRequestWithDefinition(final Tuple2<GetSpaceQuotaRequest, String> tuple) {
        return new Function<SpaceQuotaDefinitionResource, Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>>() {

            @Override
            public Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource> apply(SpaceQuotaDefinitionResource spaceQuotaDefinitionResource) {
                return Tuple.of(tuple.t1, spaceQuotaDefinitionResource);
            }

        };
    }

    private static Function<Throwable, Mono<SpaceQuota>> convertException(final String message) {
        return new Function<Throwable, Mono<SpaceQuota>>() {

            @Override
            public Mono<SpaceQuota> apply(Throwable throwable) {
                if (throwable instanceof NoSuchElementException) {

                    return Mono.error(new IllegalArgumentException(message, throwable));
                } else {
                    return Mono.error(throwable);
                }
            }

        };
    }

    private static Predicate<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>> equalRequestAndDefinitionName() {
        return new Predicate<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>>() {

            @Override
            public boolean test(Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource> tuple) {
                GetSpaceQuotaRequest request = tuple.t1;
                SpaceQuotaDefinitionResource resource = tuple.t2;

                return request.getName().equals(Resources.getEntity(resource).getName());
            }

        };
    }

    private static Function<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>, SpaceQuotaDefinitionResource> extractQuotaDefinition() {
        return new Function<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>, SpaceQuotaDefinitionResource>() {

            @Override
            public SpaceQuotaDefinitionResource apply(Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource> tuple) {
                return tuple.t2;
            }

        };
    }

    private static Stream<SpaceQuotaDefinitionResource> fromSpaceQuotaDefinitionResourceStream(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return Paginated
                .requestResources(new Function<Integer, Mono<ListOrganizationSpaceQuotaDefinitionsResponse>>() {

                    @Override
                    public Mono<ListOrganizationSpaceQuotaDefinitionsResponse> apply(Integer page) {
                        ListOrganizationSpaceQuotaDefinitionsRequest request = ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                                .id(organizationId)
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

    private static Function<Tuple2<GetSpaceQuotaRequest, String>, Stream<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>>> requestSpaceQuotaDefinitionsWithContext(
            final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<GetSpaceQuotaRequest, String>, Stream<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>>>() {

            @Override
            public Stream<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>> apply(final Tuple2<GetSpaceQuotaRequest, String> tuple) {
                String organizationId = tuple.t2;

                return fromSpaceQuotaDefinitionResourceStream(cloudFoundryClient, organizationId)
                        .map(combineRequestWithDefinition(tuple));
            }

        };
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
