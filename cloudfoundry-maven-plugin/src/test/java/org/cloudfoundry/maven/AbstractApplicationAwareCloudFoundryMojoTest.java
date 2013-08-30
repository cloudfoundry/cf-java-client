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

import static org.mockito.Mockito.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.StubArtifactRepository;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 *
 * @author Gunnar Hillert
 * @author Scott Frederick
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

		assertNull(mojo.getUrl());

	}

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
	
	public void testRepositoryArtifactResolution() throws Exception {
		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "artifactId", "cf-maven-tests" );

		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.PATH);
		doReturn("http://api.cloudfoundry.com").when(mojo).getCommandlineProperty(SystemProperties.TARGET);
		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.APP_NAME);
		
		setVariableValueToObject(mojo, "artifact", "groupId:artifactId:version:type");
		
		final File file = File.createTempFile("cf-maven-plugin", "testrepo");
		file.delete();
		file.mkdir();
		file.deleteOnExit();

		final File artifactFile = File.createTempFile(
				"test", "artifact", file);
		artifactFile.deleteOnExit();

		setVariableValueToObject(mojo, "localRepository",
				new StubArtifactRepository(file.getAbsolutePath()) {
					@Override
					public String pathOf(Artifact artifact) {
						//Return tmp file name
						return artifactFile.getName();
					}
				});
		assertEquals(mojo.getPath(), artifactFile);
	}
	
	public void testGAVProcessing() throws Exception {
		File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

		Push unspiedMojo = (Push) lookupMojo ( "push", testPom );

		Push mojo = spy(unspiedMojo);

		/**
		 * Injecting some test values as expressions are not evaluated.
		 */
		setVariableValueToObject( mojo, "artifactId", "cf-maven-tests" );

		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.PATH);
		doReturn("http://api.cloudfoundry.com").when(mojo).getCommandlineProperty(SystemProperties.TARGET);
		doReturn(null).when(mojo).getCommandlineProperty(SystemProperties.APP_NAME);

		{ //Test No Classifier or Type
			setVariableValueToObject(mojo, "artifact", "groupId:artifactId:version");
			
			boolean exception = false;
			try {
				mojo.createArtifactFromGAV();
			} catch(MojoExecutionException e) {
				exception = true;
			}
			if(!exception) {
				fail("Should have thrown a MojoExcecutionException");
			}
		}

		{ //Test No Classifier
			setVariableValueToObject(mojo, "artifact", "groupId:artifactId:version:type");
			
			Artifact artifact = mojo.createArtifactFromGAV();
			assertEquals("groupId", artifact.getGroupId());
			assertEquals("artifactId", artifact.getArtifactId());
			assertEquals("version", artifact.getVersion());
			assertEquals("type", artifact.getType());
			assertNull(artifact.getClassifier());
		}

		{ //All
			setVariableValueToObject(mojo, "artifact", "groupId:artifactId:version:type:classifier");
			
			Artifact artifact = mojo.createArtifactFromGAV();
			assertEquals("groupId", artifact.getGroupId());
			assertEquals("artifactId", artifact.getArtifactId());
			assertEquals("version", artifact.getVersion());
			assertEquals("type", artifact.getType());
			assertEquals("classifier", artifact.getClassifier());
		}

	}

}
