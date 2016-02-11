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

package org.cloudfoundry.client.spring.v2.services;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.services.DeleteServiceRequest;
import org.cloudfoundry.client.v2.services.DeleteServiceResponse;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceResponse;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ListServicesResponse;
import org.cloudfoundry.client.v2.services.Services;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Services}
 */
@ToString(callSuper = true)
public final class SpringServices extends AbstractSpringOperations implements Services {


    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringServices(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<DeleteServiceResponse> delete(final DeleteServiceRequest request) {
        return delete(request, DeleteServiceResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "services", request.getServiceId());
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<GetServiceResponse> get(final GetServiceRequest request) {
        return get(request, GetServiceResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "services", request.getServiceId());
            }

        });
    }

    @Override
    public Mono<ListServicesResponse> list(final ListServicesRequest request) {
        return get(request, ListServicesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "services");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

}
