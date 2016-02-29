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

package org.cloudfoundry.spring.uaa.identityzonemanagement;

import lombok.ToString;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.uaa.identityzonemanagement.CreateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzonemanagement.CreateIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzonemanagement.GetIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzonemanagement.GetIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzonemanagement.IdentityZoneManagement;
import org.cloudfoundry.uaa.identityzonemanagement.ListIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzonemanagement.ListIdentityZoneResponse;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;

import java.net.URI;

/**
 * The Spring-based implementation of {@link IdentityZoneManagement}
 */
@ToString(callSuper = true)
public final class SpringIdentityZoneManagement extends AbstractSpringOperations implements IdentityZoneManagement {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringIdentityZoneManagement(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CreateIdentityZoneResponse> create(CreateIdentityZoneRequest request) {
        return post(request, CreateIdentityZoneResponse.class, builder -> builder.pathSegment("identity-zones"));
    }

    @Override
    public Mono<GetIdentityZoneResponse> get(final GetIdentityZoneRequest request) {
        return get(request, GetIdentityZoneResponse.class, builder -> builder.pathSegment("identity-zones", request.getIdentityZoneId()));
    }

    @Override
    public Mono<ListIdentityZoneResponse> list(final ListIdentityZoneRequest request) {
        return get(request, ListIdentityZoneResponse.class, builder -> builder.pathSegment("identity-zones"));
    }

}
