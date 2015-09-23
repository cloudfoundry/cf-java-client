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

package org.cloudfoundry.client.spring.v2.events;

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.spring.ExpectedExceptionSubscriber;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.events.ListEventsResponse;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringEventsTest extends AbstractRestTest {

    private final SpringEvents events = new SpringEvents(this.restTemplate, this.root);

    @Test
    public void list() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/events?q=actee%20IN%20test-actee&page=-1"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v2/events/GET_response.json"))
                        .contentType(APPLICATION_JSON));

        ListEventsRequest request = new ListEventsRequest()
                .withActee("test-actee")
                .withPage(-1);

        Streams.wrap(this.events.list(request)).consume(response -> {
            assertNull(response.getNextUrl());
            assertNull(response.getPreviousUrl());
            assertEquals(Integer.valueOf(1), response.getTotalPages());
            assertEquals(Integer.valueOf(3), response.getTotalResults());

            assertEquals(3, response.getResources().size());
            Resource<ListEventsResponse.Entity> resource = response.getResources().get(0);

            Resource.Metadata metadata = resource.getMetadata();
            assertEquals("2015-07-27T22:43:23Z", metadata.getCreatedAt());
            assertEquals("2cc565c7-18e7-4fff-8fb0-52525f09ee6b", metadata.getId());
            assertNull(metadata.getUpdatedAt());
            assertEquals("/v2/events/2cc565c7-18e7-4fff-8fb0-52525f09ee6b", metadata.getUrl());

            ListEventsResponse.Entity entity = resource.getEntity();
            assertEquals("guid-16ac41e9-c30c-45e1-b51c-226fb37e4197", entity.getActee());
            assertEquals("name-1038", entity.getActeeName());
            assertEquals("name-1037", entity.getActeeType());
            assertEquals("guid-ddc7f725-c67f-4e68-8118-1ae1687f9fff", entity.getActor());
            assertEquals("name-1036", entity.getActorName());
            assertEquals("name-1035", entity.getActorType());
            assertEquals(Collections.emptyMap(), entity.getMetadatas());
            assertEquals("49723c2a-a11e-43f8-971a-b34e9134ce00", entity.getOrganizationId());
            assertEquals("1a769af6-8ddb-4508-a35a-cc61c51fdcdf", entity.getSpaceId());
            assertEquals("2015-07-27T22:43:23Z", entity.getTimestamp());
            assertEquals("name-1034", entity.getType());

            this.mockServer.verify();
        });
    }

    @Test
    public void listError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/events?q=actee%20IN%20test-actee&page=-1"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        ListEventsRequest request = new ListEventsRequest()
                .withActee("test-actee")
                .withPage(-1);

        this.events.list(request).subscribe(new ExpectedExceptionSubscriber());
    }

}
