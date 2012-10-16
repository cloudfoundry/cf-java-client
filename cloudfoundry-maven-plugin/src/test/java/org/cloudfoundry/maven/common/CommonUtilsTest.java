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
package org.cloudfoundry.maven.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;

import org.junit.Test;

/**
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 *
 */
public class CommonUtilsTest {

	@Test
	public void testValidEmailAddress() {
		Assert.assertTrue(CommonUtils.isValidEmail("test@cloudfoundry.com"));
	}

	@Test
	public void testInValidEmailAddress() {
		Assert.assertFalse(CommonUtils.isValidEmail("test123"));
	}

	@Test
	public void testFrameworksToCommaDelimitedString() {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", "custom");
		data.put("runtimes", new ArrayList<Runtime>());

		List<CloudInfo.Framework> frameworks = new ArrayList<CloudInfo.Framework>();
		frameworks.add(new CloudInfo.Framework(data));

		Assert.assertEquals(CommonUtils.frameworksToCommaDelimitedString(frameworks), "custom");

		frameworks.add(new CloudInfo.Framework(data));
		Assert.assertEquals(CommonUtils.frameworksToCommaDelimitedString(frameworks), "custom, custom");
	}

	@Test
	public void testSserviceConfigurationsToCommaDelimitedString() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("vendor", "mysql");

		List<ServiceConfiguration> list = new ArrayList<ServiceConfiguration>();
		list.add(new ServiceConfiguration(data));

		Assert.assertEquals(CommonUtils.serviceConfigurationsToCommaDelimitedString(list), "mysql");

		list.add(new ServiceConfiguration(data));
		Assert.assertEquals(CommonUtils.serviceConfigurationsToCommaDelimitedString(list), "mysql, mysql");
	}
}
