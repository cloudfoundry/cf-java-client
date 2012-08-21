package org.cloudfoundry.client.lib;

/**
 * @author: trisberg
 */
public class AbstractCloudFoundryClientTest {

	protected String computeAppUrl(String ccUrl, String appName) {
		int ix1 =  2 + ccUrl.indexOf("//");
		int ix2 = ccUrl.indexOf('.');
		return ccUrl.substring(0, ix1) + appName + ccUrl.substring(ix2);
	}

	protected String computeAppUrlNoProtocol(String ccUrl, String appName) {
		//TODO: remove ccng at some point?
		return ccUrl.replace("api", appName).replace("ccng", appName).replace("http://", "").replace("https://", "");
	}

	protected String namespacedAppName(String namespace, String basename) {
		return namespace + "-" + basename;
	}

	protected static String defaultNamespace(String email) {
		return email.substring(0, email.indexOf('@')).replaceAll("\\.", "_").replaceAll("\\+", "_");
	}
}
