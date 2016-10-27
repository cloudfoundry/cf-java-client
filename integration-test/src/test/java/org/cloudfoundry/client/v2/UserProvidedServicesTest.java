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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.GetUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceEntity;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceResource;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class UserProvidedServicesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String instanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectNext(instanceName)
            .expectComplete();

        this.spaceId
            .then(spaceId -> this.cloudFoundryClient.userProvidedServiceInstances()
                .create(CreateUserProvidedServiceInstanceRequest.builder()
                    .name(instanceName)
                    .spaceId(spaceId)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String instanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<UserProvidedServiceInstanceResource> subscriber = ScriptedSubscriber.<UserProvidedServiceInstanceResource>create()
            .expectComplete();

        this.spaceId
            .then(spaceId -> getCreateUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId))
            .then(instanceId -> this.cloudFoundryClient.userProvidedServiceInstances()
                .delete(DeleteUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceId(instanceId)
                    .build()))
            .thenMany(requestListUserProvidedServiceInstances(this.cloudFoundryClient, instanceName))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String instanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectNext(instanceName)
            .expectComplete();

        this.spaceId
            .then(spaceId -> getCreateUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId))
            .then(instanceId -> this.cloudFoundryClient.userProvidedServiceInstances()
                .get(GetUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceId(instanceId)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String instanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectNext(instanceName)
            .expectComplete();

        this.spaceId
            .then(spaceId -> requestCreateUserProvidedServiceInstance(this.cloudFoundryClient, instanceName, spaceId))
            .flatMap(ignore -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.userProvidedServiceInstances()
                    .list(ListUserProvidedServiceInstancesRequest.builder()
                        .name(instanceName)
                        .page(page)
                        .build())))
            .single()
            .map(response -> ResourceUtils.getEntity(response).getName())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceBindings() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String instanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<Tuple3<String, String, ServiceBindingResource>> subscriber = serviceBindingEquality();

        this.spaceId
            .then(spaceId -> Mono.when(
                getCreateApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                getCreateUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId)
            ))
            .as(thenKeep(function((applicationId, instanceId) -> this.cloudFoundryClient.serviceBindingsV2()
                .create(CreateServiceBindingRequest.builder()
                    .applicationId(applicationId)
                    .serviceInstanceId(instanceId)
                    .build()))))
            .then(function((applicationId, instanceId) -> Mono
                .when(
                    Mono.just(applicationId),
                    Mono.just(instanceId),
                    PaginationUtils
                        .requestClientV2Resources(page -> this.cloudFoundryClient.userProvidedServiceInstances()
                            .listServiceBindings(ListUserProvidedServiceInstanceServiceBindingsRequest.builder()
                                .applicationId(applicationId)
                                .page(page)
                                .userProvidedServiceInstanceId(instanceId)
                                .build()))
                        .single())))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String instanceName = this.nameFactory.getServiceInstanceName();
        String newInstanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<Tuple2<UserProvidedServiceInstanceEntity, UserProvidedServiceInstanceEntity>> subscriber =
            ScriptedSubscriber.<Tuple2<UserProvidedServiceInstanceEntity, UserProvidedServiceInstanceEntity>>create()
                .consumeNextWith(consumer((entity1, entity2) -> {
                    assertThat(entity1.getName()).isEqualTo(newInstanceName);
                    assertThat(entity1.getCredentials()).containsEntry("test-cred", "some value");
                    assertThat(entity2.getCredentials()).isEmpty();
                }))
                .expectComplete();

        this.spaceId
            .then(spaceId -> getCreateUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId))
            .then(instanceId -> Mono
                .when(
                    Mono.just(instanceId),
                    this.cloudFoundryClient.userProvidedServiceInstances()
                        .update(UpdateUserProvidedServiceInstanceRequest.builder()
                            .userProvidedServiceInstanceId(instanceId)
                            .name(newInstanceName)
                            .credential("test-cred", "some value")
                            .build())
                        .map(UpdateUserProvidedServiceInstanceResponse::getEntity)
                ))
            .then(function((instanceId, entity1) -> Mono
                .when(
                    Mono.just(entity1),
                    this.cloudFoundryClient.userProvidedServiceInstances()
                        .update(UpdateUserProvidedServiceInstanceRequest.builder()
                            .userProvidedServiceInstanceId(instanceId)
                            .credentials(Collections.emptyMap())
                            .build())
                        .map(UpdateUserProvidedServiceInstanceResponse::getEntity)
                )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    private static Mono<String> getCreateApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getCreateUserProvidedServiceInstanceId(CloudFoundryClient cloudFoundryClient, String instanceName, String spaceId) {
        return requestCreateUserProvidedServiceInstance(cloudFoundryClient, instanceName, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateUserProvidedServiceInstanceResponse> requestCreateUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String instanceName, String spaceId) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .create(CreateUserProvidedServiceInstanceRequest.builder()
                .name(instanceName)
                .spaceId(spaceId)
                .build());
    }

    private static Flux<UserProvidedServiceInstanceResource> requestListUserProvidedServiceInstances(CloudFoundryClient cloudFoundryClient, String instanceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.userProvidedServiceInstances()
                .list(ListUserProvidedServiceInstancesRequest.builder()
                    .name(instanceName)
                    .page(page)
                    .build()));
    }

    private static ScriptedSubscriber<Tuple3<String, String, ServiceBindingResource>> serviceBindingEquality() {
        return ScriptedSubscriber.<Tuple3<String, String, ServiceBindingResource>>create()
            .consumeNextWith(consumer((applicationId, instanceId, resource) -> {
                assertThat(resource.getEntity().getApplicationId()).isEqualTo(applicationId);
                assertThat(resource.getEntity().getServiceInstanceId()).isEqualTo(instanceId);
            }))
            .expectComplete();
    }

}