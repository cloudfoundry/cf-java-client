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

package org.cloudfoundry.client.spring.v3.application;

import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v3.FilterBuilder;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.application.Applications;
import org.cloudfoundry.client.v3.application.CreateApplicationRequest;
import org.cloudfoundry.client.v3.application.CreateApplicationResponse;
import org.cloudfoundry.client.v3.application.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.application.DeleteApplicationResponse;
import org.cloudfoundry.client.v3.application.GetApplicationRequest;
import org.cloudfoundry.client.v3.application.GetApplicationResponse;
import org.cloudfoundry.client.v3.application.ListApplicationsRequest;
import org.cloudfoundry.client.v3.application.ListApplicationsResponse;
import org.cloudfoundry.client.v3.application.StartApplicationRequest;
import org.cloudfoundry.client.v3.application.StartApplicationResponse;
import org.springframework.web.client.RestOperations;
import rx.Observable;

import java.net.URI;
import java.util.Optional;

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
    public Observable<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, builder -> builder.pathSegment("v3", "apps"));
    }

    @Override
    public Observable<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId()));
    }

    @Override
    public Observable<DeleteApplicationResponse> delete(DeleteApplicationRequest request) {
        return delete(request, new DeleteApplicationResponse(),
                builder -> builder.pathSegment("v3", "apps", request.getId()));
    }

    @Override
    public Observable<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, builder -> {
            builder.pathSegment("v3", "apps");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Observable<StartApplicationResponse> start(StartApplicationRequest request) {
        return put(request, StartApplicationResponse.class, builder -> {
            Optional<Link> link = Optional.ofNullable(request.getLink());

            if (link.isPresent()) {
                builder.path(link.get().getHref());
            } else {
                builder.pathSegment("v3", "apps", request.getId(), "start");
            }
        });
    }

}
