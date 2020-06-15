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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletResponse;
import org.cloudfoundry.client.v3.deployments.CancelDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.CreateDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.CreateDeploymentResponse;
import org.cloudfoundry.client.v3.deployments.DeploymentRelationships;
import org.cloudfoundry.client.v3.deployments.DeploymentResource;
import org.cloudfoundry.client.v3.deployments.DeploymentState;
import org.cloudfoundry.client.v3.deployments.DeploymentStatusValue;
import org.cloudfoundry.client.v3.deployments.GetDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.ListDeploymentsRequest;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.ApplicationHealthCheck;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.time.Duration;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_4)
public final class DeploymentsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @SuppressWarnings("deprecation")
    @Test
    public void cancel() throws Exception {
        String name = this.nameFactory.getApplicationName();
        Path path = new ClassPathResource("test-application.zip").getFile().toPath();

        createApplicationId(this.cloudFoundryOperations, name, path)
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)
            ))
            .flatMap(function((applicationId, dropletId) -> Mono.zip(
                Mono.just(applicationId),
                createDeploymentId(this.cloudFoundryClient, applicationId, dropletId)
            )))
            .flatMap(function((applicationId, deploymentId) -> this.cloudFoundryClient.deploymentsV3()
                .cancel(CancelDeploymentRequest.builder()
                    .deploymentId(deploymentId)
                    .build())
                .then(Mono.just(applicationId))))
            .flatMapMany(applicationId -> requestListDeployments(this.cloudFoundryClient, applicationId))
            .map(DeploymentResource::getState)
            .as(StepVerifier::create)
            .consumeNextWith(isCancel())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws Exception {
        String name = this.nameFactory.getApplicationName();
        Path path = new ClassPathResource("test-application.zip").getFile().toPath();

        createApplicationId(this.cloudFoundryOperations, name, path)
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)
            ))
            .flatMap(function((applicationId, dropletId) -> this.cloudFoundryClient.deploymentsV3()
                .create(CreateDeploymentRequest.builder()
                    .droplet(Relationship.builder()
                        .id(dropletId)
                        .build())
                    .relationships(DeploymentRelationships.builder()
                        .app(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id(applicationId)
                                .build())
                            .build())
                        .build())
                    .build())
                .then(Mono.just(applicationId))))
            .flatMapMany(applicationId -> requestListDeployments(this.cloudFoundryClient, applicationId))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws Exception {
        String name = this.nameFactory.getApplicationName();
        Path path = new ClassPathResource("test-application.zip").getFile().toPath();

        createApplicationId(this.cloudFoundryOperations, name, path)
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)
            ))
            .flatMap(function((applicationId, dropletId) -> createDeploymentId(this.cloudFoundryClient, applicationId, dropletId)))
            .flatMap(deploymentId -> this.cloudFoundryClient.deploymentsV3()
                .get(GetDeploymentRequest.builder()
                    .deploymentId(deploymentId)
                    .build()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws Exception {
        String name = this.nameFactory.getApplicationName();
        Path path = new ClassPathResource("test-application.zip").getFile().toPath();

        createApplicationId(this.cloudFoundryOperations, name, path)
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)
            ))
            .flatMap(function((applicationId, dropletId) -> createDeploymentId(this.cloudFoundryClient, applicationId, dropletId)))
            .flatMapMany(deploymentId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.deploymentsV3()
                .list(ListDeploymentsRequest.builder()
                    .page(page)
                    .build()))
                .filter(resource -> deploymentId.equals(resource.getId())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByApplication() throws Exception {
        String name = this.nameFactory.getApplicationName();
        Path path = new ClassPathResource("test-application.zip").getFile().toPath();

        createApplicationId(this.cloudFoundryOperations, name, path)
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)
            ))
            .flatMap(function((applicationId, dropletId) -> requestCreateDeployment(this.cloudFoundryClient, applicationId, dropletId)
                .then(Mono.just(applicationId))))
            .flatMapMany(applicationId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.deploymentsV3()
                .list(ListDeploymentsRequest.builder()
                    .applicationId(applicationId)
                    .page(page)
                    .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByState() throws Exception {
        String name = this.nameFactory.getApplicationName();
        Path path = new ClassPathResource("test-application.zip").getFile().toPath();

        createApplicationId(this.cloudFoundryOperations, name, path)
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)
            ))
            .flatMap(function((applicationId, dropletId) -> createDeploymentId(this.cloudFoundryClient, applicationId, dropletId)))
            .flatMapMany(deploymentId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.deploymentsV3()
                .list(ListDeploymentsRequest.builder()
                    .states(DeploymentState.DEPLOYED, DeploymentState.DEPLOYING)
                    .page(page)
                    .build()))
                .filter(resource -> deploymentId.equals(resource.getId())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_7)
    @Test
    public void listFilterByStatusValues() throws Exception {
        String name = this.nameFactory.getApplicationName();
        Path path = new ClassPathResource("test-application.zip").getFile().toPath();

        createApplicationId(this.cloudFoundryOperations, name, path)
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)
            ))
            .flatMap(function((applicationId, dropletId) -> createDeploymentId(this.cloudFoundryClient, applicationId, dropletId)))
            .flatMapMany(deploymentId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.deploymentsV3()
                .list(ListDeploymentsRequest.builder()
                    .statusValues(DeploymentStatusValue.DEPLOYING, DeploymentStatusValue.ACTIVE)
                    .page(page)
                    .build()))
                .filter(resource -> deploymentId.equals(resource.getId())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryOperations cloudFoundryOperations, String name, Path path) {
        return requestCreateApplication(cloudFoundryOperations, name, path)
            .then(getApplicationId(cloudFoundryOperations, name));
    }

    private static Mono<String> createDeploymentId(CloudFoundryClient cloudFoundryClient, String applicationId, String dropletId) {
        return requestCreateDeployment(cloudFoundryClient, applicationId, dropletId)
            .map(CreateDeploymentResponse::getId);
    }

    private static Mono<String> getApplicationId(CloudFoundryOperations cloudFoundryOperations, String name) {
        return cloudFoundryOperations.applications()
            .get(GetApplicationRequest.builder()
                .name(name)
                .build())
            .map(ApplicationDetail::getId);
    }

    private static Mono<String> getDropletId(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestCurrentDroplet(cloudFoundryClient, applicationId)
            .map(GetApplicationCurrentDropletResponse::getId);
    }

    private static Consumer<DeploymentState> isCancel() {
        return state -> assertThat(state).isIn(DeploymentState.CANCELING, DeploymentState.CANCELED);
    }

    private static Mono<Void> requestCreateApplication(CloudFoundryOperations cloudFoundryOperations, String name, Path path) {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .path(path)
                .buildpack("staticfile_buildpack")
                .diskQuota(256)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .memory(64)
                .name(name)
                .noStart(false)
                .build());
    }

    private static Mono<CreateDeploymentResponse> requestCreateDeployment(CloudFoundryClient cloudFoundryClient, String applicationId, String dropletId) {
        return cloudFoundryClient.deploymentsV3()
            .create(CreateDeploymentRequest.builder()
                .droplet(Relationship.builder()
                    .id(dropletId)
                    .build())
                .relationships(DeploymentRelationships.builder()
                    .app(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(applicationId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Mono<GetApplicationCurrentDropletResponse> requestCurrentDroplet(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV3()
            .getCurrentDroplet(GetApplicationCurrentDropletRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Flux<DeploymentResource> requestListDeployments(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.deploymentsV3()
            .list(ListDeploymentsRequest.builder()
                .applicationId(applicationId)
                .page(page)
                .build()));
    }

}
