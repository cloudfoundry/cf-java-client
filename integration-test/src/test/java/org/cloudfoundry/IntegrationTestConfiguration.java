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

package org.cloudfoundry;

import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.spring.SpringLoggingClient;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.CloudFoundryOperationsBuilder;
import org.cloudfoundry.utils.PaginationUtils;
import org.cloudfoundry.utils.ResourceUtils;
import org.cloudfoundry.utils.test.FailingDeserializationProblemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import reactor.core.publisher.Mono;
import reactor.fn.Predicate;
import reactor.rx.Promise;
import reactor.rx.Stream;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.cloudfoundry.utils.tuple.TupleUtils.function;

@Configuration
@EnableAutoConfiguration
public class IntegrationTestConfiguration {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    @Bean
    SpringCloudFoundryClient cloudFoundryClient(@Value("${test.host}") String host,
                                                @Value("${test.username}") String username,
                                                @Value("${test.password}") String password,
                                                @Value("${test.skipSslValidation:false}") Boolean skipSslValidation,
                                                List<DeserializationProblemHandler> problemHandlers) {

        return SpringCloudFoundryClient.builder()
            .host(host)
            .username(username)
            .password(password)
            .skipSslValidation(skipSslValidation)
            .problemHandlers(problemHandlers)
            .build();
    }

    @Bean
    @DependsOn({"organizationId", "spaceId"})
    CloudFoundryOperations cloudFoundryOperations(CloudFoundryClient cloudFoundryClient,
                                                  @Value("${test.organization}") String organization,
                                                  @Value("${test.space}") String space) {
        return new CloudFoundryOperationsBuilder()
            .cloudFoundryClient(cloudFoundryClient)
            .target(organization, space)
            .build();
    }

    @Bean
    Predicate<DomainResource> domainsPredicate(@Value("${test.domain}") String domain) {
        return resource -> {
            String name = ResourceUtils.getEntity(resource).getName();
            return !name.endsWith(domain);
        };
    }

    @Bean
    FailingDeserializationProblemHandler failingDeserializationProblemHandler() {
        return new FailingDeserializationProblemHandler();
    }

    @Bean
    SpringLoggingClient loggingClient(SpringCloudFoundryClient cloudFoundryClient) {
        return SpringLoggingClient.builder()
            .cloudFoundryClient(cloudFoundryClient)
            .build();
    }

    @Bean
    Mono<String> organizationId(CloudFoundryClient cloudFoundryClient, @Value("${test.organization}") String organization, Mono<Optional<String>> systemOrganizationId,
                                Mono<List<String>> systemSpaceIds, Predicate<DomainResource> domainsPredicate) throws InterruptedException {

        Mono<String> organizationId = Mono
            .when(systemOrganizationId, systemSpaceIds)
            .flatMap(function((systemOrganizationId2, systemSpaceIds2) -> {

                Predicate<ApplicationResource> applicationPredicate = systemOrganizationId2
                    .map(id -> (Predicate<ApplicationResource>) r -> !systemSpaceIds2.contains(ResourceUtils.getEntity(r).getSpaceId()))
                    .orElse(r -> true);

                Predicate<OrganizationResource> organizationPredicate = systemOrganizationId2
                    .map(id -> (Predicate<OrganizationResource>) r -> !ResourceUtils.getId(r).equals(id))
                    .orElse(r -> true);

                Predicate<RouteResource> routePredicate = r -> true;

                Predicate<SpaceResource> spacePredicate = systemOrganizationId2
                    .map(id -> (Predicate<SpaceResource>) r -> !ResourceUtils.getEntity(r).getOrganizationId().equals(id))
                    .orElse(r -> true);

                return CloudFoundryCleaner.clean(cloudFoundryClient, applicationPredicate, domainsPredicate, organizationPredicate, routePredicate, spacePredicate);
            }))
            .as(Stream::from)
            .after(() -> cloudFoundryClient.organizations()
                .create(CreateOrganizationRequest.builder()
                    .name(organization)
                    .build()))
            .map(ResourceUtils::getId)
            .doOnSubscribe(s -> this.logger.debug(">> ORGANIZATION <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnComplete(() -> this.logger.debug("<< ORGANIZATION >>"))
            .as(Promise::from);

        organizationId.get();
        return organizationId;
    }

    @Bean
    Mono<String> spaceId(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, @Value("${test.space}") String space) throws InterruptedException {
        Mono<String> spaceId = organizationId
            .then(orgId -> cloudFoundryClient.spaces()
                .create(CreateSpaceRequest.builder()
                    .name(space)
                    .organizationId(orgId)
                    .build()))
            .map(ResourceUtils::getId)
            .doOnSubscribe(s -> this.logger.debug(">> SPACE <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< SPACE >>"))
            .as(Promise::from);

        spaceId.get();
        return spaceId;
    }

    @Bean
    Mono<String> stackId(CloudFoundryClient cloudFoundryClient, @Value("${test.stack:cflinuxfs2}") String stack) throws InterruptedException {
        Mono<String> stackId = PaginationUtils
            .requestResources(page -> cloudFoundryClient.stacks()
                .list(ListStacksRequest.builder()
                    .name(stack)
                    .page(page)
                    .build()))
            .single()
            .map(ResourceUtils::getId)
            .doOnSubscribe(s -> this.logger.debug(">> STACK <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< STACK >>"))
            .as(Promise::from);

        stackId.get();
        return stackId;
    }

    @Bean
    Mono<Optional<String>> systemOrganizationId(CloudFoundryClient cloudFoundryClient) {
        Mono<Optional<String>> systemOrganizationId = PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .name("system")
                    .page(page)
                    .build()))
            .singleOrEmpty()
            .map(ResourceUtils::getId)
            .map(Optional::of)
            .otherwiseIfEmpty(Mono.just(Optional.empty()))
            .doOnSubscribe(s -> this.logger.debug(">> SYSTEM ORGANIZATION <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< SYSTEM ORGANIZATION >>"))
            .as(Promise::from);

        systemOrganizationId.get();
        return systemOrganizationId;
    }

    @Bean
    Mono<List<String>> systemSpaceIds(CloudFoundryClient cloudFoundryClient, Mono<Optional<String>> systemOrganizationId) {
        Mono<List<String>> systemSpaceIds = systemOrganizationId
            .then(systemOrganizationId2 -> systemOrganizationId2
                .map(id -> PaginationUtils
                    .requestResources(page -> cloudFoundryClient.spaces()
                        .list(ListSpacesRequest.builder()
                            .organizationId(id)
                            .build()))
                    .map(ResourceUtils::getId)
                    .toList())
                .orElse(Mono.just(Collections.emptyList())));

        systemSpaceIds.get();
        return systemSpaceIds;
    }

    @Bean
    Mono<String> userId(CloudFoundryClient cloudFoundryClient, @Value("${test.username}") String user) {  // TODO: Create new user when APIs available
        Mono<String> userId = PaginationUtils
            .requestResources(page -> cloudFoundryClient.users()
                .listUsers(ListUsersRequest.builder()
                    .page(page)
                    .build()))
            .filter(resource -> user.equals(ResourceUtils.getEntity(resource).getUsername()))
            .map(ResourceUtils::getId)
            .single()
            .doOnSubscribe(s -> this.logger.debug(">> USER <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< USER >>"))
            .as(Promise::from);

        userId.get();
        return userId;
    }

}
