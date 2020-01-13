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

package org.cloudfoundry.operations.serviceadmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v2.servicebrokers.UpdateServiceBrokerResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityEntity;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityResource;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ListServicesResponse;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultServiceAdminTest extends AbstractOperationsTest {

    private final DefaultServiceAdmin serviceAdmin = new DefaultServiceAdmin(Mono.just(this.cloudFoundryClient), Mono.just(TEST_SPACE_ID));

    @Test
    public void createServiceBroker() {
        requestCreateServiceBroker(this.cloudFoundryClient, "test-service-broker-name", "test-service-broker-url", "test-service-broker-username", "test-service-broker-password", null);

        this.serviceAdmin
            .create(CreateServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .url("test-service-broker-url")
                .username("test-service-broker-username")
                .password("test-service-broker-password")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createServiceBrokerWithSpaceScope() {
        requestCreateServiceBroker(this.cloudFoundryClient, "test-service-broker-name", "test-service-broker-url", "test-service-broker-username", "test-service-broker-password", TEST_SPACE_ID);

        this.serviceAdmin
            .create(CreateServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .url("test-service-broker-url")
                .username("test-service-broker-username")
                .password("test-service-broker-password")
                .spaceScoped(true)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceBroker() {
        requestListServiceBrokers(this.cloudFoundryClient, "test-service-broker-name");
        requestDeleteServiceBroker(this.cloudFoundryClient, "test-service-broker-id");

        this.serviceAdmin
            .delete(DeleteServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceBrokerNoServiceBroker() {
        requestListServiceBrokersEmpty(this.cloudFoundryClient, "test-service-broker-name");
        requestDeleteServiceBroker(this.cloudFoundryClient, "test-service-broker-id");

        this.serviceAdmin
            .delete(DeleteServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service Broker test-service-broker-name not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disableServiceAccess() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestListServicePlanVisibilities(this.cloudFoundryClient, "test-service-plan-id");
        requestDeleteServicePlanVisibility(this.cloudFoundryClient, "test-service-plan-visibility-id");
        requestUpdateServicePlan(this.cloudFoundryClient, false, "test-service-plan-id");

        this.serviceAdmin
            .disableServiceAccess(DisableServiceAccessRequest.builder()
                .serviceName("test-service-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disableServiceAccessSpecifyAll() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestListServicePlanVisibilities(this.cloudFoundryClient, "test-organization-id", "test-service-plan-id");
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestDeleteServicePlanVisibility(this.cloudFoundryClient, "test-service-plan-visibility-id");
        requestUpdateServicePlan(this.cloudFoundryClient, false, "test-service-plan-id");

        this.serviceAdmin
            .disableServiceAccess(DisableServiceAccessRequest.builder()
                .organizationName("test-organization-name")
                .serviceName("test-service-name")
                .servicePlanName("test-service-plan-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disableServiceAccessSpecifyOrganization() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestListServicePlanVisibilities(this.cloudFoundryClient, "test-organization-id", "test-service-plan-id");
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestDeleteServicePlanVisibility(this.cloudFoundryClient, "test-service-plan-visibility-id");
        requestUpdateServicePlan(this.cloudFoundryClient, false, "test-service-plan-id");

        this.serviceAdmin
            .disableServiceAccess(DisableServiceAccessRequest.builder()
                .organizationName("test-organization-name")
                .serviceName("test-service-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disableServiceAccessSpecifyServicePlan() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestListServicePlanVisibilities(this.cloudFoundryClient, "test-service-plan-id");
        requestDeleteServicePlanVisibility(this.cloudFoundryClient, "test-service-plan-visibility-id");
        requestUpdateServicePlan(this.cloudFoundryClient, false, "test-service-plan-id");

        this.serviceAdmin
            .disableServiceAccess(DisableServiceAccessRequest.builder()
                .serviceName("test-service-name")
                .servicePlanName("test-service-plan-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void enableServiceAccess() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestListServicePlanVisibilities(this.cloudFoundryClient, "test-service-plan-id");
        requestDeleteServicePlanVisibility(this.cloudFoundryClient, "test-service-plan-visibility-id");

        this.serviceAdmin
            .enableServiceAccess(EnableServiceAccessRequest.builder()
                .serviceName("test-service-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void enableServiceAccessOrganizationNotFound() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestListOrganizationsEmpty(this.cloudFoundryClient, "bogus-organization-name");

        this.serviceAdmin
            .enableServiceAccess(EnableServiceAccessRequest.builder()
                .organizationName("bogus-organization-name")
                .serviceName("test-service-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Organization bogus-organization-name not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void enableServiceAccessServiceNotFound() {
        requestListServicesWithNameEmpty(this.cloudFoundryClient, "bogus-service-name");

        this.serviceAdmin
            .enableServiceAccess(EnableServiceAccessRequest.builder()
                .serviceName("bogus-service-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service offering bogus-service-name not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void enableServiceAccessSpecifyAll() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestCreateServicePlanVisibility(this.cloudFoundryClient, "test-organization-id", "test-service-plan-id");

        this.serviceAdmin
            .enableServiceAccess(EnableServiceAccessRequest.builder()
                .organizationName("test-organization-name")
                .serviceName("test-service-name")
                .servicePlanName("test-service-plan-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void enableServiceAccessSpecifyOrganization() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestListServicePlanVisibilities(this.cloudFoundryClient, "test-service-plan-id");
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestCreateServicePlanVisibility(this.cloudFoundryClient, "test-organization-id", "test-service-plan-id");

        this.serviceAdmin
            .enableServiceAccess(EnableServiceAccessRequest.builder()
                .organizationName("test-organization-name")
                .serviceName("test-service-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void enableServiceAccessSpecifyServicePlan() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestListServicePlanVisibilities(this.cloudFoundryClient, "test-service-plan-id");
        requestDeleteServicePlanVisibility(this.cloudFoundryClient, "test-service-plan-visibility-id");

        this.serviceAdmin
            .enableServiceAccess(EnableServiceAccessRequest.builder()
                .serviceName("test-service-name")
                .servicePlanName("test-service-plan-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceAccessSettings() {
        requestListServiceBrokers(this.cloudFoundryClient);
        requestListServicePlanVisibilitiesEmpty(this.cloudFoundryClient);
        requestListServicesWithBroker(this.cloudFoundryClient, "test-service-broker-id");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");

        this.serviceAdmin
            .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName("test-service-broker-resource-name")
                .organizationNames()
                .planName("test-service-plan-name")
                .serviceName("test-service-name")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceAccessSettingsNoBrokers() {
        requestListServiceBrokersEmpty(this.cloudFoundryClient);
        requestListServicePlanVisibilitiesEmpty(this.cloudFoundryClient);

        this.serviceAdmin
            .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("No Service Brokers found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceAccessSettingsSpecifyBroker() {
        requestListServiceBrokers(this.cloudFoundryClient);
        requestListServicePlanVisibilitiesEmpty(this.cloudFoundryClient);
        requestListServicesWithBroker(this.cloudFoundryClient, "test-service-broker-id");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");

        this.serviceAdmin
            .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                .brokerName("test-service-broker-resource-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName("test-service-broker-resource-name")
                .organizationNames()
                .planName("test-service-plan-name")
                .serviceName("test-service-name")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceAccessSettingsSpecifyBrokerNotFound() {
        requestListServiceBrokers(this.cloudFoundryClient);
        requestListServicePlanVisibilitiesEmpty(this.cloudFoundryClient);

        this.serviceAdmin
            .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                .brokerName("bogus-service-broker-resource-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service Broker bogus-service-broker-resource-name not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceAccessSettingsSpecifyOrganization() {
        requestListServiceBrokers(this.cloudFoundryClient);
        requestListServicePlanVisibilities(this.cloudFoundryClient);
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestListServicesWithBroker(this.cloudFoundryClient, "test-service-broker-id");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");
        requestGetOrganization(this.cloudFoundryClient, "test-organization-id");

        this.serviceAdmin
            .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                .organizationName("test-organization-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName("test-service-broker-resource-name")
                .organizationName("test-organization-name")
                .planName("test-service-plan-name")
                .serviceName("test-service-name")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceAccessSettingsSpecifyOrganizationNotFound() {
        requestListServiceBrokers(this.cloudFoundryClient);
        requestListServicePlanVisibilities(this.cloudFoundryClient);
        requestListOrganizationsEmpty(this.cloudFoundryClient, "bogus-organization-name");

        this.serviceAdmin
            .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                .organizationName("bogus-organization-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Organization bogus-organization-name not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceAccessSettingsSpecifyService() {
        requestListServicesWithName(this.cloudFoundryClient, "test-service-name");
        requestListServiceBrokers(this.cloudFoundryClient);
        requestListServicePlanVisibilitiesEmpty(this.cloudFoundryClient);
        requestListServicesWithBroker(this.cloudFoundryClient, "test-service-broker-id");
        requestListServicePlans(this.cloudFoundryClient, "test-service-id");

        this.serviceAdmin
            .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                .serviceName("test-service-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName("test-service-broker-resource-name")
                .organizationNames()
                .planName("test-service-plan-name")
                .serviceName("test-service-name")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceAccessSettingsSpecifyServiceNotFound() {
        requestListServiceBrokers(this.cloudFoundryClient);
        requestListServicePlanVisibilitiesEmpty(this.cloudFoundryClient);
        requestListServicesWithNameEmpty(this.cloudFoundryClient, "bogus-service-name");

        this.serviceAdmin
            .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                .serviceName("bogus-service-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service bogus-service-name not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceBrokers() {
        requestListServiceBrokers(this.cloudFoundryClient);

        this.serviceAdmin
            .list()
            .as(StepVerifier::create)
            .expectNext(ServiceBroker.builder()
                .id("test-service-broker-id")
                .name("test-service-broker-resource-name")
                .url("test-service-broker-resource-brokerUrl")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceBrokersNoBrokers() {
        requestListServiceBrokersEmpty(this.cloudFoundryClient);

        this.serviceAdmin
            .list()
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServiceBroker() {
        requestListServiceBrokers(this.cloudFoundryClient, "test-service-broker-name");
        requestUpdateServiceBroker(this.cloudFoundryClient, "test-service-broker-name", "test-service-broker-url", "test-service-broker-username",
            "test-service-broker-password", "test-service-broker-id");

        this.serviceAdmin
            .update(UpdateServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .url("test-service-broker-url")
                .username("test-service-broker-username")
                .password("test-service-broker-password")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateServiceBrokerNoServiceBroker() {
        requestListServiceBrokersEmpty(this.cloudFoundryClient, "test-service-broker-name");
        requestUpdateServiceBroker(this.cloudFoundryClient, "test-service-broker-name", "test-service-broker-url", "test-service-broker-username",
            "test-service-broker-password", "test-service-broker-id");

        this.serviceAdmin
            .update(UpdateServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .url("test-service-broker-url")
                .username("test-service-broker-username")
                .password("test-service-broker-password")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service Broker test-service-broker-name not found"))
            .verify(Duration.ofSeconds(5));
    }

    private static void requestCreateServiceBroker(CloudFoundryClient cloudFoundryClient, String name, String url, String username, String password, String spaceId) {
        when(cloudFoundryClient.serviceBrokers()
            .create(org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest.builder()
                .name(name)
                .brokerUrl(url)
                .authenticationUsername(username)
                .authenticationPassword(password)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServiceBrokerResponse.builder())
                    .build()));
    }

    private static void requestCreateServicePlanVisibility(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .create(CreateServicePlanVisibilityRequest.builder()
                .organizationId(organizationId)
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServicePlanVisibilityResponse.builder())
                    .build()));
    }

    private static void requestDeleteServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        when(cloudFoundryClient.serviceBrokers()
            .delete(org.cloudfoundry.client.v2.servicebrokers.DeleteServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestDeleteServicePlanVisibility(CloudFoundryClient cloudFoundryClient, String servicePlanVisibilityId) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .delete(DeleteServicePlanVisibilityRequest.builder()
                .async(true)
                .servicePlanVisibilityId(servicePlanVisibilityId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestGetOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .get(GetOrganizationRequest.builder()
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetOrganizationResponse.builder(), "organization-")
                    .build()));
    }

    private static void requestListOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organizationName)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .build())
                    .build()));
    }

    private static void requestListOrganizationsEmpty(CloudFoundryClient cloudFoundryClient, String organizationName) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organizationName)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .build()));
    }

    private static void requestListServiceBrokers(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.serviceBrokers()
            .list(ListServiceBrokersRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBrokersResponse.builder())
                    .resource(fill(ServiceBrokerResource.builder(), "service-broker-")
                        .metadata(fill(Metadata.builder(), "service-broker-")
                            .id("test-service-broker-id")
                            .build())
                        .entity(fill(ServiceBrokerEntity.builder(), "service-broker-resource-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServiceBrokers(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        when(cloudFoundryClient.serviceBrokers()
            .list(ListServiceBrokersRequest.builder()
                .name(serviceBrokerName)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBrokersResponse.builder())
                    .resource(fill(ServiceBrokerResource.builder(), "service-broker-")
                        .entity(fill(ServiceBrokerEntity.builder(), "service-broker-resource-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServiceBrokersEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.serviceBrokers()
            .list(ListServiceBrokersRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBrokersResponse.builder())
                    .build()));
    }

    private static void requestListServiceBrokersEmpty(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        when(cloudFoundryClient.serviceBrokers()
            .list(ListServiceBrokersRequest.builder()
                .name(serviceBrokerName)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBrokersResponse.builder())
                    .build()));
    }

    private static void requestListServicePlanVisibilities(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .list(ListServicePlanVisibilitiesRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlanVisibilitiesResponse.builder())
                    .resource(fill(ServicePlanVisibilityResource.builder())
                        .entity(fill(ServicePlanVisibilityEntity.builder())
                            .organizationId("test-organization-id")
                            .servicePlanId("test-service-plan-id")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .list(ListServicePlanVisibilitiesRequest.builder()
                .page(1)
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlanVisibilitiesResponse.builder(), "service-plan-visibility-")
                    .resource(fill(ServicePlanVisibilityResource.builder(), "service-plan-visibility-")
                        .entity(fill(ServicePlanVisibilityEntity.builder())
                            .organizationId("test-organization-id")
                            .servicePlanId(servicePlanId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .list(ListServicePlanVisibilitiesRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlanVisibilitiesResponse.builder(), "service-plan-visibility-")
                    .resource(fill(ServicePlanVisibilityResource.builder(), "service-plan-visibility-")
                        .entity(fill(ServicePlanVisibilityEntity.builder())
                            .organizationId(organizationId)
                            .servicePlanId(servicePlanId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicePlanVisibilitiesEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .list(ListServicePlanVisibilitiesRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlanVisibilitiesResponse.builder())
                    .build()));
    }

    private static void requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        when(cloudFoundryClient.servicePlans()
            .list(ListServicePlansRequest.builder()
                .page(1)
                .serviceId(serviceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlansResponse.builder())
                    .resource(fill(ServicePlanResource.builder(), "service-plan-")
                        .entity(fill(ServicePlanEntity.builder(), "service-plan-")
                            .serviceId("test-service-id")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicesWithBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        when(cloudFoundryClient.services()
            .list(ListServicesRequest.builder()
                .serviceBrokerId(serviceBrokerId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicesResponse.builder())
                    .resource(fill(ServiceResource.builder(), "service-")
                        .entity(fill(ServiceEntity.builder())
                            .label("test-service-name")
                            .serviceBrokerId("test-service-broker-id")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicesWithName(CloudFoundryClient cloudFoundryClient, String label) {
        when(cloudFoundryClient.services()
            .list(ListServicesRequest.builder()
                .label(label)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicesResponse.builder())
                    .resource(fill(ServiceResource.builder(), "service-")
                        .entity(fill(ServiceEntity.builder())
                            .label("test-service-name")
                            .serviceBrokerId("test-service-broker-id")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicesWithNameEmpty(CloudFoundryClient cloudFoundryClient, String label) {
        when(cloudFoundryClient.services()
            .list(ListServicesRequest.builder()
                .label(label)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicesResponse.builder())
                    .build()));
    }

    private static void requestUpdateServiceBroker(CloudFoundryClient cloudFoundryClient, String name, String url, String username, String password, String serviceBrokerId) {
        when(cloudFoundryClient.serviceBrokers()
            .update(org.cloudfoundry.client.v2.servicebrokers.UpdateServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerId)
                .name(name)
                .brokerUrl(url)
                .authenticationUsername(username)
                .authenticationPassword(password)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateServiceBrokerResponse.builder())
                    .build()));
    }

    private static void requestUpdateServicePlan(CloudFoundryClient cloudFoundryClient, boolean publiclyVisible, String servicePlanId) {
        when(cloudFoundryClient.servicePlans()
            .update(UpdateServicePlanRequest.builder()
                .publiclyVisible(publiclyVisible)
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateServicePlanResponse.builder())
                    .build()));
    }

}
