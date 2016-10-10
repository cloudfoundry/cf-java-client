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
import org.cloudfoundry.uaa.clients.BatchChangeSecretRequest;
import org.cloudfoundry.uaa.clients.BatchChangeSecretResponse;
import org.cloudfoundry.uaa.clients.BatchCreateClientsRequest;
import org.cloudfoundry.uaa.clients.BatchCreateClientsResponse;
import org.cloudfoundry.uaa.clients.BatchDeleteClientsRequest;
import org.cloudfoundry.uaa.clients.BatchUpdateClientsRequest;
import org.cloudfoundry.uaa.clients.BatchUpdateClientsResponse;
import org.cloudfoundry.uaa.clients.ChangeSecret;
import org.cloudfoundry.uaa.clients.ChangeSecretRequest;
import org.cloudfoundry.uaa.clients.ChangeSecretResponse;
import org.cloudfoundry.uaa.clients.Client;
import org.cloudfoundry.uaa.clients.CreateClient;
import org.cloudfoundry.uaa.clients.CreateClientRequest;
import org.cloudfoundry.uaa.clients.CreateClientResponse;
import org.cloudfoundry.uaa.clients.DeleteClientRequest;
import org.cloudfoundry.uaa.clients.GetClientRequest;
import org.cloudfoundry.uaa.clients.GetClientResponse;
import org.cloudfoundry.uaa.clients.GetMetadataRequest;
import org.cloudfoundry.uaa.clients.GetMetadataResponse;
import org.cloudfoundry.uaa.clients.ListClientsRequest;
import org.cloudfoundry.uaa.clients.ListClientsResponse;
import org.cloudfoundry.uaa.clients.ListMetadatasRequest;
import org.cloudfoundry.uaa.clients.ListMetadatasResponse;
import org.cloudfoundry.uaa.clients.Metadata;
import org.cloudfoundry.uaa.clients.UpdateClient;
import org.cloudfoundry.uaa.clients.UpdateClientRequest;
import org.cloudfoundry.uaa.clients.UpdateMetadataRequest;
import org.cloudfoundry.uaa.clients.UpdateMetadataResponse;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpException;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;

import static org.cloudfoundry.uaa.tokens.GrantType.CLIENT_CREDENTIALS;
import static org.cloudfoundry.uaa.tokens.GrantType.IMPLICIT;
import static org.cloudfoundry.uaa.tokens.GrantType.PASSWORD;
import static org.cloudfoundry.uaa.tokens.GrantType.REFRESH_TOKEN;
import static org.junit.Assert.assertEquals;

public final class ClientsTest extends AbstractIntegrationTest {

    @Autowired
    private String clientId;

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void batchChangeSecret() {
        String clientId1 = this.nameFactory.getClientId();
        String clientId2 = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();
        String newClientSecret1 = this.nameFactory.getClientSecret();
        String newClientSecret2 = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId1, clientSecret)
            .then(requestCreateClient(this.uaaClient, clientId2, clientSecret))
            .then(this.uaaClient.clients()
                .batchChangeSecret(BatchChangeSecretRequest.builder()
                    .changeSecret(ChangeSecret.builder()
                            .clientId(clientId1)
                            .oldSecret(clientSecret)
                            .secret(newClientSecret1)
                            .build(),
                        ChangeSecret.builder()
                            .clientId(clientId2)
                            .oldSecret(clientSecret)
                            .secret(newClientSecret2)
                            .build())
                    .build()))
            .flatMapIterable(BatchChangeSecretResponse::getClients)
            .subscribe(this.testSubscriber()
                .expectCount(2));
    }

    @Test
    public void batchCreate() {
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
            .subscribe(this.<Client>testSubscriber()
                .expectThat(response -> {
                    assertEquals(Arrays.asList(PASSWORD, REFRESH_TOKEN), response.getAuthorizedGrantTypes());
                    assertEquals(clientId1, response.getClientId());
                    assertEquals(Arrays.asList("client.read", "client.write"), response.getScopes());
                    assertEquals("test-token-salt", response.getTokenSalt());
                }));
    }

    @Test
    public void batchDelete() {
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
            .subscribe(this.testSubscriber()
                .expectCount(0));
    }

    @Test
    public void batchUpdate() {
        String clientId1 = this.nameFactory.getClientId();
        String clientId2 = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId1, clientSecret)
            .then(requestCreateClient(this.uaaClient, clientId2, clientSecret))
            .then(this.uaaClient.clients()
                .batchUpdate(BatchUpdateClientsRequest.builder()
                    .client(UpdateClient.builder()
                            .authorizedGrantType(CLIENT_CREDENTIALS, IMPLICIT)
                            .clientId(clientId1)
                            .name("test-name")
                            .scope("client.read", "client.write")
                            .tokenSalt("test-token-salt")
                            .build(),
                        UpdateClient.builder()
                            .authorizedGrantType(PASSWORD)
                            .clientId(clientId2)
                            .name("filtered-test-name")
                            .scope("client.write")
                            .tokenSalt("filtered-test-token-salt")
                            .build())
                    .build()))
            .flatMapIterable(BatchUpdateClientsResponse::getClients)
            .filter(client -> clientId1.equals(client.getClientId()))
            .subscribe(this.<Client>testSubscriber()
                .expectThat(client -> {
                    assertEquals(Arrays.asList(IMPLICIT, CLIENT_CREDENTIALS), client.getAuthorizedGrantTypes());
                    assertEquals(clientId1, client.getClientId());
                    assertEquals("test-name", client.getName());
                    assertEquals(Arrays.asList("client.read", "client.write"), client.getScopes());
                    assertEquals("test-token-salt", client.getTokenSalt());
                }));
    }

    @Test
    public void changeSecret() {
        String clientId = this.nameFactory.getClientId();
        String newClientSecret = this.nameFactory.getClientSecret();
        String oldClientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId, oldClientSecret)
            .then(this.uaaClient.clients()
                .changeSecret(ChangeSecretRequest.builder()
                    .clientId(clientId)
                    .oldSecret(oldClientSecret)
                    .secret(newClientSecret)
                    .build()))
            .subscribe(this.testSubscriber()
                .expectError(HttpException.class, "HTTP request failed with code: 400"));
//        TODO: Update test based on https://www.pivotaltracker.com/n/projects/997278/stories/130645469 to use the following
//            .subscribe(this.<ChangeSecretResponse>testSubscriber()
//                .expectThat(response -> {
//                    assertEquals("secret updated", response.getMessage());
//                    assertEquals("ok", response.getStatus());
//                }));
    }

    @Test
    public void create() {
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
            .subscribe(this.<CreateClientResponse>testSubscriber()
                .expectThat(response -> {
                    assertEquals(Arrays.asList(PASSWORD, REFRESH_TOKEN), response.getAuthorizedGrantTypes());
                    assertEquals(clientId, response.getClientId());
                    assertEquals(Arrays.asList("client.read", "client.write"), response.getScopes());
                    assertEquals("test-token-salt", response.getTokenSalt());
                }));
    }

    @Test
    public void delete() {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .delete(DeleteClientRequest.builder()
                    .clientId(clientId)
                    .build()))
            .flatMap(ignore -> requestListClients(this.uaaClient))
            .filter(client -> clientId.equals(client.getClientId()))
            .subscribe(this.testSubscriber()
                .expectCount(0));
    }

    @Test
    public void get() {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .get(GetClientRequest.builder()
                    .clientId(clientId)
                    .build()))
            .subscribe(this.<GetClientResponse>testSubscriber()
                .expectThat(response -> {
                    assertEquals(Arrays.asList(PASSWORD, REFRESH_TOKEN), response.getAuthorizedGrantTypes());
                    assertEquals(clientId, response.getClientId());
                }));
    }

    @Test
    public void getMetadata() {
        requestUpdateMetadata(this.uaaClient, this.clientId, "http://test.get.url")
            .then(this.uaaClient.clients()
                .getMetadata(GetMetadataRequest.builder()
                    .clientId(this.clientId)
                    .build()))
            .subscribe(this.<GetMetadataResponse>testSubscriber()
                .expectThat(metadata -> {
                    assertEquals("http://test.get.url", metadata.getAppLaunchUrl());
                    assertEquals(this.clientId, metadata.getClientId());
                }));
    }

    @Test
    public void list() {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .list(ListClientsRequest.builder()
                    .build()))
            .flatMapIterable(ListClientsResponse::getResources)
            .filter(client -> clientId.equals(client.getClientId()))
            .subscribe(this.testSubscriber()
                .expectCount(1));
    }

    @Test
    public void listMetadatas() {
        requestUpdateMetadata(this.uaaClient, this.clientId, "http://test.list.url")
            .then(this.uaaClient.clients()
                .listMetadatas(ListMetadatasRequest.builder()
                    .build()))
            .flatMapIterable(ListMetadatasResponse::getMetadatas)
            .filter(metadata -> this.clientId.equals(metadata.getClientId()))
            .single()
            .subscribe(this.<Metadata>testSubscriber()
                .expectThat(metadata -> {
                    assertEquals("http://test.list.url", metadata.getAppLaunchUrl());
                    assertEquals(this.clientId, metadata.getClientId());
                }));
    }

    @Ignore("TODO: Await https://www.pivotaltracker.com/story/show/125572641")
    @Test
    public void mixedActions() {
        //
    }

    @Test
    public void update() {
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
            .subscribe(this.<Client>testSubscriber()
                .expectThat(client -> {
                    assertEquals(Collections.singletonList(CLIENT_CREDENTIALS), client.getAuthorizedGrantTypes());
                    assertEquals(clientId, client.getClientId());
                    assertEquals("test-name", client.getName());
                }));
    }

    @Test
    public void updateMetadata() {
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
            .subscribe(this.<GetMetadataResponse>testSubscriber()
                .expectThat(metadata -> {
                    assertEquals(appIcon, metadata.getAppIcon());
                    assertEquals("http://test.app.launch.url", metadata.getAppLaunchUrl());
                    assertEquals(this.clientId, metadata.getClientId());
                    assertEquals("test-name", metadata.getClientName());
                    assertEquals(true, metadata.getShowOnHomePage());
                }));
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
