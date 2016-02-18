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

package org.cloudfoundry.spring.util.network;

import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.cloudfoundry.util.Optional;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

@Accessors(chain = true, fluent = true)
@Setter
@ToString
public final class AccessTokenUriBuilder {

    private String host;

    private Integer port;

    private RestOperations restOperations;

    private SslCertificateTruster sslCertificateTruster;

    @SuppressWarnings("unchecked")
    public URI build() {
        Map<String, String> info = this.restOperations
            .getForObject(getInfoUri(this.host, this.port, this.sslCertificateTruster), Map.class);

        return getAccessTokenUri(info.get("token_endpoint"), this.sslCertificateTruster);
    }

    private static URI getAccessTokenUri(String tokenEndpoint, SslCertificateTruster sslCertificateTruster) {
        URI uri = UriComponentsBuilder.fromUriString(tokenEndpoint)
            .pathSegment("oauth", "token")
            .build().toUri();

        sslCertificateTruster.trust(uri.getHost(), uri.getPort(), 5, SECONDS);
        return uri;
    }

    private static URI getInfoUri(String host, Integer port, SslCertificateTruster sslCertificateTruster) {
        URI uri = UriComponentsBuilder.newInstance()
            .scheme("https").host(host).port(Optional.ofNullable(port).orElse(443)).pathSegment("v2", "info")
            .build().toUri();

        sslCertificateTruster.trust(uri.getHost(), uri.getPort(), 5, SECONDS);
        return uri;
    }

}
