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

import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.spring.SpringLoggregatorClient;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.operations.UnexpectedResponseException;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.cloudfoundry.utils.test.FailingDeserializationProblemHandler;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@Lazy
public class ClientConfiguration {

    @Bean
    SpringCloudFoundryClient cloudFoundryClient(@Value("${test.host}") String host,
                                                @Value("${test.username}") String username,
                                                @Value("${test.password}") String password,
                                                @Value("${test.skipSslValidation:false}") Boolean skipSslValidation,
                                                List<DeserializationProblemHandler> deserializationProblemHandlers) {

        return SpringCloudFoundryClient.builder()
                .host(host)
                .username(username)
                .password(password)
                .skipSslValidation(skipSslValidation)
                .deserializationProblemHandlers(deserializationProblemHandlers)
                .build();
    }

    @Bean
    FailingDeserializationProblemHandler failingDeserializationProblemHandler() {
        return new FailingDeserializationProblemHandler();
    }

    @Bean
    SpringLoggregatorClient loggregatorClient(SpringCloudFoundryClient cloudFoundryClient) {
        return SpringLoggregatorClient.builder()
                .cloudFoundryClient(cloudFoundryClient)
                .build();
    }

    @Bean
    Stream<String> organizationId(CloudFoundryClient cloudFoundryClient, @Value("${test.organization}") String organization) {
        return Paginated
                .requestResources(page -> {
                    ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                            .name(organization)
                            .page(page)
                            .build();

                    return cloudFoundryClient.organizations().list(request);
                })
                .reduce((resource, resource2) -> failIfMoreThanOne(String.format("Organization %s was listed more than once", organization)))
                .switchIfEmpty(this.<ListOrganizationsResponse.Resource>failIfLessThanOne(String.format("Organization %s does not exist", organization)))
                .map(Resources::getId)
                .take(1)
                .cache(1);
    }

    @Bean
    Stream<String> spaceId(CloudFoundryClient cloudFoundryClient, Stream<String> organizationId, @Value("${test.space}") String space) {
        return organizationId
                .flatMap(orgId -> Paginated
                        .requestResources(page -> {
                            ListSpacesRequest request = ListSpacesRequest.builder()
                                    .organizationId(orgId)
                                    .name(space)
                                    .page(page)
                                    .build();

                            return cloudFoundryClient.spaces().list(request);
                        }))
                .reduce((resource, resource2) -> failIfMoreThanOne(String.format("Space %s was listed more than once", space)))
                .switchIfEmpty(failIfLessThanOne(String.format("Space %s does not exist", space)))
                .map(Resources::getId)
                .take(1)
                .cache(1);
    }

    private static <T extends Resource<?>> T failIfMoreThanOne(String message) {
        throw new UnexpectedResponseException(message);
    }


    private <T extends Resource<?>> Publisher<T> failIfLessThanOne(String message) {
        return Streams.fail(new IllegalArgumentException(message));
    }

}
