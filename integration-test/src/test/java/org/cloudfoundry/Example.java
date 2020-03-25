/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
final class Example implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger("example");

    @Autowired
    CloudFoundryClient cloudFoundryClient;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Example.class, ExampleConfiguration.class)
            .run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        this.cloudFoundryClient.spaces()
            .list(ListSpacesRequest.builder()
                .build())
            .subscribe(System.out::println, t -> {
                t.printStackTrace();
                latch.countDown();
            }, latch::countDown);

        latch.await();
    }

}