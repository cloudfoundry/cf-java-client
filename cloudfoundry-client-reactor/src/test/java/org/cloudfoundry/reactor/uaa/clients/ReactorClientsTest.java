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

package org.cloudfoundry.reactor.uaa.clients;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.SortOrder;
import org.cloudfoundry.uaa.clients.ActionClient;
import org.cloudfoundry.uaa.clients.BatchChangeSecretRequest;
import org.cloudfoundry.uaa.clients.BatchChangeSecretResponse;
import org.cloudfoundry.uaa.clients.BatchCreateClientsRequest;
import org.cloudfoundry.uaa.clients.BatchCreateClientsResponse;
import org.cloudfoundry.uaa.clients.BatchDeleteClientsRequest;
import org.cloudfoundry.uaa.clients.BatchDeleteClientsResponse;
import org.cloudfoundry.uaa.clients.BatchUpdateClientsRequest;
import org.cloudfoundry.uaa.clients.BatchUpdateClientsResponse;
import org.cloudfoundry.uaa.clients.ChangeSecret;
import org.cloudfoundry.uaa.clients.ChangeSecretRequest;
import org.cloudfoundry.uaa.clients.ChangeSecretResponse;
import org.cloudfoundry.uaa.clients.Client;
import org.cloudfoundry.uaa.clients.CreateClient;
import org.cloudfoundry.uaa.clients.CreateClientAction;
import org.cloudfoundry.uaa.clients.CreateClientRequest;
import org.cloudfoundry.uaa.clients.CreateClientResponse;
import org.cloudfoundry.uaa.clients.DeleteClientAction;
import org.cloudfoundry.uaa.clients.DeleteClientRequest;
import org.cloudfoundry.uaa.clients.DeleteClientResponse;
import org.cloudfoundry.uaa.clients.GetClientRequest;
import org.cloudfoundry.uaa.clients.GetClientResponse;
import org.cloudfoundry.uaa.clients.GetMetadataRequest;
import org.cloudfoundry.uaa.clients.GetMetadataResponse;
import org.cloudfoundry.uaa.clients.ListClientsRequest;
import org.cloudfoundry.uaa.clients.ListClientsResponse;
import org.cloudfoundry.uaa.clients.ListMetadatasRequest;
import org.cloudfoundry.uaa.clients.ListMetadatasResponse;
import org.cloudfoundry.uaa.clients.Metadata;
import org.cloudfoundry.uaa.clients.MixedActionsRequest;
import org.cloudfoundry.uaa.clients.MixedActionsResponse;
import org.cloudfoundry.uaa.clients.UpdateClient;
import org.cloudfoundry.uaa.clients.UpdateClientRequest;
import org.cloudfoundry.uaa.clients.UpdateClientResponse;
import org.cloudfoundry.uaa.clients.UpdateMetadataRequest;
import org.cloudfoundry.uaa.clients.UpdateMetadataResponse;
import org.cloudfoundry.uaa.clients.UpdateSecretAction;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.uaa.tokens.GrantType.AUTHORIZATION_CODE;
import static org.cloudfoundry.uaa.tokens.GrantType.CLIENT_CREDENTIALS;
import static org.cloudfoundry.uaa.tokens.GrantType.REFRESH_TOKEN;

public final class ReactorClientsTest extends AbstractUaaApiTest {

    private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void batchChangeSecret() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/clients/tx/secret")
                .payload("fixtures/uaa/clients/POST_tx_secret_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/clients/POST_tx_secret_response.json")
                .build())
            .build());

        this.clients
            .batchChangeSecret(BatchChangeSecretRequest.builder()
                .changeSecret(ChangeSecret.builder()
                    .clientId("Zkgt1Y")
                    .secret("new_secret")
                    .build())
                .changeSecret(ChangeSecret.builder()
                    .clientId("Xm43aH")
                    .secret("new_secret")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(BatchChangeSecretResponse.builder()
                .client(Client.builder()
                    .accessTokenValidity(2700L)
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .approvalsDeleted(true)
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("Zkgt1Y")
                    .lastModified(1474923482301L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .refreshTokenValidity(7000L)
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("uHICvG")
                    .build())
                .client(Client.builder()
                    .accessTokenValidity(2700L)
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .approvalsDeleted(true)
                    .authorities("clients.read", "new.authority", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("Xm43aH")
                    .lastModified(1474923482302L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .refreshTokenValidity(7000L)
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("WjlWvu")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void batchCreate() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/clients/tx")
                .payload("fixtures/uaa/clients/POST_tx_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/clients/POST_tx_response.json")
                .build())
            .build());

        this.clients
            .batchCreate(BatchCreateClientsRequest.builder()
                .client(CreateClient.builder()
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .clientSecret("secret")
                    .name("My Client Name")
                    .redirectUriPatterns("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(CreateClient.builder()
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("0Tgnfy")
                    .clientSecret("secret")
                    .name("My Client Name")
                    .redirectUriPatterns("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("4wMTwN")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(BatchCreateClientsResponse.builder()
                .client(Client.builder()
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .lastModified(1468364444218L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(Client.builder()
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("0Tgnfy")
                    .lastModified(1468364444318L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("4wMTwN")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void batchDelete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/clients/tx/delete")
                .payload("fixtures/uaa/clients/POST_tx_delete_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/POST_tx_delete_response.json")
                .build())
            .build());

        this.clients
            .batchDelete(BatchDeleteClientsRequest.builder()
                .clientIds("14pnUs", "qECLyr")
                .build())
            .as(StepVerifier::create)
            .expectNext(BatchDeleteClientsResponse.builder()
                .client(Client.builder()
                    .approvalsDeleted(true)
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .lastModified(1468364444461L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(Client.builder()
                    .approvalsDeleted(true)
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("qECLyr")
                    .lastModified(1468364444868L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("48TIsq")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void batchUpdate() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/oauth/clients/tx")
                .payload("fixtures/uaa/clients/PUT_tx_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/PUT_tx_response.json")
                .build())
            .build());

        this.clients
            .batchUpdate(BatchUpdateClientsRequest.builder()
                .client(UpdateClient.builder()
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .name("My Client Name")
                    .redirectUriPatterns("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(UpdateClient.builder()
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "new.authority", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("0Tgnfy")
                    .name("My Client Name")
                    .redirectUriPatterns("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("4wMTwN")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(BatchUpdateClientsResponse.builder()
                .client(Client.builder()
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .lastModified(1468364444218L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(Client.builder()
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "new.authority", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("0Tgnfy")
                    .lastModified(1468364444318L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("4wMTwN")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void changeSecrets() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/oauth/clients/BMGkqk/secret")
                .payload("fixtures/uaa/clients/PUT_{id}_secret_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/PUT_{id}_secret_response.json")
                .build())
            .build());

        this.clients
            .changeSecret(ChangeSecretRequest.builder()
                .clientId("BMGkqk")
                .secret("new_secret")
                .build())
            .as(StepVerifier::create)
            .expectNext(ChangeSecretResponse.builder()
                .message("secret updated")
                .status("ok")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/clients")
                .payload("fixtures/uaa/clients/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/clients/POST_response.json")
                .build())
            .build());

        this.clients
            .create(CreateClientRequest.builder()
                .allowedProviders("uaa", "ldap", "my-saml-provider")
                .authorities("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("true")
                .clientId("aPq3I1")
                .clientSecret("secret")
                .name("My Client Name")
                .redirectUriPatterns("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                .scopes("clients.read", "clients.write")
                .tokenSalt("hRZ21X")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateClientResponse.builder()
                .allowedProviders("uaa", "ldap", "my-saml-provider")
                .authorities("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("true")
                .clientId("aPq3I1")
                .lastModified(1468364445109L)
                .name("My Client Name")
                .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                .resourceId("none")
                .scopes("clients.read", "clients.write")
                .tokenSalt("hRZ21X")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/oauth/clients/test-client-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/DELETE_{id}_response.json")
                .build())
            .build());

        this.clients
            .delete(DeleteClientRequest.builder()
                .clientId("test-client-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteClientResponse.builder()
                .allowedProviders("uaa", "ldap", "my-saml-provider")
                .authorities("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("true")
                .clientId("Gieovr")
                .lastModified(1468364443957L)
                .name("My Client Name")
                .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                .resourceId("none")
                .scopes("clients.read", "clients.write")
                .tokenSalt("a4mzKu")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deserialize() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/oauth/clients")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/GET_response_deserialize.json")
                .build())
            .build());

        this.clients
            .list(ListClientsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListClientsResponse.builder()
                .resource(Client.builder()
                    .action("none")
                    .authority("uaa.none")
                    .authorizedGrantTypes(AUTHORIZATION_CODE, REFRESH_TOKEN)
                    .autoApprove("true")
                    .clientId("ssh-proxy")
                    .lastModified(1469112324000L)
                    .redirectUriPattern("/login")
                    .resourceId("none")
                    .scopes("openid", "cloud_controller.read", "cloud_controller.write")
                    .build())
                .resource(Client.builder()
                    .action("none")
                    .authorities("routing.routes.write", "routing.routes.read")
                    .authorizedGrantTypes(CLIENT_CREDENTIALS, REFRESH_TOKEN)
                    .autoApproves("routing.routes.write", "routing.routes.read")
                    .clientId("tcp_emitter")
                    .lastModified(1469112324000L)
                    .resourceId("none")
                    .scope("uaa.none")
                    .build())
                .startIndex(1)
                .itemsPerPage(2)
                .totalResults(2)
                .schema("http://cloudfoundry.org/schema/scim/oauth-clients-1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/oauth/clients/test-client-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/GET_{id}_response.json")
                .build())
            .build());

        this.clients
            .get(GetClientRequest.builder()
                .clientId("test-client-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetClientResponse.builder()
                .allowedProviders("uaa", "ldap", "my-saml-provider")
                .authorities("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("true")
                .clientId("4Z3t1r")
                .lastModified(1468364445592L)
                .name("My Client Name")
                .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                .resourceId("none")
                .scopes("clients.read", "clients.write")
                .tokenSalt("mr80UZ")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getMetadata() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/oauth/clients/P4vuAaSe/meta")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/GET_{id}_meta_response.json")
                .build())
            .build());

        this.clients
            .getMetadata(GetMetadataRequest.builder()
                .clientId("P4vuAaSe")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetMetadataResponse.builder()
                .appIcon("aWNvbiBmb3IgY2xpZW50IDQ=")
                .appLaunchUrl("http://myloginpage.com")
                .clientId("P4vuAaSe")
                .showOnHomePage(true)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/oauth/clients?count=10&filter=client_id%2Beq%2B%22EGgNW3%22&sortBy=client_id&sortOrder=descending&startIndex=1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/GET_response.json")
                .build())
            .build());

        this.clients
            .list(ListClientsRequest.builder()
                .count(10)
                .filter("client_id+eq+\"EGgNW3\"")
                .sortBy("client_id")
                .sortOrder(SortOrder.DESCENDING)
                .startIndex(1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListClientsResponse.builder()
                .resource(Client.builder()
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("EGgNW3")
                    .lastModified(1468364445334L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("7uDPJX")
                    .build())
                .startIndex(1)
                .itemsPerPage(1)
                .totalResults(1)
                .schema("http://cloudfoundry.org/schema/scim/oauth-clients-1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listMetadatas() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/oauth/clients/meta")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/GET_meta_response.json")
                .build())
            .build());

        this.clients
            .listMetadatas(ListMetadatasRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListMetadatasResponse.builder()
                .metadata(Metadata.builder()
                    .appIcon("Y2xpZW50IDMgaWNvbg==")
                    .appLaunchUrl("http://client3.com/app")
                    .clientId("9134O7y4")
                    .showOnHomePage(true)
                    .build())
                .metadata(Metadata.builder()
                    .appIcon("")
                    .appLaunchUrl("http://changed.app.launch/url")
                    .clientId("RpFRZpY3")
                    .showOnHomePage(false)
                    .build())
                .metadata(Metadata.builder()
                    .appIcon("aWNvbiBmb3IgY2xpZW50IDQ=")
                    .appLaunchUrl("http://client4.com/app")
                    .clientId("ewegZo0R")
                    .showOnHomePage(false)
                    .build())
                .metadata(Metadata.builder()
                    .appIcon("aWNvbiBmb3IgY2xpZW50IDQ=")
                    .appLaunchUrl("http://myloginpage.com")
                    .clientId("lqhK1n8q")
                    .showOnHomePage(true)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void mixedActions() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/clients/tx/modify")
                .payload("fixtures/uaa/clients/POST_tx_modify_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/POST_tx_modify_response.json")
                .build())
            .build());

        this.clients
            .mixedActions(MixedActionsRequest.builder()
                .action(UpdateSecretAction.builder()
                    .clientId("Zkgt1Y")
                    .secret("new_secret")
                    .build())
                .action(DeleteClientAction.builder()
                    .clientId("Xm43aH")
                    .build())
                .action(CreateClientAction.builder()
                    .accessTokenValidity(2700L)
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("tlA1z5")
                    .clientSecret("secret")
                    .name("My Client Name")
                    .redirectUriPatterns("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                    .resourceIds()
                    .refreshTokenValidity(7000L)
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("UpzrHR")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(MixedActionsResponse.builder()
                .client(ActionClient.builder()
                    .action("secret")
                    .approvalsDeleted(false)
                    .clientId("Zkgt1Y")
                    .build())
                .client(ActionClient.builder()
                    .accessTokenValidity(2700L)
                    .action("delete")
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .approvalsDeleted(true)
                    .authorities("clients.read", "new.authority", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("Xm43aH")
                    .lastModified(1474923482302L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .refreshTokenValidity(7000L)
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("WjlWvu")
                    .build())
                .client(ActionClient.builder()
                    .accessTokenValidity(2700L)
                    .action("add")
                    .allowedProviders("uaa", "ldap", "my-saml-provider")
                    .authorities("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("tlA1z5")
                    .lastModified(1474923482727L)
                    .name("My Client Name")
                    .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .refreshTokenValidity(7000L)
                    .resourceId("none")
                    .scopes("clients.read", "clients.write")
                    .tokenSalt("UpzrHR")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/oauth/clients/55pTMX")
                .payload("fixtures/uaa/clients/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/PUT_{id}_response.json")
                .build())
            .build());

        this.clients
            .update(UpdateClientRequest.builder()
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("clients.autoapprove")
                .clientId("55pTMX")
                .scopes("clients.new", "clients.autoapprove")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateClientResponse.builder()
                .allowedProviders("uaa", "ldap", "my-saml-provider")
                .authorities("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("clients.autoapprove")
                .clientId("55pTMX")
                .lastModified(1468364443857L)
                .name("My Client Name")
                .redirectUriPatterns("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                .resourceId("none")
                .scopes("clients.new", "clients.autoapprove")
                .tokenSalt("8mwCEy")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateMetadata() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/oauth/clients/RpFRZpY3/meta")
                .payload("fixtures/uaa/clients/PUT_{id}_meta_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/clients/PUT_{id}_meta_response.json")
                .build())
            .build());

        this.clients
            .updateMetadata(UpdateMetadataRequest.builder()
                .appLaunchUrl("http://changed.app.launch/url")
                .clientId("RpFRZpY3")
                .showOnHomePage(false)
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateMetadataResponse.builder()
                .appLaunchUrl("http://changed.app.launch/url")
                .appIcon("")
                .clientId("RpFRZpY3")
                .showOnHomePage(false)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
