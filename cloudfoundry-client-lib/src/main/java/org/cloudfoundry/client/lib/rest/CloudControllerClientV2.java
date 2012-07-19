package org.cloudfoundry.client.lib.rest;

import com.sun.tools.corba.se.idl.InvalidArgument;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.util.JsonEntityResourceMapper;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Assert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Risberg
 */
public class CloudControllerClientV2 extends AbstractCloudControllerClient {

	OauthClient oauthCLient;

	CloudSpace sessionSpace;

	JsonEntityResourceMapper resourceMapper = new JsonEntityResourceMapper();

	public CloudControllerClientV2(URL cloudControllerUrl) {
		this(null, null, cloudControllerUrl);
	}

	public CloudControllerClientV2(String email, String password, URL cloudControllerUrl) {
		this(email, password, cloudControllerUrl, null);
	}

	public CloudControllerClientV2(String email, String password, URL cloudControllerUrl, ClientHttpRequestFactory requestFactory) {
		super(email, password, cloudControllerUrl, requestFactory);
		this.oauthCLient = new OauthClient(authorizationUrl);
	}

	@Override
	public void setAuthorizationUrl(String authorizationUrl) {
		super.setAuthorizationUrl(authorizationUrl);
		this.oauthCLient = new OauthClient(authorizationUrl);
	}

	public String login() {
		OAuth2AccessToken token = oauthCLient.getToken(email, password);
		this.token = token.getTokenType() + " " + token.getValue();
		return this.token;
	}

	public void logout() {
		token = null;
		proxyUser = null;
	}

	public void register(String email, String password) {
		throw new NotImplementedException();
	}

	public void updatePassword(String newPassword) {
		throw new NotImplementedException();
	}

	public void unregister() {
		throw new NotImplementedException();
	}

	public boolean supportsSpaces() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CloudSpace> getSpaces() {
		String resp = getRestTemplate().getForObject(getUrl("v2/spaces?inline-relations-depth=0"), String.class);
		Map<String, Object> respMap = convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		List<CloudSpace> spaces = new ArrayList<CloudSpace>();
		for (Map<String, Object> resource : resourceList) {
			spaces.add(resourceMapper.mapJsonResource(resource, CloudSpace.class));
		}
		return spaces;
	}

	@Override
	public void setSessionSpace(CloudSpace space) {
		sessionSpace = space;
	}

	@SuppressWarnings("unchecked")
	public List<CloudService> getServices() {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "v2";
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlPath = urlPath + "/service_instances?inline-relations-depth={depth}";
		urlVars.put("depth", 2);
		String resp = getRestTemplate().getForObject(getUrl(urlPath), String.class, urlVars);
		Map<String, Object> respMap = convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		List<CloudService> services = new ArrayList<CloudService>();
		for (Map<String, Object> resource : resourceList) {
			services.add(resourceMapper.mapJsonResource(resource, CloudService.class));
		}
		return services;
	}

	public void createService(CloudService service) {
		throw new NotImplementedException();
	}

	@SuppressWarnings("unchecked")
	public CloudService getService(String serviceName) {
		Assert.hasLength(serviceName, "The service name must be specified");
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "v2";
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlPath = urlPath + "/service_instances?inline-relations-depth={depth}&q={q}";
		urlVars.put("depth", 2);
		urlVars.put("q", "name:" + serviceName);
		String resp = getRestTemplate().getForObject(getUrl(urlPath), String.class, urlVars);
		Map<String, Object> respMap = convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		CloudService service = null;
		if (resourceList.size() > 0) {
			if (resourceList.size() == 1) {
				service = resourceMapper.mapJsonResource(resourceList.get(0), CloudService.class);
			}
			else {
				throw new IllegalArgumentException("Found too many service instances (" +
						resourceList.size() + ") with name " + serviceName);
			}
		}
		return service;
	}

	public void deleteService(String service) {
		throw new NotImplementedException();
	}

	public void deleteAllServices() {
		throw new NotImplementedException();
	}

	public List<ServiceConfiguration> getServiceConfigurations() {
		throw new NotImplementedException();
	}

	public List<CloudApplication> getApplications() {
		throw new NotImplementedException();
	}

	public CloudApplication getApplication(String appName) {
		throw new NotImplementedException();
	}

	public ApplicationStats getApplicationStats(String appName) {
		throw new NotImplementedException();
	}

	public int[] getApplicationMemoryChoices() {
		throw new NotImplementedException();
	}

	public int getDefaultApplicationMemory(String framework) {
		throw new NotImplementedException();
	}

	public void createApplication(String appName, Staging staging, int memory, List<String> uris, List<String> serviceNames, boolean checkExists) {
		throw new NotImplementedException();
	}

	public void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException {
		throw new NotImplementedException();
	}

	public void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback) throws IOException {
		throw new NotImplementedException();
	}

	public void startApplication(String appName) {
		throw new NotImplementedException();
	}

	public void debugApplication(String appName, CloudApplication.DebugMode mode) {
		throw new NotImplementedException();
	}

	public void stopApplication(String appName) {
		throw new NotImplementedException();
	}

	public void restartApplication(String appName) {
		throw new NotImplementedException();
	}

	public void deleteApplication(String appName) {
		throw new NotImplementedException();
	}

	public void deleteAllApplications() {
		throw new NotImplementedException();
	}

	public void updateApplicationMemory(String appName, int memory) {
		throw new NotImplementedException();
	}

	public void updateApplicationInstances(String appName, int instances) {
		throw new NotImplementedException();
	}

	public void updateApplicationServices(String appName, List<String> services) {
		throw new NotImplementedException();
	}

	public void updateApplicationUris(String appName, List<String> uris) {
		throw new NotImplementedException();
	}

	public void updateApplicationEnv(String appName, Map<String, String> env) {
		throw new NotImplementedException();
	}

	public void updateApplicationEnv(String appName, List<String> env) {
		throw new NotImplementedException();
	}

	public String getFile(String appName, int instanceIndex, String filePath) {
		throw new NotImplementedException();
	}

	public void bindService(String appName, String serviceName) {
		throw new NotImplementedException();
	}

	public void unbindService(String appName, String serviceName) {
		throw new NotImplementedException();
	}

	public InstancesInfo getApplicationInstances(String appName) {
		throw new NotImplementedException();
	}

	public CrashesInfo getCrashes(String appName) {
		throw new NotImplementedException();
	}

	public void rename(String appName, String newName) {
		throw new NotImplementedException();
	}


	@Override
	protected Map<String, Object> getInfoMap(URL cloudControllerUrl) {
		String resp = getRestTemplate().getForObject(cloudControllerUrl + "/info", String.class);
		return convertJsonToMap(resp);
	}

	private Map<String, Object> convertJsonToMap(String resp) {
		Map<String, Object> respMap = new HashMap<String, Object>();
		try {
			respMap = mapper.readValue(resp, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			//TODO: log error ?
		}
		return respMap;
	}

}
