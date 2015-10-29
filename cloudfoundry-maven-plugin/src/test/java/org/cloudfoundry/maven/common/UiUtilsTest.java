/*
 * Copyright 2009-2013 the original author or authors.
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
package org.cloudfoundry.maven.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;
import junit.framework.Assert;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.AppState;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.FileCopyUtils;

/**
 * @author Gunnar Hillert
 */
public class UiUtilsTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(UiUtilsTest.class);

	@Test
	public void testRenderAppNullUsageTextTable() {
		@SuppressWarnings("unchecked")
		final List<String> services = Collections.<String>singletonList("mysql");
		@SuppressWarnings("unchecked")
		final List<String> uris = Collections.<String>singletonList("cf-rocks.api.run.pivotal.io");

		final CloudApplication app1 = new CloudApplication("first", "command",
				"buildpack", 1024, 2, uris, services, AppState.STARTED);

		Map<String, Object> instStatsNullUsage = new HashMap<>();
		instStatsNullUsage.put("cores", "2");
		instStatsNullUsage.put("name", "test-name");
		instStatsNullUsage.put("usage", null);

		Map<String, Object> instAttrs = new HashMap<>();
		instAttrs.put("state", "RUNNING");
		instAttrs.put("stats", instStatsNullUsage);

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id1", instAttrs);

		ApplicationStats applicationStats = new ApplicationStats(attributes);

		final String renderedAppTableAsString = UiUtils.renderCloudApplicationDataAsTable(app1, applicationStats);

		Assert.assertEquals(getExpectedStringFromResource("testRenderAppStatsNullUsageTextTable-expected-output.txt"),
				renderedAppTableAsString);
	}

	@Test
	public void testRenderAppUsageTextTable() {
		@SuppressWarnings("unchecked")
		final List<String> services = Collections.<String>singletonList("mysql");
		@SuppressWarnings("unchecked")
		final List<String> uris = Collections.<String>singletonList("cf-rocks.api.run.pivotal.io");

		final CloudApplication app1 = new CloudApplication("first", "command",
				"buildpack", 1024, 2, uris, services, AppState.STARTED);

		Map<String, Object> usageAttrs = new HashMap<>();
		usageAttrs.put("time", "1984-01-01 11:11:11 UTC");
		usageAttrs.put("cpu", "1e-3");
		usageAttrs.put("disk", "512");
		usageAttrs.put("mem", "513");

		Map<String, Object> instUsage = new HashMap<>();
		instUsage.put("cores", "3");
		instUsage.put("name", "test-name-2");
		instUsage.put("usage", usageAttrs);
		instUsage.put("disk_quota", "1025");
		instUsage.put("port", "2020");
		instUsage.put("mem_quota", "613");
		instUsage.put("uris", "uri1.com, uri2.org");
		instUsage.put("fds_quota", "99");
		instUsage.put("host", "test-host");
		instUsage.put("uptime", "1e-2");

		Map<String, Object> instAttrs = new HashMap<>();
		instAttrs.put("state", "RUNNING");
		instAttrs.put("stats", instUsage);

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id1", instAttrs);

		ApplicationStats applicationStats = new ApplicationStats(attributes);

		final String renderedAppTableAsString = UiUtils.renderCloudApplicationDataAsTable(app1, applicationStats);

		Assert.assertEquals(getExpectedStringFromResource("testRenderAppStatsTextTable-expected-output.txt"),
				renderedAppTableAsString);
	}

	private static String getExpectedStringFromResource(String resourceFile) {
		String expectedString = null;
		try {
			InputStream resourceAsStream = UiUtilsTest.class.getResourceAsStream(resourceFile);
			Assert.assertNotNull("Expected string file " + resourceFile + " not found", resourceAsStream);
			expectedString = FileCopyUtils.copyToString(new InputStreamReader(resourceAsStream));
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertNotNull("Expected string file " + resourceFile + " not readable", expectedString);
		return expectedString;
	}

	@Test
	public void testRenderTextTable() {
		final List<String> services = new ArrayList<>();

		services.add("mysql");
		services.add("MyMongoInstance");

		final List<String> uris = new ArrayList<>();

		uris.add("cf-rocks.api.run.pivotal.io");
		uris.add("spring-rocks.api.run.pivotal.io");

		final CloudApplication app1 = new CloudApplication("first", "command",
				"buildpack", 512, 1, uris, services, AppState.STARTED);
		final CloudApplication app2 = new CloudApplication("second", "command",
				"buildpack", 1024, 2, uris, services, AppState.STOPPED);

		final List<CloudApplication> applications = Arrays.asList(app1, app2);

		final String renderedTableAsString = UiUtils.renderCloudApplicationsDataAsTable(applications);

		LOGGER.info("\n" + renderedTableAsString);

		Assert.assertEquals(getExpectedStringFromResource("testRenderTextTable-expected-output.txt"),
				renderedTableAsString);
	}
}
