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

package org.cloudfoundry.client.spring.v2.info;


import org.cloudfoundry.client.spring.v2.CloudFoundryExceptionBuilder;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.cloudfoundry.client.v2.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Info}
 */
public final class SpringInfo implements Info {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestOperations restOperations;

    private final URI root;

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringInfo(RestOperations restOperations, URI root) {
        this.restOperations = restOperations;
        this.root = root;
    }

    @Override
    public Observable<GetInfoResponse> get() {
        return BehaviorSubject.create(subscriber -> {
            URI uri = UriComponentsBuilder.fromUri(this.root).pathSegment("v2", "info").build().toUri();
            this.logger.debug("Requesting Get Info at {}", uri);

            try {
                GetInfoResponse response = this.restOperations.getForObject(uri, GetInfoResponse.class);
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (HttpStatusCodeException e) {
                subscriber.onError(CloudFoundryExceptionBuilder.build(e));
            }
        });
    }

}
