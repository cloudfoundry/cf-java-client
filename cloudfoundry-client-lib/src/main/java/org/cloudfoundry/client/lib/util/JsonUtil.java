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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
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

}
