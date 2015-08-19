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

package org.cloudfoundry.client.spring.v3.applications.packages;

import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.v3.applications.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.applications.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.applications.packages.Packages;
import org.springframework.web.client.RestOperations;
import rx.Observable;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Packages}
 */
public final class SpringPackages extends AbstractSpringOperations implements Packages {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringPackages(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Observable<CreatePackageResponse> create(CreatePackageRequest request) {
        return post(request, CreatePackageResponse.class, builder -> {
            optional(request.getLink(),
                    link -> builder.path(link.getHref()),
                    () -> builder.pathSegment("v3", "apps", request.getApplicationId(), "packages"));
        });
    }

}
