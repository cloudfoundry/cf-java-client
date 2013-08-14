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
import java.util.List;

import junit.framework.Assert;

import org.cloudfoundry.client.lib.domain.CloudServiceOffering;

import org.junit.Test;

/**
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 * @author Scott Frederick
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
	public void testServiceConfigurationsToCommaDelimitedString() {
		List<CloudServiceOffering> list = new ArrayList<CloudServiceOffering>();
		list.add(new CloudServiceOffering(null, "mysql", "vendor", "version"));

		Assert.assertEquals(CommonUtils.serviceOfferingsToCommaDelimitedString(list), "mysql");

		list.add(new CloudServiceOffering(null, "mysql", "vendor", "version"));
		Assert.assertEquals(CommonUtils.serviceOfferingsToCommaDelimitedString(list), "mysql, mysql");
	}
}
