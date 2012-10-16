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
package org.cloudfoundry.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.cloudfoundry.client.lib.domain.CloudInfo;

/**
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 *
 * @since 1.0.0
 *
 */
public class PushTest extends AbstractMojoTestCase {

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @throws Exception
	 */
	public void testValidateMemoryChoice() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );
		Push mojo = (Push) lookupMojo ( "push", testPom );

		final int[] availableMemoryChoices = new int[]{64, 128, 256, 512};
		final Integer desiredMemory = 128;

		mojo.validateMemoryChoice(availableMemoryChoices, desiredMemory);

		//This should succeed.

	}

	/**
	 * @throws Exception
	 */
	public void testValidateMemoryChoice2() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );
		Push mojo = (Push) lookupMojo ( "push", testPom );

		final int[] availableMemoryChoices = new int[]{64, 128, 256};
		final Integer desiredMemory = 512;

		try {
			mojo.validateMemoryChoice(availableMemoryChoices, desiredMemory);
		} catch (IllegalStateException e) {
			assertEquals("Memory must be one of the following values: 64, 128, 256", e.getMessage());
			return;
		}

		fail();

	}


	/**
	 * Test for a custom framework choice if it is validated correctly.
	 */
	public void testValidateFrameworkChoiceHappyPath() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );
		Push mojo = (Push) lookupMojo ( "push", testPom );

		List<CloudInfo.Framework> frameworks = new ArrayList<CloudInfo.Framework>();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", "custom");
		data.put("runtimes", new ArrayList());

		frameworks.add(new CloudInfo.Framework(data));
		final String desiredFramework = "custom";

		try {
			Assert.assertTrue(mojo.validateFrameworkChoice(frameworks, desiredFramework));
		} catch (IllegalStateException e) {
			fail();
		}
	}

	/**
	 * Test for a custom framework choice with invalid choice.
	 */
	public void testValidateFrameworkChoiceNonExisting() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );
		Push mojo = (Push) lookupMojo ( "push", testPom );

		List<CloudInfo.Framework> frameworks = new ArrayList<CloudInfo.Framework>();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", "custom");
		data.put("runtimes", new ArrayList());
		frameworks.add(new CloudInfo.Framework(data));

		final String desiredFramework = "spring";

		try {
			mojo.validateFrameworkChoice(frameworks, desiredFramework);
			fail();
		} catch (IllegalStateException e) {
			Assert.assertEquals(e.getMessage(), "Framework must be one of the following values: custom");
		}
	}

}
