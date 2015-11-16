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

package org.cloudfoundry.client.spring.v2.applications;


import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;

import java.net.URI;

/**
 * The Spring-based implementation of {@link ApplicationsV2}
 */
@ToString(callSuper = true)
public final class SpringApplicationsV2 extends AbstractSpringOperations implements ApplicationsV2 {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringApplicationsV2(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId()));
    }

    @Override
    public Publisher<ApplicationInstancesResponse> instances(ApplicationInstancesRequest request) {
        return get(request, ApplicationInstancesResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "instances"));
    }

    @Override
    public Publisher<SummaryApplicationResponse> summary(SummaryApplicationRequest request) {
        return get(request, SummaryApplicationResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "summary"));
    }

}
