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

package org.cloudfoundry.operations.serviceadmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultServiceAdmin implements ServiceAdmin {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<String> spaceId;

    public DefaultServiceAdmin(Mono<CloudFoundryClient> cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Void> create(CreateServiceBrokerRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> requestCreateServiceBroker(cloudFoundryClient, request.getName(), request.getUrl(), request.getUsername(), request.getPassword(),
                request.getSpaceScoped(), spaceId)))
            .then()
            .transform(OperationsLogging.log("Create Service Broker"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteServiceBrokerRequest request) {
        return this.cloudFoundryClient
            .then(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                getServiceBrokerId(cloudFoundryClient, request.getName())
            ))
            .then(function(DefaultServiceAdmin::requestDeleteServiceBroker))
            .transform(OperationsLogging.log("Delete Service Broker"))
            .checkpoint();
    }

    @Override
    public Flux<ServiceBroker> list() {
        return this.cloudFoundryClient
            .flatMap(DefaultServiceAdmin::requestServiceBrokers)
            .map(this::toServiceBroker)
            .transform(OperationsLogging.log("List Service Brokers"))
            .checkpoint();
    }

    private static Mono<ServiceBrokerResource> getServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        return requestListServiceBrokers(cloudFoundryClient, serviceBrokerName)
            .single()
            .otherwise(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service Broker %s does not exist", serviceBrokerName));
    }

    private static Mono<String> getServiceBrokerId(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        return getServiceBroker(cloudFoundryClient, serviceBrokerName)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateServiceBrokerResponse> requestCreateServiceBroker(CloudFoundryClient cloudFoundryClient, String name, String url, String username, String password,
                                                                                Boolean isSpaceScoped, String spaceId) {
        return cloudFoundryClient.serviceBrokers()
            .create(org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest.builder()
                .name(name)
                .brokerUrl(url)
                .authenticationUsername(username)
                .authenticationPassword(password)
                .spaceId(Optional.ofNullable(isSpaceScoped).orElse(false) ? spaceId : null)
                .build());
    }

    private static Mono<Void> requestDeleteServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return cloudFoundryClient.serviceBrokers()
            .delete(org.cloudfoundry.client.v2.servicebrokers.DeleteServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerId)
                .build());
    }

    private static Flux<ServiceBrokerResource> requestListServiceBrokers(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceBrokers()
                .list(ListServiceBrokersRequest.builder()
                    .name(serviceBrokerName)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceBrokerResource> requestServiceBrokers(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceBrokers()
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
