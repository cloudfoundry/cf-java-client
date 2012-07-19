package org.cloudfoundry.client.lib;

import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.cloud.JsonEntityInfoMapper;
import org.cloudfoundry.client.lib.cloud.ServiceInfo;
import org.cloudfoundry.client.lib.cloud.SpaceInfo;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
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
public class CloudControllerV2 extends AbstractCloudController {

	OauthClient oauthCLient;

	SpaceInfo sessionSpace;

	public CloudControllerV2(URL cloudControllerUrl) {
		this(null, null, cloudControllerUrl);
	}

	public CloudControllerV2(String email, String password, URL cloudControllerUrl) {
		this(email, password, cloudControllerUrl, null);
	}

	public CloudControllerV2(String email, String password, URL cloudControllerUrl, ClientHttpRequestFactory requestFactory) {
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
	public List<CloudSpace> getSpaces() {
		@SuppressWarnings("unchecked")
		String resp = getRestTemplate().getForObject(getUrl("v2/app_spaces?inline-relations-depth=0"), String.class);
		Map<String, Object> respMap = new HashMap<String, Object>();
		try {
			respMap = mapper.readValue(resp, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> entityList = (List<Map<String, Object>>) respMap.get("resources");
		List<SpaceInfo> spaceInfos = JsonEntityInfoMapper.toSpaceInfoList(entityList);
		List<CloudSpace> spaces = new ArrayList<CloudSpace>();
		for (SpaceInfo si : spaceInfos) {
			spaces.add(new CloudSpace(si.getGuid(), si.getName()));
		}
		return spaces;
	}

	@Override
	public void setSpace(SpaceInfo space) {
		sessionSpace = space;
	}

	public List<CloudService> getServices() {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "v2/service_instances?inline-relations-depth={d}";
		if (sessionSpace != null) {
			urlVars.put("q", "app_space_guid:" + sessionSpace.getGuid());
			urlPath = urlPath + "&q={q}";
		}
		urlVars.put("d", 1);
		@SuppressWarnings("unchecked")
		String resp = getRestTemplate().getForObject(getUrl(urlPath), String.class, urlVars);
//		System.out.println("++RESP: " + resp);
		Map<String, Object> respMap = new HashMap<String, Object>();
		try {
			respMap = mapper.readValue(resp, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> entityList = (List<Map<String, Object>>) respMap.get("resources");
		List<ServiceInfo> serviceInfos = JsonEntityInfoMapper.toServiceInfoList(entityList);
		List<CloudService> services = new ArrayList<CloudService>();
		for (ServiceInfo svcInfo : serviceInfos) {
			CloudService cs = new CloudService();
			cs.setName(svcInfo.getName());
			cs.setType(svcInfo.getType());
			cs.setVendor(svcInfo.getVendor());
			cs.setVersion(svcInfo.getVersion());
			cs.setTier(svcInfo.getServicePlanInfo().getName());
			services.add(cs);
		}
		return services;
	}

	public void createService(CloudService service) {
		throw new NotImplementedException();
	}

	public CloudService getService(String service) {
		throw new NotImplementedException();
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
		Map<String, Object> infoMap = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		String s = getRestTemplate().getForObject(cloudControllerUrl + "/info", String.class);
		try {
			infoMap = mapper.readValue(s, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infoMap;
	}

}
