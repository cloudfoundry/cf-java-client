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

import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.spring.SpringCloudFoundryClientBuilder;
import org.cloudfoundry.client.spring.SpringLoggregatorClient;
import org.cloudfoundry.client.spring.SpringLoggregatorClientBuilder;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import reactor.rx.Streams;

@Configuration
@EnableAutoConfiguration
@Lazy
public class ClientConfiguration {

    @Bean
    SpringCloudFoundryClient cloudFoundryClient(@Value("${test.host}") String host,
                                                @Value("${test.username}") String username,
                                                @Value("${test.password}") String password,
                                                @Value("${test.skipSslValidation:false}") Boolean skipSslValidation) {
        return new SpringCloudFoundryClientBuilder()
                .withApi(host)
                .withCredentials(username, password)
                .withSkipSslValidation(skipSslValidation)
                .build();
    }

    @Bean
    SpringLoggregatorClient loggregatorClient(SpringCloudFoundryClient cloudFoundryClient) {
        return new SpringLoggregatorClientBuilder()
                .withCloudFoundryClient(cloudFoundryClient)
                .build();
    }

    @Bean
    String spaceId(@Value("${test.space}") String space, CloudFoundryClient cloudFoundryClient) {
        return Streams.wrap(cloudFoundryClient.spaces().list(new ListSpacesRequest().withName(space)))
                .flatMap(response -> Streams.from(response.getResources()))
                .map(resource -> resource.getMetadata().getId())
                .next().poll();
    }

}
