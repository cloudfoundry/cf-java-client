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

package org.cloudfoundry.reactor.tokenprovider;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.netty.http.client.HttpClientForm;
import reactor.netty.http.client.HttpClientRequest;

/**
 * The OAuth Password Grant implementation of {@link TokenProvider}
 */
@Value.Immutable
abstract class _PasswordGrantTokenProvider extends AbstractUaaTokenProvider {

    /**
     * The login hint
     */
    @Nullable
    abstract String getLoginHint();

    /**
     * The password
     */
    abstract String getPassword();

    /**
     * The username
     */
    abstract String getUsername();

    @Override
    void tokenRequestTransformer(HttpClientRequest request, HttpClientForm form) {
        form.multipart(false)
            .attr("client_id", getClientId())
            .attr("client_secret", getClientSecret())
            .attr("grant_type", "password")
            .attr("password", getPassword())
            .attr("username", getUsername())
            .attr("login_hint", getLoginHint());
    }

	@Override
	String getIdentityZoneSubdomain() {
		return "user_name";
	}
}
