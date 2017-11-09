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

package org.cloudfoundry.operations.networkpolicies;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.applications.ApplicationResource;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.networking.NetworkingClient;
import org.cloudfoundry.networking.v1.policies.ListPoliciesRequest;
import org.cloudfoundry.networking.v1.policies.ListPoliciesResponse;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.PaginationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultNetworkPolicies implements NetworkPolicies {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<NetworkingClient> networkingClient;

    private final Mono<String> spaceId;

    public DefaultNetworkPolicies(Mono<CloudFoundryClient> cloudFoundryClient, Mono<NetworkingClient> networkingClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.networkingClient = networkingClient;
        this.spaceId = spaceId;
    }

    @Override
    public Flux<Policy> list(ListNetworkPoliciesRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.networkingClient, this.spaceId)
            .flatMapMany(function((cloudFoundryClient, networkingClient, spaceId) -> Mono.when(
                getApplications(cloudFoundryClient, spaceId),
                getPolicies(networkingClient)
            )))
            .flatMap(function((applications, policies) -> toPolicy(applications, policies, request)))
            .transform(OperationsLogging.log("List Network Policies"))
            .checkpoint();
    }

    private static Mono<Map<String, String>> getApplications(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListApplications(cloudFoundryClient, spaceId)
            .map(resource -> Tuples.of(resource.getId(), resource.getName()))
            .collectMap(function((id, name) -> id), function((id, name) -> name));
    }

    private static Mono<List<org.cloudfoundry.networking.v1.policies.Policy>> getPolicies(NetworkingClient networkingClient) {
        return requestListNetworkPolicies(networkingClient)
            .map(ListPoliciesResponse::getPolicies);
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
