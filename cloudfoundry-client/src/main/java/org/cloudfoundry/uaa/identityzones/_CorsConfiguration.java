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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * The payload for the CORS policy configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _CorsConfiguration {

    /**
     * Access-Control-Allow-Credentials header. Indicates whether the response to request can be exposed when the omit credentials flag is unset. When part of the response to a preflight request it
     * indicates that the actual request can include user credentials.
     */
    @JsonProperty("allowedCredentials")
    abstract Boolean getAllowedCredentials();

    /**
     * Access-Control-Allow-Headers header. Indicates which header field names can be used during the actual response
     */
    @JsonProperty("allowedHeaders")
    abstract List<String> getAllowedHeaders();

    /**
     * Access-Control-Allow-Methods header. Indicates which method will be used in the actual request as part of the preflight request.
     */
    @JsonProperty("allowedMethods")
    abstract List<String> getAllowedMethods();

    /**
     * Indicates whether a resource can be shared based by returning the value of the Origin patterns.
     */
    @JsonProperty("allowedOriginPatterns")
    abstract List<String> getAllowedOriginPatterns();

    /**
     * Access-Control-Allow-Origin header. Indicates whether a resource can be shared based by returning the value of the Origin request header, “*”, or “null” in the response.
     */
    @JsonProperty("allowedOrigins")
    abstract List<String> getAllowedOrigins();

    /**
     * The list of allowed URI patterns.
     */
    @JsonProperty("allowedUriPatterns")
    abstract List<String> getAllowedUriPatterns();

    /**
     * The list of allowed URIs.
     */
    @JsonProperty("allowedUris")
    abstract List<String> getAllowedUris();

    /**
     * Access-Control-Max-Age header. Indicates how long the results of a preflight request can be cached in a preflight result cache
     */
    @JsonProperty("maxAge")
    abstract Long getMaxAge();

}
