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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.rx.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Configuration
@EnableAutoConfiguration
public class OAuthPerformance {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OAuthPerformance.class).web(false).run(args)
                .getBean(Runner.class).run();
    }

    @Component
    private static final class Runner {

        private final int count;

        private final String host;

        private final String username;

        private final String password;

        @Autowired
        private Runner(@Value("${test.count:100}") int count,
                       @Value("${test.host}") String host,
                       @Value("${test.username}") String username,
                       @Value("${test.password}") String password) {
            this.count = count;
            this.host = host;
            this.username = username;
            this.password = password;
        }

        private void run() {
            List<Long> startup = new ArrayList<>();
            List<Long> first = new ArrayList<>();
            List<Long> subsequent = new ArrayList<>();

            AtomicReference<CloudFoundryClient> client = new AtomicReference<>();

            for (int i = 0; i < this.count; i++) {
                System.out.printf("Iteration %d%n", i);

                startup.add(time(() -> {
                    client.set(new SpringCloudFoundryClientBuilder()
                            .withApi(this.host)
                            .withCredentials(this.username, this.password)
                            .build());
                }));

                first.add(time(() -> Streams.wrap(client.get().info().get()).next().poll()));
                subsequent.add(time(() -> Streams.wrap(client.get().info().get()).next().poll()));
            }

            System.out.printf("Startup:    %f ms %n", startup.stream().collect(Collectors.averagingLong(l -> l)));
            System.out.printf("First:      %f ms %n", first.stream().collect(Collectors.averagingLong(l -> l)));
            System.out.printf("Subsequent: %f ms %n", subsequent.stream().collect(Collectors.averagingLong(l -> l)));
        }

        private static long time(Timer timer) {
            long startTime = System.currentTimeMillis();
            timer.time();
            return System.currentTimeMillis() - startTime;
        }

    }

    private interface Timer {
        void time();
    }

}
