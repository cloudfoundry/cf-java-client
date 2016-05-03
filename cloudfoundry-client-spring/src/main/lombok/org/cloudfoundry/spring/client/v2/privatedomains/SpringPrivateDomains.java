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

package org.cloudfoundry.spring.client.v2.privatedomains;

import lombok.ToString;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomains;
import org.cloudfoundry.reactor.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;

/**
 * The Spring-based implementation of {@link PrivateDomains}
 */
@ToString(callSuper = true)
public final class SpringPrivateDomains extends AbstractSpringOperations implements PrivateDomains {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringPrivateDomains(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CreatePrivateDomainResponse> create(CreatePrivateDomainRequest request) {
        return post(request, CreatePrivateDomainResponse.class, builder -> builder.pathSegment("v2", "private_domains"));
    }

    @Override
    public Mono<DeletePrivateDomainResponse> delete(DeletePrivateDomainRequest request) {
        return delete(request, DeletePrivateDomainResponse.class, builder -> {
            builder.pathSegment("v2", "private_domains", request.getPrivateDomainId());
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<GetPrivateDomainResponse> get(GetPrivateDomainRequest request) {
        return get(request, GetPrivateDomainResponse.class, builder -> builder.pathSegment("v2", "private_domains", request.getPrivateDomainId()));
    }

    @Override
    public Mono<ListPrivateDomainsResponse> list(ListPrivateDomainsRequest request) {
        return get(request, ListPrivateDomainsResponse.class, builder -> {
            builder.pathSegment("v2", "private_domains");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

}
