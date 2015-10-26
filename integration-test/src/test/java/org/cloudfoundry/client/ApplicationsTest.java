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

import org.cloudfoundry.client.v3.PaginatedResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ClientConfiguration.class)
public final class ApplicationsTest {

    @Autowired
    private volatile CloudFoundryClient cloudFoundryClient;

    @Autowired
    private volatile String organizationId;

    @Autowired
    private volatile String spaceId;

    @Before
    public void zeroExistingApplications() {
        ListApplicationsRequest request = new ListApplicationsRequest()
                .withOrganizationId(this.organizationId)
                .withSpaceId(this.spaceId);

        long size = Streams.wrap(this.cloudFoundryClient.applications().list(request))
                .map(PaginatedResponse::getResources)
                .flatMap(Streams::from)
                .count()
                .next().poll();

        assertEquals("Unexpected applications exist", 0, size);
    }

    @Test
    public void create() {
        CreateApplicationRequest createRequest = new CreateApplicationRequest()
                .withSpaceId(this.spaceId)
                .withName("test-name");
        Streams.wrap(this.cloudFoundryClient.applications().create(createRequest))
                .next().poll();

        ListApplicationsRequest listRequest = new ListApplicationsRequest().withSpaceId(this.spaceId);
        long size = Streams.wrap(this.cloudFoundryClient.applications().list(listRequest))
                .map(PaginatedResponse::getResources)
                .flatMap(Streams::from)
                .count()
                .next().poll();

        assertEquals(1, size);
    }

}
