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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 */
public class AbstractApplicationAwareCloudFoundryMojoTest extends AbstractMojoTestCase {

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @throws Exception
	 */
	public void testGetUrl() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "artifactId", "cf-maven-tests" );

		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.URL);
		doReturn("http://api.cloudfoundry.com").when(mojo).getCommandlineProperty(SystemProperties.TARGET);
		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.APP_NAME);
		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.FRAMEWORK);

		assertEquals("cf-maven-tests.cloudfoundry.com", mojo.getUrl());

	}

	public void testGetUrl2() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "artifactId", "cf-maven-tests" );

		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.URL);
		doReturn("http://api.cloudfoundry.com").when(mojo).getCommandlineProperty(SystemProperties.TARGET);
		doReturn("myapp").when(mojo).getCommandlineProperty(SystemProperties.APP_NAME);
		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.FRAMEWORK);

		assertEquals("myapp.cloudfoundry.com", mojo.getUrl());

	}

	public void testGetUrl3() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "artifactId", "cf-maven-tests" );

		doReturn("custom.expliciturl.com").when(mojo).getCommandlineProperty(SystemProperties.URL);
		doReturn("http://api.cloudfoundry.com").when(mojo).getCommandlineProperty(SystemProperties.TARGET);
		doReturn("myapp").when(mojo).getCommandlineProperty(SystemProperties.APP_NAME);

		assertEquals("custom.expliciturl.com", mojo.getUrl());

	}

	public void testGetUrlWithNullTarget() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "artifactId", "cf-maven-tests" );

		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.URL);
		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.TARGET);
		doReturn("myapp").when(mojo).getCommandlineProperty(SystemProperties.APP_NAME);
		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.FRAMEWORK);

		assertEquals("myapp.<undefined target>", mojo.getUrl());

	}

	public void testGetUrlWithBadTarget() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "artifactId", "cf-maven-tests" );

		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.URL);
		doReturn("http://badtarget").when(mojo).getCommandlineProperty(SystemProperties.TARGET);
		doReturn("myapp").when(mojo).getCommandlineProperty(SystemProperties.APP_NAME);
		doReturn("standalone").when(mojo).getCommandlineProperty(SystemProperties.FRAMEWORK);

		assertNull(mojo.getUrl());

	}

//	public void testGetServices() throws Exception {
//
//		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );
//
//		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );
//
//		Push mojo = spy(unspiedMojo);
//
//		/**
//		 * Injecting some test values as expressions are not evaluated.
//		 */
//		setVariableValueToObject( mojo, "artifactId", "cf-maven-tests" );
//
//		doReturn("serviceA, mongo, mysql, rabbitmq").when(mojo).getCommandlineProperty(SystemProperties.SERVICES);
//
//		assertTrue("Expecting 4 Services", mojo.getServices().size() == 4);
//
//	}
//
//	public void testGetServices2() throws Exception {
//
//		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );
//
//		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );
//
//		Push mojo = spy(unspiedMojo);
//
//		/**
//		 * Injecting some test values as expressions are not evaluated.
//		 */
//		setVariableValueToObject( mojo, "services", "service1, super service2  " );
//		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.SERVICES);
//
//		assertTrue("Expecting 2 Services but got " + mojo.getServices().size(), mojo.getServices().size() == 2);
//
//		assertEquals("service1", mojo.getServices().get(0));
//		assertEquals("super service2", mojo.getServices().get(1));
//
//	}

	public void testGetNoStart() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "noStart", Boolean.TRUE );
		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.NO_START);

		assertEquals(Boolean.TRUE, mojo.isNoStart());

	}

	public void testGetNoStartPrecedence() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "noStart", Boolean.FALSE );
		doReturn("false").when(mojo).getCommandlineProperty(SystemProperties.NO_START);

		assertEquals(Boolean.FALSE, mojo.isNoStart());

	}

	public void testGetFramework() throws Exception {
		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "framework", "custom");
		doReturn("custom").when(mojo).getCommandlineProperty(SystemProperties.FRAMEWORK);

		assertEquals("custom", mojo.getFramework());

	}

	public void testGetEnv() throws Exception {
				File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		Map<String,String> env = new HashMap<String, String>();
		env.put("JAVA_OPTS", "-XX:MaxPermSize=256m");

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "env", env);

		assertEquals("-XX:MaxPermSize=256m", mojo.getEnv().get("JAVA_OPTS"));
	}

}
