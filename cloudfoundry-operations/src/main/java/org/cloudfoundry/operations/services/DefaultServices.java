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

package org.cloudfoundry.operations.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.AbstractServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.BaseServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.LastOperation;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.ListServiceKeysRequest;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyResource;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceResponse;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.util.DelayUtils;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.util.Exceptions;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultServices implements Services {

    private static final int CF_SERVICE_ALREADY_BOUND = 90003;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> spaceId;

    public DefaultServices(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Void> bind(BindServiceInstanceRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> Mono
                .when(
                    getApplicationId(this.cloudFoundryClient, request1.getApplicationName(), spaceId),
                    getSpaceServiceInstanceId(this.cloudFoundryClient, request1.getServiceInstanceName(), spaceId),
                    Mono.just(request1)
                )))
            .then(function((applicationId, serviceInstanceId, request1) -> createServiceBinding(this.cloudFoundryClient, applicationId, serviceInstanceId, request1.getParameters())))
            .after();
    }

    @Override
    public Mono<Void> createInstance(CreateServiceInstanceRequest createRequest) {
        return Mono
            .when(ValidationUtils.validate(createRequest), this.spaceId)
            .then(function((request, spaceId) -> Mono
                .when(
                    Mono.just(request),
                    Mono.just(spaceId),
                    getServiceIdByName(this.cloudFoundryClient, spaceId, request.getServiceName())
                )))
            .then(function((request, spaceId, serviceId) -> Mono
                .when(
                    Mono.just(request),
                    Mono.just(spaceId),
                    getServicePlanIdByName(this.cloudFoundryClient, serviceId, request.getPlanName())
                )))
            .then(function((request, spaceId, planId) -> createServiceInstance(this.cloudFoundryClient, spaceId, planId, request)))
            .then(serviceInstance -> waitForCreateInstance(this.cloudFoundryClient, serviceInstance))
            .after();
    }

    @Override
    public Mono<Void> createServiceKey(CreateServiceKeyRequest createServiceKeyRequest) {
        return Mono
            .when(ValidationUtils.validate(createServiceKeyRequest), this.spaceId)
            .then(function((request, spaceId) -> Mono
                .when(
                    Mono.just(request),
                    getSpaceServiceInstanceId(this.cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .then(function((request, serviceInstanceId) -> requestCreateServiceKey(this.cloudFoundryClient, serviceInstanceId, request.getServiceKeyName(), request.getParameters())))
            .after();
    }

    @Override
    public Mono<Void> createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest createRequest) {
        return Mono
            .when(ValidationUtils.validate(createRequest), this.spaceId)
            .then(function((request, spaceId) -> requestCreateUserProvidedServiceInstance(this.cloudFoundryClient, spaceId, request.getName(), request.getCredentials(), request.getRouteServiceUrl(),
                request.getSyslogDrainUrl())))
            .after();
    }

    @Override
    public Mono<Void> deleteServiceKey(DeleteServiceKeyRequest deleteServiceKeyRequest) {
        return Mono
            .when(ValidationUtils.validate(deleteServiceKeyRequest), this.spaceId)
            .then(function((request, spaceId) -> Mono
                .when(
                    Mono.just(request),
                    getSpaceServiceInstanceId(this.cloudFoundryClient, request.getServiceInstanceName(), spaceId)
                )))
            .then(function((request, serviceInstanceId) -> getServiceKeyIdByName(this.cloudFoundryClient, serviceInstanceId, request.getServiceKeyName())))
            .then(serviceKeyId -> deleteServiceKey(this.cloudFoundryClient, serviceKeyId));
    }

    @Override
    public Mono<ServiceInstance> getInstance(GetServiceInstanceRequest getRequest) {
        return Mono
            .when(ValidationUtils.validate(getRequest), this.spaceId)
            .then(function((request, spaceId) -> getSpaceServiceInstance(cloudFoundryClient, request.getName(), spaceId)))
            .then(resource -> Mono
                .when(
                    Mono.just(resource),
                    getServicePlanEntity(this.cloudFoundryClient, ResourceUtils.getEntity(resource).getServicePlanId())
                ))
            .then(function((resource, servicePlanEntity) -> Mono
                .when(
                    Mono.just(resource),
                    Mono.just(Optional.ofNullable(servicePlanEntity.getName())),
                    getBoundApplications(this.cloudFoundryClient, ResourceUtils.getId(resource)),
                    getServiceEntity(this.cloudFoundryClient, Optional.ofNullable(servicePlanEntity.getServiceId()))
                )))
            .map(function(DefaultServices::toServiceInstance));
    }

    @Override
    public Flux<ServiceInstance> listInstances() {
        return this.spaceId
            .flatMap(spaceId -> requestListServiceInstances(this.cloudFoundryClient, spaceId))
            .flatMap(resource -> Mono
                .when(
                    Mono.just(resource),
                    getServicePlanEntity(this.cloudFoundryClient, ResourceUtils.getEntity(resource).getServicePlanId())
                ))
            .flatMap(function((resource, servicePlanEntity) -> Mono
                .when(
                    Mono.just(resource),
                    Mono.just(Optional.ofNullable(servicePlanEntity.getName())),
                    getBoundApplications(this.cloudFoundryClient, ResourceUtils.getId(resource)),
                    getServiceEntity(this.cloudFoundryClient, Optional.ofNullable(servicePlanEntity.getServiceId()))
                )))
            .map(function(DefaultServices::toServiceInstance));
    }

    @Override
    public Flux<ServiceOffering> listServiceOfferings(ListServiceOfferingsRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .flatMap(function((request1, spaceId) -> Optional.ofNullable(request1.getServiceName())
                    .map(serviceName -> getSpaceService(this.cloudFoundryClient, spaceId, serviceName).flux())
                    .orElse(requestListServices(this.cloudFoundryClient, spaceId))
            ))
            .flatMap(resource -> Mono
                .when(
                    Mono.just(resource),
                    getServicePlans(this.cloudFoundryClient, ResourceUtils.getId(resource))
                ))
            .map(function(DefaultServices::toServiceOffering));
    }

    @Override
    public Mono<Void> renameInstance(RenameServiceInstanceRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((validRequest, spaceId) -> Mono.when(
                Mono.just(validRequest),
                getSpaceServiceInstance(this.cloudFoundryClient, validRequest.getName(), spaceId)
            )))
            .then(function((validRequest, serviceInstance) ->
                    renameServiceInstance(this.cloudFoundryClient, serviceInstance, validRequest.getNewName())
            ))
            .after();
    }

    @Override
    public Mono<Void> unbind(UnbindServiceInstanceRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> Mono
                .when(
                    Mono.just(request1.getServiceInstanceName()),
                    getApplicationId(this.cloudFoundryClient, request1.getApplicationName(), spaceId),
                    getSpaceServiceInstanceId(this.cloudFoundryClient, request1.getServiceInstanceName(), spaceId)
                )))
            .then(function((serviceInstanceName, applicationId, serviceInstanceId) -> getServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId, serviceInstanceName)))
            .then(serviceBindingId -> deleteServiceBinding(this.cloudFoundryClient, serviceBindingId))
            .after();
    }

    private static ServiceInstanceType convertToInstanceType(String type) {
        if (ServiceInstanceType.USER_PROVIDED.getText().equals(type)) {
            return ServiceInstanceType.USER_PROVIDED;
        } else if (ServiceInstanceType.MANAGED.getText().equals(type)) {
            return ServiceInstanceType.MANAGED;
        } else {
            return null;
        }
    }

    private static Mono<CreateServiceBindingResponse> createServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId,
                                                                           Map<String, Object> parameters) {
        return requestCreateServiceBinding(cloudFoundryClient, applicationId, serviceInstanceId, parameters)
            .otherwise(ExceptionUtils.replace(CF_SERVICE_ALREADY_BOUND, () -> Mono.empty()));
    }

    private static Mono<AbstractServiceInstanceResource> createServiceInstance(CloudFoundryClient cloudFoundryClient, String spaceId, String planId, CreateServiceInstanceRequest request) {
        return requestCreateServiceInstance(cloudFoundryClient, spaceId, planId, request.getServiceInstanceName(), request.getParameters(), request.getTags())
            .cast(AbstractServiceInstanceResource.class);
    }

    private static Mono<Void> deleteServiceBinding(CloudFoundryClient cloudFoundryClient, String serviceBindingId) {
        return requestDeleteServiceBinding(cloudFoundryClient, serviceBindingId)
            .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Mono<Void> deleteServiceKey(CloudFoundryClient cloudFoundryClient, String serviceKeyId) {
        return requestDeleteServiceKey(cloudFoundryClient, serviceKeyId);
    }

    private static String extractState(AbstractServiceInstanceResource serviceInstance) {
        return ResourceUtils.getEntity(serviceInstance).getLastOperation().getState();
    }

    private static Mono<ApplicationResource> getApplication(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestListApplications(cloudFoundryClient, applicationName, spaceId)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Application %s does not exist", applicationName)));
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
            .toList();
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

    private static Mono<String> getServiceBindingId(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId, String serviceInstanceName) {
        return requestListServiceBindings(cloudFoundryClient, applicationId, serviceInstanceId)
            .singleOrEmpty()
            .otherwiseIfEmpty(ExceptionUtils.illegalState("Service instance %s is not bound to application", serviceInstanceName))
            .map(ResourceUtils::getId);
    }

    private static Mono<ServiceEntity> getServiceEntity(CloudFoundryClient cloudFoundryClient, Optional<String> serviceId) {
        return Mono
            .justOrEmpty(serviceId)
            .then(serviceId1 -> requestGetService(cloudFoundryClient, serviceId1))
            .map(ResourceUtils::getEntity)
            .otherwiseIfEmpty(Mono.just(ServiceEntity.builder().build()));
    }

    private static Mono<String> getServiceIdByName(CloudFoundryClient cloudFoundryClient, String spaceId, String service) {
        return getSpaceService(cloudFoundryClient, spaceId, service)
            .map(ResourceUtils::getId);
    }

    private static Mono<ServiceKeyResource> getServiceKey(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKeyName) {
        return requestListServiceKeys(cloudFoundryClient, serviceInstanceId, serviceKeyName)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Service key %s does not exist", serviceKeyName)));
    }

    private static Mono<String> getServiceKeyIdByName(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKeyName) {
        return getServiceKey(cloudFoundryClient, serviceInstanceId, serviceKeyName)
            .map(ResourceUtils::getId);
    }

    private static Mono<ServicePlanEntity> getServicePlanEntity(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        return Mono
            .justOrEmpty(servicePlanId)
            .then(servicePlanId1 -> requestGetServicePlan(cloudFoundryClient, servicePlanId1))
            .map(ResourceUtils::getEntity)
            .otherwiseIfEmpty(Mono.just(ServicePlanEntity.builder().build()));
    }

    private static Mono<String> getServicePlanIdByName(CloudFoundryClient cloudFoundryClient, String serviceId, String plan) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
            .filter(resource -> plan.equals(ResourceUtils.getEntity(resource).getName()))
            .single()
            .map(ResourceUtils::getId)
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Service plan %s does not exist", plan)));
    }

    private static Mono<ServiceResource> getSpaceService(CloudFoundryClient cloudFoundryClient, String spaceId, String service) {
        return requestListServices(cloudFoundryClient, spaceId, service)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Service %s does not exist", service)));
    }

    private static Mono<UnionServiceInstanceResource> getSpaceServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return requestListServiceInstances(cloudFoundryClient, spaceId, serviceInstanceName)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Service instance %s does not exist", serviceInstanceName)));
    }

    private static Mono<String> getSpaceServiceInstanceId(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return getSpaceServiceInstance(cloudFoundryClient, serviceInstanceName, spaceId)
            .map(ResourceUtils::getId);
    }

    private static boolean isNotInProgress(String state) {
        return !state.equals("in progress");
    }

    private static boolean isUserProvidedService(UnionServiceInstanceResource serviceInstance) {
        return ResourceUtils.getEntity(serviceInstance).getType().equals("user_provided_service_instance");
    }

    private static Mono<BaseServiceInstanceEntity> renameServiceInstance(CloudFoundryClient cloudFoundryClient, UnionServiceInstanceResource serviceInstance, String newName) {
        if (isUserProvidedService(serviceInstance)) {
            return requestUserProvidedServiceInstanceUpdate(cloudFoundryClient, ResourceUtils.getId(serviceInstance), newName)
                .cast(BaseServiceInstanceEntity.class);
        } else {
            return requestServiceInstanceUpdate(cloudFoundryClient, ResourceUtils.getId(serviceInstance), newName)
                .cast(BaseServiceInstanceEntity.class);
        }
    }

    private static Mono<CreateServiceBindingResponse> requestCreateServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId,
                                                                                  Map<String, Object> parameters) {
        return cloudFoundryClient.serviceBindings()
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

    private static Mono<CreateUserProvidedServiceInstanceResponse> requestCreateUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String spaceId, String name,
                                                                                                            Map<String, Object> credentials, String routeServiceUrl, String syslogDrainUrl) {
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
        return cloudFoundryClient.serviceBindings()
            .delete(DeleteServiceBindingRequest.builder()
                .serviceBindingId(serviceBindingId)
                .async(true)
                .build());
    }

    private static Mono<Void> requestDeleteServiceKey(CloudFoundryClient cloudFoundryClient, String serviceKeyId) {
        return cloudFoundryClient.serviceKeys()
            .delete(org.cloudfoundry.client.v2.servicekeys.DeleteServiceKeyRequest.builder()
                .serviceKeyId(serviceKeyId)
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
            .requestResources(page -> cloudFoundryClient.spaces()
                .listApplications(ListSpaceApplicationsRequest.builder()
                    .name(application)
                    .spaceId(spaceId)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceBindingResource> requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .page(page)
                    .applicationId(applicationId)
                    .serviceInstanceId(serviceInstanceId)
                    .build()));
    }

    private static Flux<ServiceBindingResource> requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.serviceBindings()
                .list(ListServiceBindingsRequest.builder()
                    .page(page)
                    .serviceInstanceId(serviceInstanceId)
                    .build()));
    }

    private static Flux<UnionServiceInstanceResource> requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                    .page(page)
                    .returnUserProvidedServiceInstances(true)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<UnionServiceInstanceResource> requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceInstanceName) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                    .page(page)
                    .returnUserProvidedServiceInstances(true)
                    .name(serviceInstanceName)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<ServiceKeyResource> requestListServiceKeys(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKeyName) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.serviceKeys()
                .list(ListServiceKeysRequest.builder()
                    .page(page)
                    .serviceInstanceId(serviceInstanceId)
                    .name(serviceKeyName)
                    .build()));
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceId(serviceId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listServices(ListSpaceServicesRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceName) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listServices(ListSpaceServicesRequest.builder()
                    .label(serviceName)
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Mono<UpdateServiceInstanceResponse> requestServiceInstanceUpdate(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String newName) {
        return cloudFoundryClient.serviceInstances()
            .update(UpdateServiceInstanceRequest.builder()
                .name(newName)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<UpdateUserProvidedServiceInstanceResponse> requestUserProvidedServiceInstanceUpdate(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String newName) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .update(UpdateUserProvidedServiceInstanceRequest.builder()
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
            .type(convertToInstanceType(serviceInstanceEntity.getType()))
            .updatedAt(lastOperation.getUpdatedAt())
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

    private static Mono<Void> waitForCreateInstance(CloudFoundryClient cloudFoundryClient, AbstractServiceInstanceResource serviceInstance) {
        if (isNotInProgress(extractState(serviceInstance))) {
            return Mono.empty();
        }

        return requestGetServiceInstance(cloudFoundryClient, ResourceUtils.getId(serviceInstance))
            .map(DefaultServices::extractState)
            .where(DefaultServices::isNotInProgress)
            .repeatWhenEmpty(DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)))
            .after();
    }

    private Mono<List<ServicePlanResource>> getServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
            .toList();
    }

}
