/*
 * Copyright 2013-2016 the original author or authors.
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

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.GetEventRequest;
import org.cloudfoundry.client.v2.events.GetEventResponse;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.operations.v2.Resources;
import org.junit.Test;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class EventsTest extends AbstractIntegrationTest {

    @Test
    public void get() {
        getFirstEvent()
                .flatMap(resource -> {
                    GetEventRequest request = GetEventRequest.builder()
                            .id(Resources.getId(resource))
                            .build();

                    Stream<GetEventResponse> actual = Streams
                            .from(this.cloudFoundryClient.events().get(request));

                    return Streams.zip(Streams.just(resource), actual);
                })
                .subscribe(this.<Tuple2<EventResource, GetEventResponse>>testSubscriber()
                        .assertThat(tuple -> {
                            EventResource expected = tuple.t1;
                            GetEventResponse actual = tuple.t2;

                            assertEquals(Resources.getId(expected), Resources.getId(actual));
                        }));
    }

    @Test
    public void list() {
        listEvents()
                .count()
                .subscribe(this.<Long>testSubscriber()
                        .assertThat(count -> assertTrue(count > 0)));
    }

    @Test
    public void listFilterByActee() {
        getFirstEvent()
                .flatMap(resource -> {
                    ListEventsRequest request = ListEventsRequest.builder()
                            .actee(Resources.getEntity(resource).getActee())
                            .build();

                    Stream<EventResource> actual = Streams
                            .from(this.cloudFoundryClient.events().list(request))
                            .flatMap(Resources::getResources);

                    return Streams.zip(Streams.just(resource), actual);
                })
                .subscribe(this.<Tuple2<EventResource, EventResource>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByTimestamp() {
        getFirstEvent()
                .flatMap(resource -> {
                    ListEventsRequest request = ListEventsRequest.builder()
                            .timestamp(Resources.getEntity(resource).getTimestamp())
                            .build();

                    Stream<EventResource> actual = Streams
                            .from(this.cloudFoundryClient.events().list(request))
                            .flatMap(Resources::getResources);

                    return Streams.zip(Streams.just(resource), actual);
                })
                .subscribe(this.<Tuple2<EventResource, EventResource>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByType() {
        getFirstEvent()
                .flatMap(resource -> {
                    ListEventsRequest request = ListEventsRequest.builder()
                            .type(Resources.getEntity(resource).getType())
                            .build();

                    Stream<EventResource> actual = Streams
                            .from(this.cloudFoundryClient.events().list(request))
                            .flatMap(Resources::getResources);

                    return Streams.zip(Streams.just(resource), actual);
                })
                .subscribe(this.<Tuple2<EventResource, EventResource>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    private Stream<EventResource> getFirstEvent() {
        return listEvents()
                .take(1);
    }

    private Stream<EventResource> listEvents() {
        ListEventsRequest request = ListEventsRequest.builder()
                .build();

        return Streams
                .from(this.cloudFoundryClient.events().list(request))
                .flatMap(Resources::getResources);
    }

}
