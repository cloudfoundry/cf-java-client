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
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.ProxyConfiguration;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.users.User;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Configuration
@EnableAutoConfiguration
public class IntegrationTestConfiguration {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    @Bean(initMethod = "clean", destroyMethod = "clean")
    CloudFoundryCleaner cloudFoundryCleaner(CloudFoundryClient cloudFoundryClient,
                                            UaaClient uaaClient,
                                            Mono<List<String>> protectedBuildpackIds,
                                            Mono<Optional<String>> protectedDomainId,
                                            Mono<List<String>> protectedFeatureFlags,
                                            Mono<Optional<String>> protectedOrganizationId,
                                            Mono<List<String>> protectedSpaceIds,
                                            Mono<List<String>> protectedUserIds) {

        return new CloudFoundryCleaner(cloudFoundryClient, uaaClient, protectedBuildpackIds, protectedDomainId, protectedFeatureFlags, protectedOrganizationId, protectedSpaceIds, protectedUserIds);
    }

    @Bean(initMethod = "checkCompatibility")
    ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();
    }

    @Bean
    DefaultCloudFoundryOperations cloudFoundryOperations(CloudFoundryClient cloudFoundryClient, DopplerClient dopplerClient, UaaClient uaaClient, String organizationName, String spaceName) {
        return DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(cloudFoundryClient)
            .dopplerClient(dopplerClient)
            .uaaClient(uaaClient)
            .organization(organizationName)
            .space(spaceName)
            .build();
    }

    @Bean
    DefaultConnectionContext connectionContext(@Value("${test.apiHost}") String apiHost,
                                               @Value("${test.proxy.host:}") String proxyHost,
                                               @Value("${test.proxy.password:}") String proxyPassword,
                                               @Value("${test.proxy.port:8080}") Integer proxyPort,
                                               @Value("${test.proxy.username:}") String proxyUsername,
                                               @Value("${test.skipSslValidation:false}") Boolean skipSslValidation) {

        return DefaultConnectionContext.builder()
            .apiHost(apiHost)
            .problemHandler(new FailingDeserializationProblemHandler())  // Test-only problem handler
            .proxyConfiguration(ProxyConfiguration.builder()
                .host(proxyHost)
                .password(proxyPassword)
                .port(proxyPort)
                .username(proxyUsername)
                .build())
            .skipSslValidation(skipSslValidation)
            .build();
    }

    @Bean
    DopplerClient dopplerClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorDopplerClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();
    }

    @Bean
    RandomNameFactory nameFactory(Random random) {
        return new RandomNameFactory(random);
    }

    @Bean(initMethod = "block")
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
            .collectList()
            .doOnSubscribe(s -> this.logger.debug(">> PROTECTED BUILDPACKS <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< PROTECTED BUILDPACKS >>"))
            .cache();
    }

    @Bean(initMethod = "block")
    Mono<Optional<String>> protectedDomainId(CloudFoundryClient cloudFoundryClient, @Value("${test.protected.domain:}") String protectedDomain) {
        return Mono
            .just(protectedDomain)
            .filter(StringUtils::hasText)
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

    @Bean(initMethod = "block")
    Mono<List<String>> protectedFeatureFlags(@Value("${test.protected.featureflags:}") List<String> protectedFlags) {
        return Mono.just(protectedFlags);
    }

    @Bean(initMethod = "block")
    Mono<Optional<String>> protectedOrganizationId(CloudFoundryClient cloudFoundryClient, @Value("${test.protected.organization:}") String protectedOrganization) {
        return Mono
            .just(protectedOrganization)
            .filter(StringUtils::hasText)
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

    @Bean(initMethod = "block")
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
                    .collectList())
                .orElse(Mono.just(Collections.emptyList())))
            .doOnSubscribe(s -> this.logger.debug(">> PROTECTED SPACES <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< PROTECTED SPACES >>"))
            .cache();
    }

    @Bean(initMethod = "block")
    Mono<List<String>> protectedUserIds(UaaClient uaaClient, @Value("${test.protected.users}") String[] protectedUsers) {
        List<String> protectedUserList = Arrays.asList(protectedUsers);

        return uaaClient.users()
            .list(org.cloudfoundry.uaa.users.ListUsersRequest.builder()
                .build())
            .flatMap(response -> Flux.fromIterable(response.getResources()))
            .filter(user -> protectedUserList.contains(user.getUserName()))
            .map(User::getId)
            .collectList()
            .doOnSubscribe(s -> this.logger.debug(">> PROTECTED USERS <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnSuccess(id -> this.logger.debug("<< PROTECTED USERS >>"))
            .cache();
    }

    @Bean
    SecureRandom random() {
        return new SecureRandom();
    }

    @Bean(initMethod = "block")
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

    @Bean(initMethod = "block")
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
    PasswordGrantTokenProvider tokenProvider(@Value("${test.username}") String username,
                                             @Value("${test.password}") String password) {

        return PasswordGrantTokenProvider.builder()
            .password(password)
            .username(username)
            .build();
    }

    @Bean
    ReactorUaaClient uaaClient(ConnectionContext connectionContext,
                               @Value("${test.username}") String username,
                               @Value("${test.password}") String password,
                               String uaaClientId,
                               String uaaClientSecret) {

        return ReactorUaaClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(PasswordGrantTokenProvider.builder()
// TODO: Use a better client id and secret
//                .clientId(uaaClientId)
//                .clientSecret(uaaClientSecret)
                .password(password)
                .username(username)
                .build())
            .build();
    }

    @Bean
    String uaaClientId(@Value("${test.uaa.clientId:}") String testClientId) {
        return testClientId;
    }

    @Bean
    String uaaClientSecret(@Value("${test.uaa.clientSecret:}") String testClientSecret) {
        return testClientSecret;
    }

    @Bean(initMethod = "block")
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
