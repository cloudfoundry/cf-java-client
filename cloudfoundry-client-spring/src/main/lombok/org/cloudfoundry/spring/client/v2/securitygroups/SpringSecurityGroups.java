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

package org.cloudfoundry.spring.client.v2.securitygroups;

import lombok.ToString;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroups;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultResponse;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;

/**
 * The Spring-based implementation of {@link SecurityGroups}
 */
@ToString(callSuper = true)
public class SpringSecurityGroups extends AbstractSpringOperations implements SecurityGroups {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringSecurityGroups(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<Void> deleteRunningDefault(DeleteSecurityGroupRunningDefaultRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "config", "running_security_groups", request.getSecurityGroupRunningDefaultId()));
    }

    @Override
    public Mono<Void> deleteStagingDefault(DeleteSecurityGroupStagingDefaultRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "config", "staging_security_groups", request.getSecurityGroupStagingDefaultId()));
    }

    @Override
    public Mono<ListSecurityGroupRunningDefaultsResponse> listRunningDefaults(ListSecurityGroupRunningDefaultsRequest request) {
        return get(request, ListSecurityGroupRunningDefaultsResponse.class, builder -> {
            builder.pathSegment("v2", "config", "running_security_groups");
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<ListSecurityGroupStagingDefaultsResponse> listStagingDefaults(ListSecurityGroupStagingDefaultsRequest request) {
        return get(request, ListSecurityGroupStagingDefaultsResponse.class, builder -> {
            builder.pathSegment("v2", "config", "staging_security_groups");
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<SetSecurityGroupRunningDefaultResponse> setRunningDefault(SetSecurityGroupRunningDefaultRequest request) {
        return put(request, SetSecurityGroupRunningDefaultResponse.class, builder -> builder.pathSegment("v2", "config", "running_security_groups", request.getSecurityGroupRunningDefaultId()));
    }

}
