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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.operations.services.CreateServiceInstanceRequest;
import org.cloudfoundry.operations.services.GetServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public final class ServicesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Ignore("Needs marketplace feature to derive planName and serviceName - https://www.pivotaltracker.com/story/show/106155472")
    @Test
    public void getManagedService() throws TimeoutException, InterruptedException {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.cloudFoundryOperations.services()
            .createInstance(CreateServiceInstanceRequest.builder()
                .planName("shared-vm") //TODO: Replace with derived value
                .serviceName("p-redis") //TODO: Replace with derived value
                .serviceInstanceName(serviceInstanceName)
                .build())
            .then(this.cloudFoundryOperations.services()
                .getInstance(GetServiceInstanceRequest.builder()
                    .name(serviceInstanceName)
                    .build()))
            .map(ServiceInstance::getName)
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

}
