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

package org.cloudfoundry.client;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The response payload for the get root operation. See <a href="https://v3-apidocs.cloudfoundry.org/index.html#root">V3 API Root</a> 
 */
@JsonDeserialize(using = org.cloudfoundry.client._GetRootResponse.RootDeserializer.class)
@Value.Immutable()
public interface _GetRootResponse {

    /**
     * The root of the cloud controller Api version 3
     */
    @Value.Parameter
    @Nullable
    public abstract String getApiV3();
    
    /**
     * The version of the cloud controller Api version 3
     */
    @Value.Parameter
    @Nullable
    public abstract String getApiVersionV3();
    
    /**
     * The root of the cloud controller Api version 2
     */
    @Value.Parameter
    @Nullable
    public abstract String getApiV2();

    /**
     * The version of the cloud controller Api version 2 (if available)
     */
    @Value.Parameter
    @Nullable
    public abstract String getApiVersion();

    /**
     * The network policy v0 endpoint
     */
    @Value.Parameter
    @Nullable
    public abstract String getNetworkPolicyV0Endpoint();

    /**
     * The network policy v1 endpoint
     */
    @Value.Parameter
    @Nullable
    public abstract String getNetworkPolicyV1Endpoint();

    /**
     * The login endpoint
     */
    @Value.Parameter
    @Nullable
    public abstract String getLoginEndpoint();

    /**
     * The uaa endpoint
     */
    @Value.Parameter
    @Nullable
    public abstract String getUaaEndpoint();

    /**
     * The credhub endpoint
     */
    @Value.Parameter
    @Nullable
    public abstract String getCredhubEndpoint();

    /**
     * The routing endpoint
     */
    @Value.Parameter
    @Nullable
    public abstract String getRoutingEndpoint();

    /**
     * The loggin encpoint
     */
    @Value.Parameter
    @Nullable
    public abstract String getLoggingEndpoint();

    /**
     * The log cache url
     */
    @Value.Parameter
    @Nullable
    public abstract String getLogCacheEndpoint();

    /**
     * The log stream url
     */
    @Value.Parameter
    @Nullable
    public abstract String getLogStreamEndpoint();

    /**
     * The ssh endpoint for apps.
     */
    @Value.Parameter
    @Nullable
    public abstract String getAppSshEndpoint();

    /**
     * The ssh host key fingerprint for apps.
     */
    @Value.Parameter
    @Nullable
    public abstract String getAppSshHostKeyFingerprint();

    /**
     * The ssh oauth client for apps.
     */
    @Value.Parameter
    @Nullable
    public abstract String getAppSshOauthClient();

    /**
     * The self url
     */
    @Value.Parameter
    @Nullable
    public abstract String getSelf();

    public class RootDeserializer extends StdDeserializer<_GetRootResponse>{
		private static final long serialVersionUID = 1L;
		
		protected RootDeserializer() {
			super(GetRootResponse.class);
		}
		@Override
		public _GetRootResponse deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JacksonException {
			JsonNode productNode = jp.getCodec().readTree(jp);
			String apiV3Endpoint = getEndpoint("cloud_controller_v3",productNode);

			JsonNode tmp = productNode.get("links").get("cloud_controller_v3");
			String apiVersionV3 = null;
			if(tmp!=null) {
				apiVersionV3 = tmp.get("meta").get("version").textValue();
			}

			String apiV2Endpoint = getEndpoint("cloud_controller_v2",productNode);
			tmp = productNode.get("links").get("cloud_controller_v2");
			String apiVersion = null;
			if(tmp!=null) {
				apiVersion = tmp.get("meta").get("version").textValue();
			}
			String networkPolicyV0Endpoint = getEndpoint("network_policy_v0",productNode);
			String networkPolicyV1Endpoint = getEndpoint("network_policy_v1",productNode);
			String loginEndpoint = getEndpoint("login",productNode);
			String uaaEndpoint = getEndpoint("uaa",productNode);
			String credhubEndpoint = getEndpoint("credhub",productNode);
			String routingEndpoint = getEndpoint("routing",productNode);
			String loggingEndpoint = getEndpoint("logging",productNode);
			String logCacheEndpoint = getEndpoint("log_cache",productNode);
			String logStreamEndpoint = getEndpoint("log_stream",productNode);
			String appSshEndpoint = getEndpoint("app_ssh",productNode);
			tmp = productNode.get("links").get("app_ssh");
			String appSshHostKeyFingerprint = null;
			if(tmp!=null) {
				appSshHostKeyFingerprint = tmp.get("meta").get("host_key_fingerprint").textValue();
			}
			tmp = productNode.get("links").get("app_ssh");
			String appSshOauthClient = null;
			if(tmp!=null) {
				appSshOauthClient = tmp.get("meta").get("oauth_client").textValue();
			}
			
			String self = getEndpoint("self",productNode);
			return GetRootResponse.of(apiV3Endpoint,apiVersionV3,apiV2Endpoint,
					apiVersion,networkPolicyV0Endpoint, networkPolicyV1Endpoint, loginEndpoint,uaaEndpoint,credhubEndpoint,
					routingEndpoint,loggingEndpoint, logCacheEndpoint, logStreamEndpoint,appSshEndpoint, appSshHostKeyFingerprint, appSshOauthClient,self);
		}
		
		// null safe access to href-endpoints
		private String getEndpoint(String name,JsonNode productNode) {
			String result = null;
			JsonNode tmp = productNode.get("links").get(name);
			if(tmp!=null&& !tmp.isNull()) {
				result = tmp.get("href").textValue();
			}
			return result;
		}
    }
}
