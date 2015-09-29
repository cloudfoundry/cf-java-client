/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.v3.applications;

import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v3.FilterBuilder;
import org.cloudfoundry.client.v3.applications.Applications;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.DeleteApplicationResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Applications}
 */
public final class SpringApplications extends AbstractSpringOperations implements Applications {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringApplications(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, builder -> builder.pathSegment("v3", "apps"));
    }

    @Override
    public Publisher<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId()));
    }

    @Override
    public Publisher<GetApplicationEnvironmentResponse> getEnvironment(GetApplicationEnvironmentRequest request) {
        return get(request, GetApplicationEnvironmentResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "env"));
    }

    @Override
    public Publisher<DeleteApplicationResponse> delete(DeleteApplicationRequest request) {
        return delete(request, new DeleteApplicationResponse(),
                builder -> builder.pathSegment("v3", "apps", request.getId()));
    }

    @Override
    public Publisher<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, builder -> {
            builder.pathSegment("v3", "apps");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Publisher<StartApplicationResponse> start(StartApplicationRequest request) {
        return put(request, StartApplicationResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "start"));
    }

}
