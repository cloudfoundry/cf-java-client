/*
 * Copyright 2013-2026 the original author or authors.
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

package org.cloudfoundry.client.v3;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.domains.CreateDomainRequest;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.routes.CreateRouteRequest;
import org.cloudfoundry.client.v3.routes.CreateRouteResponse;
import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstance;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstanceRelationships;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstanceType;
import org.cloudfoundry.client.v3.serviceroutebindings.CreateServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.CreateServiceRouteBindingResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.DeleteServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.GetServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.ListServiceRouteBindingsRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.ServiceRouteBinding;
import org.cloudfoundry.client.v3.serviceroutebindings.ServiceRouteBindingRelationships;
import org.cloudfoundry.client.v3.serviceroutebindings.ServiceRouteBindingResource;
import org.cloudfoundry.client.v3.serviceroutebindings.UpdateServiceRouteBindingRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_11)
public class ServiceRouteBindingsTest extends AbstractIntegrationTest {

    @Autowired private CloudFoundryClient cloudFoundryClient;

    @Autowired private Mono<String> organizationId;

    @Autowired private Mono<String> spaceId;

    @Test
    public void create() {
        String domainName = this.nameFactory.getDomainName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.organizationId
                .flatMap(
                        organizationId ->
                                Mono.zip(
                                        createDomainId(
                                                this.cloudFoundryClient,
                                                domainName,
                                                organizationId),
                                        this.spaceId))
                .flatMap(
                        function(
                                (domainId, spaceId) ->
                                        Mono.zip(
                                                createRouteId(
                                                        this.cloudFoundryClient, domainId, spaceId),
                                                createUserProvidedServiceInstanceId(
                                                        this.cloudFoundryClient,
                                                        serviceInstanceName,
                                                        spaceId))))
                .flatMap(
                        function(
                                (routeId, serviceInstanceId) ->
                                        this.cloudFoundryClient
                                                .serviceRouteBindingsV3()
                                                .create(
                                                        CreateServiceRouteBindingRequest.builder()
                                                                .relationships(
                                                                        ServiceRouteBindingRelationships
                                                                                .builder()
                                                                                .route(
                                                                                        ToOneRelationship
                                                                                                .builder()
                                                                                                .data(
                                                                                                        Relationship
                                                                                                                .builder()
                                                                                                                .id(
                                                                                                                        routeId)
                                                                                                                .build())
                                                                                                .build())
                                                                                .serviceInstance(
                                                                                        ToOneRelationship
                                                                                                .builder()
                                                                                                .data(
                                                                                                        Relationship
                                                                                                                .builder()
                                                                                                                .id(
                                                                                                                        serviceInstanceId)
                                                                                                                .build())
                                                                                                .build())
                                                                                .build())
                                                                .build())))
                .map(response -> response.getServiceRouteBinding().isPresent())
                .as(StepVerifier::create)
                .expectNext(true)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String domainName = this.nameFactory.getDomainName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.organizationId
                .flatMap(
                        organizationId ->
                                Mono.zip(
                                        createDomainId(
                                                this.cloudFoundryClient,
                                                domainName,
                                                organizationId),
                                        this.spaceId))
                .flatMap(
                        function(
                                (domainId, spaceId) ->
                                        Mono.zip(
                                                createRouteId(
                                                        this.cloudFoundryClient, domainId, spaceId),
                                                createUserProvidedServiceInstanceId(
                                                        this.cloudFoundryClient,
                                                        serviceInstanceName,
                                                        spaceId))))
                .flatMap(
                        function(
                                (routeId, serviceInstanceId) ->
                                        createServiceRouteBinding(
                                                        this.cloudFoundryClient,
                                                        routeId,
                                                        serviceInstanceId)
                                                .map(ServiceRouteBinding::getId)
                                                .flatMap(
                                                        bindingId ->
                                                                this.cloudFoundryClient
                                                                        .serviceRouteBindingsV3()
                                                                        .delete(
                                                                                DeleteServiceRouteBindingRequest
                                                                                        .builder()
                                                                                        .serviceRouteBindingId(
                                                                                                bindingId)
                                                                                        .build()))
                                                .thenMany(
                                                        requestListServiceRouteBindings(
                                                                this.cloudFoundryClient, routeId))
                                                .hasElements()))
                .as(StepVerifier::create)
                .expectNext(false)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String domainName = this.nameFactory.getDomainName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.organizationId
                .flatMap(
                        organizationId ->
                                Mono.zip(
                                        createDomainId(
                                                this.cloudFoundryClient,
                                                domainName,
                                                organizationId),
                                        this.spaceId))
                .flatMap(
                        function(
                                (domainId, spaceId) ->
                                        Mono.zip(
                                                createRouteId(
                                                        this.cloudFoundryClient, domainId, spaceId),
                                                createUserProvidedServiceInstanceId(
                                                        this.cloudFoundryClient,
                                                        serviceInstanceName,
                                                        spaceId))))
                .flatMap(
                        function(
                                (routeId, serviceInstanceId) ->
                                        createServiceRouteBinding(
                                                        this.cloudFoundryClient,
                                                        routeId,
                                                        serviceInstanceId)
                                                .map(ServiceRouteBinding::getId)
                                                .flatMap(
                                                        bindingId ->
                                                                this.cloudFoundryClient
                                                                        .serviceRouteBindingsV3()
                                                                        .get(
                                                                                GetServiceRouteBindingRequest
                                                                                        .builder()
                                                                                        .serviceRouteBindingId(
                                                                                                bindingId)
                                                                                        .build()))
                                                .map(
                                                        binding ->
                                                                binding.getRelationships()
                                                                        .getServiceInstance()
                                                                        .getData()
                                                                        .getId())
                                                .zipWith(Mono.just(serviceInstanceId))))
                .as(StepVerifier::create)
                .consumeNextWith(tupleEquality())
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String domainName = this.nameFactory.getDomainName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.organizationId
                .flatMap(
                        organizationId ->
                                Mono.zip(
                                        createDomainId(
                                                this.cloudFoundryClient,
                                                domainName,
                                                organizationId),
                                        this.spaceId))
                .flatMap(
                        function(
                                (domainId, spaceId) ->
                                        Mono.zip(
                                                createRouteId(
                                                        this.cloudFoundryClient, domainId, spaceId),
                                                createUserProvidedServiceInstanceId(
                                                        this.cloudFoundryClient,
                                                        serviceInstanceName,
                                                        spaceId))))
                .flatMap(
                        function(
                                (routeId, serviceInstanceId) ->
                                        createServiceRouteBinding(
                                                        this.cloudFoundryClient,
                                                        routeId,
                                                        serviceInstanceId)
                                                .thenMany(
                                                        requestListServiceRouteBindings(
                                                                this.cloudFoundryClient, routeId))
                                                .map(ServiceRouteBindingResource::getRelationships)
                                                .map(
                                                        relationships ->
                                                                relationships
                                                                        .getServiceInstance()
                                                                        .getData()
                                                                        .getId())
                                                .single()
                                                .zipWith(Mono.just(serviceInstanceId))))
                .as(StepVerifier::create)
                .consumeNextWith(tupleEquality())
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String domainName = this.nameFactory.getDomainName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.organizationId
                .flatMap(
                        organizationId ->
                                Mono.zip(
                                        createDomainId(
                                                this.cloudFoundryClient,
                                                domainName,
                                                organizationId),
                                        this.spaceId))
                .flatMap(
                        function(
                                (domainId, spaceId) ->
                                        Mono.zip(
                                                createRouteId(
                                                        this.cloudFoundryClient, domainId, spaceId),
                                                createUserProvidedServiceInstanceId(
                                                        this.cloudFoundryClient,
                                                        serviceInstanceName,
                                                        spaceId))))
                .flatMap(
                        function(
                                (routeId, serviceInstanceId) ->
                                        createServiceRouteBinding(
                                                this.cloudFoundryClient,
                                                routeId,
                                                serviceInstanceId)))
                .map(ServiceRouteBinding::getId)
                .flatMap(
                        bindingId ->
                                this.cloudFoundryClient
                                        .serviceRouteBindingsV3()
                                        .update(
                                                UpdateServiceRouteBindingRequest.builder()
                                                        .serviceRouteBindingId(bindingId)
                                                        .metadata(
                                                                Metadata.builder()
                                                                        .label(
                                                                                "test-label",
                                                                                "test-label-value")
                                                                        .build())
                                                        .build()))
                .map(binding -> binding.getMetadata().getLabels())
                .as(StepVerifier::create)
                .expectNext(Collections.singletonMap("test-label", "test-label-value"))
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createDomainId(
            CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return requestCreateDomain(cloudFoundryClient, domainName, organizationId)
                .map(CreateDomainResponse::getId);
    }

    private static Mono<String> createRouteId(
            CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, spaceId)
                .map(CreateRouteResponse::getId);
    }

    private static Mono<String> createUserProvidedServiceInstanceId(
            CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return requestCreateUserProvidedServiceInstance(
                        cloudFoundryClient, serviceInstanceName, spaceId)
                .map(
                        response ->
                                response.getServiceInstance()
                                        .map(ServiceInstance::getId)
                                        .orElseThrow(
                                                () ->
                                                        new IllegalStateException(
                                                                "Service instance not returned"
                                                                        + " synchronously")));
    }

    private static Mono<ServiceRouteBindingResource> createServiceRouteBinding(
            CloudFoundryClient cloudFoundryClient, String routeId, String serviceInstanceId) {
        return requestCreateServiceRouteBinding(cloudFoundryClient, routeId, serviceInstanceId)
                .flatMap(
                        response -> {
                            if (response.getJobId().isPresent()) {
                                return JobUtils.waitForCompletion(
                                                cloudFoundryClient,
                                                Duration.ofMinutes(5),
                                                response.getJobId().get())
                                        .thenMany(
                                                requestListServiceRouteBindingsByServiceInstance(
                                                        cloudFoundryClient, serviceInstanceId))
                                        .single();
                            }
                            return Mono.just(response.getServiceRouteBinding().get());
                        });
    }

    private static Mono<CreateDomainResponse> requestCreateDomain(
            CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return cloudFoundryClient
                .domainsV3()
                .create(
                        CreateDomainRequest.builder()
                                .internal(false)
                                .name(domainName)
                                .relationships(
                                        DomainRelationships.builder()
                                                .organization(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(organizationId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(
            CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return cloudFoundryClient
                .routesV3()
                .create(
                        CreateRouteRequest.builder()
                                .relationships(
                                        RouteRelationships.builder()
                                                .domain(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(domainId)
                                                                                .build())
                                                                .build())
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(spaceId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build());
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateUserProvidedServiceInstance(
            CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return cloudFoundryClient
                .serviceInstancesV3()
                .create(
                        CreateServiceInstanceRequest.builder()
                                .type(ServiceInstanceType.USER_PROVIDED)
                                .name(serviceInstanceName)
                                .routeServiceUrl("https://route-service.example.com")
                                .relationships(
                                        ServiceInstanceRelationships.builder()
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(spaceId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build());
    }

    private static Mono<CreateServiceRouteBindingResponse> requestCreateServiceRouteBinding(
            CloudFoundryClient cloudFoundryClient, String routeId, String serviceInstanceId) {
        return cloudFoundryClient
                .serviceRouteBindingsV3()
                .create(
                        CreateServiceRouteBindingRequest.builder()
                                .relationships(
                                        ServiceRouteBindingRelationships.builder()
                                                .route(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(routeId)
                                                                                .build())
                                                                .build())
                                                .serviceInstance(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        serviceInstanceId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build());
    }

    private static Flux<ServiceRouteBindingResource> requestListServiceRouteBindings(
            CloudFoundryClient cloudFoundryClient, @Nullable String routeId) {
        return PaginationUtils.requestClientV3Resources(
                page -> {
                    ListServiceRouteBindingsRequest.Builder builder =
                            ListServiceRouteBindingsRequest.builder().page(page);
                    if (routeId != null) {
                        builder.routeId(routeId);
                    }
                    return cloudFoundryClient.serviceRouteBindingsV3().list(builder.build());
                });
    }

    private static Flux<ServiceRouteBindingResource>
            requestListServiceRouteBindingsByServiceInstance(
                    CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .serviceRouteBindingsV3()
                                .list(
                                        ListServiceRouteBindingsRequest.builder()
                                                .page(page)
                                                .serviceInstanceId(serviceInstanceId)
                                                .build()));
    }
}
