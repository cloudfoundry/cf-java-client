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

	/**
	 * @throws Exception
	 */
	public void testExistenceOfAppsMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Apps mojo = (Apps) lookupMojo ( "apps", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfAppMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		App mojo = (App) lookupMojo ( "app", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfDeleteMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Delete mojo = (Delete) lookupMojo ( "delete", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfHelpMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Help mojo = (Help) lookupMojo ( "help", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfTargetMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Target mojo = (Target) lookupMojo ( "target", testPom );

		assertNotNull( mojo );
	}


	/**
	 * @throws Exception
	 */
	public void testExistenceOfScaleMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Scale mojo = (Scale) lookupMojo ( "scale", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfPushMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Push mojo = (Push) lookupMojo ( "push", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfRestartMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Restart mojo = (Restart) lookupMojo ( "restart", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfStartMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Start mojo = (Start) lookupMojo ( "start", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfStopMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Stop mojo = (Stop) lookupMojo ( "stop", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfServicesMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Services mojo = (Services) lookupMojo ( "services", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfCreateServicesMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		CreateServices mojo = (CreateServices) lookupMojo ( "create-services", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfDeleteServicesMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		DeleteServices mojo = (DeleteServices) lookupMojo ( "delete-services", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfLogsMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Logs mojo = (Logs) lookupMojo ( "logs", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfBindServicesMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		BindServices mojo = (BindServices) lookupMojo ( "bind-services", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfUnbindServicesMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		UnbindServices mojo = (UnbindServices) lookupMojo ( "unbind-services", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testNonExistingMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);

		try {
			lookupMojo ( "something", testPom );
		} catch (ComponentLookupException e) {
			return;
		}

		fail("A ComponentLookupException should have been thrown.");
	}
}
