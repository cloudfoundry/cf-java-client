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

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.CloudFoundryOperationsBuilder;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.test.FailingDeserializationProblemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Configuration
@EnableAutoConfiguration
public class IntegrationTestConfiguration {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    @Bean(initMethod = "clean", destroyMethod = "clean")
    CloudFoundryCleaner cloudFoundryCleaner(CloudFoundryClient cloudFoundryClient, Mono<List<String>> protectedBuildpackIds, Mono<Optional<String>> protectedDomainId, Mono<List<String>>
        protectedFeatureFlags, Mono<Optional<String>>
                                                protectedOrganizationId, Mono<List<String>> protectedSpaceIds) {
        return new CloudFoundryCleaner(cloudFoundryClient, protectedBuildpackIds, protectedDomainId, protectedFeatureFlags, protectedOrganizationId, protectedSpaceIds);
    }

    @Bean
    SpringCloudFoundryClient cloudFoundryClient(@Value("${test.host}") String host,
                                                @Value("${test.username}") String username,
                                                @Value("${test.password}") String password,
                                                @Value("${test.skipSslValidation:false}") Boolean skipSslValidation) {

        return SpringCloudFoundryClient.builder()
            .host(host)
            .username(username)
            .password(password)
            .skipSslValidation(skipSslValidation)
            .problemHandler(new FailingDeserializationProblemHandler())  // Test-only problem handler
            .build();
    }

    @Bean
    CloudFoundryOperations cloudFoundryOperations(CloudFoundryClient cloudFoundryClient, DopplerClient dopplerClient, UaaClient uaaClient, String organizationName, String spaceName) {
        return new CloudFoundryOperationsBuilder()
            .cloudFoundryClient(cloudFoundryClient)
            .dopplerClient(dopplerClient)
            .uaaClient(uaaClient)
            .target(organizationName, spaceName)
            .build();
    }

    @Bean
    DopplerClient loggingClient(SpringCloudFoundryClient cloudFoundryClient) {
        return ReactorDopplerClient.builder()
            .cloudFoundryClient(cloudFoundryClient)
            .build();
    }

    @Bean
    RandomNameFactory nameFactory(Random random) {
        return new RandomNameFactory(random);
    }

    @Bean(initMethod = "get")
    @DependsOn("cloudFoundryCleaner")
    Mono<String> organizationId(CloudFoundryClient cloudFoundryClient, String organizationName) throws InterruptedException {
        return cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build())
            .map(ResourceUtils::getId)
            .doOnSubscribe(s -> this.logger.debug(">> ORGANIZATION name({}) <<", organizationName))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< ORGANIZATION id({}) >>", id))
            .cache();
    }

    @Bean
    String organizationName(NameFactory nameFactory) {
        return nameFactory.getName("test-organization-");
    }

    @Bean
    Mono<List<String>> protectedBuildpackIds(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.buildpacks()
                .list(ListBuildpacksRequest.builder()
                    .page(page)
                    .build()))
            .map(ResourceUtils::getId)
            .toList()
            .doOnSubscribe(s -> this.logger.debug(">> PROTECTED BUILDPACKS <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< PROTECTED BUILDPACKS >>"))
            .cache();
    }

    @Bean(initMethod = "get")
    Mono<Optional<String>> protectedDomainId(CloudFoundryClient cloudFoundryClient, @Value("${test.protected.domain:}") String protectedDomain) {
        return Mono
            .just(protectedDomain)
            .where(StringUtils::hasText)
            .flatMap(protectedDomain1 -> PaginationUtils
                .requestResources(page -> cloudFoundryClient.domains()
                    .list(ListDomainsRequest.builder()
                        .name(protectedDomain1)
                        .page(page)
                        .build())))
            .singleOrEmpty()
            .map(ResourceUtils::getId)
            .map(Optional::of)
            .otherwiseIfEmpty(Mono.just(Optional.empty()))
            .doOnSubscribe(s -> this.logger.debug(">> PROTECTED DOMAIN <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< PROTECTED DOMAIN >>"))
            .cache();
    }

    @Bean(initMethod = "get")
    Mono<List<String>> protectedFeatureFlags(@Value("${test.protected.featureflags:}") List<String> protectedFlags) {
        return Mono.just(protectedFlags);
    }

    @Bean(initMethod = "get")
    Mono<Optional<String>> protectedOrganizationId(CloudFoundryClient cloudFoundryClient, @Value("${test.protected.organization:}") String protectedOrganization) {
        return Mono
            .just(protectedOrganization)
            .where(StringUtils::hasText)
            .flatMap(protectedOrganization1 -> PaginationUtils
                .requestResources(page -> cloudFoundryClient.organizations()
                    .list(ListOrganizationsRequest.builder()
                        .name(protectedOrganization1)
                        .page(page)
                        .build())))
            .singleOrEmpty()
            .map(ResourceUtils::getId)
            .map(Optional::of)
            .otherwiseIfEmpty(Mono.just(Optional.empty()))
            .doOnSubscribe(s -> this.logger.debug(">> PROTECTED ORGANIZATION <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< PROTECTED ORGANIZATION >>"))
            .cache();
    }

    @Bean(initMethod = "get")
    Mono<List<String>> protectedSpaceIds(CloudFoundryClient cloudFoundryClient, Mono<Optional<String>> protectedOrganizationId) {
        return protectedOrganizationId
            .then(protectedOrganizationId1 -> protectedOrganizationId1
                .map(id -> PaginationUtils
                    .requestResources(page -> cloudFoundryClient.spaces()
                        .list(ListSpacesRequest.builder()
                            .organizationId(id)
                            .page(page)
                            .build()))
                    .map(ResourceUtils::getId)
                    .toList())
                .orElse(Mono.just(Collections.emptyList())))
            .doOnSubscribe(s -> this.logger.debug(">> PROTECTED SPACES <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< PROTECTED SPACES >>"))
            .cache();
    }

    @Bean
    SecureRandom random() {
        return new SecureRandom();
    }

    @Bean(initMethod = "get")
    @DependsOn("cloudFoundryCleaner")
    Mono<String> spaceId(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, String spaceName) throws InterruptedException {
        return organizationId
            .then(orgId -> cloudFoundryClient.spaces()
                .create(CreateSpaceRequest.builder()
                    .name(spaceName)
                    .organizationId(orgId)
                    .build()))
            .map(ResourceUtils::getId)
            .doOnSubscribe(s -> this.logger.debug(">> SPACE name({}) <<", spaceName))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< SPACE id({}) >>", id))
            .cache();
    }

    @Bean
    String spaceName(NameFactory nameFactory) {
        return nameFactory.getName("test-space-");
    }

    @Bean(initMethod = "get")
    @DependsOn("cloudFoundryCleaner")
    Mono<String> stackId(CloudFoundryClient cloudFoundryClient, String stackName) throws InterruptedException {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.stacks()
                .list(ListStacksRequest.builder()
                    .name(stackName)
                    .page(page)
                    .build()))
            .single()
            .map(ResourceUtils::getId)
            .doOnSubscribe(s -> this.logger.debug(">> STACK name({}) <<", stackName))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< STACK >>"))
            .cache();
    }

    @Bean
    String stackName() {
        return "cflinuxfs2";
    }

    @Bean
    ReactorUaaClient uaaClient(SpringCloudFoundryClient cloudFoundryClient) {
        return ReactorUaaClient.builder()
            .cloudFoundryClient(cloudFoundryClient)
            .build();
    }

    @Bean(initMethod = "get")
    @DependsOn("cloudFoundryCleaner")
    Mono<String> userId(CloudFoundryClient cloudFoundryClient, String userName) {  // TODO: Create new user when APIs available
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.users()
                .list(ListUsersRequest.builder()
                    .page(page)
                    .build()))
            .filter(resource -> userName.equals(ResourceUtils.getEntity(resource).getUsername()))
            .map(ResourceUtils::getId)
            .single()
            .doOnSubscribe(s -> this.logger.debug(">> USER name({}) <<", userName))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< USER >>"))
            .cache();
    }

    @Bean
    String userName(@Value("${test.username}") String username) {
        return username;
    }

}
