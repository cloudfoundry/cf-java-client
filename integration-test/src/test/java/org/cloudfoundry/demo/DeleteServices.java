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
import org.cloudfoundry.client.spring.SpringCloudFoundryClientBuilder;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.rx.Streams;

@Configuration
@EnableAutoConfiguration
public class DeleteServices {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DeleteServices.class).web(false).run(args)
                .getBean(Runner.class).run();
    }

    @Bean
    CloudFoundryClient cloudFoundryClient(@Value("${test.host}") String host,
                                          @Value("${test.username}") String username,
                                          @Value("${test.password}") String password) {
        return new SpringCloudFoundryClientBuilder()
                .withApi(host)
                .withCredentials(username, password)
                .build();
    }

    @Component
    private static final class Runner {

        private final CloudFoundryClient cloudFoundryClient;

        @Autowired
        private Runner(CloudFoundryClient cloudFoundryClient) {
            this.cloudFoundryClient = cloudFoundryClient;
        }

        private void run() {
            ListServiceInstancesRequest request = new ListServiceInstancesRequest()
                    .withSpaceId("371e051f-8521-42bc-acef-ac11cc896323");

            Streams.wrap(this.cloudFoundryClient.serviceInstances().list(request))
                    .flatMap(r -> Streams.from(r.getResources()))
                    .count()
                    .consume(System.out::println);
        }

    }

}
