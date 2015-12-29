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

import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ClientConfiguration.class)
public abstract class AbstractClientIntegrationTest {

    @Autowired
    protected CloudFoundryClient cloudFoundryClient;

    @Autowired
    protected Stream<String> organizationId;

    @Autowired
    protected Stream<String> spaceId;

    @Value("${test.space}")
    protected String spaceName;

    @After
    public final void cleanup() throws Exception {
        Paginated
                .requestResources(page -> {
                    ListApplicationsRequest listApplicationsRequest = ListApplicationsRequest.builder()
                            .page(page)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().list(listApplicationsRequest);
                })
                .flatMap(resource -> {
                    DeleteApplicationRequest deleteApplicationRequest = DeleteApplicationRequest.builder()
                            .id(Resources.getId(resource))
                            .build();

                    return this.cloudFoundryClient.applicationsV2().delete(deleteApplicationRequest);
                })
                .subscribe(new TestSubscriber<>());


        Paginated
                .requestResources(page -> {
                    ListRoutesRequest listRoutesRequest = ListRoutesRequest.builder()
                            .page(page)
                            .build();

                    return this.cloudFoundryClient.routes().list(listRoutesRequest);
                })
                .flatMap(resource -> {
                    DeleteRouteRequest deleteRouteRequest = DeleteRouteRequest.builder()
                            .id(Resources.getId(resource))
                            .build();

                    return this.cloudFoundryClient.routes().delete(deleteRouteRequest);
                })
                .subscribe(new TestSubscriber<>());

        Paginated
                .requestResources(page -> {
                    ListDomainsRequest listDomainsRequest = ListDomainsRequest.builder()
                            .page(page)
                            .build();

                    return this.cloudFoundryClient.domains().list(listDomainsRequest);
                })
                .filter(resource -> !Resources.getEntity(resource).getName().equals("local.micropcf.io"))
                .flatMap(resource -> {
                    DeleteDomainRequest deleteDomainRequest = DeleteDomainRequest.builder()
                            .id(Resources.getId(resource))
                            .build();

                    return this.cloudFoundryClient.domains().delete(deleteDomainRequest);
                })
                .subscribe(new TestSubscriber<>());
    }

    protected final <T> void assertTupleEquality(Tuple2<T, T> tuple) {
        T expected = tuple.t1;
        T actual = tuple.t2;

        assertEquals(expected, actual);
    }

}
