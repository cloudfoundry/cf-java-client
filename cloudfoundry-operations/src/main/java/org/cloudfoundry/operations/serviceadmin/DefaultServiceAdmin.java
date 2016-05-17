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

package org.cloudfoundry.operations.serviceadmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultServiceAdmin implements ServiceAdmin {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> spaceId;

    public DefaultServiceAdmin(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Void> create(CreateServiceBrokerRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.spaceId)
            .then(function((validRequest, spaceId) -> requestCreateServiceBroker(this.cloudFoundryClient, validRequest.getName(), validRequest.getUrl(), validRequest.getUsername(),
                validRequest.getPassword(), Optional.ofNullable(validRequest.getIsSpaceScoped()).orElse(false), spaceId)))
            .then();
    }

    @Override
    public Flux<ServiceBroker> listServiceBrokers() {
        return requestServiceBrokers(this.cloudFoundryClient)
            .map(this::toServiceBroker);
    }

    private static Mono<CreateServiceBrokerResponse> requestCreateServiceBroker(CloudFoundryClient cloudFoundryClient, String name, String url, String username, String password,
                                                                                Boolean isSpaceScoped, String spaceId) {
        return cloudFoundryClient.serviceBrokers()
            .create(org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest.builder()
                .name(name)
                .brokerUrl(url)
                .authenticationUsername(username)
                .authenticationPassword(password)
                .spaceId(isSpaceScoped ? spaceId : null)
                .build());
    }

    private static Flux<ServiceBrokerResource> requestServiceBrokers(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.serviceBrokers()
                .list(ListServiceBrokersRequest.builder()
                    .page(page)
                    .build()));
    }

    private ServiceBroker toServiceBroker(ServiceBrokerResource resource) {
        ServiceBrokerEntity entity = ResourceUtils.getEntity(resource);

        return ServiceBroker.builder()
            .id(ResourceUtils.getId(resource))
            .name(entity.getName())
            .url(entity.getBrokerUrl())
            .build();
    }

}
