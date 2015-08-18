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

package org.cloudfoundry.client.spring.v2.space;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.CloudFoundryExceptionBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.space.ListSpacesRequest;
import org.cloudfoundry.client.v2.space.ListSpacesResponse;
import org.cloudfoundry.client.v2.space.Spaces;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.net.URI;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;

/**
 * The Spring-based implementation of {@link Spaces}
 */
public final class SpringSpaces extends AbstractSpringOperations implements Spaces {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringSpaces(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Observable<ListSpacesResponse> list(ListSpacesRequest request) {
        return get(request, ListSpacesResponse.class, builder -> {
            builder.pathSegment("v2", "spaces");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });

    }

}
