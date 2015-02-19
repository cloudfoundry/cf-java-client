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
package org.cloudfoundry.maven;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class CheckExistenceOfMojosTest extends AbstractMojoTestCase {

	private static final String testPomXmlPath = "src/test/resources/test-pom.xml";

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testExistenceOfAppsMojo() throws Exception {
		assertMojoExists("apps", Apps.class);
	}

	public void testExistenceOfAppMojo() throws Exception {
		assertMojoExists("app", App.class);
	}

	public void testExistenceOfDeleteMojo() throws Exception {
		assertMojoExists("delete", Delete.class);
	}

	public void testExistenceOfEnvMojo() throws Exception {
		assertMojoExists("env", Env.class);
	}

	public void testExistenceOfHelpMojo() throws Exception {
		assertMojoExists("help", Help.class);
	}

	public void testExistenceOfTargetMojo() throws Exception {
		assertMojoExists("target", Target.class);
	}

	public void testExistenceOfScaleMojo() throws Exception {
		assertMojoExists("scale", Scale.class);
	}

	public void testExistenceOfPushMojo() throws Exception {
		assertMojoExists("push", Push.class);
	}

	public void testExistenceOfRestartMojo() throws Exception {
		assertMojoExists("restart", Restart.class);
	}

	public void testExistenceOfStartMojo() throws Exception {
		assertMojoExists("start", Start.class);
	}

	public void testExistenceOfStopMojo() throws Exception {
		assertMojoExists("stop", Stop.class);
	}

	public void testExistenceOfServicesMojo() throws Exception {
		assertMojoExists("services", Services.class);
	}

	public void testExistenceOfServicePlanssMojo() throws Exception {
		assertMojoExists("service-plans", ServicePlans.class);
	}

	public void testExistenceOfCreateServicesMojo() throws Exception {
		assertMojoExists("create-services", CreateServices.class);
	}

	public void testExistenceOfDeleteServicesMojo() throws Exception {
		assertMojoExists("delete-services", DeleteServices.class);
	}

	public void testExistenceOfLogsMojo() throws Exception {
		assertMojoExists("logs", Logs.class);
	}

	public void testExistenceOfBindServicesMojo() throws Exception {
		assertMojoExists("bind-services", BindServices.class);
	}

	public void testExistenceOfUnbindServicesMojo() throws Exception {
		assertMojoExists("unbind-services", UnbindServices.class);
	}

	public void testNonExistingMojo() throws Exception {
		File testPom = new File(getBasedir(), testPomXmlPath);

		try {
			lookupMojo("something", testPom);
			fail("A ComponentLookupException should have been thrown.");
		} catch (ComponentLookupException e) {
		}
	}

	private void assertMojoExists(String mojoName, Class<?> mojoType) throws Exception {
		File testPom = new File(getBasedir(), testPomXmlPath);
		Mojo mojo = lookupMojo(mojoName, testPom);

		assertTrue(mojoType.isInstance(mojo));
		assertNotNull(mojo);
	}
}
