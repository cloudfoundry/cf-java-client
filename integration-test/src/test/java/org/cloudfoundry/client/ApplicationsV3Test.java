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

import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.rx.Streams;

@Ignore("V3 APIs are unstable and prone to breakage")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ClientConfiguration.class)
public final class ApplicationsV3Test {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private String organizationId;

    @Autowired
    private String spaceId;

    @Test
    public void create() {
        CreateApplicationRequest createRequest = CreateApplicationRequest.builder()
                .spaceId(this.spaceId)
                .name("test-name")
                .build();

        Streams
                .wrap(this.cloudFoundryClient.applicationsV3().create(createRequest))
                .flatMap(createApplicationResponse -> {
                    ListApplicationsRequest listRequest = ListApplicationsRequest.builder()
                            .spaceId(ApplicationsV3Test.this.spaceId)
                            .build();

                    return ApplicationsV3Test.this.cloudFoundryClient.applicationsV3().list(listRequest);
                })
                .map(ListApplicationsResponse::getResources)
                .flatMap(Streams::from)
                .count()
                .subscribe(new TestSubscriber<>()
                        .assertEquals(14L));
    }

    @Before
    public void zeroExistingApplications() {
        ListApplicationsRequest request = ListApplicationsRequest.builder()
                .organizationId(this.organizationId)
                .spaceId(this.spaceId)
                .build();

        Streams
                .wrap(this.cloudFoundryClient.applicationsV3().list(request))
                .map(ListApplicationsResponse::getResources)
                .flatMap(Streams::from)
                .count()
                .subscribe(new TestSubscriber<>()
                        .assertEquals(0));
    }

}
