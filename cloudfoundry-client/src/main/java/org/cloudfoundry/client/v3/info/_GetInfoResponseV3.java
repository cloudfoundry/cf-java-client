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

package org.cloudfoundry.client.v3.info;

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
 * The response payload for the Info operation
 */
@JsonDeserialize(using = org.cloudfoundry.client.v3.info._GetInfoResponseV3.InfoV3Deserializer.class)
@Value.Immutable()
public interface _GetInfoResponseV3 {

    /**
     * The build number
     */
    @Value.Parameter
    @Nullable
    public abstract String getBuildNumber();

    /**
     * The description
     */
    @Value.Parameter
    @Nullable
    public abstract String getDescription();

    /**
     * The minimum CLI version
     */
    @Value.Parameter
    @Nullable
    public abstract String getMinCliVersion();

    /**
     * The minimum recommended CLI version
     */
    @Value.Parameter
    @Nullable
    public abstract String getMinRecommendedCliVersion();

    /**
     * The name
     */
    @Value.Parameter
    @Nullable
    public abstract String getName();

    /**
     * The support url
     */
    @Value.Parameter
    @Nullable
    public abstract String getSupport();

    /**
     * The self url
     */
    @Value.Parameter
    @Nullable
    public abstract String getSelf();

    /**
     * The version
     */
    @Value.Parameter
    @Nullable
    public abstract Integer getVersion();

    public class InfoV3Deserializer extends StdDeserializer<_GetInfoResponseV3>{
		private static final long serialVersionUID = 1L;
		
		protected InfoV3Deserializer() {
			super(GetInfoResponseV3.class);
		}
		@Override
		public _GetInfoResponseV3 deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JacksonException {
			JsonNode productNode = jp.getCodec().readTree(jp);
			String buildNumber = productNode.get("build").textValue();
			String description = productNode.get("description").textValue();
			String minCliVersion = productNode.get("cli_version").get("minimum").textValue();
			String minRecommendedCliVersion = productNode.get("cli_version").get("recommended").textValue();
			String name = productNode.get("name").textValue();
			String support = productNode.get("links").get("support").get("href").textValue();
			String self = productNode.get("links").get("self").get("href").textValue();
			Integer version = productNode.get("version").asInt();
			return GetInfoResponseV3.of(buildNumber,description,minCliVersion, minRecommendedCliVersion,name,support,self,version);
		}    	
    }
}
