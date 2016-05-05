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

package org.cloudfoundry.spring.client.v2.userprovidedserviceinstances;

import lombok.ToString;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.GetUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.GetUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceServiceBindingsResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstances;
import org.cloudfoundry.reactor.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;

/**
 * The Spring-based implementation of {@link UserProvidedServiceInstances}
 */
@ToString(callSuper = true)
public final class SpringUserProvidedServiceInstances extends AbstractSpringOperations implements UserProvidedServiceInstances {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringUserProvidedServiceInstances(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CreateUserProvidedServiceInstanceResponse> create(CreateUserProvidedServiceInstanceRequest request) {
        return post(request, CreateUserProvidedServiceInstanceResponse.class, builder -> builder.pathSegment("v2", "user_provided_service_instances"));
    }

    @Override
    public Mono<Void> delete(DeleteUserProvidedServiceInstanceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "user_provided_service_instances", request.getUserProvidedServiceInstanceId()));
    }

    @Override
    public Mono<GetUserProvidedServiceInstanceResponse> get(GetUserProvidedServiceInstanceRequest request) {
        return get(request, GetUserProvidedServiceInstanceResponse.class, builder -> builder.pathSegment("v2", "user_provided_service_instances", request.getUserProvidedServiceInstanceId()));
    }

    @Override
    public Mono<ListUserProvidedServiceInstancesResponse> list(ListUserProvidedServiceInstancesRequest request) {
        return get(request, ListUserProvidedServiceInstancesResponse.class, builder -> {
            builder.pathSegment("v2", "user_provided_service_instances");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<ListUserProvidedServiceInstanceServiceBindingsResponse> listServiceBindings(ListUserProvidedServiceInstanceServiceBindingsRequest request) {
        return get(request, ListUserProvidedServiceInstanceServiceBindingsResponse.class, builder -> {
            builder.pathSegment("v2", "user_provided_service_instances", request.getUserProvidedServiceInstanceId(), "service_bindings");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<UpdateUserProvidedServiceInstanceResponse> update(UpdateUserProvidedServiceInstanceRequest request) {
        return put(request, UpdateUserProvidedServiceInstanceResponse.class, builder -> builder.pathSegment("v2", "user_provided_service_instances", request.getUserProvidedServiceInstanceId()));
    }

}
