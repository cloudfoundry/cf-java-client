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

package org.cloudfoundry.reactor.uaa.authorizations;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.uaa.ResponseType;
import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantBrowserRequest;
import org.cloudfoundry.util.ExceptionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import java.util.Optional;

/**
 * The Reactor-based implementation of {@link Authorizations}
 */
public final class ReactorAuthorizations extends AbstractUaaOperations implements Authorizations {

    private static final AsciiString LOCATION = new AsciiString("Location");

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorAuthorizations(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<String> authorizeByAuthorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.CODE))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .then(location -> extractParameterFromLocation(location, "code"));
    }

    @Override
    public Mono<String> authorizeByAuthorizationCodeGrantBrowser(AuthorizeByAuthorizationCodeGrantBrowserRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.CODE))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .then(location -> extractParameterFromLocation(location, "code"));
    }

    private static Mono<String> extractParameterFromLocation(String location, String parameter) {
        return Optional.ofNullable(UriComponentsBuilder.fromUriString(location).build().getQueryParams().getFirst(parameter))
            .map(parameterValue -> Mono.just(parameterValue))
            .orElse(ExceptionUtils.illegalState(String.format("Parameter %s not found in location", parameter)));
    }
}
