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
import static org.mockito.Mockito.spy;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 */
public class InstancesTest extends AbstractMojoTestCase {

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @throws Exception
	 */
	public void testDefaultParameter() throws Exception {

		File testPom = new File( getBasedir(), "src/test/resources/test-pom.xml" );

		Instances unspiedMojo = (Instances) lookupMojo ( "instances", testPom );

		Instances mojo = spy(unspiedMojo);

		doReturn(null).when(mojo).getCommandlineProperty(any(SystemProperties.class));

		junit.framework.Assert.assertEquals("Instances should default to 1.", Integer.valueOf(1), mojo.getInstances());

	}

}
