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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.impl.security.AbstractJwk;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

final class UsernameProviderTest {

    private final ConnectionContext connectionContext = mock(ConnectionContext.class);

    private final SigningKeyResolver signingKeyResolver = mock(SigningKeyResolver.class);

    private final TokenProvider tokenProvider = mock(TokenProvider.class);

    private final UsernameProvider usernameProvider =
            new UsernameProvider(
                    this.connectionContext, this.signingKeyResolver, this.tokenProvider);

    @SuppressWarnings("unchecked")
    @Test
    void getInvalidToken() throws NoSuchAlgorithmException {
        KeyPair keyPair = getKeyPair();
        when(this.signingKeyResolver.resolveSigningKey(any(JwsHeader.class), any(Claims.class)))
                .thenReturn(keyPair.getPublic());

        String invalidToken =
                String.format(
                        "bearer %s",
                        getToken(keyPair.getPrivate(), Instant.now().minus(Duration.ofHours(1))));
        String validToken =
                String.format(
                        "bearer %s",
                        getToken(keyPair.getPrivate(), Instant.now().plus(Duration.ofHours(1))));
        when(this.tokenProvider.getToken(this.connectionContext))
                .thenReturn(Mono.just(invalidToken), Mono.just(validToken));

        this.usernameProvider
                .get()
                .as(StepVerifier::create)
                .expectNext("test-username")
                .expectComplete()
                .verify(Duration.ofSeconds(1));

        verify(this.tokenProvider).invalidate(this.connectionContext);
    }

    @Test
    void getValidToken() throws NoSuchAlgorithmException {
        KeyPair keyPair = getKeyPair();
        when(this.signingKeyResolver.resolveSigningKey(any(JwsHeader.class), any(Claims.class)))
                .thenReturn(keyPair.getPublic());

        String token =
                String.format(
                        "bearer %s",
                        getToken(keyPair.getPrivate(), Instant.now().plus(Duration.ofHours(1))));
        when(this.tokenProvider.getToken(this.connectionContext)).thenReturn(Mono.just(token));

        this.usernameProvider
                .get()
                .as(StepVerifier::create)
                .expectNext("test-username")
                .expectComplete()
                .verify(Duration.ofSeconds(1));
    }

    @SuppressWarnings("unchecked")
    private static String getToken(PrivateKey privateKey, Instant expiration) {
        HashMap<String, String> params = new HashMap<>();
        params.put(AbstractJwk.KID.getId(), "test-key-id");
        DefaultJwsHeader header = new DefaultJwsHeader(params);
        return Jwts.builder()
                .setHeader(header)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .claim("user_name", "test-username")
                .setExpiration(Date.from(expiration))
                .compact();
    }

    private KeyPair getKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
