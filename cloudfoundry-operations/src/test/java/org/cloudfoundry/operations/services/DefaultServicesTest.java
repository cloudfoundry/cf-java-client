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

package org.cloudfoundry.operations.services;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v2.MaintenanceInfo;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.jobs.ErrorDetails;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.LastOperation;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysResponse;
import org.cloudfoundry.client.v2.serviceinstances.Plan;
import org.cloudfoundry.client.v2.serviceinstances.Service;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyEntity;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyResource;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityEntity;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityResource;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceResponse;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.RemoveUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceEntity;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultServicesTest extends AbstractOperationsTest {

    private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

    @Test
    public void bindRouteServiceInstanceAlreadyBound() {
        requestListOrganizationPrivateDomains(this.cloudFoundryClient, "test-domain-name", TEST_ORGANIZATION_ID);
        requestListSpaceServiceInstancesUserProvided(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListRoutes(this.cloudFoundryClient, "test-private-domain-id");
        requestAssociateUserProvidedServiceInstanceRouteError(this.cloudFoundryClient, "test-route-id", "test-service-instance-id",
            Collections.singletonMap("test-parameter-key", "test-parameter-value"), 130008);

        this.services
            .bindRoute(BindRouteServiceInstanceRequest.builder()
                .domainName("test-domain-name")
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void bindRouteServiceInstanceDomainNotFound() {
        requestListOrganizationPrivateDomainsEmpty(this.cloudFoundryClient, "test-domain-name", TEST_ORGANIZATION_ID);
        requestListSharedDomainsEmpty(this.cloudFoundryClient, "test-domain-name");

        this.services
            .bindRoute(BindRouteServiceInstanceRequest.builder()
                .domainName("test-domain-name")
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Domain test-domain-name not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void bindRouteServiceInstancePrivateDomain() {
        requestListOrganizationPrivateDomains(this.cloudFoundryClient, "test-domain-name", TEST_ORGANIZATION_ID);
        requestListSpaceServiceInstancesUserProvided(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListRoutes(this.cloudFoundryClient, "test-private-domain-id");
        requestAssociateUserProvidedServiceInstanceRoute(this.cloudFoundryClient, "test-route-id", "test-service-instance-id",
            Collections.singletonMap("test-parameter-key", "test-parameter-value"));

        this.services
            .bindRoute(BindRouteServiceInstanceRequest.builder()
                .domainName("test-domain-name")
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void bindRouteServiceInstancePrivateDomainWithHostAndPath() {
        requestListOrganizationPrivateDomains(this.cloudFoundryClient, "test-domain-name", TEST_ORGANIZATION_ID);
        requestListSpaceServiceInstancesUserProvided(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListRoutes(this.cloudFoundryClient, "test-private-domain-id", "test-host", "test-path");
        requestAssociateUserProvidedServiceInstanceRoute(this.cloudFoundryClient, "test-route-id", "test-service-instance-id",
            Collections.singletonMap("test-parameter-key", "test-parameter-value"));

        this.services
            .bindRoute(BindRouteServiceInstanceRequest.builder()
                .domainName("test-domain-name")
                .hostname("test-host")
                .parameter("test-parameter-key", "test-parameter-value")
                .path("test-path")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void bindRouteServiceInstanceServiceInstanceNotFound() {
        requestListOrganizationPrivateDomains(this.cloudFoundryClient, "test-domain-name", TEST_ORGANIZATION_ID);
        requestListSpaceServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListRoutes(this.cloudFoundryClient, "test-private-domain-id");

        this.services
            .bindRoute(BindRouteServiceInstanceRequest.builder()
                .domainName("test-domain-name")
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service instance test-service-instance-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void bindRouteServiceInstanceSharedDomain() {
        requestListOrganizationPrivateDomainsEmpty(this.cloudFoundryClient, "test-domain-name", TEST_ORGANIZATION_ID);
        requestListSharedDomains(this.cloudFoundryClient, "test-domain-name");
        requestListSpaceServiceInstancesUserProvided(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListRoutes(this.cloudFoundryClient, "test-shared-domain-id");
        requestAssociateUserProvidedServiceInstanceRoute(this.cloudFoundryClient, "test-route-id", "test-service-instance-id",
            Collections.singletonMap("test-parameter-key", "test-parameter-value"));

        this.services
            .bindRoute(BindRouteServiceInstanceRequest.builder()
                .domainName("test-domain-name")
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void bindServiceInstance() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestCreateServiceBinding(this.cloudFoundryClient, "test-application-id", "test-service-instance-id", Collections.singletonMap("test-parameter-key", "test-parameter-value"));

        this.services
            .bind(BindServiceInstanceRequest.builder()
                .applicationName("test-application-name")
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void bindServiceInstanceAlreadyBound() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestCreateServiceBindingError(this.cloudFoundryClient, "test-application-id", "test-service-instance-id", Collections.singletonMap("test-parameter-key", "test-parameter-value"), 90003);

        this.services
            .bind(BindServiceInstanceRequest.builder()
                .applicationName("test-application-name")
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void bindServiceInstanceNoApplication() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);

        this.services
            .bind(BindServiceInstanceRequest.builder()
                .applicationName("test-application-name")
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-application-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void bindServiceInstanceNoServiceInstance() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        requestListSpaceServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);

        this.services
            .bind(BindServiceInstanceRequest.builder()
                .applicationName("test-application-name")
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service instance test-service-instance-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createServiceInstance() {
        requestListSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID, "test-service");
        requestListSpaceServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
        requestCreateServiceInstance(this.cloudFoundryClient, TEST_SPACE_ID, "test-plan-id", "test-service-instance", null, null, "test-service-instance-id", "in progress");
        requestGetServiceInstance(this.cloudFoundryClient, "test-service-instance-id", "successful");

        this.services
            .createInstance(CreateServiceInstanceRequest.builder()
                .planName("test-plan")
                .serviceInstanceName("test-service-instance")
                .serviceName("test-service")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createServiceKey() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance", TEST_SPACE_ID);
        requestCreateServiceKey(this.cloudFoundryClient, "test-service-instance-id", "test-service-key",
            Collections.singletonMap("test-parameter-key", "test-parameter-value"));

        this.services
            .createServiceKey(CreateServiceKeyRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance")
                .serviceKeyName("test-service-key")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createServiceKeyNoServiceInstance() {
        requestListSpaceServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance-does-not-exist", TEST_SPACE_ID);

        this.services
            .createServiceKey(CreateServiceKeyRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service-instance-does-not-exist")
                .serviceKeyName("test-service-key")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service instance test-service-instance-does-not-exist does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createUserProvidedServiceInstance() {
        requestCreateUserProvidedServiceInstance(this.cloudFoundryClient, TEST_SPACE_ID, "test-user-provided-service-instance",
            Collections.singletonMap("test-credential-key", "test-credential-value"), "test-route-url", "test-syslog-url", "test-tag", "test-user-provided-service-instance-id");

        this.services
            .createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest.builder()
                .credential("test-credential-key", "test-credential-value")
                .name("test-user-provided-service-instance")
                .routeServiceUrl("test-route-url")
                .syslogDrainUrl("test-syslog-url")
                .tags("test-tag")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceInstance() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestDeleteServiceInstance(this.cloudFoundryClient, "test-service-instance-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");

        StepVerifier.withVirtualTime(() -> this.services
            .deleteInstance(DeleteServiceInstanceRequest.builder()
                .name("test-service-instance-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceInstanceAcceptsIncomplete() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestDeleteServiceInstanceAcceptsIncomplete(this.cloudFoundryClient, "test-service-instance-id");
        requestGetServiceInstanceNotExist(this.cloudFoundryClient, "test-service-instance-id");

        this.services
            .deleteInstance(DeleteServiceInstanceRequest.builder()
                .name("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceInstanceNotFound() {
        requestListSpaceServiceInstancesEmpty(this.cloudFoundryClient, "test-invalid-name", TEST_SPACE_ID);

        this.services
            .deleteInstance(DeleteServiceInstanceRequest.builder()
                .name("test-invalid-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service instance test-invalid-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceKey() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListSpaceServiceInstanceServiceKeys(this.cloudFoundryClient, "test-service-instance-id", "test-service-key-name", "key", "val");
        requestDeleteServiceKey(this.cloudFoundryClient, "test-service-key-id");

        this.services
            .deleteServiceKey(DeleteServiceKeyRequest.builder()
                .serviceInstanceName("test-service-instance-name")
                .serviceKeyName("test-service-key-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceKeyNoServiceInstance() {
        requestListSpaceServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance", TEST_SPACE_ID);

        this.services
            .deleteServiceKey(DeleteServiceKeyRequest.builder()
                .serviceInstanceName("test-service-instance")
                .serviceKeyName("test-service-key")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service instance test-service-instance does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceKeyNoServiceKey() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListSpaceServiceInstanceServiceKeysEmpty(this.cloudFoundryClient, "test-service-instance-id", "test-service-key-not-found");

        this.services
            .deleteServiceKey(DeleteServiceKeyRequest.builder()
                .serviceInstanceName("test-service-instance-name")
                .serviceKeyName("test-service-key-not-found")
                .build()).as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service key test-service-key-not-found does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteUserProvidedServiceInstance() {
        requestListSpaceServiceInstancesUserProvided(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestDeleteUserProvidedServiceInstance(this.cloudFoundryClient, "test-service-instance-id");

        this.services
            .deleteInstance(DeleteServiceInstanceRequest.builder()
                .name("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getServiceInstanceManaged() {
        requestListSpaceServiceInstancesManaged(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service-id");
        requestListSpaceServiceBindings(this.cloudFoundryClient, "test-service-instance-id", "test-application-id");
        requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
        requestGetApplication(this.cloudFoundryClient, "test-application-id", "test-application");

        this.services
            .getInstance(GetServiceInstanceRequest.builder()
                .name("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ServiceInstance.builder())
                .application("test-application")
                .documentationUrl("test-documentation-url")
                .id("test-service-instance-id")
                .lastOperation("test-type")
                .maintenanceInfo(MaintenanceInfo.builder()
                    .description("test-description")
                    .version("test-version")
                    .build())
                .name("test-service-instance-name")
                .plan("test-service-plan")
                .tag("test-tag")
                .type(ServiceInstanceType.MANAGED)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getServiceInstanceNoInstances() {
        requestListSpaceServiceInstancesEmpty(this.cloudFoundryClient, "test-invalid-name", TEST_SPACE_ID);

        this.services
            .getInstance(GetServiceInstanceRequest.builder()
                .name("test-invalid-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service instance test-invalid-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getServiceInstanceUserProvided() {
        requestListSpaceServiceInstancesUserProvided(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListSpaceServiceBindings(this.cloudFoundryClient, "test-service-instance-id", "test-application-id");
        requestGetApplication(this.cloudFoundryClient, "test-application-id", "test-application");

        this.services
            .getInstance(GetServiceInstanceRequest.builder()
                .name("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(ServiceInstance.builder()
                .application("test-application")
                .id("test-service-instance-id")
                .name("test-service-instance-name")
                .type(ServiceInstanceType.USER_PROVIDED)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getServiceKey() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListSpaceServiceInstanceServiceKeys(this.cloudFoundryClient, "test-service-instance-id", "test-service-key-name", "key", "val");

        this.services
            .getServiceKey(GetServiceKeyRequest.builder()
                .serviceInstanceName("test-service-instance-name")
                .serviceKeyName("test-service-key-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(ServiceKey.builder()
                .credential("key", "val")
                .id("test-service-key-id")
                .name("test-service-key-name")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getServiceKeyNoKeys() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListSpaceServiceInstanceServiceKeysEmpty(this.cloudFoundryClient, "test-service-instance-id", "test-service-key-not-found");

        this.services
            .getServiceKey(GetServiceKeyRequest.builder()
                .serviceInstanceName("test-service-instance-name")
                .serviceKeyName("test-service-key-not-found")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service key test-service-key-not-found does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listInstancesManagedServices() {
        requestGetSpaceSummaryManagedServices(this.cloudFoundryClient, TEST_SPACE_ID);

        this.services
            .listInstances()
            .as(StepVerifier::create)
            .expectNext(ServiceInstanceSummary.builder()
                .applications("test-application-name-1", "test-application-name-2")
                .id("test-service-id-2")
                .lastOperation("test-last-operation-description-2")
                .name("test-service-name-2")
                .plan("test-service-plan-name-2")
                .service("test-provided-service-label")
                .type(ServiceInstanceType.MANAGED)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listInstancesNoApplications() {
        requestGetSpaceSummaryManagedServicesNoApplications(this.cloudFoundryClient, TEST_SPACE_ID);

        this.services
            .listInstances()
            .as(StepVerifier::create)
            .expectNext(ServiceInstanceSummary.builder()
                .id("test-service-id")
                .lastOperation("test-last-operation-description")
                .name("test-service-name")
                .plan("test-service-plan-name")
                .service("test-provided-service-label")
                .type(ServiceInstanceType.MANAGED)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listInstancesNoInstances() {
        requestGetSpaceSummaryEmpty(this.cloudFoundryClient, TEST_SPACE_ID);

        this.services
            .listInstances()
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listInstancesOneApplication() {
        requestGetSpaceSummaryManagedServicesOneApplication(this.cloudFoundryClient, TEST_SPACE_ID);

        this.services
            .listInstances()
            .as(StepVerifier::create)
            .expectNext(ServiceInstanceSummary.builder()
                .application("test-application-name")
                .id("test-service-id-1")
                .lastOperation("test-last-operation-description-1")
                .name("test-service-name-1")
                .plan("test-service-plan-name-1")
                .service("test-provided-service-label")
                .type(ServiceInstanceType.MANAGED)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listInstancesUserProvidedServices() {
        requestGetSpaceSummaryUserProvidedServices(this.cloudFoundryClient, TEST_SPACE_ID);

        this.services
            .listInstances()
            .as(StepVerifier::create)
            .expectNext(ServiceInstanceSummary.builder()
                .applications("test-application-name-1", "test-application-name-2")
                .id("test-service-id-2")
                .name("test-service-name-2")
                .type(ServiceInstanceType.USER_PROVIDED)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceKeys() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListSpaceServiceInstanceServiceKeys(this.cloudFoundryClient, "test-service-instance-id", "key", "val");

        this.services
            .listServiceKeys(ListServiceKeysRequest.builder()
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(ServiceKey.builder()
                .credential("key", "val")
                .id("test-service-key-id")
                .name("test-service-key-entity-name")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceKeysEmpty() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestListSpaceServiceInstanceServiceKeysEmpty(this.cloudFoundryClient, "test-service-instance-id");

        this.services
            .listServiceKeys(ListServiceKeysRequest.builder()
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceKeysNoServiceInstance() {
        requestListSpaceServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);

        this.services
            .listServiceKeys(ListServiceKeysRequest.builder()
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service instance test-service-instance-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceOfferings() {
        requestListSpaceServicesTwo(this.cloudFoundryClient, TEST_SPACE_ID, "test-service1", "test-service2");
        requestListSpaceServicePlans(this.cloudFoundryClient, "test-service1-id", "test-service1-plan", "test-service1-plan-id");
        requestListSpaceServicePlans(this.cloudFoundryClient, "test-service2-id", "test-service2-plan", "test-service2-plan-id");

        this.services
            .listServiceOfferings(ListServiceOfferingsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ServiceOffering.builder()
                    .description("test-service1-description")
                    .id("test-service1-id")
                    .label("test-service1")
                    .servicePlan(ServicePlan.builder()
                        .description("test-description")
                        .free(true)
                        .id("test-service1-plan-id")
                        .name("test-service1-plan")
                        .build())
                    .build(),
                ServiceOffering.builder()
                    .description("test-service2-description")
                    .id("test-service2-id")
                    .label("test-service2")
                    .servicePlan(ServicePlan.builder()
                        .description("test-description")
                        .free(true)
                        .id("test-service2-plan-id")
                        .name("test-service2-plan")
                        .build())
                    .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceOfferingsSingle() {
        requestListSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID, "test-service");
        requestListSpaceServicePlans(this.cloudFoundryClient, "test-service-id", "test-service-plan", "test-service-plan-id");

        this.services
            .listServiceOfferings(ListServiceOfferingsRequest.builder()
                .serviceName("test-service")
                .build())
            .as(StepVerifier::create)
            .expectNext(ServiceOffering.builder()
                .description("test-service-description")
                .id("test-service-id")
                .label("test-service")
                .servicePlan(ServicePlan.builder()
                    .description("test-description")
                    .free(true)
                    .id("test-service-plan-id")
                    .name("test-service-plan")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void renameServiceInstance() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestRenameServiceInstance(this.cloudFoundryClient, "test-service-instance-id", "test-service-instance-new-name");

        this.services
            .renameInstance(RenameServiceInstanceRequest.builder()
                .name("test-service-instance-name")
                .newName("test-service-instance-new-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unbindRoute() {
        requestListOrganizationPrivateDomains(this.cloudFoundryClient, "test-domain-name", TEST_ORGANIZATION_ID);
        requestListRoutes(this.cloudFoundryClient, "test-private-domain-id");
        requestListSpaceServiceInstancesUserProvided(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestRemoveUserProvidedServiceInstanceRoute(this.cloudFoundryClient, "test-route-id", "test-service-instance-id");

        this.services
            .unbindRoute(UnbindRouteServiceInstanceRequest.builder()
                .domainName("test-domain-name")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unbindRouteServiceInstanceRouteNotFound() {
        requestListOrganizationPrivateDomains(this.cloudFoundryClient, "test-domain-name", TEST_ORGANIZATION_ID);
        requestListRoutesEmpty(this.cloudFoundryClient, "test-private-domain-id");
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);

        this.services
            .unbindRoute(UnbindRouteServiceInstanceRequest.builder()
                .domainName("test-domain-name")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Route test-domain-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unbindRouteServiceInstanceServiceInstanceNotFound() {
        requestListOrganizationPrivateDomains(this.cloudFoundryClient, "test-domain-name", TEST_ORGANIZATION_ID);
        requestListRoutes(this.cloudFoundryClient, "test-private-domain-id");
        requestListSpaceServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);

        this.services
            .unbindRoute(UnbindRouteServiceInstanceRequest.builder()
                .domainName("test-domain-name")
                .serviceInstanceName("test-service-instance-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service instance test-service-instance-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unbindServiceInstance() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestApplicationsListServiceBindings(this.cloudFoundryClient, "test-application-id", "test-service-instance-id");
        requestDeleteServiceBinding(this.cloudFoundryClient, "test-service-binding-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");

        StepVerifier.withVirtualTime(() -> this.services
            .unbind(UnbindServiceInstanceRequest.builder()
                .applicationName("test-application-name")
                .serviceInstanceName("test-service-instance-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unbindServiceInstanceFailure() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        requestApplicationsListServiceBindings(this.cloudFoundryClient, "test-application-id", "test-service-instance-id");
        requestDeleteServiceBinding(this.cloudFoundryClient, "test-service-binding-id");
        requestJobFailure(this.cloudFoundryClient, "test-job-entity-id");

        StepVerifier.withVirtualTime(() -> this.services
            .unbind(UnbindServiceInstanceRequest.builder()
                .applicationName("test-application-name")
                .serviceInstanceName("test-service-instance-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessage("test-error-details-errorCode(1): test-error-details-description"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateService() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-service-plan-id");
        requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service-id");
        requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
        requestListSpaceServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
        requestUpdateServiceInstance(this.cloudFoundryClient, Collections.singletonMap("test-parameter-key", "test-parameter-value"), "test-service-instance-id", "test-plan-id",
            Collections.singletonList("test-tag"));
        requestGetServiceInstance(this.cloudFoundryClient, "test-id", "successful");

        this.services
            .updateInstance(UpdateServiceInstanceRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .planName("test-plan")
                .serviceInstanceName("test-service")
                .tag("test-tag")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServiceNewPlanDoesNotExist() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-service-plan-id");
        requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service-id");
        requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
        requestListSpaceServicePlans(this.cloudFoundryClient, "test-service-id", "test-other-plan-not-this-one", "test-plan-id");

        this.services
            .updateInstance(UpdateServiceInstanceRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .planName("test-plan")
                .serviceInstanceName("test-service")
                .tag("test-tag")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("New service plan test-plan not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServiceNoParameters() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-service-plan-id");
        requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service-id");
        requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
        requestListSpaceServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-service-plan-id");
        requestUpdateServiceInstance(this.cloudFoundryClient, null, "test-service-instance-id", "test-service-plan-id", Collections.singletonList("test-tag"));
        requestGetServiceInstance(this.cloudFoundryClient, "test-id", "successful");

        this.services
            .updateInstance(UpdateServiceInstanceRequest.builder()
                .planName("test-plan")
                .serviceInstanceName("test-service")
                .tag("test-tag")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServiceNoPlan() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID);
        requestUpdateServiceInstance(this.cloudFoundryClient, Collections.singletonMap("test-parameter-key", "test-parameter-value"), "test-service-instance-id", null,
            Collections.singletonList("test-tag"));
        requestGetServiceInstance(this.cloudFoundryClient, "test-id", "successful");

        this.services
            .updateInstance(UpdateServiceInstanceRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .serviceInstanceName("test-service")
                .tag("test-tag")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServiceNoPlanExists() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, null);

        this.services
            .updateInstance(UpdateServiceInstanceRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .planName("test-plan")
                .serviceInstanceName("test-service")
                .tag("test-tag")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Plan does not exist for the test-name service"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServiceNoTags() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-service-plan-id");
        requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service-id");
        requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
        requestListSpaceServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
        requestUpdateServiceInstance(this.cloudFoundryClient, Collections.singletonMap("test-parameter-key", "test-parameter-value"), "test-service-instance-id", "test-plan-id", null);
        requestGetServiceInstance(this.cloudFoundryClient, "test-id", "successful");

        this.services
            .updateInstance(UpdateServiceInstanceRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .planName("test-plan")
                .serviceInstanceName("test-service")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServiceNotPublic() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-plan-id");
        requestGetServicePlan(this.cloudFoundryClient, "test-plan-id", "test-service-plan", "test-service-id");
        requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
        requestListSpaceServicePlansNotPublic(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
        requestListSpaceServicePlanVisibilities(this.cloudFoundryClient, "test-organization-id", "test-plan-id");
        requestUpdateServiceInstance(this.cloudFoundryClient, Collections.singletonMap("test-parameter-key", "test-parameter-value"), "test-service-instance-id", "test-plan-id",
            Collections.singletonList("test-tag"));
        requestGetServiceInstance(this.cloudFoundryClient, "test-id", "successful");

        this.services
            .updateInstance(UpdateServiceInstanceRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .planName("test-plan")
                .serviceInstanceName("test-service")
                .tag("test-tag")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServiceNotVisible() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-plan-id");
        requestGetServicePlan(this.cloudFoundryClient, "test-plan-id", "test-service-plan", "test-service-id");
        requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
        requestListSpaceServicePlansNotPublic(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
        requestListSpaceServicePlanVisibilitiesEmpty(this.cloudFoundryClient, "test-organization-id", "test-plan-id");

        this.services
            .updateInstance(UpdateServiceInstanceRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .planName("test-plan")
                .serviceInstanceName("test-service")
                .tag("test-tag")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service Plan test-plan is not visible to your organization"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServicePlanNotUpdateable() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-service-plan-id");
        requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service-id");
        requestGetServiceNotPlanUpdateable(this.cloudFoundryClient, "test-service-id", "test-service");

        this.services
            .updateInstance(UpdateServiceInstanceRequest.builder()
                .parameter("test-parameter-key", "test-parameter-value")
                .planName("test-plan")
                .serviceInstanceName("test-service")
                .tag("test-tag")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Plan for the test-name service cannot be updated"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateUserProvidedService() {
        requestListSpaceServiceInstancesUserProvided(this.cloudFoundryClient, "test-service", TEST_SPACE_ID);
        requestUpdateUserProvidedServiceInstance(this.cloudFoundryClient, Collections.singletonMap("test-credential-key", "test-credential-value"),
            "syslog-url", Collections.singletonList("tag1"), "test-service-instance-id");

        this.services
            .updateUserProvidedInstance(UpdateUserProvidedServiceInstanceRequest.builder()
                .credential("test-credential-key", "test-credential-value")
                .syslogDrainUrl("syslog-url")
                .tags("tag1")
                .userProvidedServiceInstanceName("test-service")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateUserProvidedServiceNotUserProvided() {
        requestListSpaceServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID);

        this.services
            .updateUserProvidedInstance(UpdateUserProvidedServiceInstanceRequest.builder()
                .userProvidedServiceInstanceName("test-service")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("User provided service instance test-service does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    private static void requestApplications(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .name(applicationName)
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                        .build())
                    .build()));
    }

    private static void requestApplicationsEmpty(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .name(applicationName)
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceApplicationsResponse.builder())
                    .build()));
    }

    private static void requestApplicationsListServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        when(cloudFoundryClient.applicationsV2()
            .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                .page(1)
                .applicationId(applicationId)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListApplicationServiceBindingsResponse.builder())
                    .resource(fill(ServiceBindingResource.builder(), "service-binding-")
                        .entity(ServiceBindingEntity.builder()
                            .applicationId(applicationId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestAssociateUserProvidedServiceInstanceRoute(CloudFoundryClient cloudFoundryClient, String routeId, String userProvidedServiceInstanceId, Map<String, Object> parameters) {
        when(cloudFoundryClient.userProvidedServiceInstances()
            .associateRoute(AssociateUserProvidedServiceInstanceRouteRequest.builder()
                .parameters(parameters)
                .routeId(routeId)
                .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateUserProvidedServiceInstanceRouteResponse.builder())
                    .build()));
    }

    private static void requestAssociateUserProvidedServiceInstanceRouteError(CloudFoundryClient cloudFoundryClient, String routeId, String userProvidedServiceInstanceId,
                                                                              Map<String, Object> parameters, Integer code) {
        when(cloudFoundryClient.userProvidedServiceInstances()
            .associateRoute(AssociateUserProvidedServiceInstanceRouteRequest.builder()
                .parameters(parameters)
                .routeId(routeId)
                .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                .build()))
            .thenReturn(Mono
                .error(new ClientV2Exception(null, code, "test-exception-description", "test-exception-errorCode")));
    }

    private static void requestCreateServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId, Map<String, Object> parameters) {
        when(cloudFoundryClient.serviceBindingsV2()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .parameters(parameters)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServiceBindingResponse.builder(), "service-binding-")
                    .build()));
    }

    private static void requestCreateServiceBindingError(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId, Map<String, Object> parameters, int code) {
        when(cloudFoundryClient.serviceBindingsV2()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .parameters(parameters)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .error(new ClientV2Exception(null, code, "test-exception-description", "test-exception-errorCode")));
    }

    private static void requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String spaceId, String planId, String serviceInstance, Map<String, Object> parameters, List<String> tags,
                                                     String serviceInstanceId, String state) {
        when(cloudFoundryClient.serviceInstances()
            .create(org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .name(serviceInstance)
                .parameters(parameters)
                .servicePlanId(planId)
                .spaceId(spaceId)
                .tags(tags)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServiceInstanceResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(serviceInstanceId)
                        .build())
                    .entity(fill(ServiceInstanceEntity.builder())
                        .lastOperation(LastOperation.builder()
                            .state(state)
                            .type("create")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestCreateServiceKey(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKey, Map<String, Object> parameters) {
        when(cloudFoundryClient.serviceKeys()
            .create(org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest.builder()
                .name(serviceKey)
                .parameters(parameters)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServiceKeyResponse.builder(), "service-key")
                    .build()));
    }

    private static void requestCreateUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String spaceId, String name, Map<String, Object> credentials, String routeServiceUrl,
                                                                 String syslogDrainUrl, String tag, String userProvidedServiceInstanceId) {
        when(cloudFoundryClient.userProvidedServiceInstances()
            .create(org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest.builder()
                .credentials(credentials)
                .name(name)
                .routeServiceUrl(routeServiceUrl)
                .spaceId(spaceId)
                .syslogDrainUrl(syslogDrainUrl)
                .tag(tag)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateUserProvidedServiceInstanceResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(userProvidedServiceInstanceId)
                        .build())
                    .entity(fill(UserProvidedServiceInstanceEntity.builder())
                        .build())
                    .build()));
    }

    private static void requestDeleteServiceBinding(CloudFoundryClient cloudFoundryClient, String serviceBindingId) {
        when(cloudFoundryClient.serviceBindingsV2()
            .delete(DeleteServiceBindingRequest.builder()
                .async(true)
                .serviceBindingId(serviceBindingId)
                .build()))
            .thenReturn(Mono
                .just(fill(DeleteServiceBindingResponse.builder())
                    .entity(fill(JobEntity.builder(), "job-entity-")
                        .build())
                    .build()));
    }

    private static void requestDeleteServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        when(cloudFoundryClient.serviceInstances()
            .delete(org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .async(true)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(DeleteServiceInstanceResponse.builder()
                    .entity(fill(JobEntity.builder(), "job-entity-")
                        .build())
                    .build()));

    }

    private static void requestDeleteServiceInstanceAcceptsIncomplete(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        when(cloudFoundryClient.serviceInstances()
            .delete(org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .async(true)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(DeleteServiceInstanceResponse.builder()
                    .entity(fill(ServiceInstanceEntity.builder(), "service-instance-entity-")
                        .lastOperation(LastOperation.builder()
                            .state("in progress")
                            .build())
                        .build())
                    .build()));

    }

    private static void requestDeleteServiceKey(CloudFoundryClient cloudFoundryClient, String serviceKeyId) {
        when(cloudFoundryClient.serviceKeys()
            .delete(org.cloudfoundry.client.v2.servicekeys.DeleteServiceKeyRequest.builder()
                .serviceKeyId(serviceKeyId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestDeleteUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        when(cloudFoundryClient.userProvidedServiceInstances()
            .delete(DeleteUserProvidedServiceInstanceRequest.builder()
                .userProvidedServiceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String application) {
        when(cloudFoundryClient.applicationsV2()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetApplicationResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(applicationId)
                        .build())
                    .entity(fill(ApplicationEntity.builder())
                        .name(application)
                        .build())
                    .build())
            );
    }

    private static void requestGetService(CloudFoundryClient cloudFoundryClient, String serviceId, String service) {
        when(cloudFoundryClient.services()
            .get(GetServiceRequest.builder()
                .serviceId(serviceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServiceResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(serviceId)
                        .build())
                    .entity(fill(ServiceEntity.builder())
                        .extra("{\"displayName\":\"test-value\",\"longDescription\":\"test-value\",\"documentationUrl\":\"test-documentation-url\",\"supportUrl\":\"test-value\"}")
                        .label(service)
                        .build())
                    .build()));
    }

    private static void requestGetServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String state) {
        when(cloudFoundryClient.serviceInstances()
            .get(org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServiceInstanceResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(serviceInstanceId)
                        .build())
                    .entity(fill(ServiceInstanceEntity.builder())
                        .lastOperation(LastOperation.builder()
                            .state(state)
                            .type("create")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestGetServiceInstanceNotExist(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        when(cloudFoundryClient.serviceInstances()
            .get(org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono.error(new ClientV2Exception(404, 404, "test-description", "test-error-code")));
    }

    private static void requestGetServiceNotPlanUpdateable(CloudFoundryClient cloudFoundryClient, String serviceId, String service) {
        when(cloudFoundryClient.services()
            .get(GetServiceRequest.builder()
                .serviceId(serviceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServiceResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(serviceId)
                        .build())
                    .entity(fill(ServiceEntity.builder())
                        .extra("{\"displayName\":\"test-value\",\"longDescription\":\"test-value\",\"documentationUrl\":\"test-documentation-url\",\"supportUrl\":\"test-value\"}")
                        .label(service)
                        .planUpdateable(false)
                        .build())
                    .build()));
    }

    private static void requestGetServicePlan(CloudFoundryClient cloudFoundryClient, String servicePlanId, String servicePlan, String serviceId) {
        when(cloudFoundryClient.servicePlans()
            .get(GetServicePlanRequest.builder()
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServicePlanResponse.builder())
                    .entity(ServicePlanEntity.builder()
                        .name(servicePlan)
                        .serviceId(serviceId)
                        .build())
                    .build()));
    }

    private static void requestGetSpaceSummaryEmpty(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .getSummary(GetSpaceSummaryRequest.builder()
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceSummaryResponse.builder())
                    .build()));
    }

    private static void requestGetSpaceSummaryManagedServices(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .getSummary(GetSpaceSummaryRequest.builder()
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceSummaryResponse.builder())
                    .id(spaceId)
                    .application(SpaceApplicationSummary.builder()
                        .id("test-application-id-1")
                        .name("test-application-name-1")
                        .serviceNames("test-service-name-1", "test-service-name-2")
                        .spaceId(spaceId)
                        .build())
                    .application(SpaceApplicationSummary.builder()
                        .id("test-application-id-2")
                        .name("test-application-name-2")
                        .serviceNames("test-service-name-2", "test-service-name-3")
                        .spaceId(spaceId)
                        .build())
                    .service(org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.builder()
                        .id("test-service-id-2")
                        .lastOperation(LastOperation.builder()
                            .description("test-last-operation-description-2")
                            .build())
                        .name("test-service-name-2")
                        .servicePlan(Plan.builder()
                            .id("test-service-plan-id-2")
                            .name("test-service-plan-name-2")
                            .service(Service.builder()
                                .id("test-provided-service-id")
                                .label("test-provided-service-label")
                                .build())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestGetSpaceSummaryManagedServicesNoApplications(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .getSummary(GetSpaceSummaryRequest.builder()
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceSummaryResponse.builder())
                    .id(spaceId)
                    .service(org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.builder()
                        .id("test-service-id")
                        .lastOperation(LastOperation.builder()
                            .description("test-last-operation-description")
                            .build())
                        .name("test-service-name")
                        .servicePlan(Plan.builder()
                            .id("test-service-plan-id")
                            .name("test-service-plan-name")
                            .service(Service.builder()
                                .id("test-provided-service-id")
                                .label("test-provided-service-label")
                                .build())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestGetSpaceSummaryManagedServicesOneApplication(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .getSummary(GetSpaceSummaryRequest.builder()
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceSummaryResponse.builder())
                    .id(spaceId)
                    .application(SpaceApplicationSummary.builder()
                        .id("test-application-id")
                        .name("test-application-name")
                        .serviceNames("test-service-name-1", "test-service-name-2")
                        .spaceId(spaceId)
                        .build())
                    .service(org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.builder()
                        .id("test-service-id-1")
                        .lastOperation(LastOperation.builder()
                            .description("test-last-operation-description-1")
                            .build())
                        .name("test-service-name-1")
                        .servicePlan(Plan.builder()
                            .id("test-service-plan-id-1")
                            .name("test-service-plan-name-1")
                            .service(Service.builder()
                                .id("test-provided-service-id")
                                .label("test-provided-service-label")
                                .build())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestGetSpaceSummaryUserProvidedServices(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .getSummary(GetSpaceSummaryRequest.builder()
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceSummaryResponse.builder())
                    .id(spaceId)
                    .application(SpaceApplicationSummary.builder()
                        .id("test-application-id-1")
                        .name("test-application-name-1")
                        .serviceNames("test-service-name-1", "test-service-name-2")
                        .spaceId(spaceId)
                        .build())
                    .application(SpaceApplicationSummary.builder()
                        .id("test-application-id-2")
                        .name("test-application-name-2")
                        .serviceNames("test-service-name-2", "test-service-name-3")
                        .spaceId(spaceId)
                        .build())
                    .service(org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.builder()
                        .id("test-service-id-2")
                        .name("test-service-name-2")
                        .boundApplicationCount(1)
                        .build())
                    .build()));
    }

    private static void requestJobFailure(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .errorDetails(fill(ErrorDetails.builder(), "error-details-")
                                    .build())
                                .status("failed")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestJobSuccess(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("finished")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestListOrganizationPrivateDomains(CloudFoundryClient cloudFoundryClient, String name, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                .name(name)
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder(), "private-domain-")
                        .build())
                    .build()));
    }

    private static void requestListOrganizationPrivateDomainsEmpty(CloudFoundryClient cloudFoundryClient, String name, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                .name(name)
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationPrivateDomainsResponse.builder())
                    .build()));
    }

    private static void requestListRoutes(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path) {
        when(cloudFoundryClient.routes()
            .list(ListRoutesRequest.builder()
                .domainId(domainId)
                .host(host)
                .page(1)
                .path(path)
                .build()))
            .thenReturn(Mono
                .just(fill(ListRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-")
                        .entity(RouteEntity.builder()
                            .domainId(domainId)
                            .host(host)
                            .path(path)
                            .serviceInstanceId("test-service-instance-id")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListRoutes(CloudFoundryClient cloudFoundryClient, String domainId) {
        when(cloudFoundryClient.routes()
            .list(ListRoutesRequest.builder()
                .domainId(domainId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-")
                        .entity(RouteEntity.builder()
                            .domainId(domainId)
                            .path("")
                            .serviceInstanceId("test-service-instance-id")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListRoutesEmpty(CloudFoundryClient cloudFoundryClient, String domainId) {
        when(cloudFoundryClient.routes()
            .list(ListRoutesRequest.builder()
                .domainId(domainId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListRoutesResponse.builder())
                    .build()));
    }

    private static void requestListSpaceServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String applicationId) {
        when(cloudFoundryClient.serviceBindingsV2()
            .list(ListServiceBindingsRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBindingsResponse.builder())
                    .resource(fill(ServiceBindingResource.builder(), "service-binding")
                        .entity(ServiceBindingEntity.builder()
                            .applicationId(applicationId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceServiceInstanceServiceKeys(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKey, String credentialKey, String credentialValue) {
        when(cloudFoundryClient.serviceInstances()
            .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                .name(serviceKey)
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceInstanceServiceKeysResponse.builder())
                    .resource(fill(ServiceKeyResource.builder(), "service-key-")
                        .entity(ServiceKeyEntity.builder()
                            .credential(credentialKey, credentialValue)
                            .name(serviceKey)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceServiceInstanceServiceKeys(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String credentialKey, String credentialValue) {
        when(cloudFoundryClient.serviceInstances()
            .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceInstanceServiceKeysResponse.builder())
                    .resource(fill(ServiceKeyResource.builder(), "service-key-")
                        .entity(fill(ServiceKeyEntity.builder(), "service-key-entity-")
                            .credential(credentialKey, credentialValue)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceServiceInstanceServiceKeysEmpty(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKey) {
        when(cloudFoundryClient.serviceInstances()
            .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                .name(serviceKey)
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceInstanceServiceKeysResponse.builder())
                    .build()));
    }

    private static void requestListSpaceServiceInstanceServiceKeysEmpty(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        when(cloudFoundryClient.serviceInstances()
            .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceInstanceServiceKeysResponse.builder())
                    .build()));
    }

    private static void requestListSpaceServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .name(serviceName)
                .page(1)
                .returnUserProvidedServiceInstances(true)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(fill(UnionServiceInstanceResource.builder(), "service-instance-")
                        .entity(fill(UnionServiceInstanceEntity.builder(), "service-instance-")
                            .type(ServiceInstanceType.MANAGED.toString())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId, String servicePlanId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .name(serviceName)
                .page(1)
                .returnUserProvidedServiceInstances(true)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(fill(UnionServiceInstanceResource.builder(), "service-instance-")
                        .entity(fill(UnionServiceInstanceEntity.builder())
                            .servicePlanId(servicePlanId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceServiceInstancesEmpty(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .name(serviceName)
                .page(1)
                .returnUserProvidedServiceInstances(true)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .build()));
    }

    private static void requestListSpaceServiceInstancesManaged(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .name(serviceName)
                .page(1)
                .returnUserProvidedServiceInstances(true)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id("test-service-instance-id")
                            .build())
                        .entity(fill(UnionServiceInstanceEntity.builder())
                            .lastOperation(LastOperation.builder()
                                .createdAt("test-startedAt")
                                .description("test-message")
                                .state("test-status")
                                .type("test-type")
                                .updatedAt("test-updatedAt")
                                .build())
                            .name(serviceName)
                            .servicePlanId("test-service-plan-id")
                            .tags(Collections.singletonList("test-tag"))
                            .type(ServiceInstanceType.MANAGED.toString())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceServiceInstancesUserProvided(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id("test-service-instance-id")
                            .build())
                        .entity(UnionServiceInstanceEntity.builder()
                            .name(serviceName)
                            .type(ServiceInstanceType.USER_PROVIDED.toString())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .list(ListServicePlanVisibilitiesRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlanVisibilitiesResponse.builder())
                    .resource(fill(ServicePlanVisibilityResource.builder())
                        .entity(ServicePlanVisibilityEntity.builder()
                            .organizationId(organizationId)
                            .servicePlanId(servicePlanId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceServicePlanVisibilitiesEmpty(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .list(ListServicePlanVisibilitiesRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlanVisibilitiesResponse.builder())
                    .build()));
    }

    private static void requestListSpaceServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId, String plan, String planId) {
        when(cloudFoundryClient.servicePlans()
            .list(ListServicePlansRequest.builder()
                .page(1)
                .serviceId(serviceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlansResponse.builder())
                    .resource(ServicePlanResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(planId)
                            .build())
                        .entity(fill(ServicePlanEntity.builder())
                            .name(plan)
                            .build())
                        .build())
                    .build())

            );
    }

    private static void requestListSpaceServicePlansNotPublic(CloudFoundryClient cloudFoundryClient, String serviceId, String plan, String planId) {
        when(cloudFoundryClient.servicePlans()
            .list(ListServicePlansRequest.builder()
                .serviceId(serviceId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlansResponse.builder())
                    .resource(ServicePlanResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(planId)
                            .build())
                        .entity(fill(ServicePlanEntity.builder())
                            .name(plan)
                            .publiclyVisible(false)
                            .build())
                        .build())
                    .build())

            );
    }

    private static void requestListSpaceServices(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceLabel) {
        when(cloudFoundryClient.spaces()
            .listServices(ListSpaceServicesRequest.builder()
                .label(serviceLabel)
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServicesResponse.builder())
                    .resource(ServiceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(serviceLabel + "-id")
                            .build())
                        .entity(fill(ServiceEntity.builder())
                            .description(serviceLabel + "-description")
                            .label(serviceLabel)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceServicesTwo(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceLabel1, String serviceLabel2) {
        when(cloudFoundryClient.spaces()
            .listServices(ListSpaceServicesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServicesResponse.builder())
                    .resource(ServiceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(serviceLabel1 + "-id")
                            .build())
                        .entity(fill(ServiceEntity.builder())
                            .description(serviceLabel1 + "-description")
                            .label(serviceLabel1)
                            .build())
                        .build())
                    .resource(ServiceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(serviceLabel2 + "-id")
                            .build())
                        .entity(fill(ServiceEntity.builder())
                            .description(serviceLabel2 + "-description")
                            .label(serviceLabel2)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestRemoveUserProvidedServiceInstanceRoute(CloudFoundryClient cloudFoundryClient, String routeId, String serviceInstanceId) {
        when(cloudFoundryClient.userProvidedServiceInstances()
            .removeRoute(RemoveUserProvidedServiceInstanceRouteRequest.builder()
                .routeId(routeId)
                .userProvidedServiceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestRenameServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String newName) {
        when(cloudFoundryClient.serviceInstances()
            .update(org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest.builder()
                .name(newName)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateServiceInstanceResponse.builder())
                    .build()));
    }

    private static void requestUpdateServiceInstance(CloudFoundryClient cloudFoundryClient, Map<String, Object> parameter, String serviceInstanceId, String servicePlanId, List<String> tags) {
        when(cloudFoundryClient.serviceInstances()
            .update(org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .parameters(parameter)
                .serviceInstanceId(serviceInstanceId)
                .servicePlanId(servicePlanId)
                .tags(tags)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateServiceInstanceResponse.builder())
                    .build()));
    }

    private static void requestUpdateUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, Map<String, Object> credentials, String syslogDrainUrl, List<String> tags,
                                                                 String userProvidedServiceInstanceId) {
        when(cloudFoundryClient.userProvidedServiceInstances()
            .update(org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest.builder()
                .credentials(credentials)
                .syslogDrainUrl(syslogDrainUrl)
                .tags(tags)
                .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateUserProvidedServiceInstanceResponse.builder())
                    .build()));
    }

    private void requestListSharedDomains(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .name(name)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .resource(fill(SharedDomainResource.builder(), "shared-domain-")
                        .build())
                    .build()));
    }

    private void requestListSharedDomainsEmpty(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .name(name)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .build()));
    }

}
