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

package org.cloudfoundry.client.spring.v2.quotadefinitions;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitions;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link OrganizationQuotaDefinitions}
 */
@ToString(callSuper = true)
public final class SpringOrganizationQuotaDefinitions extends AbstractSpringOperations implements OrganizationQuotaDefinitions {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringOrganizationQuotaDefinitions(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<GetOrganizationQuotaDefinitionResponse> get(final GetOrganizationQuotaDefinitionRequest request) {
        return get(request, GetOrganizationQuotaDefinitionResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "quota_definitions", request.getOrganizationQuotaDefinitionId());
            }

        });
    }

    @Override
    public Publisher<ListOrganizationQuotaDefinitionsResponse> list(final ListOrganizationQuotaDefinitionsRequest request) {
        return get(request, ListOrganizationQuotaDefinitionsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "quota_definitions");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }


}
