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

package org.cloudfoundry.spring.uaa;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.cloudfoundry.spring.uaa.accesstokenadministration.SpringAccessTokenAdministration;
import org.cloudfoundry.spring.uaa.identityzonemanagement.SpringIdentityZoneManagement;
import org.cloudfoundry.spring.util.SchedulerGroupBuilder;
import org.cloudfoundry.spring.util.network.ConnectionContext;
import org.cloudfoundry.spring.util.network.FallbackHttpMessageConverter;
import org.cloudfoundry.spring.util.network.OAuth2RestTemplateBuilder;
import org.cloudfoundry.spring.util.network.SslCertificateTruster;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.accesstokenadministration.AccessTokenAdministration;
import org.cloudfoundry.uaa.identityzonemanagement.IdentityZoneManagement;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * The Spring-based implementation of {@link UaaClient}
 */
@ToString
public final class SpringUaaClient implements UaaClient {

    private final SpringAccessTokenAdministration accessTokenAdministration;

    private final SpringIdentityZoneManagement identityZoneManagement;

    SpringUaaClient(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        this.accessTokenAdministration = new SpringAccessTokenAdministration(restOperations, root, schedulerGroup);
        this.identityZoneManagement = new SpringIdentityZoneManagement(restOperations, root, schedulerGroup);
    }

    @Builder
    SpringUaaClient(@NonNull SpringCloudFoundryClient cloudFoundryClient) {
        this(getRestOperations(cloudFoundryClient.getConnectionContext()),
            getRoot(cloudFoundryClient.getConnectionContext().getCloudFoundryClient(), cloudFoundryClient.getConnectionContext().getSslCertificateTruster()),
            getSchedulerGroup());
    }

    @Override
    public AccessTokenAdministration accessTokenAdministration() {
        return this.accessTokenAdministration;
    }

    @Override
    public IdentityZoneManagement identityZoneManagement() {
        return this.identityZoneManagement;
    }

    private static OAuth2RestOperations getRestOperations(ConnectionContext connectionContext) {
        return new OAuth2RestTemplateBuilder()
            .clientContext(connectionContext.getClientContext())
            .protectedResourceDetails(connectionContext.getProtectedResourceDetails())
            .hostnameVerifier(connectionContext.getHostnameVerifier())
            .sslContext(connectionContext.getSslContext())
            .messageConverter(new FallbackHttpMessageConverter())
            .build();
    }

    private static URI getRoot(CloudFoundryClient cloudFoundryClient, SslCertificateTruster sslCertificateTruster) {
        URI uri = requestInfo(cloudFoundryClient)
            .map(GetInfoResponse::getTokenEndpoint)
            .map(URI::create)
            .get(Duration.ofSeconds(5));

        sslCertificateTruster.trust(uri.getHost(), uri.getPort(), 5, SECONDS);
        return uri;
    }

    private static Scheduler getSchedulerGroup() {
        return new SchedulerGroupBuilder()
            .name("uaa")
            .autoShutdown(false)
            .build();
    }

    private static Mono<GetInfoResponse> requestInfo(CloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.info()
            .get(GetInfoRequest.builder()
                .build());
    }

}
