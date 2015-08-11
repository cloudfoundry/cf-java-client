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

package org.cloudfoundry.v3.client.spring.application;

import org.cloudfoundry.v3.client.RequestValidationException;
import org.cloudfoundry.v3.client.ValidationResult;
import org.cloudfoundry.v3.client.application.Application;
import org.cloudfoundry.v3.client.application.CreateApplicationRequest;
import org.cloudfoundry.v3.client.application.CreateApplicationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.net.URI;

import static org.cloudfoundry.v3.client.ValidationResult.Status.INVALID;
import static org.springframework.http.HttpStatus.CREATED;

public final class SpringApplication implements Application {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestOperations restOperations;

    private final URI root;

    public SpringApplication(RestOperations restOperations, URI root) {
        this.restOperations = restOperations;
        this.root = root;
    }

    @Override
    public Observable<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return BehaviorSubject.create(subscriber -> {
            ValidationResult validationResult = request.isValid();
            if (validationResult.getStatus() == INVALID) {
                subscriber.onError(new RequestValidationException(validationResult));
                return;
            }

            URI uri = UriComponentsBuilder.fromUri(this.root).pathSegment("v3", "apps").build().toUri();
            this.logger.debug("Requesting Create Application at {}", uri);

            ResponseEntity<CreateApplicationResponse> response = this.restOperations.postForEntity(uri, request,
                    CreateApplicationResponse.class);
            if (response.getStatusCode() == CREATED) {
                subscriber.onNext(response.getBody());
            }

            subscriber.onCompleted();
        });
    }

}
