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

package org.cloudfoundry.operations.serviceadmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityResource;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

public final class DefaultServiceAdmin implements ServiceAdmin {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<String> spaceId;

    public DefaultServiceAdmin(Mono<CloudFoundryClient> cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Void> create(CreateServiceBrokerRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> requestCreateServiceBroker(cloudFoundryClient, request.getName(), request.getUrl(), request.getUsername(), request.getPassword(),
                request.getSpaceScoped(), spaceId)))
            .then()
            .transform(OperationsLogging.log("Create Service Broker"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteServiceBrokerRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                getServiceBrokerId(cloudFoundryClient, request.getName())
            ))
            .flatMap(function(DefaultServiceAdmin::requestDeleteServiceBroker))
            .transform(OperationsLogging.log("Delete Service Broker"))
            .checkpoint();
    }

    @Override
    public Mono<Void> disableServiceAccess(DisableServiceAccessRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getServiceId(cloudFoundryClient, request.getServiceName())))
            .flatMap(function((cloudFoundryClient, serviceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getServicePlans(cloudFoundryClient, serviceId)
                )))
            .flatMap(function((cloudFoundryClient, servicePlans) -> Mono
                .when(
                    updateServicePlanVisibilities(cloudFoundryClient, request, servicePlans),
                    updateServicePlansPublicStatus(cloudFoundryClient, request, servicePlans)
                )))
            .then()
            .transform(OperationsLogging.log("Disable Service Access"))
            .checkpoint();
    }

    @Override
    public Mono<Void> enableServiceAccess(EnableServiceAccessRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getServiceId(cloudFoundryClient, request.getServiceName())))
            .flatMap(function((cloudFoundryClient, serviceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getServicePlans(cloudFoundryClient, serviceId)
                )))
            .flatMap(function((cloudFoundryClient, servicePlans) -> Mono
                .when(
                    updateServicePlanVisibilities(cloudFoundryClient, request, servicePlans),
                    updateServicePlansPublicStatus(cloudFoundryClient, request, servicePlans)
                )))
            .then()
            .transform(OperationsLogging.log("Enable Service Access"))
            .checkpoint();
    }

    @Override
    public Flux<ServiceBroker> list() {
        return this.cloudFoundryClient
            .flatMapMany(DefaultServiceAdmin::requestListServiceBrokers)
            .map(this::toServiceBroker)
            .transform(OperationsLogging.log("List Service Brokers"))
            .checkpoint();
    }

    @Override
    public Flux<ServiceAccess> listServiceAccessSettings(ListServiceAccessSettingsRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    listServiceBrokers(cloudFoundryClient),
                    Mono.just(request),
                    requestListServicePlanVisibilities(cloudFoundryClient)
                ))
            .flatMapMany(function(DefaultServiceAdmin::collectServiceAccessSettings))
            .transform(OperationsLogging.log("List Service Access Settings"))
            .checkpoint();
    }

    private static Flux<ServiceAccess> collectServiceAccessSettings(CloudFoundryClient cloudFoundryClient, List<ServiceBrokerResource> brokers, ListServiceAccessSettingsRequest request,
                                                                    List<ServicePlanVisibilityResource> visibilities) {
        List<String> brokerIds = brokers.stream()
            .filter(isRequiredServiceBroker(request.getBrokerName()))
            .map(ResourceUtils::getId)
            .collect(Collectors.toList());

        if (brokerIds.size() == 0) {
            throw new IllegalArgumentException(String.format("Service Broker %s not found", request.getBrokerName()));
        }

        return Mono
            .when(
                validateOrganization(cloudFoundryClient, request.getOrganizationName()),
                validateService(cloudFoundryClient, request.getServiceName()))
            .thenMany(requestListServices(cloudFoundryClient, brokerIds))
            .filter(service -> isVisibleService(request, service))
            .collectList()
            .flatMapMany(services -> getServicePlans(cloudFoundryClient, services)
                .map(servicePlan -> Tuples.of(services, servicePlan)))
            .flatMap(function((services, servicePlan) -> getOrganizationNames(cloudFoundryClient, servicePlan, visibilities)
                .map(organizationNames -> Tuples.of(organizationNames, services, servicePlan))))
            .filter(predicate((organizationNames, services, servicePlan) -> isVisibleOrganization(organizationNames, request.getOrganizationName(), servicePlan)))
            .map(function((organizationNames, services, servicePlan) -> toServiceAccess(brokers, organizationNames, services, servicePlan)));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestListOrganizations(cloudFoundryClient, organizationName)
            .singleOrEmpty()
            .map(ResourceUtils::getId)
            .switchIfEmpty(ExceptionUtils.illegalArgument("Organization %s not found", organizationName));
    }

    private static Mono<List<String>> getOrganizationNames(CloudFoundryClient cloudFoundryClient, ServicePlanResource servicePlan, List<ServicePlanVisibilityResource> visibilities) {
        if (visibilities == null || visibilities.size() == 0) {
            return Mono.just(Collections.emptyList());
        }

        return Flux.fromIterable(visibilities)
            .filter(visibility -> servicePlan.getMetadata().getId().equals(visibility.getEntity().getServicePlanId()))
            .flatMap(visibility -> requestGetOrganization(cloudFoundryClient, visibility.getEntity().getOrganizationId()))
            .map(organization -> ResourceUtils.getEntity(organization).getName())
            .collectList();
    }

    private static Mono<ServiceBrokerResource> getServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        return requestListServiceBrokers(cloudFoundryClient, serviceBrokerName)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service Broker %s not found", serviceBrokerName));
    }

    private static Mono<String> getServiceBrokerId(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        return getServiceBroker(cloudFoundryClient, serviceBrokerName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getServiceId(CloudFoundryClient cloudFoundryClient, String serviceName) {
        return requestListServices(cloudFoundryClient, serviceName)
            .singleOrEmpty()
            .map(ResourceUtils::getId)
            .switchIfEmpty(ExceptionUtils.illegalArgument("Service offering %s not found", serviceName));
    }

    private static Mono<List<ServicePlanResource>> getServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return requestListServicePlans(cloudFoundryClient, Collections.singletonList(serviceId))
            .collectList();
    }

    private static Flux<ServicePlanResource> getServicePlans(CloudFoundryClient cloudFoundryClient, List<ServiceResource> services) {
        List<String> serviceIds = services.stream()
            .map(ResourceUtils::getId)
            .collect(Collectors.toList());

        return requestListServicePlans(cloudFoundryClient, serviceIds);
    }

    private static Predicate<ServiceBrokerResource> isRequiredServiceBroker(String brokerName) {
        if (brokerName == null || brokerName.isEmpty()) {
            return broker -> true;
        } else {
            return broker -> brokerName.equals(ResourceUtils.getEntity(broker).getName());
        }
    }

    private static boolean isUpdateableServicePlan(String servicePlanName, ServicePlanResource servicePlan) {
        return servicePlanName == null || servicePlanName.isEmpty() || servicePlanName.equals(ResourceUtils.getEntity(servicePlan).getName());
    }

    private static boolean isVisibleOrganization(List<String> organizationNames, String requiredOrganization, ServicePlanResource servicePlan) {
        return ResourceUtils.getEntity(servicePlan).getPubliclyVisible() ||
            requiredOrganization == null ||
            requiredOrganization.isEmpty() ||
            organizationNames != null && organizationNames.contains(requiredOrganization);
    }

    private static boolean isVisibleService(ListServiceAccessSettingsRequest request, ServiceResource service) {
        return request.getServiceName() == null ||
            request.getServiceName().isEmpty() ||
            request.getServiceName().equals(ResourceUtils.getEntity(service).getLabel());
    }

    private static Mono<List<ServiceBrokerResource>> listServiceBrokers(CloudFoundryClient cloudFoundryClient) {
        return requestListServiceBrokers(cloudFoundryClient)
            .switchIfEmpty(ExceptionUtils.illegalArgument("No Service Brokers found"))
            .collectList();
    }

    private static Flux<String> listServicePlanVisibilityIds(CloudFoundryClient cloudFoundryClient, List<String> servicePlanIds) {
        return requestListServicePlanVisibilities(cloudFoundryClient, servicePlanIds)
            .map(ResourceUtils::getId);
    }

    private static Flux<String> listServicePlanVisibilityIds(CloudFoundryClient cloudFoundryClient, String organizationId, List<String> servicePlanIds) {
        return requestListServicePlanVisibilities(cloudFoundryClient, organizationId, servicePlanIds)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateServiceBrokerResponse> requestCreateServiceBroker(CloudFoundryClient cloudFoundryClient, String name, String url, String username, String password, Boolean isSpaceScoped,
                                                                                String spaceId) {
        return cloudFoundryClient.serviceBrokers()
            .create(org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest.builder()
                .name(name)
                .brokerUrl(url)
                .authenticationUsername(username)
                .authenticationPassword(password)
                .spaceId(Optional.ofNullable(isSpaceScoped).orElse(false) ? spaceId : null)
                .build());
    }

    private static Mono<CreateServicePlanVisibilityResponse> requestCreateServicePlanVisibility(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        return cloudFoundryClient.servicePlanVisibilities()
            .create(CreateServicePlanVisibilityRequest.builder()
                .organizationId(organizationId)
                .servicePlanId(servicePlanId)
                .build());
    }

    private static Mono<Void> requestDeleteServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return cloudFoundryClient.serviceBrokers()
            .delete(org.cloudfoundry.client.v2.servicebrokers.DeleteServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerId)
                .build());
    }

    private static Mono<DeleteServicePlanVisibilityResponse> requestDeleteServicePlanVisibility(CloudFoundryClient cloudFoundryClient, String visibilityId) {
        return cloudFoundryClient.servicePlanVisibilities()
            .delete(DeleteServicePlanVisibilityRequest.builder()
                .async(true)
                .servicePlanVisibilityId(visibilityId)
                .build());
    }

    private static Mono<GetOrganizationResponse> requestGetOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.organizations()
            .get(GetOrganizationRequest.builder()
                .organizationId(organizationId)
                .build());
    }

    private static Flux<OrganizationResource> requestListOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .name(organizationName)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceBrokerResource> requestListServiceBrokers(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceBrokers()
                .list(ListServiceBrokersRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceBrokerResource> requestListServiceBrokers(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceBrokers()
                .list(ListServiceBrokersRequest.builder()
                    .name(serviceBrokerName)
                    .page(page)
                    .build()));
    }

    private static Mono<List<ServicePlanVisibilityResource>> requestListServicePlanVisibilities(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlanVisibilities()
                .list(ListServicePlanVisibilitiesRequest.builder()
                    .page(page)
                    .build()))
            .collectList();
    }

    private static Flux<ServicePlanVisibilityResource> requestListServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, List<String> servicePlanIds) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlanVisibilities()
                .list(ListServicePlanVisibilitiesRequest.builder()
                    .page(page)
                    .servicePlanIds(servicePlanIds)
                    .build()));
    }

    private static Flux<ServicePlanVisibilityResource> requestListServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, String organizationId, List<String> servicePlanIds) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlanVisibilities()
                .list(ListServicePlanVisibilitiesRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .servicePlanIds(servicePlanIds)
                    .build()));
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, List<String> services) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
            .list(ListServicePlansRequest.builder()
                .page(page)
                .serviceIds(services)
                .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, List<String> brokerIds) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .page(page)
                    .serviceBrokerIds(brokerIds)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String serviceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .page(page)
                    .label(serviceName)
                    .build()));
    }

    private static Mono<UpdateServicePlanResponse> requestUpdateServicePlanPublicStatus(CloudFoundryClient cloudFoundryClient, boolean publiclyVisible, String servicePlanId) {
        return cloudFoundryClient.servicePlans()
            .update(UpdateServicePlanRequest.builder()
                .publiclyVisible(publiclyVisible)
                .servicePlanId(servicePlanId)
                .build());
    }

    private static ServiceAccess toServiceAccess(List<ServiceBrokerResource> brokers, List<String> organizationNames, List<ServiceResource> services,
                                                 ServicePlanResource servicePlan) {
        Access access = Access.NONE;
        if (organizationNames != null && organizationNames.size() > 0) {
            access = Access.LIMITED;
        }
        if (ResourceUtils.getEntity(servicePlan).getPubliclyVisible()) {
            access = Access.ALL;
        }

        String servicePlanName = ResourceUtils.getEntity(servicePlan).getName();
        ServiceResource service = services.stream()
            .filter(item -> ResourceUtils.getId(item).equals(servicePlan.getEntity().getServiceId()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("Unable to find service for %s", servicePlanName)));

        String serviceName = ResourceUtils.getEntity(service).getLabel();
        String brokerName = brokers.stream()
            .filter(broker -> ResourceUtils.getId(broker).equals(ResourceUtils.getEntity(service).getServiceBrokerId()))
            .map(broker -> ResourceUtils.getEntity(broker).getName())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("Unable to find broker for %s", serviceName)));

        return ServiceAccess.builder()
            .access(access)
            .brokerName(brokerName)
            .organizationNames(organizationNames)
            .planName(servicePlanName)
            .serviceName(serviceName)
            .build();
    }

    private static Mono<Void> updateServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, EnableServiceAccessRequest request, List<ServicePlanResource> servicePlans) {
        List<String> servicePlanIds = servicePlans.stream()
            .filter(servicePlan -> isUpdateableServicePlan(request.getServicePlanName(), servicePlan))
            .map(ResourceUtils::getId)
            .collect(Collectors.toList());

        if (request.getOrganizationName() != null && !request.getOrganizationName().isEmpty()) {
            return getOrganizationId(cloudFoundryClient, request.getOrganizationName())
                .flatMapMany(organizationId -> Flux.fromIterable(servicePlanIds)
                    .flatMap(servicePlanId -> requestCreateServicePlanVisibility(cloudFoundryClient, organizationId, servicePlanId)))
                .then();
        } else {
            return listServicePlanVisibilityIds(cloudFoundryClient, servicePlanIds)
                .flatMap(visibilityId -> requestDeleteServicePlanVisibility(cloudFoundryClient, visibilityId)
                    .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, request.getCompletionTimeout(), job)))
                .then();
        }
    }

    private static Mono<Void> updateServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, DisableServiceAccessRequest request, List<ServicePlanResource> servicePlans) {
        List<String> servicePlanIds = servicePlans.stream()
            .filter(servicePlan -> isUpdateableServicePlan(request.getServicePlanName(), servicePlan))
            .map(ResourceUtils::getId)
            .collect(Collectors.toList());

        if (request.getOrganizationName() != null && !request.getOrganizationName().isEmpty()) {
            return getOrganizationId(cloudFoundryClient, request.getOrganizationName())
                .flatMap(organizationId -> listServicePlanVisibilityIds(cloudFoundryClient, organizationId, servicePlanIds)
                    .flatMap(visibilityId -> requestDeleteServicePlanVisibility(cloudFoundryClient, visibilityId)
                        .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, request.getCompletionTimeout(), job)))
                    .then());
        } else {
            return listServicePlanVisibilityIds(cloudFoundryClient, servicePlanIds)
                .flatMap(visibilityId -> requestDeleteServicePlanVisibility(cloudFoundryClient, visibilityId)
                    .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, request.getCompletionTimeout(), job)))
                .then();
        }
    }

    private static Mono<Void> updateServicePlansPublicStatus(CloudFoundryClient cloudFoundryClient, DisableServiceAccessRequest request, List<ServicePlanResource> servicePlans) {
        if (request.getOrganizationName() != null && !request.getOrganizationName().isEmpty()) {
            return Mono.empty();
        }

        if (request.getServicePlanName() != null && !request.getServicePlanName().isEmpty()) {
            return Mono.empty();
        }

        return Flux.fromIterable(servicePlans)
            .filter(servicePlan -> ResourceUtils.getEntity(servicePlan).getPubliclyVisible())
            .flatMap(servicePlan -> requestUpdateServicePlanPublicStatus(cloudFoundryClient, false, ResourceUtils.getId(servicePlan)))
            .then();
    }

    private static Mono<Void> updateServicePlansPublicStatus(CloudFoundryClient cloudFoundryClient, EnableServiceAccessRequest request, List<ServicePlanResource> servicePlans) {
        if (request.getOrganizationName() != null && !request.getOrganizationName().isEmpty()) {
            return Mono.empty();
        }

        return Flux.fromIterable(servicePlans)
            .filter(servicePlan -> !ResourceUtils.getEntity(servicePlan).getPubliclyVisible())
            .flatMap(servicePlan -> requestUpdateServicePlanPublicStatus(cloudFoundryClient, true, ResourceUtils.getId(servicePlan)))
            .then();
    }

    private static Mono<Void> validateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        if (organizationName != null) {
            return requestListOrganizations(cloudFoundryClient, organizationName)
                .switchIfEmpty(ExceptionUtils.illegalArgument("Organization %s not found", organizationName))
                .then();
        } else {
            return Mono.empty();
        }
    }

    private static Mono<Void> validateService(CloudFoundryClient cloudFoundryClient, String serviceName) {
        if (serviceName != null) {
            return requestListServices(cloudFoundryClient, serviceName)
                .switchIfEmpty(ExceptionUtils.illegalArgument("Service %s not found", serviceName))
                .then();
        } else {
            return Mono.empty();
        }
    }

    private ServiceBroker toServiceBroker(ServiceBrokerResource resource) {
        ServiceBrokerEntity entity = ResourceUtils.getEntity(resource);

        return ServiceBroker.builder()
            .id(ResourceUtils.getId(resource))
            .name(entity.getName())
            .url(entity.getBrokerUrl())
            .build();
    }

}
