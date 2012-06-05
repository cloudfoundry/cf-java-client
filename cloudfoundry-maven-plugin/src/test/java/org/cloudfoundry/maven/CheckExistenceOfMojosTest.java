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
	public void testExistenceOfInfoMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Info mojo = (Info) lookupMojo ( "info", testPom );

		assertNotNull( mojo );
	}


	/**
	 * @throws Exception
	 */
	public void testExistenceOfInstancesMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Instances mojo = (Instances) lookupMojo ( "instances", testPom );

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
	public void testExistenceOfUpdateMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Update mojo = (Update) lookupMojo ( "update", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfRegisterMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		Register mojo = (Register) lookupMojo ( "register", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfAddUserMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		AddUser mojo = (AddUser) lookupMojo ( "add-user", testPom );

		assertNotNull( mojo );
	}

	/**
	 * @throws Exception
	 */
	public void testExistenceOfDeleteUserMojo() throws Exception {

		File testPom = new File( getBasedir(), testPomXmlPath);
		DeleteUser mojo = (DeleteUser) lookupMojo ( "delete-user", testPom );

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
