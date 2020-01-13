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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceResource;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.ApplicationHealthCheck;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
import org.cloudfoundry.operations.routes.CreateRouteRequest;
import org.cloudfoundry.operations.routes.ListRoutesRequest;
import org.cloudfoundry.operations.routes.Route;
import org.cloudfoundry.operations.services.BindRouteServiceInstanceRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.cloudfoundry.operations.services.CreateServiceInstanceRequest;
import org.cloudfoundry.operations.services.CreateServiceKeyRequest;
import org.cloudfoundry.operations.services.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.operations.services.DeleteServiceInstanceRequest;
import org.cloudfoundry.operations.services.DeleteServiceKeyRequest;
import org.cloudfoundry.operations.services.GetServiceInstanceRequest;
import org.cloudfoundry.operations.services.GetServiceKeyRequest;
import org.cloudfoundry.operations.services.ListServiceKeysRequest;
import org.cloudfoundry.operations.services.ListServiceOfferingsRequest;
import org.cloudfoundry.operations.services.RenameServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.cloudfoundry.operations.services.ServiceInstanceSummary;
import org.cloudfoundry.operations.services.ServiceKey;
import org.cloudfoundry.operations.services.ServiceOffering;
import org.cloudfoundry.operations.services.UnbindRouteServiceInstanceRequest;
import org.cloudfoundry.operations.services.UnbindServiceInstanceRequest;
import org.cloudfoundry.operations.services.UpdateServiceInstanceRequest;
import org.cloudfoundry.operations.services.UpdateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.routes.Level.SPACE;

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

    @Test
    public void bindRoutePrivateDomain() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();
        String userProvidedServiceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono
            .when(
                requestCreatePrivateDomain(this.cloudFoundryOperations, domainName, this.organizationName),
                requestCreateUserProvidedServiceInstance(this.cloudFoundryOperations, userProvidedServiceInstanceName)
            )
            .then(requestCreateRoute(this.cloudFoundryOperations, domainName, hostName, path, this.spaceName))
            .then(this.cloudFoundryOperations.services()
                .bindRoute(BindRouteServiceInstanceRequest.builder()
                    .domainName(domainName)
                    .hostname(hostName)
                    .parameter("integration-test-key", "integration-test-value")
                    .path(path)
                    .serviceInstanceName(userProvidedServiceInstanceName)
                    .build()))
            .thenMany(requestListRoutes(this.cloudFoundryOperations))
            .filter(response -> domainName.equals(response.getDomain()))
            .map(Route::getHost)
            .as(StepVerifier::create)
            .expectNext(hostName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void bindService() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono
            .when(
                requestCreateApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName),
                requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName)
            )
            .then(this.cloudFoundryOperations.services()
                .bind(BindServiceInstanceRequest.builder()
                    .applicationName(applicationName)
                    .serviceInstanceName(serviceInstanceName)
                    .build()))
            .then(getApplicationId(this.cloudFoundryOperations, applicationName))
            .flatMapMany(applicationId -> requestListServiceBindings(this.cloudFoundryClient, applicationId))
            .as(StepVerifier::create)
            .expectNextCount(1)
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
            .thenMany(requestListServiceInstances(this.cloudFoundryOperations)
                .map(ServiceInstanceSummary::getName))
            .filter(serviceInstanceName::equals)
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createServiceKey() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName)
            .then(this.cloudFoundryOperations.services()
                .createServiceKey(CreateServiceKeyRequest.builder()
                    .parameter("test-key", "test-value")
                    .serviceInstanceName(serviceInstanceName)
                    .serviceKeyName(serviceKeyName)
                    .build()))
            .thenMany(requestGetServiceKey(this.cloudFoundryOperations, serviceInstanceName, serviceKeyName)
                .map(ServiceKey::getName))
            .as(StepVerifier::create)
            .expectNext(serviceKeyName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createUserProvidedServiceInstance() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.cloudFoundryOperations.services()
            .createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest.builder()
                .credential("test-credential-key", "test-credential-value")
                .name(serviceInstanceName)
                .syslogDrainUrl("test.url")
                .routeServiceUrl("https://test.url")
                .build())
            .thenMany(requestListServiceInstances(this.cloudFoundryOperations)
                .map(ServiceInstanceSummary::getName))
            .filter(serviceInstanceName::equals)
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
            .thenMany(requestListServiceInstances(this.cloudFoundryOperations)
                .map(ServiceInstanceSummary::getName))
            .filter(serviceInstanceName::equals)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteServiceKey() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName)
            .then(requestCreateServiceKey(this.cloudFoundryOperations, serviceKeyName, serviceInstanceName))
            .then(this.cloudFoundryOperations.services()
                .deleteServiceKey(DeleteServiceKeyRequest.builder()
                    .serviceInstanceName(serviceInstanceName)
                    .serviceKeyName(serviceKeyName)
                    .build()))
            .thenMany(requestGetServiceKey(this.cloudFoundryOperations, serviceInstanceName, serviceKeyName))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service key %s does not exist", serviceKeyName))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getManagedService() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName)
            .then(this.cloudFoundryOperations.services()
                .getInstance(GetServiceInstanceRequest.builder()
                    .name(serviceInstanceName)
                    .build())
                .map(ServiceInstance::getPlan))
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getServiceKey() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName)
            .then(requestCreateServiceKey(this.cloudFoundryOperations, serviceKeyName, serviceInstanceName))
            .thenMany(this.cloudFoundryOperations.services()
                .getServiceKey(GetServiceKeyRequest.builder()
                    .serviceInstanceName(serviceInstanceName)
                    .serviceKeyName(serviceKeyName)
                    .build())
                .map(ServiceKey::getName))
            .as(StepVerifier::create)
            .expectNext(serviceKeyName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceKey() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName)
            .then(requestCreateServiceKey(this.cloudFoundryOperations, serviceKeyName, serviceInstanceName))
            .thenMany(this.cloudFoundryOperations.services()
                .listServiceKeys(ListServiceKeysRequest.builder()
                    .serviceInstanceName(serviceInstanceName)
                    .build())
                .map(ServiceKey::getName))
            .as(StepVerifier::create)
            .expectNext(serviceKeyName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceOfferings() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName)
            .thenMany(this.cloudFoundryOperations.services()
                .listServiceOfferings(ListServiceOfferingsRequest.builder()
                    .serviceName(this.serviceName)
                    .build())
                .map(ServiceOffering::getDescription))
            .as(StepVerifier::create)
            .expectNext("test-service-description")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void rename() {
        String serviceInstanceName1 = this.nameFactory.getServiceInstanceName();
        String serviceInstanceName2 = this.nameFactory.getServiceInstanceName();

        requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName1, this.serviceName)
            .then(this.cloudFoundryOperations.services()
                .renameInstance(RenameServiceInstanceRequest.builder()
                    .name(serviceInstanceName1)
                    .newName(serviceInstanceName2)
                    .build()))
            .thenMany(requestListServiceInstances(this.cloudFoundryOperations)
                .map(ServiceInstanceSummary::getName))
            .filter(serviceInstanceName2::equals)
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName2)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unbindRoute() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono
            .when(
                requestCreatePrivateDomain(this.cloudFoundryOperations, domainName, this.organizationName),
                requestCreateUserProvidedServiceInstance(this.cloudFoundryOperations, serviceInstanceName)
            )
            .then(requestCreateRoute(this.cloudFoundryOperations, domainName, hostName, path, this.spaceName))
            .then(requestBindRoute(this.cloudFoundryOperations, domainName, hostName, path, serviceInstanceName))
            .then(this.cloudFoundryOperations.services()
                .unbindRoute(UnbindRouteServiceInstanceRequest.builder()
                    .domainName(domainName)
                    .hostname(hostName)
                    .path(path)
                    .serviceInstanceName(serviceInstanceName)
                    .build()))
            .thenMany(requestGetInstance(this.cloudFoundryOperations, serviceInstanceName))
            .filter(response -> domainName.equals(response.getLastOperation()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unbindService() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono
            .when(
                requestCreateApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName),
                requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName)
            )
            .then(requestBindService(this.cloudFoundryOperations, applicationName, serviceInstanceName))
            .then(this.cloudFoundryOperations.services()
                .unbind(UnbindServiceInstanceRequest.builder()
                    .applicationName(applicationName)
                    .serviceInstanceName(serviceInstanceName)
                    .build()))
            .then(getApplicationId(this.cloudFoundryOperations, applicationName))
            .flatMapMany(applicationId -> requestListServiceBindings(this.cloudFoundryClient, applicationId))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateInstance() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> requestCreateServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName))
            .then(this.cloudFoundryOperations.services()
                .updateInstance(UpdateServiceInstanceRequest.builder()
                    .serviceInstanceName(serviceInstanceName)
                    .tag("test-tag")
                    .build()))
            .then(requestGetInstance(this.cloudFoundryOperations, serviceInstanceName)
                .map(serviceInstance -> serviceInstance.getTags().get(0)))
            .as(StepVerifier::create)
            .expectNext("test-tag")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateUserProvidedInstance() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> requestCreateUserProvidedServiceInstance(this.cloudFoundryOperations, serviceInstanceName))
            .then(this.cloudFoundryOperations.services()
                .updateUserProvidedInstance(UpdateUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceName(serviceInstanceName)
                    .syslogDrainUrl("test.url")
                    .build()))
            .thenMany(requestListUserProvidedServiceInstances(this.cloudFoundryClient, serviceInstanceName)
                .map(r -> r.getEntity().getSyslogDrainUrl()))
            .as(StepVerifier::create)
            .expectNext("test.url")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> getApplicationId(CloudFoundryOperations cloudFoundryOperations, String applicationName) {
        return requestGetApplication(cloudFoundryOperations, applicationName)
            .map(ApplicationDetail::getId);
    }

    private static Mono<Void> requestBindRoute(CloudFoundryOperations cloudFoundryOperations, String domainName, String hostName, String path, String userProvidedServiceInstanceName) {
        return cloudFoundryOperations.services()
            .bindRoute(BindRouteServiceInstanceRequest.builder()
                .domainName(domainName)
                .hostname(hostName)
                .path(path)
                .serviceInstanceName(userProvidedServiceInstanceName)
                .build());
    }

    private static Mono<Void> requestBindService(CloudFoundryOperations cloudFoundryOperations, String applicationName, String serviceInstanceName) {
        return cloudFoundryOperations.services()
            .bind(BindServiceInstanceRequest.builder()
                .applicationName(applicationName)
                .serviceInstanceName(serviceInstanceName)
                .build());
    }

    private static Mono<Void> requestCreateApplication(CloudFoundryOperations cloudFoundryOperations, Path application, String name) {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .path(application)
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .memory(64)
                .name(name)
                .noStart(true)
                .build());
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

    private static Mono<Void> requestCreateServiceKey(CloudFoundryOperations cloudFoundryOperations, String serviceKeyName, String serviceInstanceName) {
        return cloudFoundryOperations.services()
            .createServiceKey(CreateServiceKeyRequest.builder()
                .serviceInstanceName(serviceInstanceName)
                .serviceKeyName(serviceKeyName)
                .build());
    }

    private static Mono<Void> requestCreateUserProvidedServiceInstance(CloudFoundryOperations cloudFoundryOperations, String userProvidedServiceInstanceName) {
        return cloudFoundryOperations.services()
            .createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest.builder()
                .name(userProvidedServiceInstanceName)
                .routeServiceUrl("https://test.route.service.url")
                .build());
    }

    private static Mono<ApplicationDetail> requestGetApplication(CloudFoundryOperations cloudFoundryOperations, String applicationName) {
        return cloudFoundryOperations.applications()
            .get(GetApplicationRequest.builder()
                .name(applicationName)
                .build());
    }

    private static Mono<ServiceInstance> requestGetInstance(CloudFoundryOperations cloudFoundryOperations, String userProvidedServiceInstanceName) {
        return cloudFoundryOperations.services()
            .getInstance(GetServiceInstanceRequest.builder()
                .name(userProvidedServiceInstanceName)
                .build());
    }

    private static Mono<ServiceKey> requestGetServiceKey(CloudFoundryOperations cloudFoundryOperations, String serviceInstanceName, String serviceKeyName) {
        return cloudFoundryOperations.services()
            .getServiceKey(GetServiceKeyRequest.builder()
                .serviceInstanceName(serviceInstanceName)
                .serviceKeyName(serviceKeyName)
                .build());
    }

    private static Flux<Route> requestListRoutes(CloudFoundryOperations cloudFoundryOperations) {
        return cloudFoundryOperations.routes()
            .list(ListRoutesRequest.builder()
                .level(SPACE)
                .build());
    }

    private static Flux<ServiceBindingResource> requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceBindingsV2()
                .list(ListServiceBindingsRequest.builder()
                    .applicationId(applicationId)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceInstanceSummary> requestListServiceInstances(CloudFoundryOperations cloudFoundryOperations) {
        return cloudFoundryOperations.services()
            .listInstances();
    }

    private static Flux<UserProvidedServiceInstanceResource> requestListUserProvidedServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceInstanceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.userProvidedServiceInstances()
                .list(ListUserProvidedServiceInstancesRequest.builder()
                    .name(serviceInstanceName)
                    .page(page)
                    .build()));
    }

}
