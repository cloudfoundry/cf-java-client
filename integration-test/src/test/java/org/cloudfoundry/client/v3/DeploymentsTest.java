/*
 * Copyright 2013-2018 the original author or authors.
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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.applications.ApplicationResource;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.deployments.CreateDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.CreateDeploymentResponse;
import org.cloudfoundry.client.v3.deployments.DeploymentRelationships;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationHealthCheck;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_4)
public final class DeploymentsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Test
    public void create() throws Exception {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, applicationName)
            .then(getApplicationId(this.cloudFoundryClient, applicationName))
            .then(applicationId -> Mono.when(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)
            ))
            .then(function(this::createDeployment))
            .map(CreateDeploymentResponse::getId)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> createApplication(CloudFoundryOperations cloudFoundryOperations, String name) throws IOException {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .path(new ClassPathResource("test-application.zip").getFile().toPath())
                .buildpack("staticfile_buildpack")
                .diskQuota(256)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .memory(64)
                .name(name)
                .noStart(false)
                .build());
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName) {
        return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.applicationsV3()
            .list(ListApplicationsRequest.builder()
                .name(applicationName)
                .build()))
            .single()
            .map(ApplicationResource::getId);
    }

    private static Mono<String> getDropletId(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV3()
            .getCurrentDroplet(GetApplicationCurrentDropletRequest.builder()
                .applicationId(applicationId)
                .build())
            .map(GetApplicationCurrentDropletResponse::getId);
    }

    private Mono<? extends CreateDeploymentResponse> createDeployment(String applicationId, String dropletId) {
        return this.cloudFoundryClient.deploymentsV3()
            .create(CreateDeploymentRequest.builder()
                .droplet(Relationship.builder().id(dropletId).build())
                .relationships(DeploymentRelationships.builder()
                    .app(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(applicationId)
                            .build())
                        .build())
                    .build())
                .build());
    }

}
