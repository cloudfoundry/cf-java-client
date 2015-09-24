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

package org.cloudfoundry.client.spring;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.events.ListEventsResponse.ListEventsResponseEntity;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.cloudfoundry.client.v2.events.Events.APP_CRASH;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_APP_CREATE;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_APP_DELETE_REQUEST;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_APP_START;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_APP_STOP;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_APP_UPDATE;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_SERVICE_BINDING_CREATE;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_SERVICE_BINDING_DELETE;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_SERVICE_INSTANCE_CREATE;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_SERVICE_INSTANCE_DELETE;
import static org.cloudfoundry.client.v2.events.Events.AUDIT_SERVICE_INSTANCE_UPDATE;

public final class ApplicationEvents {

    private static final List<String> EVENT_TYPES = Arrays.asList(APP_CRASH, AUDIT_APP_CREATE,
            AUDIT_APP_DELETE_REQUEST, AUDIT_APP_START, AUDIT_APP_STOP, AUDIT_APP_UPDATE,
            AUDIT_SERVICE_BINDING_CREATE, AUDIT_SERVICE_BINDING_DELETE, AUDIT_SERVICE_INSTANCE_CREATE,
            AUDIT_SERVICE_INSTANCE_DELETE, AUDIT_SERVICE_INSTANCE_UPDATE);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile CloudFoundryClient client;

    @Before
    public void configure() throws Exception {
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(new StandardEnvironment(), null);

        this.client = new SpringCloudFoundryClientBuilder()
                .withApi(resolver.getRequiredProperty("test.host"))
                .withCredentials(
                        resolver.getRequiredProperty("test.username"),
                        resolver.getRequiredProperty("test.password"))
                .build();
    }


    @Test
    public void applicationEvents() {
        Map<String, String> organizations = new HashMap<>();

        listOrganizations()
                .flatMap(p -> Streams.from(p.getResources()))
                .filter(r -> r.getEntity().getName().startsWith("s1-scs-demo-"))
                .consume(r -> {
                            String key = r.getMetadata().getId();
                            String value = r.getEntity().getName();
                            organizations.put(key, value);
                        },
                        System.out::println);

        this.logger.info("{} Organizations Found", organizations.size());

        getAllPages(page -> new ListEventsRequest()
                        .withTypes(EVENT_TYPES)
                        .withTimestamp("2015-09-14T00:00:00Z")
                        .withResultsPerPage(100)
                        .withPage(page),
                request -> this.client.events().list(request))
                .flatMap(eventsPage -> Streams.from(eventsPage.getResources()))
//                .observe(r -> this.logger.info(r.getEntity().getOrganizationId()))
                .filter(r -> organizations.keySet().contains(r.getEntity().getOrganizationId()))
                .consume(r -> {
                            ListEventsResponseEntity entity = r.getEntity();

                            String timestamp = entity.getTimestamp();
                            String organization = organizations.get(entity.getOrganizationId());
                            String type = entity.getType();

                            System.out.printf("%s,%s,%s%n", timestamp, organization, type);
                        },
                        System.out::println,
                        r -> this.logger.info("Finished"));

    }

    private <T extends PaginatedResponse, U> Stream<T> getAllPages(Function<Integer, U> requestProvider,
                                                                   Function<U, Publisher<T>> operationExecutor) {
        U initialRequest = requestProvider.apply(1);
        return Streams.wrap(operationExecutor.apply(initialRequest))
                .flatMap(response -> {
                    List<Publisher<T>> pages = new ArrayList<>();
                    pages.add(Streams.just(response));

                    for (int i = 2; i <= response.getTotalPages(); i++) {
                        U request = requestProvider.apply(i);
                        pages.add(operationExecutor.apply(request));
                    }

                    return Streams.concat(pages);
                });
    }

    private Stream<ListOrganizationsResponse> listOrganizations() {
        return getAllPages(page -> new ListOrganizationsRequest().withPage(page),
                request -> this.client.organizations().list(request));
    }

}
