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

package org.cloudfoundry.reactor.uaa;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.impl.Base64Codec;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import org.cloudfoundry.uaa.tokens.KeyType;
import org.cloudfoundry.uaa.tokens.ListTokenKeysRequest;
import org.cloudfoundry.uaa.tokens.ListTokenKeysResponse;
import org.cloudfoundry.uaa.tokens.TokenKey;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class UaaSigningKeyResolverTest {

    private static final Base64Codec BASE64 = new Base64Codec();

    private final Tokens tokens = mock(Tokens.class);

    private final UaaSigningKeyResolver signingKeyResolver = new UaaSigningKeyResolver(this.tokens);

    @Test
    public void resolveExistingKey() throws NoSuchAlgorithmException {
        PublicKey publicKey = getKeyPair().getPublic();

        when(
            this.tokens.listKeys(ListTokenKeysRequest.builder()
                .build())
        ).thenReturn(
            Mono.just(ListTokenKeysResponse.builder()
                .key(TokenKey.builder()
                    .algorithm(publicKey.getAlgorithm())
                    .e("")
                    .id("test-key-id")
                    .keyType(KeyType.RSA)
                    .n("")
                    .use("")
                    .value(getEncoded(publicKey))
                    .build())
                .build())
        );

        JwsHeader<?> header = new DefaultJwsHeader().setKeyId("test-key-id");
        Claims claims = new DefaultClaims();

        this.signingKeyResolver.resolveSigningKey(header, claims);
        assertThat(this.signingKeyResolver.resolveSigningKey(header, claims)).isNotNull();
    }

    @Test
    public void resolveRefreshedKey() throws NoSuchAlgorithmException {
        PublicKey publicKey = getKeyPair().getPublic();

        when(
            this.tokens.listKeys(ListTokenKeysRequest.builder()
                .build())
        ).thenReturn(
            Mono.just(ListTokenKeysResponse.builder()
                .key(TokenKey.builder()
                    .algorithm(publicKey.getAlgorithm())
                    .e("")
                    .id("test-key-id")
                    .keyType(KeyType.RSA)
                    .n("")
                    .use("")
                    .value(getEncoded(publicKey))
                    .build())
                .build())
        );

        JwsHeader<?> header = new DefaultJwsHeader().setKeyId("test-key-id");
        Claims claims = new DefaultClaims();

        assertThat(this.signingKeyResolver.resolveSigningKey(header, claims)).isNotNull();
    }

    @Test(expected = IllegalStateException.class)
    public void resolveUnknownKey() {
        when(
            this.tokens.listKeys(ListTokenKeysRequest.builder()
                .build())
        ).thenReturn(
            Mono.just(ListTokenKeysResponse.builder()
                .build())
        );

        JwsHeader<?> header = new DefaultJwsHeader().setKeyId("test-key-id");
        Claims claims = new DefaultClaims();

        this.signingKeyResolver.resolveSigningKey(header, claims);
    }

    private static String getEncoded(PublicKey publicKey) {
        return String.format("-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----", BASE64.encode(publicKey.getEncoded()));
    }

    private KeyPair getKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.generateKeyPair();
    }

}
