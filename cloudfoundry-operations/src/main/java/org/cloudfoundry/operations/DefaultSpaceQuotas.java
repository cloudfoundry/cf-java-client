/*
 * Copyright 2013-2015 the original author or authors.
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
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.operations.v2.PageUtils;
import org.reactivestreams.Publisher;
import reactor.fn.BiFunction;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;
import reactor.rx.Streams;

final class DefaultSpaceQuotas extends AbstractOperations implements SpaceQuotas {

    private final CloudFoundryClient cloudFoundryClient;

    DefaultSpaceQuotas(CloudFoundryClient cloudFoundryClient, String organizationId) {
        super(organizationId, null);
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Publisher<SpaceQuota> get(GetSpaceQuotaRequest getSpaceQuotaRequest) {
        return Streams.zip(checkRequestValid(getSpaceQuotaRequest), getTargetedOrganization(), new BiFunction<GetSpaceQuotaRequest, String, Tuple2<GetSpaceQuotaRequest, String>>() {

            @Override
            public Tuple2<GetSpaceQuotaRequest, String> apply(GetSpaceQuotaRequest getSpaceQuotaRequest, String organizationId) {
                return Tuple.of(getSpaceQuotaRequest, organizationId);
            }

        })
                .flatMap(new Function<Tuple2<GetSpaceQuotaRequest, String>, Publisher<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>>>() {

                    @Override
                    public Publisher<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>> apply(final Tuple2<GetSpaceQuotaRequest, String> tuple) {
                        return toSpaceQuotaDefinitionResourceStream(tuple.getT2())
                                .map(new Function<SpaceQuotaDefinitionResource, Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>>() {

                                    @Override
                                    public Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource> apply(SpaceQuotaDefinitionResource spaceQuotaDefinitionResource) {
                                        return Tuple.of(tuple.getT1(), spaceQuotaDefinitionResource);
                                    }

                                });
                    }

                })
                .filter(new Predicate<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>>() {

                    @Override
                    public boolean test(Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource> tuple) {
                        return tuple.getT1().getName().equals(tuple.getT2().getEntity().getName());
                    }

                })
                .map(new Function<Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource>, SpaceQuotaDefinitionResource>() {

                    @Override
                    public SpaceQuotaDefinitionResource apply(Tuple2<GetSpaceQuotaRequest, SpaceQuotaDefinitionResource> tuple) {
                        return tuple.getT2();
                    }

                })
                .map(toSpaceQuota());
    }

    @Override
    public Publisher<SpaceQuota> list() {
        return getTargetedOrganization()
                .flatMap(new Function<String, Publisher<SpaceQuotaDefinitionResource>>() {

                    @Override
                    public Publisher<SpaceQuotaDefinitionResource> apply(String organizationId) {
                        return toSpaceQuotaDefinitionResourceStream(organizationId);
                    }

                })
                .map(toSpaceQuota());
    }

    private Function<SpaceQuotaDefinitionResource, SpaceQuota> toSpaceQuota() {
        return new Function<SpaceQuotaDefinitionResource, SpaceQuota>() {

            @Override
            public SpaceQuota apply(SpaceQuotaDefinitionResource spaceQuotaDefinitionResource) {
                SpaceQuotaDefinitionEntity entity = spaceQuotaDefinitionResource.getEntity();
                Resource.Metadata metadata = spaceQuotaDefinitionResource.getMetadata();

                return SpaceQuota.builder()
                        .id(metadata.getId())
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

    private Stream<SpaceQuotaDefinitionResource> toSpaceQuotaDefinitionResourceStream(final String organizationId) {
        return PageUtils.resourceStream(new Function<Integer, Publisher<ListOrganizationSpaceQuotaDefinitionsResponse>>() {

            @Override
            public Publisher<ListOrganizationSpaceQuotaDefinitionsResponse> apply(Integer page) {
                ListOrganizationSpaceQuotaDefinitionsRequest request = ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                        .id(organizationId)
                        .page(page)
                        .build();

                return DefaultSpaceQuotas.this.cloudFoundryClient.organizations().listSpaceQuotaDefinitions(request);
            }

        });
    }

}
