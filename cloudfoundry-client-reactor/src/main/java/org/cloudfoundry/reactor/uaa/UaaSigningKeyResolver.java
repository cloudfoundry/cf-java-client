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
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.cloudfoundry.uaa.tokens.ListTokenKeysRequest;
import org.cloudfoundry.uaa.tokens.ListTokenKeysResponse;
import org.cloudfoundry.uaa.tokens.TokenKey;
import org.cloudfoundry.uaa.tokens.Tokens;
import reactor.core.Exceptions;

final class UaaSigningKeyResolver implements SigningKeyResolver {

    private static final String BEGIN = "-----BEGIN PUBLIC KEY-----";

    private static final String END = "-----END PUBLIC KEY-----";

    private final Object monitor = new Object();

    private final Map<String, Key> signingKeys = new HashMap<>();

    private final Tokens tokens;

    UaaSigningKeyResolver(Tokens tokens) {
        this.tokens = tokens;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        return getKey(header.getKeyId());
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Key resolveSigningKey(JwsHeader jwsHeader, byte[] bytes) {
        return getKey(jwsHeader.getKeyId());
    }

    private static byte[] decode(TokenKey tokenKey) {
        return Base64.getMimeDecoder()
                .decode(tokenKey.getValue().replace(BEGIN, "").replace(END, "").trim());
    }

    private static Key generateKey(TokenKey tokenKey) {
        try {
            return KeyFactory.getInstance(tokenKey.getKeyType().toString())
                    .generatePublic(new X509EncodedKeySpec(decode(tokenKey)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw Exceptions.propagate(e);
        }
    }

    private Key getKey(String keyId) {
        synchronized (this.monitor) {
            Key key = this.signingKeys.get(keyId);
            if (key != null) {
                return key;
            }

            refreshKeys();

            key = this.signingKeys.get(keyId);
            if (key != null) {
                return key;
            }

            throw new IllegalStateException(
                    String.format("Unable to retrieve signing key %s", keyId));
        }
    }

    private void refreshKeys() {
        this.signingKeys.clear();
        this.signingKeys.putAll(
                this.tokens
                        .listKeys(ListTokenKeysRequest.builder().build())
                        .flatMapIterable(ListTokenKeysResponse::getKeys)
                        .collectMap(TokenKey::getId, UaaSigningKeyResolver::generateKey)
                        .block(Duration.ofMinutes(5)));
    }
}
