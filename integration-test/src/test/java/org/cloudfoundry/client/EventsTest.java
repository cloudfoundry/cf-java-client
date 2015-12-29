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

package org.cloudfoundry.client;

import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.GetEventRequest;
import org.cloudfoundry.client.v2.events.GetEventResponse;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.events.ListEventsResponse;
import org.cloudfoundry.operations.v2.Resources;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Test;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;
import reactor.rx.Streams;

import static org.junit.Assert.assertTrue;

public final class EventsTest extends AbstractClientIntegrationTest {

    @Test
    public void get() {
        getFirstEvent()
                .flatMap(resource -> {
                    GetEventRequest request = GetEventRequest.builder()
                            .id(Resources.getId(resource))
                            .build();

                    Stream<GetEventResponse> actual = Streams.wrap(this.cloudFoundryClient.events().get(request));

                    return Streams.<EventResource, EventResource>zip(Streams.just(resource), actual);
                })
                .subscribe(new TestSubscriber<Tuple2<EventResource, EventResource>>()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void list() {
        listEvents()
                .count()
                .subscribe(new TestSubscriber<Long>()
                        .assertThat(count -> assertTrue(count > 0)));
    }

    @Test
    public void listFilterByActee() {
        getFirstEvent()
                .flatMap(resource -> {
                    ListEventsRequest request = ListEventsRequest.builder()
                            .actee(Resources.getEntity(resource).getActee())
                            .build();

                    Stream<ListEventsResponse.Resource> actual = Streams
                            .wrap(this.cloudFoundryClient.events().list(request))
                            .flatMap(Resources::getResources);

                    return Streams.<EventResource, EventResource>zip(Streams.just(resource), actual);
                })
                .subscribe(new TestSubscriber<Tuple2<EventResource, EventResource>>()
                        .assertThat(this::assertTupleEquality));


        ListEventsRequest listEventsRequest = ListEventsRequest.builder()
                .actee("fc77e7f1-48b1-42f8-b0ff-ceb1a0dc52a9")
                .build();

        Streams
                .wrap(this.cloudFoundryClient.events().list(listEventsRequest))
                .flatMap(Resources::getResources)
                .count()
                .subscribe(new TestSubscriber<Long>()
                        .assertThat(count -> assertTrue(count > 0)));
    }

    @Test
    public void listFilterByTimestamp() {
        getFirstEvent()
                .flatMap(resource -> {
                    ListEventsRequest request = ListEventsRequest.builder()
                            .timestamp(Resources.getEntity(resource).getTimestamp())
                            .build();

                    Stream<ListEventsResponse.Resource> actual = Streams
                            .wrap(this.cloudFoundryClient.events().list(request))
                            .flatMap(Resources::getResources);

                    return Streams.<EventResource, EventResource>zip(Streams.just(resource), actual);
                })
                .subscribe(new TestSubscriber<Tuple2<EventResource, EventResource>>()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByType() {
        getFirstEvent()
                .flatMap(resource -> {
                    ListEventsRequest request = ListEventsRequest.builder()
                            .type(Resources.getEntity(resource).getType())
                            .build();

                    Stream<ListEventsResponse.Resource> actual = Streams
                            .wrap(this.cloudFoundryClient.events().list(request))
                            .flatMap(Resources::getResources);

                    return Streams.<EventResource, EventResource>zip(Streams.just(resource), actual);
                })
                .subscribe(new TestSubscriber<Tuple2<EventResource, EventResource>>()
                        .assertThat(this::assertTupleEquality));
    }

    private Stream<ListEventsResponse.Resource> getFirstEvent() {
        return listEvents()
                .take(1);
    }

    private Stream<ListEventsResponse.Resource> listEvents() {
        ListEventsRequest request = ListEventsRequest.builder()
                .build();

        return Streams
                .wrap(this.cloudFoundryClient.events().list(request))
                .flatMap(Resources::getResources);
    }

}
