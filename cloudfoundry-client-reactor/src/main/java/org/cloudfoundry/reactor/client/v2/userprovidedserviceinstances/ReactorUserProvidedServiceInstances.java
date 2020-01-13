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

package org.cloudfoundry.reactor.client.v2.userprovidedserviceinstances;

import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.GetUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.GetUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceRoutesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceRoutesResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceServiceBindingsResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.RemoveUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstances;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link UserProvidedServiceInstances}
 */
public final class ReactorUserProvidedServiceInstances extends AbstractClientV2Operations implements UserProvidedServiceInstances {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorUserProvidedServiceInstances(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<AssociateUserProvidedServiceInstanceRouteResponse> associateRoute(AssociateUserProvidedServiceInstanceRouteRequest request) {
        return put(request, AssociateUserProvidedServiceInstanceRouteResponse.class,
            builder -> builder.pathSegment("user_provided_service_instances", request.getUserProvidedServiceInstanceId(), "routes", request.getRouteId()))
            .checkpoint();
    }

    @Override
    public Mono<CreateUserProvidedServiceInstanceResponse> create(CreateUserProvidedServiceInstanceRequest request) {
        return post(request, CreateUserProvidedServiceInstanceResponse.class, builder -> builder.pathSegment("user_provided_service_instances"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteUserProvidedServiceInstanceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("user_provided_service_instances", request.getUserProvidedServiceInstanceId()))
            .checkpoint();
    }

    @Override
    public Mono<GetUserProvidedServiceInstanceResponse> get(GetUserProvidedServiceInstanceRequest request) {
        return get(request, GetUserProvidedServiceInstanceResponse.class, builder -> builder.pathSegment("user_provided_service_instances", request.getUserProvidedServiceInstanceId()))
            .checkpoint();
    }

    @Override
    public Mono<ListUserProvidedServiceInstancesResponse> list(ListUserProvidedServiceInstancesRequest request) {
        return get(request, ListUserProvidedServiceInstancesResponse.class, builder -> builder.pathSegment("user_provided_service_instances"))
            .checkpoint();
    }

    @Override
    public Mono<ListUserProvidedServiceInstanceRoutesResponse> listRoutes(ListUserProvidedServiceInstanceRoutesRequest request) {
        return get(request, ListUserProvidedServiceInstanceRoutesResponse.class, builder -> builder.pathSegment("user_provided_service_instances", request.getUserProvidedServiceInstanceId(),
            "routes"))
            .checkpoint();
    }

    @Override
    public Mono<ListUserProvidedServiceInstanceServiceBindingsResponse> listServiceBindings(ListUserProvidedServiceInstanceServiceBindingsRequest request) {
        return get(request, ListUserProvidedServiceInstanceServiceBindingsResponse.class,
            builder -> builder.pathSegment("user_provided_service_instances", request.getUserProvidedServiceInstanceId(), "service_bindings"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeRoute(RemoveUserProvidedServiceInstanceRouteRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("user_provided_service_instances", request.getUserProvidedServiceInstanceId(), "routes", request.getRouteId()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateUserProvidedServiceInstanceResponse> update(UpdateUserProvidedServiceInstanceRequest request) {
        return put(request, UpdateUserProvidedServiceInstanceResponse.class, builder -> builder.pathSegment("user_provided_service_instances", request.getUserProvidedServiceInstanceId()))
            .checkpoint();
    }

}
