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

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesResponse;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import org.cloudfoundry.util.tuple.Function2;
import org.cloudfoundry.util.tuple.Function3;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple3;
import reactor.rx.Stream;

import java.util.Map;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultServices implements Services {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> spaceId;

    public DefaultServices(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Void> bind(BindServiceRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function(new Function2<BindServiceRequest, String, Mono<Tuple3<String, String, BindServiceRequest>>>() {

                @Override
                public Mono<Tuple3<String, String, BindServiceRequest>> apply(BindServiceRequest request, String spaceId) {
                    return Mono
                        .when(
                            getSpaceServiceInstanceId(DefaultServices.this.cloudFoundryClient, request.getServiceName(), spaceId),
                            getApplicationId(DefaultServices.this.cloudFoundryClient, request.getApplicationName(), spaceId),
                            Mono.just(request)
                        );
                }

            }))
            .then(function(new Function3<String, String, BindServiceRequest, Mono<CreateServiceBindingResponse>>() {

                @Override
                public Mono<CreateServiceBindingResponse> apply(String serviceInstanceId, String applicationId, BindServiceRequest request) {
                    return requestServiceBinding(DefaultServices.this.cloudFoundryClient, serviceInstanceId, applicationId, request.getParameters());
                }

            }))
            .after();
    }

    private static Mono<ApplicationResource> getApplication(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestApplications(cloudFoundryClient, applicationName, spaceId)
            .single()
            .otherwise(ExceptionUtils.<ApplicationResource>convert("Application %s does not exist", applicationName));
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return getApplication(cloudFoundryClient, applicationName, spaceId)
            .map(ResourceUtils.extractId());
    }

    private static Mono<ServiceInstanceResource> getSpaceServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        return requestSpaceServiceInstances(cloudFoundryClient, serviceName, spaceId)
            .single()
            .otherwise(ExceptionUtils.<ServiceInstanceResource>convert("Service %s does not exist", serviceName));
    }

    private static Mono<String> getSpaceServiceInstanceId(final CloudFoundryClient cloudFoundryClient, final String serviceName, final String spaceId) {
        return getSpaceServiceInstance(cloudFoundryClient, serviceName, spaceId)
            .map(ResourceUtils.extractId());
    }

    private static Stream<ApplicationResource> requestApplications(final CloudFoundryClient cloudFoundryClient, final String application, final String spaceId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

                @Override
                public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                    return cloudFoundryClient.spaces()
                        .listApplications(ListSpaceApplicationsRequest.builder()
                            .name(application)
                            .spaceId(spaceId)
                            .page(page)
                            .build());
                }

            });
    }

    private static Mono<CreateServiceBindingResponse> requestServiceBinding(final CloudFoundryClient cloudFoundryClient, final String serviceInstanceId, final String applicationId,
                                                                            final Map<String, Object> parameters) {
        return cloudFoundryClient.serviceBindings()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .parameters(parameters)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Stream<ServiceInstanceResource> requestSpaceServiceInstances(final CloudFoundryClient cloudFoundryClient, final String serviceName, final String spaceId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpaceServiceInstancesResponse>>() {

                @Override
                public Mono<ListSpaceServiceInstancesResponse> apply(Integer page) {
                    return cloudFoundryClient.spaces()
                        .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                            .page(page)
                            .returnUserProvidedServiceInstances(true)
                            .name(serviceName)
                            .spaceId(spaceId)
                            .build());
                }

            });
    }

}
