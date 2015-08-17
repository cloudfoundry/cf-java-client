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

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.CloudFoundryExceptionBuilder;
import org.cloudfoundry.client.spring.v3.FilterBuilder;
import org.cloudfoundry.client.v3.application.Application;
import org.cloudfoundry.client.v3.application.CreateApplicationRequest;
import org.cloudfoundry.client.v3.application.CreateApplicationResponse;
import org.cloudfoundry.client.v3.application.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.application.DeleteApplicationResponse;
import org.cloudfoundry.client.v3.application.ListApplicationsRequest;
import org.cloudfoundry.client.v3.application.ListApplicationsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Application}
 */
public final class SpringApplication implements Application {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestOperations restOperations;

    private final URI root;

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringApplication(RestOperations restOperations, URI root) {
        this.restOperations = restOperations;
        this.root = root;
    }

    @Override
    public Observable<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return BehaviorSubject.create(subscriber -> {
            ValidationResult validationResult = request.isValid();
            if (validationResult.getStatus() == ValidationResult.Status.INVALID) {
                subscriber.onError(new RequestValidationException(validationResult));
                return;
            }

            URI uri = UriComponentsBuilder.fromUri(this.root).pathSegment("v3", "apps").build().toUri();
            this.logger.debug("Requesting Create Application at {}", uri);

            try {
                CreateApplicationResponse response = this.restOperations.postForObject(uri, request,
                        CreateApplicationResponse.class);
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (HttpStatusCodeException e) {
                subscriber.onError(CloudFoundryExceptionBuilder.build(e));
            }
        });
    }

    @Override
    public Observable<DeleteApplicationResponse> delete(DeleteApplicationRequest request) {
        return BehaviorSubject.create(subscriber -> {
            ValidationResult validationResult = request.isValid();
            if (validationResult.getStatus() == ValidationResult.Status.INVALID) {
                subscriber.onError(new RequestValidationException(validationResult));
                return;
            }

            URI uri = UriComponentsBuilder.fromUri(this.root).pathSegment("v3", "apps", request.getId())
                    .build().toUri();
            this.logger.debug("Requesting Delete Application at {}", uri);

            try {
                this.restOperations.delete(uri);
                subscriber.onNext(new DeleteApplicationResponse());
                subscriber.onCompleted();
            } catch (HttpStatusCodeException e) {
                subscriber.onError(CloudFoundryExceptionBuilder.build(e));
            }
        });
    }

    @Override
    public Observable<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return BehaviorSubject.create(subscriber -> {
            ValidationResult validationResult = request.isValid();
            if (validationResult.getStatus() == ValidationResult.Status.INVALID) {
                subscriber.onError(new RequestValidationException(validationResult));
                return;
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root).pathSegment("v3", "apps");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
            URI uri = builder.build().toUri();
            this.logger.debug("Requesting List Applications at {}", uri);

            try {
                ListApplicationsResponse response = this.restOperations.getForObject(uri,
                        ListApplicationsResponse.class);
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (HttpStatusCodeException e) {
                subscriber.onError(CloudFoundryExceptionBuilder.build(e));
            }
        });
    }

}
