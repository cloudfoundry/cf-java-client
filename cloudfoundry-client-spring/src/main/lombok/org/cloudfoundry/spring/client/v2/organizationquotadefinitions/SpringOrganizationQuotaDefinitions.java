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

package org.cloudfoundry.spring.client.v2.organizationquotadefinitions;

import lombok.ToString;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitions;
import org.cloudfoundry.client.v2.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.reactor.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

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
    public SpringOrganizationQuotaDefinitions(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CreateOrganizationQuotaDefinitionResponse> create(CreateOrganizationQuotaDefinitionRequest request) {
        return post(request, CreateOrganizationQuotaDefinitionResponse.class, builder -> builder.pathSegment("v2", "quota_definitions"));
    }

    @Override
    public Mono<DeleteOrganizationQuotaDefinitionResponse> delete(DeleteOrganizationQuotaDefinitionRequest request) {
        return delete(request, DeleteOrganizationQuotaDefinitionResponse.class, builder -> {
            builder.pathSegment("v2", "quota_definitions", request.getOrganizationQuotaDefinitionId());
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<GetOrganizationQuotaDefinitionResponse> get(GetOrganizationQuotaDefinitionRequest request) {
        return get(request, GetOrganizationQuotaDefinitionResponse.class, builder -> builder.pathSegment("v2", "quota_definitions", request.getOrganizationQuotaDefinitionId()));
    }

    @Override
    public Mono<ListOrganizationQuotaDefinitionsResponse> list(ListOrganizationQuotaDefinitionsRequest request) {
        return get(request, ListOrganizationQuotaDefinitionsResponse.class, builder -> {
            builder.pathSegment("v2", "quota_definitions");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<UpdateOrganizationQuotaDefinitionResponse> update(UpdateOrganizationQuotaDefinitionRequest request) {
        return put(request, UpdateOrganizationQuotaDefinitionResponse.class, builder -> builder.pathSegment("v2", "quota_definitions", request.getOrganizationQuotaDefinitionId()));
    }

}
