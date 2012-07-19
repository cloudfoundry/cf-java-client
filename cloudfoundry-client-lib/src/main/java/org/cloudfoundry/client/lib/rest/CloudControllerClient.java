package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.transfer.SpaceInfo;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.Staging;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 */
public interface CloudControllerClient {

	// User and Info methods

	URL getCloudControllerUrl();

	CloudInfo getInfo();

	void setCredentials(String email, String password);

	void setToken(String token);

	void setProxyUser(String proxyUser);

	String login();

	void logout();

	void register(String email, String password);

	void updatePassword(String newPassword);

	void unregister();

	boolean supportsSpaces();

	List<CloudSpace> getSpaces();

	void setSpace(SpaceInfo space);

	// Service methods

	List<CloudService> getServices();

	void createService(CloudService service);

	CloudService getService(String service);

	void deleteService(String service);

	void deleteAllServices();

	List<ServiceConfiguration> getServiceConfigurations();

	// App methods

	List<CloudApplication> getApplications();

	CloudApplication getApplication(String appName);

	ApplicationStats getApplicationStats(String appName);

	int[] getApplicationMemoryChoices();

	int getDefaultApplicationMemory(String framework);

	void createApplication(String appName, Staging staging, int memory, List<String> uris,
									  List<String> serviceNames, boolean checkExists);

	void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException;

	void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback) throws IOException;

	void startApplication(String appName);

	void debugApplication(String appName, CloudApplication.DebugMode mode);

	void stopApplication(String appName);

	void restartApplication(String appName);

	void deleteApplication(String appName);

	void deleteAllApplications();

	void updateApplicationMemory(String appName, int memory);

	void updateApplicationInstances(String appName, int instances);

	void updateApplicationServices(String appName, List<String> services);

	void updateApplicationUris(String appName, List<String> uris);

	void updateApplicationEnv(String appName, Map<String, String> env);

	void updateApplicationEnv(String appName, List<String> env);

	String getFile(String appName, int instanceIndex, String filePath);

	void bindService(String appName, String serviceName);

	void unbindService(String appName, String serviceName);

	InstancesInfo getApplicationInstances(String appName);

	CrashesInfo getCrashes(String appName);

	void rename(String appName, String newName);


}
