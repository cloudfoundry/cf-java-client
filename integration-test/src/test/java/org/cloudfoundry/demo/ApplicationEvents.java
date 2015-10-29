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

package org.cloudfoundry.demo;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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


@Configuration
@EnableAutoConfiguration
public class ApplicationEvents {

    public static void main(String[] args) throws IOException {
        new SpringApplicationBuilder(ApplicationEvents.class).web(false).run(args)
                .getBean(Runner.class).run();
    }

    @Bean
    SpringCloudFoundryClient cloudFoundryClient(@Value("${test.host}") String host,
                                                @Value("${test.username}") String username,
                                                @Value("${test.password}") String password,
                                                @Value("${test.skipSslValidation:false}") Boolean skipSslValidation) {

        return SpringCloudFoundryClient.builder()
                .host(host)
                .username(username)
                .password(password)
                .skipSslValidation(skipSslValidation)
                .build();
    }

    @Component
    private static final class Runner {

        private static final List<String> EVENT_TYPES = Arrays.asList(AUDIT_APP_CREATE, AUDIT_APP_DELETE_REQUEST,
                AUDIT_APP_START, AUDIT_APP_STOP, AUDIT_APP_UPDATE, AUDIT_SERVICE_BINDING_CREATE,
                AUDIT_SERVICE_BINDING_DELETE, AUDIT_SERVICE_INSTANCE_CREATE, AUDIT_SERVICE_INSTANCE_DELETE,
                AUDIT_SERVICE_INSTANCE_UPDATE);

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final CloudFoundryClient cloudFoundryClient;

        @Autowired
        private Runner(CloudFoundryClient cloudFoundryClient) {
            this.cloudFoundryClient = cloudFoundryClient;
        }

        private void run() throws IOException {
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

            try (Writer writer = new FileWriter("/Users/bhale/Desktop/scs-usage.csv", true)) {
                paginate(page -> ListEventsRequest.builder()
                                .types(EVENT_TYPES)
                                .timestamp("2015-10-01T05:18:38Z")
                                .page(page)
                                .resultsPerPage(100)
                                .build(),
                        request -> cloudFoundryClient.events().list(request))
                        .flatMap(eventsPage -> Streams.from(eventsPage.getResources()))
                        .filter(r -> organizations.keySet().contains(r.getEntity().getOrganizationId()))
                        .map(r -> {
                            EventResource.EventEntity entity = r.getEntity();

                            String id = r.getMetadata().getId();
                            String timestamp = entity.getTimestamp();
                            String organization = organizations.get(entity.getOrganizationId());
                            String type = entity.getType();

                            return String.format("%s,%s,%s,%s%n", id, organization, timestamp, type);
                        })
                        .consume(m -> {
                                    try {
                                        writer.write(m);
                                        writer.flush();
                                        System.out.print(m);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                },
                                System.out::println,
                                r -> this.logger.info("Finished"));
            }
        }

        private <T extends PaginatedRequest, U extends PaginatedResponse> Stream<U> paginate(
                Function<Integer, T> requestProvider, Function<T, Publisher<U>> operationExecutor) {

            return Streams.just(Streams.wrap(operationExecutor.apply(requestProvider.apply(1))))
                    .concatMap(responseStream -> responseStream
                            .take(1)
                            .concatMap(response -> Streams.range(2, response.getTotalPages() - 1)
                                            .flatMap(page -> operationExecutor.apply(requestProvider.apply(page)))
                                            .startWith(response)
                            ));
        }

        private Stream<ListOrganizationsResponse> listOrganizations() {
            return paginate(
                    page -> ListOrganizationsRequest.builder().page(page).build(),
                    request -> this.cloudFoundryClient.organizations().list(request));
        }
    }

}
