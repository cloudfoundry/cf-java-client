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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 * @author Gunnar Hillert
 * @since 1.0.0
 */
public class AbstractCloudFoundryMojoTest extends AbstractMojoTestCase {

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @throws Exception
	 */
	public void testGetTarget() throws Exception {

		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo("push", testPom);

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject(mojo, "artifactId", "cf-maven-tests");

		doReturn("http://api.run.pivotal.io").when(mojo).getCommandlineProperty(SystemProperties.TARGET);

		assertEquals("http://api.run.pivotal.io", mojo.getTarget().toString());

	}

	/**
	 * @throws Exception
	 */
	public void testGetTargetWithMalformedUrl() throws Exception {

		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo("push", testPom);

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject(mojo, "artifactId", "cf-maven-tests");

		doReturn("api.run.pivotal.io").when(mojo).getCommandlineProperty(SystemProperties.TARGET);

		try {
			mojo.getTarget();
		}
		catch (IllegalStateException e) {

			String expectedMessage = "The Url parameter 'api.run.pivotal.io' "
					+ "which was passed in as system property is not valid.";

			assertEquals(expectedMessage, e.getMessage());
			return;
		}

		fail("Was a expecting an exception being thrown.");

	}

	/**
	 * @throws Exception
	 */
	public void testGetTargetWithMalformedUrl2() throws Exception {

		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo("push", testPom);

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject(mojo, "artifactId", "cf-maven-tests");

		doReturn("/some/path").when(mojo).getCommandlineProperty(SystemProperties.TARGET);

		try {
			mojo.getTarget();
		}
		catch (IllegalStateException e) {

			String expectedMessage = "The Url parameter '/some/path' "
					+ "which was passed in as system property is not valid.";

			assertEquals(expectedMessage, e.getMessage());
			return;
		}

		fail("Was a expecting an exception being thrown.");

	}

	/**
	 * @throws Exception
	 */
	public void testPrecedenceWhenExecutingGetTarget() throws Exception {

		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo("push", testPom);

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject(mojo, "artifactId", "cf-maven-tests");
		setVariableValueToObject(mojo, "target", "http://blog.hillert.com/");
		doReturn("http://api.run.pivotal.io").when(mojo).getCommandlineProperty(SystemProperties.TARGET);

		assertEquals("http://api.run.pivotal.io", mojo.getTarget().toString());

	}

	/**
	 * @throws Exception
	 */
	public void testPrecedenceWhenExecutingGetTarget2() throws Exception {

		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo("push", testPom);

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject(mojo, "artifactId", "cf-maven-tests");
		setVariableValueToObject(mojo, "target", "http://blog.hillert.com/");
		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.TARGET);

		assertEquals("http://blog.hillert.com/", mojo.getTarget().toString());

	}

	/**
	 * @throws Exception
	 */
	public void testParameterValidationWhenConnctingtoCF() throws Exception {

		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo("push", testPom);

		Push mojo = spy(unspiedMojo);

		setVariableValueToObject(mojo, "target", "https://api.run.pivotal.io");
		setVariableValueToObject(mojo, "org", "my-org");
		setVariableValueToObject(mojo, "space", "development");

		doReturn(null).when(mojo).getCommandlineProperty(any(SystemProperties.class));
		doThrow(new IOException()).when(mojo).retrieveToken();

		// TODO May need to think about handling parameter validation more intelligently

		String expectedErrorMessage = null;
		try {
			Assert.configurationNotNull(null, "username", SystemProperties.USERNAME);
		}
		catch (MojoExecutionException e) {
			expectedErrorMessage = e.getMessage();
		}

		try {
			mojo.execute();
		}
		catch (MojoExecutionException e) {
			assertEquals(expectedErrorMessage, e.getMessage());
			return;
		}

		fail();

	}

	/**
	 * @throws Exception
	 */
	public void testParameterValidationWhenConnctingtoCF2() throws Exception {

		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo("push", testPom);

		Push mojo = spy(unspiedMojo);

		setVariableValueToObject(mojo, "target", "https://api.run.pivotal.io");
		setVariableValueToObject(mojo, "org", "my-org");
		setVariableValueToObject(mojo, "space", "development");
		setVariableValueToObject(mojo, "username", "tester@test.com");

		doReturn(null).when(mojo).getCommandlineProperty(any(SystemProperties.class));
		doThrow(new IOException()).when(mojo).retrieveToken();

		// TODO May need to think about handling parameter validation more intelligently

		String expectedErrorMessage = null;
		try {
			Assert.configurationNotNull(null, "password", SystemProperties.PASSWORD);
		}
		catch (MojoExecutionException e) {
			expectedErrorMessage = e.getMessage();
		}

		try {
			mojo.execute();
		}
		catch (MojoExecutionException e) {
			assertEquals(expectedErrorMessage, e.getMessage());
			return;
		}

		fail();

	}

	/**
	 * @throws Exception
	 */
	public void testParameterValidationWhenConnctingtoCF3() throws Exception {

		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo("push", testPom);

		Push mojo = spy(unspiedMojo);

		setVariableValueToObject(mojo, "username", "tester@test.com");
		setVariableValueToObject(mojo, "password", "secret");
		setVariableValueToObject(mojo, "org", "my-org");
		setVariableValueToObject(mojo, "space", "development");

		doReturn(null).when(mojo).getCommandlineProperty(any(SystemProperties.class));

		// TODO May need to think about handling parameter validation more intelligently

		String expectedErrorMessage = null;
		try {
			Assert.configurationNotNull(null, "target", SystemProperties.TARGET);
		}
		catch (MojoExecutionException e) {
			expectedErrorMessage = e.getMessage();
		}

		try {
			mojo.execute();
		}
		catch (MojoExecutionException e) {
			assertEquals(expectedErrorMessage, e.getMessage());
			return;
		}

		fail();
	}
}
