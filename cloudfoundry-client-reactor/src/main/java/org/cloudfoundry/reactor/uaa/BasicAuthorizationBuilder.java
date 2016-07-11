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

package org.cloudfoundry.reactor.uaa;

import io.netty.util.AsciiString;
import org.cloudfoundry.uaa.BasicAuthorized;
import reactor.io.netty.http.HttpClientRequest;

import java.util.Base64;

public final class BasicAuthorizationBuilder {

    private static final AsciiString AUTHORIZATION = new AsciiString("Authorization");

    private static final AsciiString BASIC_PREAMBLE = new AsciiString("Basic ");

    private BasicAuthorizationBuilder(){}

    public static void augment(HttpClientRequest outbound, Object request) {
        if(request instanceof BasicAuthorized) {
            BasicAuthorized basicAuthorized = (BasicAuthorized)request;
            String encoded = Base64.getEncoder().encodeToString(new AsciiString(basicAuthorized.getClientId()).concat(":").concat(basicAuthorized.getClientSecret()).toByteArray());
            outbound.headers().set(AUTHORIZATION, BASIC_PREAMBLE + encoded);
        }
    }

}
