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

package org.cloudfoundry.operations.networkpolicies;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.applications.ApplicationResource;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.networking.NetworkingClient;
import org.cloudfoundry.networking.v1.policies.CreatePoliciesRequest;
import org.cloudfoundry.networking.v1.policies.DeletePoliciesRequest;
import org.cloudfoundry.networking.v1.policies.Destination;
import org.cloudfoundry.networking.v1.policies.ListPoliciesRequest;
import org.cloudfoundry.networking.v1.policies.ListPoliciesResponse;
import org.cloudfoundry.networking.v1.policies.Ports;
import org.cloudfoundry.networking.v1.policies.Source;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.PaginationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultNetworkPolicies implements NetworkPolicies {

    private static final Integer DEFAULT_PORT = 8080;

    private static final String DEFAULT_PROTOCOL = "tcp";

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<NetworkingClient> networkingClient;

    private final Mono<String> spaceId;

    public DefaultNetworkPolicies(Mono<CloudFoundryClient> cloudFoundryClient, Mono<NetworkingClient> networkingClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.networkingClient = networkingClient;
        this.spaceId = spaceId;
    }

    @Override
    public Flux<Void> add(AddNetworkPolicyRequest request) {
        return Mono
            .zip(this.cloudFoundryClient, this.networkingClient, this.spaceId)
            .flatMapMany(function((cloudFoundryClient, networkingClient, spaceId) -> Mono.zip(
                Mono.just(networkingClient),
                getApplicationsByName(cloudFoundryClient, spaceId)
            )))
            .flatMap(function((networkingClient, applications) -> requestAddPolicy(networkingClient, applications, request)))
            .transform(OperationsLogging.log("Add Network Policy"))
            .checkpoint();
    }

    @Override
    public Flux<Policy> list(ListNetworkPoliciesRequest request) {
        return Mono
            .zip(this.cloudFoundryClient, this.networkingClient, this.spaceId)
            .flatMapMany(function((cloudFoundryClient, networkingClient, spaceId) -> Mono.zip(
                getApplicationsById(cloudFoundryClient, spaceId),
                getPolicies(networkingClient)
            )))
            .flatMap(function((applications, policies) -> toPolicy(applications, policies, request)))
            .transform(OperationsLogging.log("List Network Policies"))
            .checkpoint();
    }

    @Override
    public Flux<Void> remove(RemoveNetworkPolicyRequest request) {
        return Mono
            .zip(this.cloudFoundryClient, this.networkingClient, this.spaceId)
            .flatMapMany(function((cloudFoundryClient, networkingClient, spaceId) -> Mono.zip(
                Mono.just(networkingClient),
                getApplicationsByName(cloudFoundryClient, spaceId)
            )))
            .flatMap(function((networkingClient, applications) -> requestRemovePolicy(networkingClient, applications, request)))
            .transform(OperationsLogging.log("Remove Network Policy"))
            .checkpoint();
    }

    private static Mono<Map<String, String>> getApplicationsById(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListApplications(cloudFoundryClient, spaceId)
            .map(resource -> Tuples.of(resource.getId(), resource.getName()))
            .collectMap(function((id, name) -> id), function((id, name) -> name));
    }

    private static Mono<Map<String, String>> getApplicationsByName(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListApplications(cloudFoundryClient, spaceId)
            .map(resource -> Tuples.of(resource.getId(), resource.getName()))
            .collectMap(function((id, name) -> name), function((id, name) -> id));
    }

    private static Mono<List<org.cloudfoundry.networking.v1.policies.Policy>> getPolicies(NetworkingClient networkingClient) {
        return requestListNetworkPolicies(networkingClient)
            .map(ListPoliciesResponse::getPolicies);
    }

    private static Mono<Void> requestAddPolicy(NetworkingClient networkingClient, Map<String, String> applications, AddNetworkPolicyRequest request) {
        return networkingClient.policies()
            .create(CreatePoliciesRequest.builder()
                .policy(org.cloudfoundry.networking.v1.policies.Policy.builder()
                    .destination(Destination.builder()
                        .id(applications.get(request.getDestination()))
                        .ports(Ports.builder()
                            .end(Optional.ofNullable(request.getEndPort()).orElse(request.getStartPort() != null ? request.getStartPort() : DEFAULT_PORT))
                            .start(Optional.ofNullable(request.getStartPort()).orElse(request.getEndPort() != null ? request.getEndPort() : DEFAULT_PORT))
                            .build())
                        .protocol(Optional.ofNullable(request.getProtocol()).orElse(DEFAULT_PROTOCOL))
                        .build())
                    .source(Source.builder()
                        .id(applications.get(request.getSource()))
                        .build())
                    .build())
                .build());
    }

    private static Flux<ApplicationResource> requestListApplications(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.applicationsV3()
            .list(ListApplicationsRequest.builder()
                .page(page)
                .spaceId(spaceId)
                .build()));
    }

    private static Mono<ListPoliciesResponse> requestListNetworkPolicies(NetworkingClient networkingClient) {
        return networkingClient.policies()
            .list(ListPoliciesRequest.builder()
                .build());
    }

    private static Mono<Void> requestRemovePolicy(NetworkingClient networkingClient, Map<String, String> applications, RemoveNetworkPolicyRequest request) {
        return networkingClient.policies()
            .delete(DeletePoliciesRequest.builder()
                .policy(org.cloudfoundry.networking.v1.policies.Policy.builder()
                    .destination(Destination.builder()
                        .id(applications.get(request.getDestination()))
                        .ports(Ports.builder()
                            .end(Optional.ofNullable(request.getEndPort()).orElse(request.getStartPort()))
                            .start(request.getStartPort())
                            .build())
                        .protocol(request.getProtocol())
                        .build())
                    .source(Source.builder()
                        .id(applications.get(request.getSource()))
                        .build())
                    .build())
                .build());
    }

    private static Flux<Policy> toPolicy(Map<String, String> applications, List<org.cloudfoundry.networking.v1.policies.Policy> policies, ListNetworkPoliciesRequest request) {
        return Flux.fromIterable(policies)
            .filter(policy -> null != applications.get(policy.getSource().getId()) && null != applications.get(policy.getDestination().getId()))
            .filter(policy -> request.getSource() == null || request.getSource().equals(applications.get(policy.getSource().getId())))
            .map(policy -> Policy.builder()
                .destination(applications.get(policy.getDestination().getId()))
                .endPort(policy.getDestination().getPorts().getEnd())
                .startPort(policy.getDestination().getPorts().getStart())
                .protocol(policy.getDestination().getProtocol())
                .source(applications.get(policy.getSource().getId()))
                .build());
    }

}
