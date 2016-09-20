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

package org.cloudfoundry.reactor.uaa.clients;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.SortOrder;
import org.cloudfoundry.uaa.clients.BatchCreateClientsRequest;
import org.cloudfoundry.uaa.clients.BatchCreateClientsResponse;
import org.cloudfoundry.uaa.clients.BatchDeleteClientsRequest;
import org.cloudfoundry.uaa.clients.BatchDeleteClientsResponse;
import org.cloudfoundry.uaa.clients.BatchUpdateClientsRequest;
import org.cloudfoundry.uaa.clients.BatchUpdateClientsResponse;
import org.cloudfoundry.uaa.clients.ChangeSecretRequest;
import org.cloudfoundry.uaa.clients.ChangeSecretResponse;
import org.cloudfoundry.uaa.clients.Client;
import org.cloudfoundry.uaa.clients.CreateClient;
import org.cloudfoundry.uaa.clients.CreateClientRequest;
import org.cloudfoundry.uaa.clients.CreateClientResponse;
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
import org.cloudfoundry.uaa.clients.UpdateClient;
import org.cloudfoundry.uaa.clients.UpdateClientRequest;
import org.cloudfoundry.uaa.clients.UpdateClientResponse;
import org.cloudfoundry.uaa.clients.UpdateMetadataRequest;
import org.cloudfoundry.uaa.clients.UpdateMetadataResponse;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.uaa.tokens.GrantType.AUTHORIZATION_CODE;
import static org.cloudfoundry.uaa.tokens.GrantType.CLIENT_CREDENTIALS;
import static org.cloudfoundry.uaa.tokens.GrantType.REFRESH_TOKEN;

public final class ReactorClientsTest {

    public static final class BatchCreate extends AbstractUaaApiTest<BatchCreateClientsRequest, BatchCreateClientsResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/oauth/clients/tx")
                    .payload("fixtures/uaa/clients/POST_tx_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/clients/POST_tx_response.json")
                    .build())
                .build();
        }

        @Override
        protected BatchCreateClientsResponse getResponse() {
            return BatchCreateClientsResponse.builder()
                .client(Client.builder()
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .lastModified(1468364444218L)
                    .name("My Client Name")
                    .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(Client.builder()
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("0Tgnfy")
                    .lastModified(1468364444318L)
                    .name("My Client Name")
                    .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("4wMTwN")
                    .build())
                .build();
        }

        @Override
        protected BatchCreateClientsRequest getValidRequest() throws Exception {
            return BatchCreateClientsRequest.builder()
                .client(CreateClient.builder()
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .clientSecret("secret")
                    .name("My Client Name")
                    .redirectUriPattern("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(CreateClient.builder()
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("0Tgnfy")
                    .clientSecret("secret")
                    .name("My Client Name")
                    .redirectUriPattern("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("4wMTwN")
                    .build())
                .build();
        }

        @Override
        protected Mono<BatchCreateClientsResponse> invoke(BatchCreateClientsRequest request) {
            return this.clients.batchCreate(request);
        }
    }

    public static final class BatchDelete extends AbstractUaaApiTest<BatchDeleteClientsRequest, BatchDeleteClientsResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/oauth/clients/tx/delete")
                    .payload("fixtures/uaa/clients/POST_tx_delete_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/POST_tx_delete_response.json")
                    .build())
                .build();
        }

        @Override
        protected BatchDeleteClientsResponse getResponse() {
            return BatchDeleteClientsResponse.builder()
                .client(Client.builder()
                    .approvalsDeleted(true)
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .lastModified(1468364444461L)
                    .name("My Client Name")
                    .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(Client.builder()
                    .approvalsDeleted(true)
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("qECLyr")
                    .lastModified(1468364444868L)
                    .name("My Client Name")
                    .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("48TIsq")
                    .build())
                .build();
        }

        @Override
        protected BatchDeleteClientsRequest getValidRequest() throws Exception {
            return BatchDeleteClientsRequest.builder()
                .clientId("14pnUs", "qECLyr")
                .build();
        }

        @Override
        protected Mono<BatchDeleteClientsResponse> invoke(BatchDeleteClientsRequest request) {
            return this.clients.batchDelete(request);
        }
    }

    public static final class BatchUpdate extends AbstractUaaApiTest<BatchUpdateClientsRequest, BatchUpdateClientsResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/oauth/clients/tx")
                    .payload("fixtures/uaa/clients/PUT_tx_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/PUT_tx_response.json")
                    .build())
                .build();
        }

        @Override
        protected BatchUpdateClientsResponse getResponse() {
            return BatchUpdateClientsResponse.builder()
                .client(Client.builder()
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .lastModified(1468364444218L)
                    .name("My Client Name")
                    .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(Client.builder()
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "new.authority", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("0Tgnfy")
                    .lastModified(1468364444318L)
                    .name("My Client Name")
                    .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("4wMTwN")
                    .build())
                .build();
        }

        @Override
        protected BatchUpdateClientsRequest getValidRequest() throws Exception {
            return BatchUpdateClientsRequest.builder()
                .client(UpdateClient.builder()
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("14pnUs")
                    .name("My Client Name")
                    .redirectUriPattern("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("erRsWH")
                    .build())
                .client(UpdateClient.builder()
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "new.authority", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("0Tgnfy")
                    .name("My Client Name")
                    .redirectUriPattern("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("4wMTwN")
                    .build())
                .build();
        }

        @Override
        protected Mono<BatchUpdateClientsResponse> invoke(BatchUpdateClientsRequest request) {
            return this.clients.batchUpdate(request);
        }
    }

    public static final class ChangeSecret extends AbstractUaaApiTest<ChangeSecretRequest, ChangeSecretResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/oauth/clients/BMGkqk/secret")
                    .payload("fixtures/uaa/clients/PUT_{id}_secret_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/PUT_{id}_secret_response.json")
                    .build())
                .build();
        }

        @Override
        protected ChangeSecretResponse getResponse() {
            return ChangeSecretResponse.builder()
                .message("secret updated")
                .status("ok")
                .build();
        }

        @Override
        protected ChangeSecretRequest getValidRequest() throws Exception {
            return ChangeSecretRequest.builder()
                .clientId("BMGkqk")
                .secret("new_secret")
                .build();
        }

        @Override
        protected Mono<ChangeSecretResponse> invoke(ChangeSecretRequest request) {
            return this.clients.changeSecret(request);
        }
    }

    public static final class Create extends AbstractUaaApiTest<CreateClientRequest, CreateClientResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/oauth/clients")
                    .payload("fixtures/uaa/clients/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/clients/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected CreateClientResponse getResponse() {
            return CreateClientResponse.builder()
                .allowedProvider("uaa", "ldap", "my-saml-provider")
                .authority("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("true")
                .clientId("aPq3I1")
                .lastModified(1468364445109L)
                .name("My Client Name")
                .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                .resourceId("none")
                .scope("clients.read", "clients.write")
                .tokenSalt("hRZ21X")
                .build();
        }

        @Override
        protected CreateClientRequest getValidRequest() throws Exception {
            return CreateClientRequest.builder()
                .allowedProvider("uaa", "ldap", "my-saml-provider")
                .authority("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("true")
                .clientId("aPq3I1")
                .clientSecret("secret")
                .name("My Client Name")
                .redirectUriPattern("http://test1.com", "http*://ant.path.wildcard/**/passback/*")
                .scope("clients.read", "clients.write")
                .tokenSalt("hRZ21X")
                .build();
        }

        @Override
        protected Mono<CreateClientResponse> invoke(CreateClientRequest request) {
            return this.clients.create(request);
        }
    }

    public static final class Delete extends AbstractUaaApiTest<DeleteClientRequest, DeleteClientResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/oauth/clients/test-client-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/DELETE_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected DeleteClientResponse getResponse() {
            return DeleteClientResponse.builder()
                .allowedProvider("uaa", "ldap", "my-saml-provider")
                .authority("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("true")
                .clientId("Gieovr")
                .lastModified(1468364443957L)
                .name("My Client Name")
                .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                .resourceId("none")
                .scope("clients.read", "clients.write")
                .tokenSalt("a4mzKu")
                .build();
        }

        @Override
        protected DeleteClientRequest getValidRequest() throws Exception {
            return DeleteClientRequest.builder()
                .clientId("test-client-id")
                .build();
        }

        @Override
        protected Mono<DeleteClientResponse> invoke(DeleteClientRequest request) {
            return this.clients.delete(request);
        }
    }

    public static final class Deserialize extends AbstractUaaApiTest<ListClientsRequest, ListClientsResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/clients")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/GET_response_deserialize.json")
                    .build())
                .build();
        }

        @Override
        protected ListClientsResponse getResponse() {
            return ListClientsResponse.builder()
                .resource(Client.builder()
                    .action("none")
                    .authority("uaa.none")
                    .authorizedGrantType(AUTHORIZATION_CODE, REFRESH_TOKEN)
                    .autoApprove("true")
                    .clientId("ssh-proxy")
                    .lastModified(1469112324000L)
                    .redirectUriPattern("/login")
                    .resourceId("none")
                    .scope("openid", "cloud_controller.read", "cloud_controller.write")
                    .build())
                .resource(Client.builder()
                    .action("none")
                    .authority("routing.routes.write", "routing.routes.read")
                    .authorizedGrantType(CLIENT_CREDENTIALS, REFRESH_TOKEN)
                    .autoApprove("routing.routes.write", "routing.routes.read")
                    .clientId("tcp_emitter")
                    .lastModified(1469112324000L)
                    .resourceId("none")
                    .scope("uaa.none")
                    .build())
                .startIndex(1)
                .itemsPerPage(2)
                .totalResults(2)
                .schema("http://cloudfoundry.org/schema/scim/oauth-clients-1.0")
                .build();
        }

        @Override
        protected ListClientsRequest getValidRequest() throws Exception {
            return ListClientsRequest.builder()
                .build();
        }

        @Override
        protected Mono<ListClientsResponse> invoke(ListClientsRequest request) {
            return this.clients.list(request);
        }
    }

    public static final class Get extends AbstractUaaApiTest<GetClientRequest, GetClientResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/clients/test-client-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetClientResponse getResponse() {
            return GetClientResponse.builder()
                .allowedProvider("uaa", "ldap", "my-saml-provider")
                .authority("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("true")
                .clientId("4Z3t1r")
                .lastModified(1468364445592L)
                .name("My Client Name")
                .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                .resourceId("none")
                .scope("clients.read", "clients.write")
                .tokenSalt("mr80UZ")
                .build();
        }

        @Override
        protected GetClientRequest getValidRequest() throws Exception {
            return GetClientRequest.builder()
                .clientId("test-client-id")
                .build();
        }

        @Override
        protected Mono<GetClientResponse> invoke(GetClientRequest request) {
            return this.clients.get(request);
        }
    }

    public static final class GetMetadata extends AbstractUaaApiTest<GetMetadataRequest, GetMetadataResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/clients/P4vuAaSe/meta")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/GET_{id}_meta_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetMetadataResponse getResponse() {
            return GetMetadataResponse.builder()
                .appIcon("aWNvbiBmb3IgY2xpZW50IDQ=")
                .appLaunchUrl("http://myloginpage.com")
                .clientId("P4vuAaSe")
                .showOnHomePage(true)
                .build();
        }

        @Override
        protected GetMetadataRequest getValidRequest() throws Exception {
            return GetMetadataRequest.builder()
                .clientId("P4vuAaSe")
                .build();
        }

        @Override
        protected Mono<GetMetadataResponse> invoke(GetMetadataRequest request) {
            return this.clients.getMetadata(request);
        }
    }

    public static final class List extends AbstractUaaApiTest<ListClientsRequest, ListClientsResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/clients?count=10&filter=client_id%2Beq%2B%22EGgNW3%22&sortBy=client_id&sortOrder=descending&startIndex=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListClientsResponse getResponse() {
            return ListClientsResponse.builder()
                .resource(Client.builder()
                    .allowedProvider("uaa", "ldap", "my-saml-provider")
                    .authority("clients.read", "clients.write")
                    .authorizedGrantType(CLIENT_CREDENTIALS)
                    .autoApprove("true")
                    .clientId("EGgNW3")
                    .lastModified(1468364445334L)
                    .name("My Client Name")
                    .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                    .resourceId("none")
                    .scope("clients.read", "clients.write")
                    .tokenSalt("7uDPJX")
                    .build())
                .startIndex(1)
                .itemsPerPage(1)
                .totalResults(1)
                .schema("http://cloudfoundry.org/schema/scim/oauth-clients-1.0")
                .build();
        }

        @Override
        protected ListClientsRequest getValidRequest() throws Exception {
            return ListClientsRequest.builder()
                .count(10)
                .filter("client_id+eq+\"EGgNW3\"")
                .sortBy("client_id")
                .sortOrder(SortOrder.DESCENDING)
                .startIndex(1)
                .build();
        }

        @Override
        protected Mono<ListClientsResponse> invoke(ListClientsRequest request) {
            return this.clients.list(request);
        }
    }

    public static final class ListMetadatas extends AbstractUaaApiTest<ListMetadatasRequest, ListMetadatasResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/clients/meta")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/GET_meta_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListMetadatasResponse getResponse() {
            return ListMetadatasResponse.builder()
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
                .build();
        }

        @Override
        protected ListMetadatasRequest getValidRequest() throws Exception {
            return ListMetadatasRequest.builder()
                .build();
        }

        @Override
        protected Mono<ListMetadatasResponse> invoke(ListMetadatasRequest request) {
            return this.clients.listMetadatas(request);
        }
    }

    public static final class Update extends AbstractUaaApiTest<UpdateClientRequest, UpdateClientResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/oauth/clients/55pTMX")
                    .payload("fixtures/uaa/clients/PUT_{id}_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/PUT_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected UpdateClientResponse getResponse() {
            return UpdateClientResponse.builder()
                .allowedProvider("uaa", "ldap", "my-saml-provider")
                .authority("clients.read", "clients.write")
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("clients.autoapprove")
                .clientId("55pTMX")
                .lastModified(1468364443857L)
                .name("My Client Name")
                .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                .resourceId("none")
                .scope("clients.new", "clients.autoapprove")
                .tokenSalt("8mwCEy")
                .build();
        }

        @Override
        protected UpdateClientRequest getValidRequest() throws Exception {
            return UpdateClientRequest.builder()
                .authorizedGrantType(CLIENT_CREDENTIALS)
                .autoApprove("clients.autoapprove")
                .clientId("55pTMX")
                .scope("clients.new", "clients.autoapprove")
                .build();
        }

        @Override
        protected Mono<UpdateClientResponse> invoke(UpdateClientRequest request) {
            return this.clients.update(request);
        }
    }

    public static final class UpdateMetadata extends AbstractUaaApiTest<UpdateMetadataRequest, UpdateMetadataResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/oauth/clients/RpFRZpY3/meta")
                    .payload("fixtures/uaa/clients/PUT_{id}_meta_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/PUT_{id}_meta_response.json")
                    .build())
                .build();
        }

        @Override
        protected UpdateMetadataResponse getResponse() {
            return UpdateMetadataResponse.builder()
                .appLaunchUrl("http://changed.app.launch/url")
                .appIcon("")
                .clientId("RpFRZpY3")
                .showOnHomePage(false)
                .build();
        }

        @Override
        protected UpdateMetadataRequest getValidRequest() throws Exception {
            return UpdateMetadataRequest.builder()
                .appLaunchUrl("http://changed.app.launch/url")
                .clientId("RpFRZpY3")
                .showOnHomePage(false)
                .build();
        }

        @Override
        protected Mono<UpdateMetadataResponse> invoke(UpdateMetadataRequest request) {
            return this.clients.updateMetadata(request);
        }
    }

}
