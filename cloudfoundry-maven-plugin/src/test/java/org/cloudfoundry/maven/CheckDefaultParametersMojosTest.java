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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 * Test the existence of several default parameter values.
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 */
public class CheckDefaultParametersMojosTest extends AbstractMojoTestCase {

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @throws Exception
	 */
	public void testDefaultParametersOfPushMojoGoal() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "artifactId", "cf-maven-tests" );
		setVariableValueToObject( mojo, "artifact", "someGAV");

		CloudFoundryClient client = mock(CloudFoundryClient.class);
		doReturn(new CloudDomain(null, "apps.cloudfoundry.com", null)).when(client).getDefaultDomain();
		doReturn(client).when(mojo).getClient();

		doReturn(null).when(mojo).getCommandlineProperty(any(SystemProperties.class));

		assertEquals("cf-maven-tests", mojo.getAppname());
		assertNull(mojo.getAppStartupTimeout());
		assertNull(mojo.getCommand());
		assertNull(mojo.getBuildpack());
		assertNull(mojo.getDiskQuota());
		assertNull(mojo.getEnv());
		assertNull(mojo.getHealthCheckTimeout());
		assertNull(mojo.getInstances());
		assertNull(mojo.getMemory());
		assertFalse(mojo.isMergeEnv());
		assertFalse(mojo.isNoStart());
		assertNull(mojo.getPassword());
//		assertNull(mojo.getPath()); // cannot be null
		assertNull(mojo.getServices());
		assertNull(mojo.getUrls());
		assertNull(mojo.getUsername());
		
		assertNull("Password by default is null.", mojo.getPassword());
		assertEquals("cloud-foundry-credentials", mojo.getServer());
		assertNull("Target Url is not backed by a default value.", mojo.getTarget());
		assertNull("Username by default is null.", mojo.getUsername());
	}
}
