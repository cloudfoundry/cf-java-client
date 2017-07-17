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

package org.cloudfoundry.operations.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.AbstractServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.BaseServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.LastOperation;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysRequest;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.client.v2.servicekeys.AbstractServiceKeyResource;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyEntity;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyResource;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityResource;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceResponse;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.RemoveUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.LastOperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultServices implements Services {

    private static final int CF_ROUTE_SERVICE_ALREADY_BOUND = 130008;

    private static final int CF_SERVICE_ALREADY_BOUND = 90003;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<String> organizationId;

    private final Mono<String> spaceId;

    public DefaultServices(Mono<CloudFoundryClient> cloudFoundryClient, Mono<String> organizationId, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
        this.organizationId = organizationId;
    }

    @Override
    public Mono<Void> bind(BindServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getApplicationId(cloudFoundryClient, request.getApplicationName(), spaceId),
                    getSpaceServiceInstanceId(cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, applicationId, serviceInstanceId) -> createServiceBinding(cloudFoundryClient, applicationId, serviceInstanceId, request.getParameters())))
            .then()
            .transform(OperationsLogging.log("Bind Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> bindRoute(BindRouteServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId, this.spaceId)
            .flatMap(function((cloudFoundryClient, organizationId, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getDomainId(cloudFoundryClient, request.getDomainName(), organizationId),
                    Mono.just(spaceId)
                )))
            .flatMap(function((cloudFoundryClient, domainId, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getRouteId(cloudFoundryClient, request.getDomainName(), domainId, request.getHostname(), request.getPath()),
                    getSpaceServiceInstanceId(cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, routeId, userProvidedServiceInstanceId) -> createRouteBinding(cloudFoundryClient, routeId, userProvidedServiceInstanceId, request.getParameters())))
            .then()
            .transform(OperationsLogging.log("Bind Route to Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> createInstance(CreateServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(spaceId),
                    getServiceIdByName(cloudFoundryClient, spaceId, request.getServiceName())
                )))
            .flatMap(function((cloudFoundryClient, spaceId, serviceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(spaceId),
                    getServicePlanIdByName(cloudFoundryClient, serviceId, request.getPlanName())
                )))
            .flatMap(function((cloudFoundryClient, spaceId, planId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(request.getCompletionTimeout()),
                    createServiceInstance(cloudFoundryClient, spaceId, planId, request)
                )))
            .flatMap(function(DefaultServices::waitForCreateInstance))
            .transform(OperationsLogging.log("Create Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> createServiceKey(CreateServiceKeyRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getSpaceServiceInstanceId(cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, serviceInstanceId) -> requestCreateServiceKey(cloudFoundryClient, serviceInstanceId, request.getServiceKeyName(), request.getParameters())))
            .then()
            .transform(OperationsLogging.log("Create Service Key"))
            .checkpoint();
    }

    @Override
    public Mono<Void> createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> requestCreateUserProvidedServiceInstance(cloudFoundryClient, request.getName(), request.getCredentials(), request.getRouteServiceUrl(),
                spaceId, request.getSyslogDrainUrl())))
            .then()
            .transform(OperationsLogging.log("Create User Provided Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> deleteInstance(DeleteServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(request.getCompletionTimeout()),
                    getSpaceServiceInstance(cloudFoundryClient, request.getName(), spaceId)
                )))
            .flatMap(function(DefaultServices::deleteServiceInstance))
            .transform(OperationsLogging.log("Delete Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> deleteServiceKey(DeleteServiceKeyRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getSpaceServiceInstanceId(cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, serviceInstanceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getServiceKey(cloudFoundryClient, serviceInstanceId, request.getServiceKeyName())
                        .map(ResourceUtils::getId)
                )))
            .flatMap(function(DefaultServices::requestDeleteServiceKey))
            .transform(OperationsLogging.log("Delete Service Key"))
            .checkpoint();
    }

    @Override
    public Mono<ServiceInstance> getInstance(GetServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getSpaceServiceInstance(cloudFoundryClient, request.getName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, resource) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(resource),
                    getServicePlanEntity(cloudFoundryClient, ResourceUtils.getEntity(resource).getServicePlanId())
                )))
            .flatMap(function((cloudFoundryClient, resource, servicePlanEntity) -> Mono
                .when(
                    Mono.just(resource),
                    Mono.just(Optional.ofNullable(servicePlanEntity.getName())),
                    getBoundApplications(cloudFoundryClient, ResourceUtils.getId(resource)),
                    getServiceEntity(cloudFoundryClient, Optional.ofNullable(servicePlanEntity.getServiceId()))
                )))
            .map(function(DefaultServices::toServiceInstance))
            .transform(OperationsLogging.log("Get Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<ServiceKey> getServiceKey(GetServiceKeyRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getSpaceServiceInstanceId(cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, serviceInstanceId) -> getServiceKey(cloudFoundryClient, serviceInstanceId, request.getServiceKeyName())))
            .map(DefaultServices::toServiceKey)
            .transform(OperationsLogging.log("Get Service Key"))
            .checkpoint();
    }

    @Override
    public Flux<ServiceInstanceSummary> listInstances() {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> cloudFoundryClient.spaces()
                .getSummary(GetSpaceSummaryRequest.builder()
                    .spaceId(spaceId)
                    .build())))
            .flatMapMany(DefaultServices::toServiceInstanceSummary)
            .transform(OperationsLogging.log("List Service Instances"))
            .checkpoint();
    }

    @Override
    public Flux<ServiceKey> listServiceKeys(ListServiceKeysRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getSpaceServiceInstanceId(cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .flatMapMany(function((cloudFoundryClient, serviceInstanceId) -> requestListServiceInstanceServiceKeys(cloudFoundryClient, serviceInstanceId)))
            .map(DefaultServices::toServiceKey)
            .transform(OperationsLogging.log("List Service Keys"))
            .checkpoint();
    }

    @Override
    public Flux<ServiceOffering> listServiceOfferings(ListServiceOfferingsRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMapMany(function((cloudFoundryClient, spaceId) -> Optional
                .ofNullable(request.getServiceName())
                .map(serviceName -> getSpaceService(cloudFoundryClient, spaceId, serviceName).flux())
                .orElse(requestListServices(cloudFoundryClient, spaceId))
                .map(resource -> Tuples.of(cloudFoundryClient, resource))
            ))
            .flatMap(function((cloudFoundryClient, resource) -> Mono
                .when(
                    Mono.just(resource),
                    getServicePlans(cloudFoundryClient, ResourceUtils.getId(resource))
                )))
            .map(function(DefaultServices::toServiceOffering))
            .transform(OperationsLogging.log("List Service Offerings"))
            .checkpoint();
    }

    @Override
    public Mono<Void> renameInstance(RenameServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getSpaceServiceInstance(cloudFoundryClient, request.getName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, serviceInstance) -> renameServiceInstance(cloudFoundryClient, serviceInstance, request.getNewName())))
            .then()
            .transform(OperationsLogging.log("Rename Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> unbind(UnbindServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getApplicationId(cloudFoundryClient, request.getApplicationName(), spaceId),
                    getSpaceServiceInstanceId(cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, applicationId, serviceInstanceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(request.getCompletionTimeout()),
                    getServiceBindingId(cloudFoundryClient, applicationId, serviceInstanceId, request.getServiceInstanceName())
                )))
            .flatMap(function(DefaultServices::deleteServiceBinding))
            .then()
            .transform(OperationsLogging.log("Unbind Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> unbindRoute(UnbindRouteServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId, this.spaceId)
            .flatMap(function((cloudFoundryClient, organizationId, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getDomainId(cloudFoundryClient, request.getDomainName(), organizationId),
                    Mono.just(spaceId)
                )))
            .flatMap(function((cloudFoundryClient, domainId, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getRouteId(cloudFoundryClient, request.getDomainName(), domainId, request.getHostname(), request.getPath()),
                    getSpaceServiceInstanceId(cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .flatMap(function(DefaultServices::requestRemoveRoute))
            .then()
            .transform(OperationsLogging.log("Unbind Route from Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> updateInstance(UpdateServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId, this.spaceId)
            .flatMap(function((cloudFoundryClient, organizationId, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(organizationId),
                    getSpaceServiceInstance(cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, organizationId, serviceInstance) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(ResourceUtils.getId(serviceInstance)),
                    getOptionalValidatedServicePlanId(cloudFoundryClient, request.getPlanName(), serviceInstance, organizationId)
                )))
            .flatMap(function((cloudFoundryClient, serviceInstanceId, servicePlanId) -> updateServiceInstance(cloudFoundryClient, request, serviceInstanceId, servicePlanId.orElse(null))))
            .then()
            .transform(OperationsLogging.log("Update Service Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> updateUserProvidedInstance(UpdateUserProvidedServiceInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getSpaceUserProvidedServiceInstanceId(cloudFoundryClient, request.getUserProvidedServiceInstanceName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, userProvidedServiceInstanceId) -> updateUserProvidedServiceInstance(cloudFoundryClient, request, userProvidedServiceInstanceId)))
            .then()
            .transform(OperationsLogging.log("Update User Provided Service Instance"))
            .checkpoint();
    }

    private static Mono<Optional<String>> checkVisibility(CloudFoundryClient cloudFoundryClient, String organizationId, ServicePlanResource resource) {
        String servicePlanId = ResourceUtils.getId(resource);

        if (resource.getEntity().getPubliclyVisible()) {
            return Mono.just(Optional.of(servicePlanId));
        }

        return requestListServicePlanVisibilities(cloudFoundryClient, organizationId, servicePlanId)
            .next()
            .switchIfEmpty(ExceptionUtils.illegalArgument("Service Plan %s is not visible to your organization", resource.getEntity().getName()))
            .then(Mono.just(Optional.of(servicePlanId)));
    }

    private static Mono<AssociateUserProvidedServiceInstanceRouteResponse> createRouteBinding(CloudFoundryClient cloudFoundryClient, String routeId, String userProvidedServiceInstanceId,
                                                                                              Map<String, Object> parameters) {
        return requestCreateRouteBinding(cloudFoundryClient, routeId, userProvidedServiceInstanceId, parameters)
            .onErrorResume(ExceptionUtils.statusCode(CF_ROUTE_SERVICE_ALREADY_BOUND), t -> Mono.empty());
    }

    private static Mono<CreateServiceBindingResponse> createServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId, Map<String, Object> parameters) {
        return requestCreateServiceBinding(cloudFoundryClient, applicationId, serviceInstanceId, parameters)
            .onErrorResume(ExceptionUtils.statusCode(CF_SERVICE_ALREADY_BOUND), t -> Mono.empty());
    }

    private static Mono<AbstractServiceInstanceResource> createServiceInstance(CloudFoundryClient cloudFoundryClient, String spaceId, String planId, CreateServiceInstanceRequest request) {
        return requestCreateServiceInstance(cloudFoundryClient, spaceId, planId, request.getServiceInstanceName(), request.getParameters(), request.getTags())
            .cast(AbstractServiceInstanceResource.class);
    }

    private static Mono<Void> deleteServiceBinding(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, String serviceBindingId) {
        return requestDeleteServiceBinding(cloudFoundryClient, serviceBindingId)
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, completionTimeout, job));
    }

    private static Mono<Void> deleteServiceInstance(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, UnionServiceInstanceResource serviceInstance) {
        if (isUserProvidedService(serviceInstance)) {
            return requestDeleteUserProvidedServiceInstance(cloudFoundryClient, ResourceUtils.getId(serviceInstance));
        } else {
            return requestDeleteServiceInstance(cloudFoundryClient, ResourceUtils.getId(serviceInstance))
                .flatMap(response -> {
                    Object entity = response.getEntity();
                    if (entity instanceof JobEntity) {
                        return JobUtils.waitForCompletion(cloudFoundryClient, completionTimeout, (JobEntity) response.getEntity());
                    } else {
                        return LastOperationUtils
                            .waitForCompletion(completionTimeout, () -> requestGetServiceInstance(cloudFoundryClient, ResourceUtils.getId(serviceInstance))
                                .map(r -> ResourceUtils.getEntity(r).getLastOperation()));
                    }
                });
        }
    }

    private static Mono<ApplicationResource> getApplication(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestListApplications(cloudFoundryClient, applicationName, spaceId)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Application %s does not exist", applicationName));
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return getApplication(cloudFoundryClient, applicationName, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<List<String>> getBoundApplications(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return requestListServiceBindings(cloudFoundryClient, serviceInstanceId)
            .map(resource -> ResourceUtils.getEntity(resource).getApplicationId())
            .flatMap(applicationId -> requestGetApplication(cloudFoundryClient, applicationId))
            .map(ResourceUtils::getEntity)
            .map(ApplicationEntity::getName)
            .collectList();
    }

    private static Mono<String> getDomainId(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return getPrivateDomainId(cloudFoundryClient, domain, organizationId)
            .switchIfEmpty(getSharedDomainId(cloudFoundryClient, domain))
            .switchIfEmpty(ExceptionUtils.illegalArgument("Domain %s not found", domain));
    }

    @SuppressWarnings("unchecked")
    private static String getExtraValue(String extra, String key) {
        if (extra == null || extra.isEmpty()) {
            return null;
        }

        try {
            return (String) OBJECT_MAPPER.readValue(extra, Map.class).get(key);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static Mono<Optional<String>> getOptionalValidatedServicePlanId(CloudFoundryClient cloudFoundryClient, String planName, UnionServiceInstanceResource serviceInstance,
                                                                            String organizationId) {
        if (planName == null || planName.isEmpty()) {
            return Mono.just(Optional.empty());
        }

        String servicePlanId = serviceInstance.getEntity().getServicePlanId();

        if (servicePlanId == null || servicePlanId.isEmpty()) {
            return ExceptionUtils.illegalArgument("Plan does not exist for the %s service", serviceInstance.getEntity().getName());
        }

        return getServiceId(cloudFoundryClient, servicePlanId)
            .flatMap(serviceId -> requestGetService(cloudFoundryClient, serviceId))
            .filter(DefaultServices::isPlanUpdateable)
            .switchIfEmpty(ExceptionUtils.illegalArgument("Plan for the %s service cannot be updated", serviceInstance.getEntity().getName()))
            .flatMapMany(response -> requestListServicePlans(cloudFoundryClient, ResourceUtils.getId(response)))
            .filter(resource -> planName.equals(resource.getEntity().getName()))
            .singleOrEmpty()
            .switchIfEmpty(ExceptionUtils.illegalArgument("New service plan %s not found", planName))
            .flatMap(resource -> checkVisibility(cloudFoundryClient, organizationId, resource));
    }

    private static Mono<String> getPrivateDomainId(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return requestPrivateDomain(cloudFoundryClient, domain, organizationId)
            .map(ResourceUtils::getId)
            .singleOrEmpty();
    }

    private static Mono<RouteResource> getRoute(CloudFoundryClient cloudFoundryClient, String domain, String domainId, String host, String path) {
        return getRoute(cloudFoundryClient, domainId, host, path)
            .switchIfEmpty(ExceptionUtils.illegalArgument("Route %s.%s does not exist", host, domain));
    }

    private static Mono<RouteResource> getRoute(CloudFoundryClient cloudFoundryClient, String domainId, String host, String routePath) {
        return requestRoutes(cloudFoundryClient, domainId, host, routePath)
            .filter(resource -> isIdentical(host, ResourceUtils.getEntity(resource).getHost()))
            .filter(resource -> isIdentical(Optional.ofNullable(routePath).orElse(""), ResourceUtils.getEntity(resource).getPath()))
            .singleOrEmpty();
    }

    private static Mono<String> getRouteId(CloudFoundryClient cloudFoundryClient, String domain, String domainId, String host, String path) {
        return getRoute(cloudFoundryClient, domain, domainId, host, path)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getServiceBindingId(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId, String serviceInstanceName) {
        return requestListServiceBindings(cloudFoundryClient, applicationId, serviceInstanceId)
            .singleOrEmpty()
            .switchIfEmpty(ExceptionUtils.illegalState("Service instance %s is not bound to application", serviceInstanceName))
            .map(ResourceUtils::getId);
    }

    private static Mono<ServiceEntity> getServiceEntity(CloudFoundryClient cloudFoundryClient, Optional<String> serviceId) {
        return Mono
            .justOrEmpty(serviceId)
            .flatMap(serviceId1 -> requestGetService(cloudFoundryClient, serviceId1))
            .map(ResourceUtils::getEntity)
            .switchIfEmpty(Mono.just(ServiceEntity.builder().build()));
    }

    private static Mono<String> getServiceId(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        return requestGetServicePlan(cloudFoundryClient, servicePlanId)
            .map(response -> response.getEntity().getServiceId());
    }

    private static Mono<String> getServiceIdByName(CloudFoundryClient cloudFoundryClient, String spaceId, String service) {
        return getSpaceService(cloudFoundryClient, spaceId, service)
            .map(ResourceUtils::getId);
    }

    private static Mono<ServiceKeyResource> getServiceKey(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKey) {
        return requestListServiceInstanceServiceKeys(cloudFoundryClient, serviceInstanceId, serviceKey)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service key %s does not exist", serviceKey));
    }

    private static Mono<ServicePlanEntity> getServicePlanEntity(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        return Mono
            .justOrEmpty(servicePlanId)
            .flatMap(servicePlanId1 -> requestGetServicePlan(cloudFoundryClient, servicePlanId1))
            .map(ResourceUtils::getEntity)
            .switchIfEmpty(Mono.just(ServicePlanEntity.builder().build()));
    }

    private static Mono<String> getServicePlanIdByName(CloudFoundryClient cloudFoundryClient, String serviceId, String plan) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
            .filter(resource -> plan.equals(ResourceUtils.getEntity(resource).getName()))
            .single()
            .map(ResourceUtils::getId)
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service plan %s does not exist", plan));
    }

    private static Mono<List<ServicePlanResource>> getServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
            .collectList();
    }

    private static Mono<String> getSharedDomainId(CloudFoundryClient cloudFoundryClient, String domain) {
        return requestSharedDomain(cloudFoundryClient, domain)
            .map(ResourceUtils::getId)
            .singleOrEmpty();
    }

    private static Mono<ServiceResource> getSpaceService(CloudFoundryClient cloudFoundryClient, String spaceId, String service) {
        return requestListServices(cloudFoundryClient, spaceId, service)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service %s does not exist", service));
    }

    private static Mono<UnionServiceInstanceResource> getSpaceServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return requestListServiceInstances(cloudFoundryClient, spaceId, serviceInstanceName)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service instance %s does not exist", serviceInstanceName));
    }

    private static Mono<String> getSpaceServiceInstanceId(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return getSpaceServiceInstance(cloudFoundryClient, serviceInstanceName, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<UnionServiceInstanceResource> getSpaceUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return requestListServiceInstances(cloudFoundryClient, spaceId, serviceInstanceName)
            .filter(DefaultServices::isUserProvidedService)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("User provided service instance %s does not exist", serviceInstanceName));
    }

    private static Mono<String> getSpaceUserProvidedServiceInstanceId(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return getSpaceUserProvidedServiceInstance(cloudFoundryClient, serviceInstanceName, spaceId)
            .map(ResourceUtils::getId);
    }

    private static boolean isIdentical(String s, String t) {
        return s == null ? t == null : s.equals(t);
    }

    private static boolean isPlanUpdateable(GetServiceResponse response) {
        return response.getEntity().getPlanUpdateable();
    }

    private static boolean isUserProvidedService(UnionServiceInstanceResource serviceInstance) {
        return ServiceInstanceType.from(ResourceUtils.getEntity(serviceInstance).getType()).equals(ServiceInstanceType.USER_PROVIDED);
    }

    private static Mono<BaseServiceInstanceEntity> renameServiceInstance(CloudFoundryClient cloudFoundryClient, UnionServiceInstanceResource serviceInstance, String newName) {
        if (isUserProvidedService(serviceInstance)) {
            return requestUserProvidedServiceInstanceUpdate(cloudFoundryClient, ResourceUtils.getId(serviceInstance), newName)
                .map(ResourceUtils::getEntity)
                .cast(BaseServiceInstanceEntity.class);
        } else {
            return requestServiceInstanceUpdate(cloudFoundryClient, ResourceUtils.getId(serviceInstance), newName)
                .map(ResourceUtils::getEntity)
                .cast(BaseServiceInstanceEntity.class);
        }
    }

    private static Mono<AssociateUserProvidedServiceInstanceRouteResponse> requestCreateRouteBinding(CloudFoundryClient cloudFoundryClient, String routeId, String userProvidedServiceInstanceId,
                                                                                                     Map<String, Object> parameters) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .associateRoute(AssociateUserProvidedServiceInstanceRouteRequest.builder()
                .parameters(parameters)
                .routeId(routeId)
                .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                .build());
    }

    private static Mono<CreateServiceBindingResponse> requestCreateServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId,
                                                                                  Map<String, Object> parameters) {
        return cloudFoundryClient.serviceBindingsV2()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .parameters(parameters)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String spaceId, String planId, String serviceInstance,
                                                                                    Map<String, Object> parameters, List<String> tags) {
        return cloudFoundryClient.serviceInstances()
            .create(org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .name(serviceInstance)
                .servicePlanId(planId)
                .spaceId(spaceId)
                .parameters(parameters)
                .tags(tags)
                .build());
    }

    private static Mono<CreateServiceKeyResponse> requestCreateServiceKey(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKey, Map<String, Object> parameters) {
        return cloudFoundryClient.serviceKeys()
            .create(org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .name(serviceKey)
                .parameters(parameters)
                .build());
    }

    private static Mono<CreateUserProvidedServiceInstanceResponse> requestCreateUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String name, Map<String, Object> credentials,
                                                                                                            String routeServiceUrl, String spaceId, String syslogDrainUrl) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .create(org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest.builder()
                .name(name)
                .credentials(credentials)
                .routeServiceUrl(routeServiceUrl)
                .spaceId(spaceId)
                .syslogDrainUrl(syslogDrainUrl)
                .build());
    }

    private static Mono<DeleteServiceBindingResponse> requestDeleteServiceBinding(CloudFoundryClient cloudFoundryClient, String serviceBindingId) {
        return cloudFoundryClient.serviceBindingsV2()
            .delete(DeleteServiceBindingRequest.builder()
                .serviceBindingId(serviceBindingId)
                .async(true)
                .build());
    }

    private static Mono<DeleteServiceInstanceResponse> requestDeleteServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return cloudFoundryClient.serviceInstances()
            .delete(org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .serviceInstanceId(serviceInstanceId)
                .async(true)
                .build());
    }

    private static Mono<Void> requestDeleteServiceKey(CloudFoundryClient cloudFoundryClient, String serviceKeyId) {
        return cloudFoundryClient.serviceKeys()
            .delete(org.cloudfoundry.client.v2.servicekeys.DeleteServiceKeyRequest.builder()
                .serviceKeyId(serviceKeyId)
                .build());
    }

    private static Mono<Void> requestDeleteUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .delete(DeleteUserProvidedServiceInstanceRequest.builder()
                .userProvidedServiceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<GetApplicationResponse> requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<GetServiceResponse> requestGetService(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return cloudFoundryClient.services()
            .get(GetServiceRequest.builder()
                .serviceId(serviceId)
                .build());
    }

    private static Mono<GetServiceInstanceResponse> requestGetServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return cloudFoundryClient.serviceInstances()
            .get(org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<GetServicePlanResponse> requestGetServicePlan(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        return cloudFoundryClient.servicePlans()
            .get(GetServicePlanRequest.builder()
                .servicePlanId(servicePlanId)
                .build());
    }

    private static Flux<ApplicationResource> requestListApplications(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listApplications(ListSpaceApplicationsRequest.builder()
                    .name(application)
                    .spaceId(spaceId)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceBindingResource> requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .page(page)
                    .applicationId(applicationId)
                    .serviceInstanceId(serviceInstanceId)
                    .build()));
    }

    private static Flux<ServiceBindingResource> requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceBindingsV2()
                .list(ListServiceBindingsRequest.builder()
                    .page(page)
                    .serviceInstanceId(serviceInstanceId)
                    .build()));
    }

    private static Flux<ServiceKeyResource> requestListServiceInstanceServiceKeys(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKey) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceInstances()
                .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .name(serviceKey)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceKeyResource> requestListServiceInstanceServiceKeys(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceInstances()
                .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                    .page(page)
                    .serviceInstanceId(serviceInstanceId)
                    .build()));
    }

    private static Flux<UnionServiceInstanceResource> requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceInstanceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                    .page(page)
                    .returnUserProvidedServiceInstances(true)
                    .name(serviceInstanceName)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<ServicePlanVisibilityResource> requestListServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlanVisibilities()
                .list(ListServicePlanVisibilitiesRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .servicePlanId(servicePlanId)
                    .build()));
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceId(serviceId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listServices(ListSpaceServicesRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listServices(ListSpaceServicesRequest.builder()
                    .label(serviceName)
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestPrivateDomain(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .name(domain)
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Mono<Void> requestRemoveRoute(CloudFoundryClient cloudFoundryClient, String routeId, String userProvidedServiceInstanceId) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .removeRoute(RemoveUserProvidedServiceInstanceRouteRequest.builder()
                .routeId(routeId)
                .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                .build());
    }

    private static Flux<RouteResource> requestRoutes(CloudFoundryClient cloudFoundryClient, UnaryOperator<ListRoutesRequest.Builder> modifier) {

        ListRoutesRequest.Builder listBuilder = modifier.apply(ListRoutesRequest.builder());

        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.routes()
                .list(listBuilder
                    .page(page)
                    .build()));
    }

    private static Flux<RouteResource> requestRoutes(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path) {
        return requestRoutes(cloudFoundryClient, builder -> builder
            .domainId(domainId)
            .hosts(Optional.ofNullable(host).map(Collections::singletonList).orElse(null))
            .paths(Optional.ofNullable(path).map(Collections::singletonList).orElse(null))
        );
    }

    private static Mono<UpdateServiceInstanceResponse> requestServiceInstanceUpdate(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String newName) {
        return cloudFoundryClient.serviceInstances()
            .update(org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest.builder()
                .name(newName)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Flux<SharedDomainResource> requestSharedDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .name(domain)
                    .page(page)
                    .build()));
    }

    private static Mono<UpdateUserProvidedServiceInstanceResponse> requestUserProvidedServiceInstanceUpdate(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String newName) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .update(org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest.builder()
                .name(newName)
                .userProvidedServiceInstanceId(serviceInstanceId)
                .build());
    }

    private static ServiceInstance toServiceInstance(UnionServiceInstanceResource resource, Optional<String> plan, List<String> applications, ServiceEntity serviceEntity) {
        String extra = Optional.ofNullable(serviceEntity.getExtra()).orElse("");
        Optional<String> documentationUrl = Optional.ofNullable(getExtraValue(extra, "documentationUrl"));
        UnionServiceInstanceEntity serviceInstanceEntity = resource.getEntity();
        LastOperation lastOperation = Optional
            .ofNullable(serviceInstanceEntity.getLastOperation())
            .orElse(LastOperation.builder()
                .build());

        return ServiceInstance.builder()
            .applications(applications)
            .dashboardUrl(serviceInstanceEntity.getDashboardUrl())
            .description(serviceEntity.getDescription())
            .documentationUrl(documentationUrl.orElse(null))
            .id(ResourceUtils.getId(resource))
            .lastOperation(lastOperation.getType())
            .message(lastOperation.getDescription())
            .name(serviceInstanceEntity.getName())
            .plan(plan.orElse(null))
            .service(serviceEntity.getLabel())
            .startedAt(lastOperation.getCreatedAt())
            .status(lastOperation.getState())
            .tags(serviceInstanceEntity.getTags())
            .type(ServiceInstanceType.from(serviceInstanceEntity.getType()))
            .updatedAt(lastOperation.getUpdatedAt())
            .build();
    }

    private static Flux<ServiceInstanceSummary> toServiceInstanceSummary(GetSpaceSummaryResponse response) {
        Map<String, List<String>> applicationBindings = new HashMap<>();

        response.getApplications()
            .forEach(application -> application.getServiceNames()
                .forEach(serviceName -> {
                    if (!applicationBindings.containsKey(serviceName)) {
                        applicationBindings.put(serviceName, new ArrayList<>());
                    }

                    applicationBindings.get(serviceName).add(application.getName());
                })
            );

        return Flux.fromIterable(response.getServices())
            .map(service -> ServiceInstanceSummary.builder()
                .applications(Optional.ofNullable(applicationBindings.get(service.getName())).orElse(Collections.emptyList()))
                .id(service.getId())
                .lastOperation(service.getLastOperation() == null ? null : service.getLastOperation().getDescription())
                .name(service.getName())
                .plan(service.getServicePlan() == null ? null : service.getServicePlan().getName())
                .service(service.getServicePlan() == null ? null : service.getServicePlan().getService().getLabel())
                .type(service.getServicePlan() == null ? ServiceInstanceType.USER_PROVIDED : ServiceInstanceType.MANAGED)
                .build());
    }

    private static ServiceKey toServiceKey(AbstractServiceKeyResource resource) {
        ServiceKeyEntity entity = ResourceUtils.getEntity(resource);

        return ServiceKey.builder()
            .credentials(entity.getCredentials())
            .id(ResourceUtils.getId(resource))
            .name(entity.getName())
            .build();
    }

    private static ServiceOffering toServiceOffering(ServiceResource resource, List<ServicePlanResource> servicePlans) {
        ServiceEntity entity = resource.getEntity();

        return ServiceOffering.builder()
            .description(entity.getDescription())
            .id(ResourceUtils.getId(resource))
            .label(entity.getLabel())
            .servicePlans(toServicePlans(servicePlans))
            .build();
    }

    private static ServicePlan toServicePlan(ServicePlanResource resource) {
        ServicePlanEntity entity = ResourceUtils.getEntity(resource);

        return ServicePlan.builder()
            .description(entity.getDescription())
            .free(entity.getFree())
            .id(ResourceUtils.getId(resource))
            .name(entity.getName())
            .build();
    }

    private static List<ServicePlan> toServicePlans(List<ServicePlanResource> servicePlans) {
        return servicePlans.stream()
            .map(DefaultServices::toServicePlan)
            .collect(Collectors.toList());
    }

    private static Mono<UpdateServiceInstanceResponse> updateServiceInstance(CloudFoundryClient cloudFoundryClient, UpdateServiceInstanceRequest request, String serviceInstanceId,
                                                                             String servicePlanId) {
        return cloudFoundryClient.serviceInstances()
            .update(org.cloudfoundry.client.v2.serviceinstances
                .UpdateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .parameters(request.getParameters())
                .serviceInstanceId(serviceInstanceId)
                .servicePlanId(servicePlanId)
                .tags(request.getTags())
                .build());
    }

    private static Mono<UpdateUserProvidedServiceInstanceResponse> updateUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient,
                                                                                                     UpdateUserProvidedServiceInstanceRequest request,
                                                                                                     String userProvidedServiceInstanceId) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .update(org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest.builder()
                .credentials(request.getCredentials())
                .syslogDrainUrl(request.getSyslogDrainUrl())
                .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                .build());
    }

    private static Mono<Void> waitForCreateInstance(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, AbstractServiceInstanceResource serviceInstance) {
        return LastOperationUtils
            .waitForCompletion(completionTimeout, () -> requestGetServiceInstance(cloudFoundryClient, ResourceUtils.getId(serviceInstance))
                .map(response -> ResourceUtils.getEntity(response).getLastOperation()));
    }

}
