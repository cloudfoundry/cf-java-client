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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.DeleteServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.ListServiceServicePlansRequest;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ServicesTest extends AbstractIntegrationTest {

    private final Path application = new ClassPathResource("test-service-broker.jar").getFile().toPath();

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private String planName;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private String serviceName;

    @Autowired
    private Mono<String> spaceId;

    public ServicesTest() throws IOException {
    }

    @Test
    public void delete() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();

        ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .then(organizationId -> createServiceBroker(this.cloudFoundryClient, organizationId, serviceBrokerName, serviceName, planName))
            .block(Duration.ofMinutes(5));

        getServiceId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            .flatMapMany(serviceId -> deleteServicePlans(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId, serviceId)
                .thenMany(Mono.just(serviceId)))
            .flatMap(serviceId -> this.cloudFoundryClient.services()
                .delete(DeleteServiceRequest.builder()
                    .async(true)
                    .serviceId(serviceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            .thenMany(requestListServices(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();

        ServicesTest.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .then(organizationId -> createServiceBroker(this.cloudFoundryClient, organizationId, serviceBrokerName, serviceName, planName))
            .block(Duration.ofMinutes(5));

        getServiceId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            .flatMapMany(serviceId -> deleteServicePlans(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId, serviceId)
                .thenMany(Mono.just(serviceId)))
            .flatMap(serviceId -> this.cloudFoundryClient.services()
                .delete(DeleteServiceRequest.builder()
                    .async(false)
                    .serviceId(serviceId)
                    .build())
                .then(Mono.just(serviceBrokerMetadata.serviceBrokerId)))
            .thenMany(requestListServices(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(1));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void deletePurge() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();

        ServicesTest.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .then(organizationId -> createServiceBroker(this.cloudFoundryClient, organizationId, serviceBrokerName, serviceName, planName))
            .block(Duration.ofMinutes(5));

        getServiceId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            .then(serviceId -> this.cloudFoundryClient.services()
                .delete(DeleteServiceRequest.builder()
                    .async(true)
                    .purge(true)
                    .serviceId(serviceId)
                    .build()))
            .then(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            .thenMany(requestListServices(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(1));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        this.serviceBrokerId
            .then(serviceBrokerId -> getServiceId(this.cloudFoundryClient, serviceBrokerId))
            .then(serviceId -> this.cloudFoundryClient.services()
                .get(GetServiceRequest.builder()
                    .serviceId(serviceId)
                    .build()))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .list(ListServicesRequest.builder()
                        .page(page)
                        .build()))
                .filter(resource -> serviceBrokerId.equals(ResourceUtils.getEntity(resource).getServiceBrokerId())))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByActive() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .list(ListServicesRequest.builder()
                        .active(true)
                        .page(page)
                        .build()))
                .filter(resource -> serviceBrokerId.equals(ResourceUtils.getEntity(resource).getServiceBrokerId())))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByLabels() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .list(ListServicesRequest.builder()
                        .label(this.serviceName)
                        .page(page)
                        .build()))
                .filter(resource -> serviceBrokerId.equals(ResourceUtils.getEntity(resource).getServiceBrokerId())))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceBrokerIds() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .list(ListServicesRequest.builder()
                        .serviceBrokerId(serviceBrokerId)
                        .page(page)
                        .build())))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listNoneFound() {
        PaginationUtils.requestClientV2Resources(page -> this.cloudFoundryClient.services()
            .list(ListServicesRequest.builder()
                .label("unmatched-filter")
                .page(page)
                .build()))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicePlans() {
        this.serviceBrokerId
            .then(serviceBrokerId -> getServiceId(this.cloudFoundryClient, serviceBrokerId))
            .flatMapMany(serviceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .listServicePlans(ListServiceServicePlansRequest.builder()
                        .serviceId(serviceId)
                        .page(page)
                        .build())))
            .map(response -> response.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicePlansFilterByActive() {
        this.serviceBrokerId
            .then(serviceBrokerId -> getServiceId(this.cloudFoundryClient, serviceBrokerId))
            .flatMapMany(serviceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .listServicePlans(ListServiceServicePlansRequest.builder()
                        .active(true)
                        .serviceId(serviceId)
                        .page(page)
                        .build())))
            .map(response -> response.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicePlansFilterByServiceInstanceIds() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .then(serviceBrokerId -> Mono
                .when(
                    getServiceId(this.cloudFoundryClient, serviceBrokerId),
                    this.spaceId
                ))
            .then(function((serviceId, spaceId) -> Mono
                .when(
                    Mono.just(serviceId),
                    createServiceInstanceId(this.cloudFoundryClient, serviceInstanceName, serviceId, spaceId)
                )))
            .flatMapMany(function((serviceId, serviceInstanceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .listServicePlans(ListServiceServicePlansRequest.builder()
                        .serviceId(serviceId)
                        .serviceInstanceId(serviceInstanceId)
                        .page(page)
                        .build()))))
            .map(response -> response.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicePlansNoneFound() {
        this.serviceBrokerId
            .then(serviceBrokerId -> getServiceId(this.cloudFoundryClient, serviceBrokerId))
            .flatMapMany(serviceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .listServicePlans(ListServiceServicePlansRequest.builder()
                        .serviceId(serviceId)
                        .serviceInstanceId("unmatched-filter")
                        .page(page)
                        .build())))
            .map(response -> response.getEntity().getName())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> deleteServiceBroker(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .delete(DeleteApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Flux<DeleteServicePlanResponse> deleteServicePlans(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceId) {
        return listServicePlanIds(cloudFoundryClient, serviceBrokerId, serviceId)
            .flatMap(servicePlanId -> requestDeleteServicePlan(cloudFoundryClient, servicePlanId));
    }

    private static Mono<String> getServiceId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .page(page)
                    .build()))
            .filter(resource -> serviceBrokerId.equals(ResourceUtils.getEntity(resource).getServiceBrokerId()))
            .single()
            .map(ResourceUtils::getId);
    }

    private static Flux<String> listServicePlanIds(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .serviceId(serviceId)
                    .build()))
            .map(ResourceUtils::getId);
    }

    private static Mono<ApplicationInstancesResponse> requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<AssociateApplicationRouteResponse> requestAssociateApplicationRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.applicationsV2()
            .associateRoute(AssociateApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
                .memory(768)
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId, String hostName) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
                .host(hostName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String name, String servicePlanId, String spaceId) {
        return cloudFoundryClient.serviceInstances()
            .create(CreateServiceInstanceRequest.builder()
                .name(name)
                .servicePlanId(servicePlanId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<DeleteServicePlanResponse> requestDeleteServicePlan(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        return cloudFoundryClient.servicePlans()
            .delete(DeleteServicePlanRequest.builder()
                .servicePlanId(servicePlanId)
                .build());
    }

    private static Mono<GetApplicationResponse> requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
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
                    .serviceBrokerId(serviceBrokerId)
                    .page(page)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Mono<UpdateApplicationResponse> requestUpdateApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String state, String serviceName, String planName) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .environmentJson("SERVICE_NAME", serviceName)
                .environmentJson("PLAN_NAME", planName)
                .state(state)
                .build());
    }

    private static Mono<UploadApplicationResponse> requestUploadApplication(CloudFoundryClient cloudFoundryClient, String applicationId, Path application) {
        return cloudFoundryClient.applicationsV2()
            .upload(UploadApplicationRequest.builder()
                .application(application)
                .applicationId(applicationId)
                .async(true)
                .build());
    }

    private Mono<ServiceBrokerMetadata> createServiceBroker(CloudFoundryClient cloudFoundryClient, String organizationId, String serviceBrokerName, String serviceName, String planName) {
        return pushServiceBroker(cloudFoundryClient, organizationId, serviceName, planName)
            .then(applicationMetadata -> this.cloudFoundryClient.serviceBrokers()
                .create(CreateServiceBrokerRequest.builder()
                    .authenticationPassword("test-authentication-password")
                    .authenticationUsername("test-authentication-username")
                    .brokerUrl(applicationMetadata.uri)
                    .name(serviceBrokerName)
                    .spaceId(applicationMetadata.spaceId)
                    .build())
                .map(response -> new ServiceBrokerMetadata(applicationMetadata, ResourceUtils.getId(response))));
    }

    private Mono<String> createServiceInstanceId(CloudFoundryClient cloudFoundryClient, String name, String serviceId, String spaceId) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
            .single()
            .map(ResourceUtils::getId)
            .then(servicePlanId -> requestCreateServiceInstance(cloudFoundryClient, name, servicePlanId, spaceId)
                .map(ResourceUtils::getId));
    }

    private Mono<ApplicationMetadata> pushServiceBroker(CloudFoundryClient cloudFoundryClient, String organizationId, String serviceName, String planName) {
        String applicationName = this.nameFactory.getApplicationName();
        String hostName = this.nameFactory.getHostName();
        String spaceName = this.nameFactory.getSpaceName();

        return Mono
            .when(
                requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
                    .map(ResourceUtils::getId),
                requestListSharedDomains(cloudFoundryClient)
                    .next()
            )
            .then(function((spaceId, domain) -> Mono
                .when(
                    requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
                        .map(ResourceUtils::getId),
                    Mono.just(ResourceUtils.getId(domain))
                        .then(domainId -> requestCreateRoute(cloudFoundryClient, domainId, spaceId, hostName))
                        .map(ResourceUtils::getId)
                )
                .then(function((applicationId, routeId) -> requestAssociateApplicationRoute(cloudFoundryClient, applicationId, routeId)
                    .then(Mono.just(applicationId))))
                .then(applicationId -> requestUploadApplication(cloudFoundryClient, applicationId, this.application)
                    .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, Duration.ofMinutes(5), job))
                    .then(Mono.just(applicationId)))
                .then(applicationId -> requestUpdateApplication(cloudFoundryClient, applicationId, "STARTED", serviceName, planName)
                    .then(Mono.just(applicationId)))
                .then(applicationId -> requestGetApplication(cloudFoundryClient, applicationId)
                    .map(response -> ResourceUtils.getEntity(response).getPackageState())
                    .filter(state -> "STAGED".equals(state) || "FAILED".equals(state))
                    .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)))
                    .then(Mono.just(applicationId)))
                .then(applicationId -> requestApplicationInstances(cloudFoundryClient, applicationId)
                    .flatMapMany(response -> Flux.fromIterable(response.getInstances().values()))
                    .single()
                    .map(ApplicationInstanceInfo::getState)
                    .filter("RUNNING"::equals)
                    .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)))
                    .map(status -> new ApplicationMetadata(applicationId, spaceId, String.format("https://%s.%s", hostName, ResourceUtils.getEntity(domain).getName()))))
            ));
    }

    private static final class ApplicationMetadata {

        private final String applicationId;

        private final String spaceId;

        private final String uri;

        private ApplicationMetadata(String applicationId, String spaceId, String uri) {
            this.applicationId = applicationId;
            this.spaceId = spaceId;
            this.uri = uri;
        }

    }

    private static final class ServiceBrokerMetadata {

        private final ApplicationMetadata applicationMetadata;

        private final String serviceBrokerId;

        private ServiceBrokerMetadata(ApplicationMetadata applicationMetadata, String serviceBrokerId) {
            this.applicationMetadata = applicationMetadata;
            this.serviceBrokerId = serviceBrokerId;
        }

    }

}
