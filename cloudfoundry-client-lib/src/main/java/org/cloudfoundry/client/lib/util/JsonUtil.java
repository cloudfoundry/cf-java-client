/*
 * Copyright 2009-2012 the original author or authors.
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

package org.cloudfoundry.client.lib.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.domain.CloudResource;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some JSON helper utilities used by the Cloud Foundry Java client.
 *
 * @author Thomas Risberg
 *
 */
public class JsonUtil {

	protected static final Log logger = LogFactory.getLog(JsonUtil.class);

	private final static ObjectMapper mapper = new ObjectMapper();

	public static final MediaType JSON_MEDIA_TYPE = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("UTF-8"));

	public static Map<String, Object> convertJsonToMap(String json) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (json != null) {
			try {
				retMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
			} catch (IOException e) {
				logger.warn("Error while reading Java Map from JSON response: " + json, e);
			}
		}
		return retMap;
	}

	public static List<String> convertJsonToList(String json) {
		List<String> retList = new ArrayList<String>();
		if (json != null) {
			try {
				retList = mapper.readValue(json, new TypeReference<List<String>>() {});
			} catch (IOException e) {
				logger.warn("Error while reading Java List from JSON response: " + json, e);
			}
		}
		return retList;
	}

	public static List<CloudResource> convertJsonToCloudResourceList(String json) {
		List<CloudResource> retList = new ArrayList<CloudResource>();
		if (json != null) {
			try {
				retList = mapper.readValue(json, new TypeReference<List<CloudResource>>() {});
			} catch (IOException e) {
				logger.warn("Error while reading Java List from JSON response: " + json, e);
			}
		}
		return retList;
	}

	public static String convertToJson(Object value) {
		if (mapper.canSerialize(value.getClass())) {
			try {
				return mapper.writeValueAsString(value);
			} catch (IOException e) {
				logger.warn("Error while serializing " + value + " to JSON", e);
				return null;
			}
		}
		else {
			throw new IllegalArgumentException("Value of type " + value.getClass().getName() +
					" can not be serialized to JSON.");
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> convertToJsonList(InputStream jsonInputStream){
		try {
			return mapper.readValue(jsonInputStream, List.class);
		} catch (JsonParseException e) {
			logger.error("Unable to parse JSON from InputStream", e);
			throw new IllegalArgumentException("Unable to parse JSON from InputStream", e);
		} catch (JsonMappingException e) {
			logger.error("Unable to parse JSON from InputStream", e);
			throw new IllegalArgumentException("Unable to parse JSON from InputStream", e);
		} catch (IOException e) {
			logger.error("Unable to process InputStream", e);
			throw new IllegalArgumentException("Unable to parse JSON from InputStream", e);
		}
	}
}
