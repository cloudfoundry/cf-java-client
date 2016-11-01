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

package org.cloudfoundry.uaa;

import io.netty.util.AsciiString;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.uaa.clients.BatchCreateClientsRequest;
import org.cloudfoundry.uaa.clients.BatchCreateClientsResponse;
import org.cloudfoundry.uaa.clients.BatchDeleteClientsRequest;
import org.cloudfoundry.uaa.clients.Client;
import org.cloudfoundry.uaa.clients.CreateClient;
import org.cloudfoundry.uaa.clients.CreateClientRequest;
import org.cloudfoundry.uaa.clients.CreateClientResponse;
import org.cloudfoundry.uaa.clients.DeleteClientRequest;
import org.cloudfoundry.uaa.clients.GetClientRequest;
import org.cloudfoundry.uaa.clients.GetMetadataRequest;
import org.cloudfoundry.uaa.clients.GetMetadataResponse;
import org.cloudfoundry.uaa.clients.ListClientsRequest;
import org.cloudfoundry.uaa.clients.ListClientsResponse;
import org.cloudfoundry.uaa.clients.ListMetadatasRequest;
import org.cloudfoundry.uaa.clients.ListMetadatasResponse;
import org.cloudfoundry.uaa.clients.UpdateClientRequest;
import org.cloudfoundry.uaa.clients.UpdateMetadataRequest;
import org.cloudfoundry.uaa.clients.UpdateMetadataResponse;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.uaa.tokens.GrantType.CLIENT_CREDENTIALS;
import static org.cloudfoundry.uaa.tokens.GrantType.PASSWORD;
import static org.cloudfoundry.uaa.tokens.GrantType.REFRESH_TOKEN;

public final class ClientsTest extends AbstractIntegrationTest {

    @Autowired
    private String clientId;

    @Autowired
    private UaaClient uaaClient;

    @Ignore("TODO: Await https://www.pivotaltracker.com/story/show/125574491")
    @Test
    public void batchChangeSecret() {
        //
    }

    @Test
    public void batchCreate() throws TimeoutException, InterruptedException {
        String clientId1 = this.nameFactory.getClientId();
        String clientId2 = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        this.uaaClient.clients()
            .batchCreate(BatchCreateClientsRequest.builder()
                .client(CreateClient.builder()
                    .approvalsDeleted(true)
                    .authorizedGrantType(PASSWORD)
                    .clientId(clientId1)
                    .clientSecret(clientSecret)
                    .scope("client.read", "client.write")
                    .tokenSalt("test-token-salt")
                    .build())
                .client(CreateClient.builder()
                    .approvalsDeleted(true)
                    .authorizedGrantType(PASSWORD, REFRESH_TOKEN)
                    .clientId(clientId2)
                    .clientSecret(clientSecret)
                    .scope("client.write")
                    .tokenSalt("filtered-test-token-salt")
                    .build())
                .build())
            .flatMapIterable(BatchCreateClientsResponse::getClients)
            .filter(client -> clientId1.equals(client.getClientId()))
            .as(StepVerifier::create)
            .consumeNextWith(response -> {
                assertThat(response.getAuthorizedGrantTypes()).containsExactly(PASSWORD, REFRESH_TOKEN);
                assertThat(response.getClientId()).isEqualTo(clientId1);
                assertThat(response.getScopes()).containsExactly("client.read", "client.write");
                assertThat(response.getTokenSalt()).isEqualTo("test-token-salt");
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void batchDelete() throws TimeoutException, InterruptedException {
        String clientId1 = this.nameFactory.getClientId();
        String clientId2 = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        batchCreateClients(this.uaaClient, clientId1, clientId2, clientSecret)
            .flatMapIterable(BatchCreateClientsResponse::getClients)
            .map(Client::getClientId)
            .collectList()
            .then(clientIds -> this.uaaClient.clients()
                .batchDelete(BatchDeleteClientsRequest.builder()
                    .clientIds(clientIds)
                    .build()))
            .flatMap(ignore -> requestListClients(this.uaaClient))
            .filter(client -> clientId1.equals(client.getClientId()) || clientId2.equals(client.getClientId()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Ignore("TODO: Await https://www.pivotaltracker.com/story/show/125572281")
    @Test
    public void batchUpdate() {
        //
    }

    @Ignore("TODO: Await https://www.pivotaltracker.com/story/show/125553341")
    @Test
    public void changeSecret() {
        //
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        this.uaaClient.clients()
            .create(CreateClientRequest.builder()
                .approvalsDeleted(true)
                .authorizedGrantType(PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope("client.read", "client.write")
                .tokenSalt("test-token-salt")
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(response -> {
                assertThat(response.getAuthorizedGrantTypes()).containsExactly(PASSWORD, REFRESH_TOKEN);
                assertThat(response.getClientId()).isEqualTo(clientId);
                assertThat(response.getScopes()).containsExactly("client.read", "client.write");
                assertThat(response.getTokenSalt()).isEqualTo("test-token-salt");
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .delete(DeleteClientRequest.builder()
                    .clientId(clientId)
                    .build()))
            .flatMap(ignore -> requestListClients(this.uaaClient))
            .filter(client -> clientId.equals(client.getClientId()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .get(GetClientRequest.builder()
                    .clientId(clientId)
                    .build()))
            .as(StepVerifier::create)
            .consumeNextWith(response -> {
                assertThat(response.getAuthorizedGrantTypes()).containsExactly(PASSWORD, REFRESH_TOKEN);
                assertThat(response.getClientId()).isEqualTo(clientId);
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getMetadata() throws TimeoutException, InterruptedException {
        requestUpdateMetadata(this.uaaClient, this.clientId, "http://test.get.url")
            .then(this.uaaClient.clients()
                .getMetadata(GetMetadataRequest.builder()
                    .clientId(this.clientId)
                    .build()))
            .as(StepVerifier::create)
            .consumeNextWith(metadata -> {
                assertThat(metadata.getAppLaunchUrl()).isEqualTo("http://test.get.url");
                assertThat(metadata.getClientId()).isEqualTo(this.clientId);
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .list(ListClientsRequest.builder()
                    .build()))
            .flatMapIterable(ListClientsResponse::getResources)
            .filter(client -> clientId.equals(client.getClientId()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listMetadatas() throws TimeoutException, InterruptedException {
        requestUpdateMetadata(this.uaaClient, this.clientId, "http://test.list.url")
            .then(this.uaaClient.clients()
                .listMetadatas(ListMetadatasRequest.builder()
                    .build()))
            .flatMapIterable(ListMetadatasResponse::getMetadatas)
            .filter(metadata -> this.clientId.equals(metadata.getClientId()))
            .single()
            .as(StepVerifier::create)
            .consumeNextWith(metadata -> {
                assertThat(metadata.getAppLaunchUrl()).isEqualTo("http://test.list.url");
                assertThat(metadata.getClientId()).isEqualTo(this.clientId);
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Ignore("TODO: Await https://www.pivotaltracker.com/story/show/125572641")
    @Test
    public void mixedActions() {
        //
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .update(UpdateClientRequest.builder()
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .clientId(clientId)
                    .name("test-name")
                    .build()))
            .flatMap(ignore -> requestListClients(this.uaaClient))
            .filter(client -> clientId.equals(client.getClientId()))
            .as(StepVerifier::create)
            .consumeNextWith(response -> {
                assertThat(response.getAuthorizedGrantTypes()).containsExactly(CLIENT_CREDENTIALS);
                assertThat(response.getClientId()).isEqualTo(clientId);
                assertThat(response.getName()).isEqualTo("test-name");
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateMetadata() throws TimeoutException, InterruptedException {
        String appIcon = Base64.getEncoder().encodeToString(new AsciiString("test-image").toByteArray());

        this.uaaClient.clients()
            .updateMetadata(UpdateMetadataRequest.builder()
                .appIcon(appIcon)
                .appLaunchUrl("http://test.app.launch.url")
                .clientId(this.clientId)
                .showOnHomePage(true)
                .clientName("test-name")
                .build())
            .then(requestGetMetadata(this.uaaClient, this.clientId))
            .as(StepVerifier::create)
            .consumeNextWith(metadata -> {
                assertThat(metadata.getAppIcon()).isEqualTo(appIcon);
                assertThat(metadata.getAppLaunchUrl()).isEqualTo("http://test.app.launch.url");
                assertThat(metadata.getClientId()).isEqualTo(this.clientId);
                assertThat(metadata.getClientName()).isEqualTo("test-name");
                assertThat(metadata.getShowOnHomePage()).isTrue();
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<BatchCreateClientsResponse> batchCreateClients(UaaClient uaaClient, String clientId1, String clientId2, String clientSecret) {
        return uaaClient.clients()
            .batchCreate(BatchCreateClientsRequest.builder()
                .client(CreateClient.builder()
                    .approvalsDeleted(true)
                    .authorizedGrantType(PASSWORD)
                    .clientId(clientId1)
                    .clientSecret(clientSecret)
                    .scope("client.read", "client.write")
                    .tokenSalt("test-token-salt")
                    .build())
                .client(CreateClient.builder()
                    .approvalsDeleted(true)
                    .authorizedGrantType(PASSWORD, REFRESH_TOKEN)
                    .clientId(clientId2)
                    .clientSecret(clientSecret)
                    .scope("client.write")
                    .tokenSalt("alternate-test-token-salt")
                    .build())
                .build());
    }

    private static Mono<CreateClientResponse> requestCreateClient(UaaClient uaaClient, String clientId, String clientSecret) {
        return uaaClient.clients()
            .create(CreateClientRequest.builder()
                .authorizedGrantType(PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build());
    }

    private static Mono<GetMetadataResponse> requestGetMetadata(UaaClient uaaClient, String clientId) {
        return uaaClient.clients()
            .getMetadata(GetMetadataRequest.builder()
                .clientId(clientId)
                .build());
    }

    private static Flux<Client> requestListClients(UaaClient uaaClient) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.clients()
                .list(ListClientsRequest.builder()
                    .startIndex(startIndex)
                    .build()));

    }

    private static Mono<UpdateMetadataResponse> requestUpdateMetadata(UaaClient uaaClient, String clientId, String appLaunchUrl) {
        return uaaClient.clients()
            .updateMetadata(UpdateMetadataRequest.builder()
                .appLaunchUrl(appLaunchUrl)
                .clientId(clientId)
                .build());
    }

}
