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

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.client.CloudFoundryClient;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * A type encapsulating networking types that are shared between clients
 */
@Data
@Builder(toBuilder = true)
public final class ConnectionContext {

    private final OAuth2ClientContext clientContext;

    private final CloudFoundryClient cloudFoundryClient;

    private final HostnameVerifier hostnameVerifier;

    private final OAuth2ProtectedResourceDetails protectedResourceDetails;

    private final SslCertificateTruster sslCertificateTruster;

    private final SSLContext sslContext;

}
