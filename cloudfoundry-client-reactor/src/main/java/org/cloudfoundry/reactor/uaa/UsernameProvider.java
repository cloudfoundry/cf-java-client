/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.reactor.uaa;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.uaa.tokens.Tokens;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.util.Optional;

final class UsernameProvider {

    private final ConnectionContext connectionContext;

    private final SigningKeyResolver signingKeyResolver;

    private final TokenProvider tokenProvider;

    UsernameProvider(ConnectionContext connectionContext, TokenProvider tokenProvider, Tokens tokens) {
        this(connectionContext, new UaaSigningKeyResolver(tokens), tokenProvider);
    }

    UsernameProvider(ConnectionContext connectionContext, SigningKeyResolver signingKeyResolver, TokenProvider tokenProvider) {
        this.connectionContext = connectionContext;
        this.tokenProvider = tokenProvider;
        this.signingKeyResolver = signingKeyResolver;
    }

    Mono<String> get() {
        return getToken(this.connectionContext, this.tokenProvider)
            .publishOn(Schedulers.boundedElastic())
            .map(this::getUsername)
            .retryWhen(Retry.max(1).filter(ExpiredJwtException.class::isInstance)
                .doAfterRetry(r -> this.tokenProvider.invalidate(this.connectionContext)));
    }

    private static Mono<String> getToken(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return Mono.defer(() -> tokenProvider
            .getToken(connectionContext))
            .map(s -> s.split(" ")[1]);
    }

    private String getUsername(String token) {
        JwtParser parser = Jwts.parserBuilder()
            .setSigningKeyResolver(this.signingKeyResolver)
            .build();

        Jws<Claims> jws = parser.parseClaimsJws(token);

        return Optional
            .ofNullable(jws.getBody().get(this.tokenProvider.getUserIdentityProperty(), String.class))
            .orElseThrow(() -> new IllegalStateException("Unable to retrieve username from token"));
    }

}
