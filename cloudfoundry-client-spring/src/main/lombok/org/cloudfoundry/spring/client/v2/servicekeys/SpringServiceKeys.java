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

package org.cloudfoundry.spring.client.v2.servicekeys;

import lombok.ToString;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstances;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.DeleteServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.GetServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.GetServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.ListServiceKeysRequest;
import org.cloudfoundry.client.v2.servicekeys.ListServiceKeysResponse;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeys;
import org.cloudfoundry.reactor.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;

/**
 * The Spring-based implementation of {@link ServiceInstances}
 */
@ToString(callSuper = true)
public final class SpringServiceKeys extends AbstractSpringOperations implements ServiceKeys {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringServiceKeys(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CreateServiceKeyResponse> create(CreateServiceKeyRequest request) {
        return post(request, CreateServiceKeyResponse.class, builder -> builder.pathSegment("v2", "service_keys"));
    }

    @Override
    public Mono<Void> delete(DeleteServiceKeyRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "service_keys", request.getServiceKeyId()));
    }

    @Override
    public Mono<GetServiceKeyResponse> get(GetServiceKeyRequest request) {
        return get(request, GetServiceKeyResponse.class, builder -> builder.pathSegment("v2", "service_keys", request.getServiceKeyId()));
    }

    @Override
    public Mono<ListServiceKeysResponse> list(ListServiceKeysRequest request) {
        return get(request, ListServiceKeysResponse.class, builder -> {
            builder.pathSegment("v2", "service_keys");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

}
