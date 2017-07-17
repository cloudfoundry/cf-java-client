/*
 * Copyright 2013-2017 the original author or authors.
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
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.ListServiceServicePlansRequest;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
import org.cloudfoundry.operations.routes.CreateRouteRequest;
import org.cloudfoundry.operations.services.BindRouteServiceInstanceRequest;
import org.cloudfoundry.operations.services.CreateServiceInstanceRequest;
import org.cloudfoundry.operations.services.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.operations.services.DeleteServiceInstanceRequest;
import org.cloudfoundry.operations.services.GetServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public final class ServicesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private String organizationName;

    @Autowired
    private String planName;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private String serviceName;

    @Autowired
    private String spaceName;

    //TODO: Ready to implement
    @Ignore("Ready to implement")
    @Test
    public void bindRoutePrivateDomain() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();
        String userProvidedServiceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono
            .when(
                requestCreatePrivateDomain(this.cloudFoundryOperations, domainName, this.organizationName),
                requestCreateRoute(this.cloudFoundryOperations, domainName, hostName, path, this.spaceName),
                requestCreateUserProvidedServiceInstance(this.cloudFoundryOperations, userProvidedServiceInstanceName)
            )
            .then(this.cloudFoundryOperations.services()
                .bindRoute(BindRouteServiceInstanceRequest.builder()
                    .domainName(domainName)
                    .hostname(hostName)
                    .parameter("integration-test-key", "integration-test-value")
                    .path(path)
                    .serviceInstanceName(userProvidedServiceInstanceName)
                    .build()))
            .then(this.cloudFoundryOperations.services()
                .getInstance(GetServiceInstanceRequest.builder()
                    .name(userProvidedServiceInstanceName)
                    .build()))
            .as(StepVerifier::create)
            //TEST
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.cloudFoundryOperations.services()
            .createInstance(CreateServiceInstanceRequest.builder()
                .planName(this.planName)
                .serviceName(this.serviceName)
                .serviceInstanceName(serviceInstanceName)
                .build())
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, serviceInstanceName)
                .map(resource -> ResourceUtils.getEntity(resource).getName()))
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName))
            .then(this.cloudFoundryOperations.services()
                .deleteInstance(DeleteServiceInstanceRequest.builder()
                    .name(serviceInstanceName)
                    .build()))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, serviceInstanceName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Ready to implement
    @Ignore("Ready to implement")
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

    private static Mono<String> getServicePlanName(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .single();
    }

    private static Mono<Void> requestCreatePrivateDomain(CloudFoundryOperations cloudFoundryOperations, String domainName, String organizationName) {
        return cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build());
    }

    private static Mono<Integer> requestCreateRoute(CloudFoundryOperations cloudFoundryOperations, String domainName, String hostName, String path, String spaceName) {
        return cloudFoundryOperations.routes()
            .create(CreateRouteRequest.builder()
                .domain(domainName)
                .host(hostName)
                .path(path)
                .space(spaceName)
                .build());
    }

    private static Mono<Void> requestCreateServiceInstance(CloudFoundryOperations cloudFoundryOperations, String planName, String serviceInstanceName, String serviceName) {
        return cloudFoundryOperations.services()
            .createInstance(CreateServiceInstanceRequest.builder()
                .planName(planName)
                .serviceName(serviceName)
                .serviceInstanceName(serviceInstanceName)
                .build());
    }

    private static Mono<Void> requestCreateUserProvidedServiceInstance(CloudFoundryOperations cloudFoundryOperations, String userProvidedServiceInstanceName) {
        return cloudFoundryOperations.services()
            .createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest.builder()
                .name(userProvidedServiceInstanceName)
                .routeServiceUrl("https://test.route.service.url")
                .build());
    }

    private static Flux<ServiceInstanceResource> requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceInstanceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceInstances()
                .list(ListServiceInstancesRequest.builder()
                    .name(serviceInstanceName)
                    .page(page)
                    .build()));
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .listServicePlans(ListServiceServicePlansRequest.builder()
                    .page(page)
                    .serviceId(serviceId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

}
