package org.cloudfoundry.client.lib;

import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

	protected void doGetFile(CloudFoundryClient client, String appName) throws Exception {
		String fileName = "tomcat/webapps/ROOT/WEB-INF/web.xml";
		String emptyPropertiesfileName = "tomcat/webapps/ROOT/WEB-INF/classes/empty.properties";

		// Test downloading full file
		String fileContent = client.getFile(appName, 0, fileName);
		assertNotNull(fileContent);
		assertTrue(fileContent.length() > 5);

		// Test downloading range of file with start and end position
		int end = fileContent.length() - 3;
		int start = end/2;
		String fileContent2 = client.getFile(appName, 0, fileName, start, end);
		assertEquals(fileContent.substring(start, end), fileContent2);

		// Test downloading range of file with just start position
		String fileContent3 = client.getFile(appName, 0, fileName, start);
		assertEquals(fileContent.substring(start), fileContent3);

		// Test downloading range of file with start position and end position exceeding the length
		int positionPastEndPosition = fileContent.length() + 999;
		String fileContent4 = client.getFile(appName, 0, fileName, start, positionPastEndPosition);
		assertEquals(fileContent.substring(start), fileContent4);

		// Test downloading end portion of file with length
		int length = fileContent.length() / 2;
		String fileContent5 = client.getFileTail(appName, 0, fileName, length);
		assertEquals(fileContent.substring(fileContent.length() - length), fileContent5);

		// Test downloading one byte of file with start and end position
		String fileContent6 = client.getFile(appName, 0, fileName, start, start + 1);
		assertEquals(fileContent.substring(start, start + 1), fileContent6);
		assertEquals(1, fileContent6.length());

		// Test downloading range of file with invalid start position
		int invalidStartPosition = fileContent.length() + 999;
		try {
			client.getFile(appName, 0, fileName, invalidStartPosition);
			fail("should have thrown exception");
		} catch (CloudFoundryException e) {
			assertTrue(e.getStatusCode().equals(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE));
		}

		// Test downloading empty file
		String fileContent7 = client.getFile(appName, 0, emptyPropertiesfileName);
		assertNotNull(fileContent7);
		assertTrue(fileContent7.length() == 0);

		// Test downloading with invalid parameters - should all throw exceptions
		try {
			client.getFile(appName, 0, fileName, -2);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("start position"));
		}
		try {
			client.getFile(appName, 0, fileName, 10, -2);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("end position"));
		}
		try {
			client.getFile(appName, 0, fileName, 29, 28);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("end position"));
		}
		try {
			client.getFile(appName, 0, fileName, 29, 28);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("29"));
		}
		try {
			client.getFileTail(appName, 0, fileName, 0);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("length"));
		}
	}

	protected InstancesInfo getInstancesWithTimeout(CloudFoundryClient client, String appName) throws InterruptedException {
		long start = System.currentTimeMillis();
		while (true) {
			Thread.sleep(2000);
			try {
				return client.getApplicationInstances(appName);
			}
			catch (HttpServerErrorException e) {
				// error 500, keep waiting
			}
			if (System.currentTimeMillis() - start > 30000) {
				fail("Timed out waiting for startup");
				break; // for the compiler
			}
		}

		return null; // for the compiler
	}
}
