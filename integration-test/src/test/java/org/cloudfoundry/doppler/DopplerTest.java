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

package org.cloudfoundry.doppler;

import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@EnableAutoConfiguration
public class DopplerTest {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DopplerTest.class)
            .web(false)
            .run(args)
            .getBean(Runner.class)
            .run();
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

    @Bean
    ReactorDopplerClient dopplerClient(SpringCloudFoundryClient cloudFoundryClient) {
        return ReactorDopplerClient.builder()
            .cloudFoundryClient(cloudFoundryClient)
            .build();
    }

    @Component
    private static final class Runner {

        private final DopplerClient dopplerClient;

        @Autowired
        Runner(DopplerClient dopplerClient) {
            this.dopplerClient = dopplerClient;
        }

        void run() {
            this.dopplerClient
                .containerMetrics(ContainerMetricsRequest.builder()
                    .applicationId("1a95eadc-95c6-4675-aa07-8c02f80ea8a4")
                    .build())
                .subscribe(System.out::println);

//            this.dopplerClient
//                .recentLogs(RecentLogsRequest.builder()
//                    .applicationId("1a95eadc-95c6-4675-aa07-8c02f80ea8a4")
//                    .build())
//                .subscribe(System.out::println);

//            this.dopplerClient
//                .stream(StreamRequest.builder()
//                    .applicationId("1a95eadc-95c6-4675-aa07-8c02f80ea8a4")
//                    .build())
//                .subscribe(System.out::println);
        }

    }

}
