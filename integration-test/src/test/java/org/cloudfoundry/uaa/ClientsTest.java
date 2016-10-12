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
import reactor.test.subscriber.ScriptedSubscriber;

import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.cloudfoundry.uaa.tokens.GrantType.CLIENT_CREDENTIALS;
import static org.cloudfoundry.uaa.tokens.GrantType.IMPLICIT;
import static org.cloudfoundry.uaa.tokens.GrantType.PASSWORD;
import static org.cloudfoundry.uaa.tokens.GrantType.REFRESH_TOKEN;

public final class ClientsTest extends AbstractIntegrationTest {

    @Autowired
    private String clientId;

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void batchChangeSecret() throws TimeoutException, InterruptedException {
        String clientId1 = this.nameFactory.getClientId();
        String clientId2 = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();
        String newClientSecret1 = this.nameFactory.getClientSecret();
        String newClientSecret2 = this.nameFactory.getClientSecret();

        ScriptedSubscriber<Client> subscriber = ScriptedSubscriber.<Client>expectValueCount(2)
            .expectComplete();

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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void batchCreate() throws TimeoutException, InterruptedException {
        String clientId1 = this.nameFactory.getClientId();
        String clientId2 = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        Function<Client, Optional<String>> assertion = response -> {
            if (!Arrays.asList(PASSWORD, REFRESH_TOKEN).equals(response.getAuthorizedGrantTypes())) {
                return Optional.of(String.format("expected authorized grant types: %s; actual authorized grant types: %s", Arrays.asList(PASSWORD, REFRESH_TOKEN), response.getAuthorizedGrantTypes()));
            }

            if (!clientId1.equals(response.getClientId())) {
                return Optional.of(String.format("expected client id: %s; actual client id: %s", clientId1, response.getClientId()));
            }

            if (!Arrays.asList("client.read", "client.write").equals(response.getScopes())) {
                return Optional.of(String.format("expected scopes: %s; actual scopes: %s", Arrays.asList("client.read", "client.write"), response.getScopes()));
            }

            if (!"test-token-salt".equals(response.getTokenSalt())) {
                return Optional.of(String.format("expected token salt: %s; actual token salt: %s", "test-token-salt", response.getTokenSalt()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<Client> subscriber = ScriptedSubscriber.<Client>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching client")))
            .expectComplete();

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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void batchDelete() throws TimeoutException, InterruptedException {
        String clientId1 = this.nameFactory.getClientId();
        String clientId2 = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        ScriptedSubscriber<Client> subscriber = ScriptedSubscriber.<Client>create()
            .expectComplete();

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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void batchUpdate() throws TimeoutException, InterruptedException {
        String clientId1 = this.nameFactory.getClientId();
        String clientId2 = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        Function<Client, Optional<String>> assertion = response -> {
            if (!Arrays.asList(IMPLICIT, CLIENT_CREDENTIALS).equals(response.getAuthorizedGrantTypes())) {
                return Optional.of(String.format("expected authorized grant types: %s; actual authorized grant types: %s", Arrays.asList(IMPLICIT, CLIENT_CREDENTIALS),
                    response.getAuthorizedGrantTypes()));
            }

            if (!clientId1.equals(response.getClientId())) {
                return Optional.of(String.format("expected client id: %s; actual client id: %s", clientId1, response.getClientId()));
            }

            if (!"test-name".equals(response.getName())) {
                return Optional.of(String.format("expected name: %s; actual name: %s", "test-name", response.getName()));
            }

            if (!Arrays.asList("client.read", "client.write").equals(response.getScopes())) {
                return Optional.of(String.format("expected scopes: %s; actual scopes: %s", Arrays.asList("client.read", "client.write"), response.getScopes()));
            }

            if (!"test-token-salt".equals(response.getTokenSalt())) {
                return Optional.of(String.format("expected token salt: %s; actual token salt: %s", "test-token-salt", response.getTokenSalt()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<Client> subscriber = ScriptedSubscriber.<Client>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching client")))
            .expectComplete();

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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void changeSecret() throws TimeoutException, InterruptedException {
        String clientId = this.nameFactory.getClientId();
        String newClientSecret = this.nameFactory.getClientSecret();
        String oldClientSecret = this.nameFactory.getClientSecret();

        ScriptedSubscriber<ChangeSecretResponse> subscriber = errorExpectation(HttpException.class, "HTTP request failed with code: 400");

        requestCreateClient(this.uaaClient, clientId, oldClientSecret)
            .then(this.uaaClient.clients()
                .changeSecret(ChangeSecretRequest.builder()
                    .clientId(clientId)
                    .oldSecret(oldClientSecret)
                    .secret(newClientSecret)
                    .build()))
            .subscribe(subscriber);
//        TODO: Update test based on https://www.pivotaltracker.com/n/projects/997278/stories/130645469 to use the following
//            .subscribe(this.<ChangeSecretResponse>testSubscriber()
//                .expectThat(response -> {
//                    assertEquals("secret updated", response.getMessage());
//                    assertEquals("ok", response.getStatus());
//                }));

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        Function<CreateClientResponse, Optional<String>> assertion = response -> {
            if (!Arrays.asList(PASSWORD, REFRESH_TOKEN).equals(response.getAuthorizedGrantTypes())) {
                return Optional.of(String.format("expected authorized grant types: %s; actual authorized grant types: %s", Arrays.asList(PASSWORD, REFRESH_TOKEN), response.getAuthorizedGrantTypes()));
            }

            if (!clientId.equals(response.getClientId())) {
                return Optional.of(String.format("expected client id: %s; actual client id: %s", clientId, response.getClientId()));
            }

            if (!Arrays.asList("client.read", "client.write").equals(response.getScopes())) {
                return Optional.of(String.format("expected scopes: %s; actual scopes: %s", Arrays.asList("client.read", "client.write"), response.getScopes()));
            }

            if (!"test-token-salt".equals(response.getTokenSalt())) {
                return Optional.of(String.format("expected token salt: %s; actual token salt: %s", "test-token-salt", response.getTokenSalt()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<CreateClientResponse> subscriber = ScriptedSubscriber.<CreateClientResponse>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching client")))
            .expectComplete();

        this.uaaClient.clients()
            .create(CreateClientRequest.builder()
                .approvalsDeleted(true)
                .authorizedGrantType(PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope("client.read", "client.write")
                .tokenSalt("test-token-salt")
                .build())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {

        ScriptedSubscriber<Client> subscriber = ScriptedSubscriber.<Client>create()
            .expectComplete();
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .delete(DeleteClientRequest.builder()
                    .clientId(clientId)
                    .build()))
            .flatMap(ignore -> requestListClients(this.uaaClient))
            .filter(client -> clientId.equals(client.getClientId()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        Function<GetClientResponse, Optional<String>> assertion = response -> {
            if (!Arrays.asList(PASSWORD, REFRESH_TOKEN).equals(response.getAuthorizedGrantTypes())) {
                return Optional.of(String.format("expected authorized grant types: %s; actual authorized grant types: %s", Arrays.asList(PASSWORD, REFRESH_TOKEN), response.getAuthorizedGrantTypes()));
            }

            if (!clientId.equals(response.getClientId())) {
                return Optional.of(String.format("expected client id: %s; actual client id: %s", clientId, response.getClientId()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<GetClientResponse> subscriber = ScriptedSubscriber.<GetClientResponse>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching client")))
            .expectComplete();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .get(GetClientRequest.builder()
                    .clientId(clientId)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void getMetadata() throws TimeoutException, InterruptedException {
        Function<GetMetadataResponse, Optional<String>> assertion = metadata -> {
            if (!"http://test.get.url".equals(metadata.getAppLaunchUrl())) {
                return Optional.of(String.format("expected app launch url: %s; actual app launch url: %s", "http://test.get.url", metadata.getAppLaunchUrl()));
            }

            if (!this.clientId.equals(metadata.getClientId())) {
                return Optional.of(String.format("expected client id: %s; actual client id: %s", this.clientId, metadata.getClientId()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<GetMetadataResponse> subscriber = ScriptedSubscriber.<GetMetadataResponse>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching metadata")))
            .expectComplete();

        requestUpdateMetadata(this.uaaClient, this.clientId, "http://test.get.url")
            .then(this.uaaClient.clients()
                .getMetadata(GetMetadataRequest.builder()
                    .clientId(this.clientId)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String clientId = this.nameFactory.getClientId();
        String clientSecret = this.nameFactory.getClientSecret();

        ScriptedSubscriber<Client> subscriber = ScriptedSubscriber.<Client>expectValueCount(1)
            .expectComplete();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .list(ListClientsRequest.builder()
                    .build()))
            .flatMapIterable(ListClientsResponse::getResources)
            .filter(client -> clientId.equals(client.getClientId()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listMetadatas() throws TimeoutException, InterruptedException {
        Function<Metadata, Optional<String>> assertion = metadata -> {
            if (!"http://test.list.url".equals(metadata.getAppLaunchUrl())) {
                return Optional.of(String.format("expected app launch url: %s; actual app launch url: %s", "http://test.list.url", metadata.getAppLaunchUrl()));
            }

            if (!this.clientId.equals(metadata.getClientId())) {
                return Optional.of(String.format("expected client id: %s; actual client id: %s", this.clientId, metadata.getClientId()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<Metadata> subscriber = ScriptedSubscriber.<Metadata>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching metadata")))
            .expectComplete();

        requestUpdateMetadata(this.uaaClient, this.clientId, "http://test.list.url")
            .then(this.uaaClient.clients()
                .listMetadatas(ListMetadatasRequest.builder()
                    .build()))
            .flatMapIterable(ListMetadatasResponse::getMetadatas)
            .filter(metadata -> this.clientId.equals(metadata.getClientId()))
            .single()
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
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

        Function<Client, Optional<String>> assertion = response -> {
            if (!Collections.singletonList(CLIENT_CREDENTIALS).equals(response.getAuthorizedGrantTypes())) {
                return Optional.of(String.format("expected authorized grant types: %s; actual authorized grant types: %s", Collections.singletonList(CLIENT_CREDENTIALS), response
                    .getAuthorizedGrantTypes()));
            }

            if (!clientId.equals(response.getClientId())) {
                return Optional.of(String.format("expected client id: %s; actual client id: %s", clientId, response.getClientId()));
            }

            if (!"test-name".equals(response.getName())) {
                return Optional.of(String.format("expected name: %s; actual name: %s", "test-name", response.getName()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<Client> subscriber = ScriptedSubscriber.<Client>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching client")))
            .expectComplete();

        requestCreateClient(this.uaaClient, clientId, clientSecret)
            .then(this.uaaClient.clients()
                .update(UpdateClientRequest.builder()
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .clientId(clientId)
                    .name("test-name")
                    .build()))
            .flatMap(ignore -> requestListClients(this.uaaClient))
            .filter(client -> clientId.equals(client.getClientId()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateMetadata() throws TimeoutException, InterruptedException {
        String appIcon = Base64.getEncoder().encodeToString(new AsciiString("test-image").toByteArray());

        Function<GetMetadataResponse, Optional<String>> assertion = metadata -> {
            if (!appIcon.equals(metadata.getAppIcon())) {
                return Optional.of(String.format("expected app icon: %s; actual app icon: %s", appIcon, metadata.getAppIcon()));
            }

            if (!"http://test.app.launch.url".equals(metadata.getAppLaunchUrl())) {
                return Optional.of(String.format("expected app launch url: %s; actual app launch url: %s", "http://test.app.launch.url", metadata.getAppLaunchUrl()));
            }

            if (!this.clientId.equals(metadata.getClientId())) {
                return Optional.of(String.format("expected client id: %s; actual client id: %s", this.clientId, metadata.getClientId()));
            }

            if (!"test-name".equals(metadata.getClientName())) {
                return Optional.of(String.format("expected client name: %s; actual client name: %s", "test-name", metadata.getClientName()));
            }

            if (!metadata.getShowOnHomePage()) {
                return Optional.of(String.format("expected show on homepage: %b; actual show on homepage: %b", true, metadata.getShowOnHomePage()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<GetMetadataResponse> subscriber = ScriptedSubscriber.<GetMetadataResponse>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching metadata")))
            .expectComplete();

        this.uaaClient.clients()
            .updateMetadata(UpdateMetadataRequest.builder()
                .appIcon(appIcon)
                .appLaunchUrl("http://test.app.launch.url")
                .clientId(this.clientId)
                .showOnHomePage(true)
                .clientName("test-name")
                .build())
            .then(requestGetMetadata(this.uaaClient, this.clientId))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
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
