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
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
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
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Random;

@Configuration
@EnableAutoConfiguration
public class IntegrationTestConfiguration {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    @Bean(initMethod = "clean", destroyMethod = "clean")
    CloudFoundryCleaner cloudFoundryCleaner(CloudFoundryClient cloudFoundryClient, UaaClient uaaClient) {
        return new CloudFoundryCleaner(cloudFoundryClient, uaaClient);
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
            .requestClientV2Resources(page -> cloudFoundryClient.stacks()
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
            .requestClientV2Resources(page -> cloudFoundryClient.users()
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
