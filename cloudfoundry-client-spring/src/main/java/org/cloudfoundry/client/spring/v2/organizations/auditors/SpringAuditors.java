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

package org.cloudfoundry.client.spring.v2.organizations.auditors;

import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.v2.organizations.auditors.Auditors;
import org.cloudfoundry.client.v2.organizations.auditors.CreateAuditorRequest;
import org.cloudfoundry.client.v2.organizations.auditors.CreateAuditorResponse;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Auditors}
 */
public final class SpringAuditors extends AbstractSpringOperations implements Auditors {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringAuditors(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<CreateAuditorResponse> create(CreateAuditorRequest request) {
        return put(request, CreateAuditorResponse.class,
                builder -> builder.pathSegment("v2", "organizations", request.getOrganizationId(), "auditors",
                        request.getAuditorId()));
    }

}
