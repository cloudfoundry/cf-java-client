/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.uaa.authorizations;

import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.ResponseType;
import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantBrowserRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantHybridRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByImplicitGrantBrowserRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithAuthorizationCodeGrantRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithIdTokenRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithImplicitGrantRequest;
import org.cloudfoundry.util.ExceptionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;

import java.util.Optional;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;

/**
 * The Reactor-based implementation of {@link Authorizations}
 */
public final class ReactorAuthorizations extends AbstractUaaOperations implements Authorizations {

    private static final AsciiString LOCATION = new AsciiString("Location");

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorAuthorizations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<String> authorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.CODE))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .flatMap(location -> {
                String candidate = UriComponentsBuilder.fromUriString(location).build().getQueryParams().getFirst("code");

                return Optional.ofNullable(candidate)
                    .map(Mono::just)
                    .orElse(ExceptionUtils.illegalState(String.format("Parameter %s not in URI %s", "code", location)));
            })
            .checkpoint();
    }

    @Override
    public Mono<String> authorizationCodeGrantBrowser(AuthorizeByAuthorizationCodeGrantBrowserRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.CODE),
            outbound -> outbound
                .map(ReactorAuthorizations::removeAuthorization))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .checkpoint();
    }

    @Override
    public Mono<String> authorizationCodeGrantHybrid(AuthorizeByAuthorizationCodeGrantHybridRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.CODE_AND_ID_TOKEN),
            outbound -> outbound
                .map(ReactorAuthorizations::removeAuthorization))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .checkpoint();
    }

    @Override
    public Mono<String> implicitGrantBrowser(AuthorizeByImplicitGrantBrowserRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.TOKEN),
            outbound -> outbound
                .map(ReactorAuthorizations::removeAuthorization))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .checkpoint();
    }

    @Override
    public Mono<String> openIdWithAuthorizationCodeAndIdToken(AuthorizeByOpenIdWithAuthorizationCodeGrantRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.CODE_AND_ID_TOKEN),
            outbound -> outbound
                .map(ReactorAuthorizations::removeAuthorization))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .checkpoint();
    }

    @Override
    public Mono<String> openIdWithIdToken(AuthorizeByOpenIdWithIdTokenRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.ID_TOKEN))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .checkpoint();
    }

    @Override
    public Mono<String> openIdWithTokenAndIdToken(AuthorizeByOpenIdWithImplicitGrantRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.TOKEN_AND_ID_TOKEN))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .checkpoint();
    }

    private static HttpClientRequest removeAuthorization(HttpClientRequest request) {
        request.requestHeaders().remove(AUTHORIZATION);
        return request;
    }

}
