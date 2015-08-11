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

package org.cloudfoundry.v3.client.spring;

import org.cloudfoundry.v3.client.CloudFoundryClient;
import org.cloudfoundry.v3.client.InfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.net.URI;

import static org.springframework.http.HttpStatus.OK;

final class SpringCloudFoundryClient implements CloudFoundryClient {

    private final RestOperations restOperations;

    private final URI root;

    SpringCloudFoundryClient(RestOperations restOperations, URI root) {
        this.restOperations = restOperations;
        this.root = root;
    }

    RestOperations getRestOperations() {
        return this.restOperations;
    }

    @Override
    public Observable<InfoResponse> info() {
        return BehaviorSubject.create(subscriber -> {
            URI uri = UriComponentsBuilder.fromUri(this.root).pathSegment("v2", "info").build().toUri();
            ResponseEntity<InfoResponse> response = this.restOperations.getForEntity(uri, InfoResponse.class);

            if (response.getStatusCode() == OK) {
                subscriber.onNext(response.getBody());
            }

            subscriber.onCompleted();
        });
    }
}
