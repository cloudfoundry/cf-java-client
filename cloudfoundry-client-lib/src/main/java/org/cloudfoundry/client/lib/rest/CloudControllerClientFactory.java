package org.cloudfoundry.client.lib.rest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class CloudControllerClientFactory {

	private static RestTemplate restTemplate = new RestTemplate();

	private static final ObjectMapper mapper = new ObjectMapper();

	public static CloudControllerClient newCloudController(URL cloudControllerUrl) {
		CloudControllerClient cc = null;
		boolean v2 = false;
		//TODO fix this
		Map<String,Object> infoMap = null;
		infoMap = getInfoMap(cloudControllerUrl);
		Object v = infoMap.get("version");
		if (v != null && v instanceof String && Double.valueOf((String) v) <= 1.0) {
			v2 = false;
		}
		else {
			v2 = true;
		}
		if (v2) {
			cc = new CloudControllerClientV2(cloudControllerUrl);
			if (infoMap.get("authorization_endpoint") != null) {
				((CloudControllerClientV2)cc).setAuthorizationUrl((String) infoMap.get("authorization_endpoint"));
			}
		}
		else {
			cc = new CloudControllerClientV1(cloudControllerUrl);
		}
		return cc;
	}

	private static Map<String, Object> getInfoMap(URL cloudControllerUrl) {
		Map<String, Object> infoMap = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		String s = restTemplate.getForObject(cloudControllerUrl + "/info", String.class);
		try {
			infoMap = mapper.readValue(s, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infoMap;
	}
}
