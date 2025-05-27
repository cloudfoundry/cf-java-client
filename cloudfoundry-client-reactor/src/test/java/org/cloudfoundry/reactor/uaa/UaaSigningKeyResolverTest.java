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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.impl.security.AbstractJwk;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import org.cloudfoundry.uaa.tokens.KeyType;
import org.cloudfoundry.uaa.tokens.ListTokenKeysRequest;
import org.cloudfoundry.uaa.tokens.ListTokenKeysResponse;
import org.cloudfoundry.uaa.tokens.TokenKey;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

final class UaaSigningKeyResolverTest {

    private final Tokens tokens = mock(Tokens.class);

    private final UaaSigningKeyResolver signingKeyResolver = new UaaSigningKeyResolver(this.tokens);

    @Test
    void resolveExistingKey() throws NoSuchAlgorithmException {
        PublicKey publicKey = getKeyPair().getPublic();

        when(this.tokens.listKeys(ListTokenKeysRequest.builder().build()))
                .thenReturn(
                        Mono.just(
                                ListTokenKeysResponse.builder()
                                        .key(
                                                TokenKey.builder()
                                                        .algorithm(publicKey.getAlgorithm())
                                                        .e("")
                                                        .id("test-key-id")
                                                        .keyType(KeyType.RSA)
                                                        .n("")
                                                        .use("")
                                                        .value(getEncoded(publicKey))
                                                        .build())
                                        .build()));

        HashMap<String, String> params = new HashMap<>();
        params.put(AbstractJwk.KID.getId(), "test-key-id");
        DefaultJwsHeader header = new DefaultJwsHeader(params);
        Claims claims = new DefaultClaims(new HashMap<>());

        this.signingKeyResolver.resolveSigningKey(header, claims);
        assertThat(this.signingKeyResolver.resolveSigningKey(header, claims)).isNotNull();
    }

    @Test
    void resolveRefreshedKey() throws NoSuchAlgorithmException {
        PublicKey publicKey = getKeyPair().getPublic();

        when(this.tokens.listKeys(ListTokenKeysRequest.builder().build()))
                .thenReturn(
                        Mono.just(
                                ListTokenKeysResponse.builder()
                                        .key(
                                                TokenKey.builder()
                                                        .algorithm(publicKey.getAlgorithm())
                                                        .e("")
                                                        .id("test-key-id")
                                                        .keyType(KeyType.RSA)
                                                        .n("")
                                                        .use("")
                                                        .value(getEncoded(publicKey))
                                                        .build())
                                        .build()));

        HashMap<String, String> params = new HashMap<>();
        params.put(AbstractJwk.KID.getId(), "test-key-id");
        DefaultJwsHeader header = new DefaultJwsHeader(params);
        Claims claims = new DefaultClaims(new HashMap<>());

        assertThat(this.signingKeyResolver.resolveSigningKey(header, claims)).isNotNull();
    }

    @Test
    void resolveUnknownKey() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    when(this.tokens.listKeys(ListTokenKeysRequest.builder().build()))
                            .thenReturn(Mono.just(ListTokenKeysResponse.builder().build()));

                    HashMap<String, String> params = new HashMap<>();
                    params.put(AbstractJwk.KID.getId(), "test-key-id");
                    DefaultJwsHeader header = new DefaultJwsHeader(params);
                    Claims claims = new DefaultClaims(new HashMap<>());

                    this.signingKeyResolver.resolveSigningKey(header, claims);
                });
    }

    private static String getEncoded(PublicKey publicKey) {
        return String.format(
                "-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----",
                Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    }

    private KeyPair getKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.generateKeyPair();
    }
}
