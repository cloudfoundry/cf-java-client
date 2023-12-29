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

import io.netty.handler.codec.http.HttpHeaders;
import java.util.Optional;
import org.cloudfoundry.uaa.IdentityZoned;

final class IdentityZoneBuilder {

    private IdentityZoneBuilder() {}

    static void augment(HttpHeaders httpHeaders, Object request) {
        if (request instanceof IdentityZoned) {
            IdentityZoned identityZoned = (IdentityZoned) request;
            Optional.ofNullable(identityZoned.getIdentityZoneId())
                    .ifPresent(
                            identityZoneId ->
                                    httpHeaders.set("X-Identity-Zone-Id", identityZoneId));
            Optional.ofNullable(identityZoned.getIdentityZoneSubdomain())
                    .ifPresent(
                            identityZoneSubdomain ->
                                    httpHeaders.set(
                                            "X-Identity-Zone-Subdomain", identityZoneSubdomain));
        }
    }
}
