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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.GetUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceRoutesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceResource;
import org.cloudfoundry.routing.RoutingClient;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsRequest;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsResponse;
import org.cloudfoundry.routing.v1.routergroups.RouterGroup;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class UserProvidedServicesTest extends AbstractIntegrationTest {

    private static final String DEFAULT_ROUTER_GROUP = "default-tcp";

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private RoutingClient routingClient;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void create() {
        String instanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> this.cloudFoundryClient.userProvidedServiceInstances()
                .create(CreateUserProvidedServiceInstanceRequest.builder()
                    .name(instanceName)
                    .spaceId(spaceId)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(instanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String instanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId))
            .flatMap(instanceId -> this.cloudFoundryClient.userProvidedServiceInstances()
                .delete(DeleteUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceId(instanceId)
                    .build()))
            .thenMany(requestListUserProvidedServiceInstances(this.cloudFoundryClient, instanceName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String instanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId))
            .flatMap(instanceId -> this.cloudFoundryClient.userProvidedServiceInstances()
                .get(GetUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceId(instanceId)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(instanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String instanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> requestCreateUserProvidedServiceInstance(this.cloudFoundryClient, instanceName, spaceId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.userProvidedServiceInstances()
                    .list(ListUserProvidedServiceInstancesRequest.builder()
                        .name(instanceName)
                        .page(page)
                        .build())))
            .single()
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(instanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutes() {
        String domainName = this.nameFactory.getDomainName();
        String instanceName = this.nameFactory.getServiceInstanceName();

        Mono.zip(
            this.organizationId
                .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
            this.spaceId
        )
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, null, null, spaceId))
            ))
            .flatMap(function((instanceId, routeId) -> requestAssociateRoute(this.cloudFoundryClient, instanceId, routeId)
                .thenReturn(instanceId)))
            .flatMapMany(instanceId -> Mono.zip(
                Mono.just(instanceId),
                PaginationUtils.requestClientV2Resources(page -> this.cloudFoundryClient.userProvidedServiceInstances()
                    .listRoutes(ListUserProvidedServiceInstanceRoutesRequest.builder()
                        .page(page)
                        .userProvidedServiceInstanceId(instanceId)
                        .build()))
                    .map(resource -> ResourceUtils.getEntity(resource).getServiceInstanceId())
                    .single()))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByDomainId() {
        String domainName = this.nameFactory.getDomainName();
        String instanceName = this.nameFactory.getServiceInstanceName();

        Mono.zip(
            this.organizationId
                .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
            this.spaceId
        )
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                Mono.just(domainId),
                createUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, null, null, spaceId))
            ))
            .flatMap(function((domainId, instanceId, routeId) -> requestAssociateRoute(this.cloudFoundryClient, instanceId, routeId)
                .thenReturn(Tuples.of(domainId, instanceId))))
            .flatMapMany(function((domainId, instanceId) -> Mono.zip(
                Mono.just(instanceId),
                PaginationUtils.requestClientV2Resources(page -> this.cloudFoundryClient.userProvidedServiceInstances()
                    .listRoutes(ListUserProvidedServiceInstanceRoutesRequest.builder()
                        .domainId(domainId)
                        .page(page)
                        .userProvidedServiceInstanceId(instanceId)
                        .build()))
                    .map(resource -> ResourceUtils.getEntity(resource).getServiceInstanceId())
                    .single())))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByHost() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String instanceName = this.nameFactory.getServiceInstanceName();

        Mono.zip(
            this.organizationId
                .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
            this.spaceId
        )
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, hostName, null, spaceId))
            ))
            .flatMap(function((instanceId, routeId) -> requestAssociateRoute(this.cloudFoundryClient, instanceId, routeId)
                .thenReturn(instanceId)))
            .flatMapMany(instanceId -> Mono.zip(
                Mono.just(instanceId),
                PaginationUtils.requestClientV2Resources(page -> this.cloudFoundryClient.userProvidedServiceInstances()
                    .listRoutes(ListUserProvidedServiceInstanceRoutesRequest.builder()
                        .host(hostName)
                        .page(page)
                        .userProvidedServiceInstanceId(instanceId)
                        .build()))
                    .map(resource -> ResourceUtils.getEntity(resource).getServiceInstanceId())
                    .single()))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByPath() {
        String domainName = this.nameFactory.getDomainName();
        String instanceName = this.nameFactory.getServiceInstanceName();
        String path = this.nameFactory.getPath();

        Mono.zip(
            this.organizationId
                .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
            this.spaceId
        )
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, null, path, spaceId))
            ))
            .flatMap(function((instanceId, routeId) -> requestAssociateRoute(this.cloudFoundryClient, instanceId, routeId)
                .thenReturn(instanceId)))
            .flatMapMany(instanceId -> Mono.zip(
                Mono.just(instanceId),
                PaginationUtils.requestClientV2Resources(page -> this.cloudFoundryClient.userProvidedServiceInstances()
                    .listRoutes(ListUserProvidedServiceInstanceRoutesRequest.builder()
                        .page(page)
                        .path(path)
                        .userProvidedServiceInstanceId(instanceId)
                        .build()))
                    .map(resource -> ResourceUtils.getEntity(resource).getServiceInstanceId())
                    .single()))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByPort() {
        String domainName = this.nameFactory.getDomainName();
        String instanceName = this.nameFactory.getServiceInstanceName();
        Integer port = this.nameFactory.getPort();

        Mono.zip(
            getRouterGroupId(this.routingClient, DEFAULT_ROUTER_GROUP)
                .flatMap(routerGroupId -> createTcpDomainId(this.cloudFoundryClient, domainName, routerGroupId)),
            this.spaceId
        )
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, port, spaceId))
            ))
            .flatMap(function((instanceId, routeId) -> requestAssociateRoute(this.cloudFoundryClient, instanceId, routeId)
                .thenReturn(instanceId)))
            .flatMapMany(instanceId -> Mono.zip(
                Mono.just(instanceId),
                PaginationUtils.requestClientV2Resources(page -> this.cloudFoundryClient.userProvidedServiceInstances()
                    .listRoutes(ListUserProvidedServiceInstanceRoutesRequest.builder()
                        .page(page)
                        .port(port.toString())
                        .userProvidedServiceInstanceId(instanceId)
                        .build()))
                    .map(resource -> ResourceUtils.getEntity(resource).getServiceInstanceId())
                    .single()))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceBindings() {
        String applicationName = this.nameFactory.getApplicationName();
        String instanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                createUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId)
            ))
            .delayUntil(function((applicationId, instanceId) -> this.cloudFoundryClient.serviceBindingsV2()
                .create(CreateServiceBindingRequest.builder()
                    .applicationId(applicationId)
                    .serviceInstanceId(instanceId)
                    .build())))
            .flatMap(function((applicationId, instanceId) -> Mono.zip(
                Mono.just(applicationId),
                Mono.just(instanceId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.userProvidedServiceInstances()
                        .listServiceBindings(ListUserProvidedServiceInstanceServiceBindingsRequest.builder()
                            .applicationId(applicationId)
                            .page(page)
                            .userProvidedServiceInstanceId(instanceId)
                            .build()))
                    .single())))
            .as(StepVerifier::create)
            .consumeNextWith(serviceBindingEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String instanceName = this.nameFactory.getServiceInstanceName();
        String newInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createUserProvidedServiceInstanceId(this.cloudFoundryClient, instanceName, spaceId))
            .flatMap(instanceId -> Mono.zip(
                Mono.just(instanceId),
                this.cloudFoundryClient.userProvidedServiceInstances()
                    .update(UpdateUserProvidedServiceInstanceRequest.builder()
                        .userProvidedServiceInstanceId(instanceId)
                        .name(newInstanceName)
                        .credential("test-cred", "some value")
                        .build())
                    .map(UpdateUserProvidedServiceInstanceResponse::getEntity)
            ))
            .flatMap(function((instanceId, entity1) -> Mono.zip(
                Mono.just(entity1),
                this.cloudFoundryClient.userProvidedServiceInstances()
                    .update(UpdateUserProvidedServiceInstanceRequest.builder()
                        .userProvidedServiceInstanceId(instanceId)
                        .credentials(Collections.emptyMap())
                        .build())
                    .map(UpdateUserProvidedServiceInstanceResponse::getEntity)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((entity1, entity2) -> {
                assertThat(entity1.getName()).isEqualTo(newInstanceName);
                assertThat(entity1.getCredentials()).containsEntry("test-cred", "some value");
                assertThat(entity2.getCredentials()).isEmpty();
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createPrivateDomainId(CloudFoundryClient cloudFoundryClient, String name, String organizationId) {
        return requestCreatePrivateDomain(cloudFoundryClient, name, organizationId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String hostName, String path, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, hostName, path, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String domainId, Integer port, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, port, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createTcpDomainId(CloudFoundryClient cloudFoundryClient, String name, String routerGroupId) {
        return requestCreateTcpDomain(cloudFoundryClient, name, routerGroupId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createUserProvidedServiceInstanceId(CloudFoundryClient cloudFoundryClient, String instanceName, String spaceId) {
        return requestCreateUserProvidedServiceInstance(cloudFoundryClient, instanceName, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getRouterGroupId(RoutingClient routingClient, String routerGroupName) {
        return requestListRouterGroups(routingClient)
            .flatMapIterable(ListRouterGroupsResponse::getRouterGroups)
            .filter(group -> routerGroupName.equals(group.getName()))
            .single()
            .map(RouterGroup::getRouterGroupId);
    }

    private static Mono<AssociateUserProvidedServiceInstanceRouteResponse> requestAssociateRoute(CloudFoundryClient cloudFoundryClient, String instanceId, String routeId) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .associateRoute(AssociateUserProvidedServiceInstanceRouteRequest.builder()
                .routeId(routeId)
                .userProvidedServiceInstanceId(instanceId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreatePrivateDomainResponse> requestCreatePrivateDomain(CloudFoundryClient cloudFoundryClient, String name, String organizationId) {
        return cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(name)
                .owningOrganizationId(organizationId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String hostName, String path, String spaceId) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
                .host(hostName)
                .path(path)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, Integer port, String spaceId) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
                .port(port)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateSharedDomainResponse> requestCreateTcpDomain(CloudFoundryClient cloudFoundryClient, String name, String routerGroupId) {
        return cloudFoundryClient.sharedDomains()
            .create(CreateSharedDomainRequest.builder()
                .name(name)
                .routerGroupId(routerGroupId)
                .build());
    }

    private static Mono<CreateUserProvidedServiceInstanceResponse> requestCreateUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String instanceName, String spaceId) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .create(CreateUserProvidedServiceInstanceRequest.builder()
                .name(instanceName)
                .routeServiceUrl("https://test.url")
                .spaceId(spaceId)
                .build());
    }

    private static Mono<ListRouterGroupsResponse> requestListRouterGroups(RoutingClient routingClient) {
        return routingClient.routerGroups()
            .list(ListRouterGroupsRequest.builder()
                .build());
    }

    private static Flux<UserProvidedServiceInstanceResource> requestListUserProvidedServiceInstances(CloudFoundryClient cloudFoundryClient, String instanceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.userProvidedServiceInstances()
                .list(ListUserProvidedServiceInstancesRequest.builder()
                    .name(instanceName)
                    .page(page)
                    .build()));
    }

    private static Consumer<Tuple3<String, String, ServiceBindingResource>> serviceBindingEquality() {
        return consumer((applicationId, instanceId, resource) -> {
            assertThat(resource.getEntity().getApplicationId()).isEqualTo(applicationId);
            assertThat(resource.getEntity().getServiceInstanceId()).isEqualTo(instanceId);
        });
    }

}
