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
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.spring.v2.CloudFoundryExceptionBuilder;
import org.cloudfoundry.client.v2.space.ListSpacesRequest;
import org.cloudfoundry.client.v2.space.ListSpacesResponse;
import org.cloudfoundry.client.v2.space.Space;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.net.URI;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;

/**
 * The Spring-based implementation of {@link Space}
 */
public final class SpringSpace implements Space {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestOperations restOperations;

    private final URI root;

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringSpace(RestOperations restOperations, URI root) {
        this.restOperations = restOperations;
        this.root = root;
    }

    @Override
    public Observable<ListSpacesResponse> list(ListSpacesRequest request) {
        return BehaviorSubject.create(subscriber -> {
            ValidationResult validationResult = request.isValid();
            if (validationResult.getStatus() == INVALID) {
                subscriber.onError(new RequestValidationException(validationResult));
                return;
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root).pathSegment("v2", "spaces");

            if (!request.getApplicationIds().isEmpty()) {
                String filter = new FilterBuilder("app_guid").in(request.getApplicationIds()).build();
                builder.queryParam("q", filter);
            }

            if (!request.getDeveloperIds().isEmpty()) {
                String filter = new FilterBuilder("developer_guid").in(request.getDeveloperIds()).build();
                builder.queryParam("q", filter);
            }

            if (!request.getNames().isEmpty()) {
                String filter = new FilterBuilder("name").in(request.getNames()).build();
                builder.queryParam("q", filter);
            }

            if (!request.getOrganizationIds().isEmpty()) {
                String filter = new FilterBuilder("organization_guid").in(request.getOrganizationIds()).build();
                builder.queryParam("q", filter);
            }

            URI uri = builder.build().toUri();
            this.logger.debug("Requesting List Spaces at {}", uri);

            try {
                ListSpacesResponse response = this.restOperations.getForObject(uri, ListSpacesResponse.class);
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (HttpStatusCodeException e) {
                throw CloudFoundryExceptionBuilder.build(e);
            }
        });
    }

}
