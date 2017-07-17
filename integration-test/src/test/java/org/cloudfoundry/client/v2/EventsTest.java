/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.GetEventRequest;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public final class EventsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void get() throws TimeoutException, InterruptedException {
        getFirstEvent(this.cloudFoundryClient)
            .flatMap(resource -> Mono.when(
                Mono.just(resource)
                    .map(ResourceUtils::getId),
                this.cloudFoundryClient.events()
                    .get(GetEventRequest.builder()
                        .eventId(ResourceUtils.getId(resource))
                        .build())
                    .map(ResourceUtils::getId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        getFirstEvent(this.cloudFoundryClient)
            .flatMap(resource -> Mono.when(
                Mono.just(resource),
                this.cloudFoundryClient.events()
                    .list(ListEventsRequest.builder()
                        .build())
                    .flatMapMany(ResourceUtils::getResources)
                    .next()
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByActee() throws TimeoutException, InterruptedException {
        getFirstEvent(this.cloudFoundryClient)
            .flatMap(resource -> Mono.when(
                Mono.just(resource),
                this.cloudFoundryClient.events()
                    .list(ListEventsRequest.builder()
                        .actee(ResourceUtils.getEntity(resource).getActee())
                        .build())
                    .flatMapMany(ResourceUtils::getResources)
                    .next()
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByTimestamp() throws TimeoutException, InterruptedException {
        getFirstEvent(this.cloudFoundryClient)
            .flatMap(resource -> Mono.when(
                Mono.just(resource),
                this.cloudFoundryClient.events()
                    .list(ListEventsRequest.builder()
                        .timestamp(ResourceUtils.getEntity(resource).getTimestamp())
                        .build())
                    .flatMapMany(ResourceUtils::getResources)
                    .next()
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByType() throws TimeoutException, InterruptedException {
        getFirstEvent(this.cloudFoundryClient)
            .flatMap(resource -> Mono.when(
                Mono.just(resource),
                this.cloudFoundryClient.events()
                    .list(ListEventsRequest.builder()
                        .type(ResourceUtils.getEntity(resource).getType())
                        .build())
                    .flatMapMany(ResourceUtils::getResources)
                    .next()
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<EventResource> getFirstEvent(CloudFoundryClient cloudFoundryClient) {
        return listEvents(cloudFoundryClient)
            .next();
    }

    private static Flux<EventResource> listEvents(CloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.events()
            .list(ListEventsRequest.builder()
                .build())
            .flatMapMany(ResourceUtils::getResources);
    }

}
