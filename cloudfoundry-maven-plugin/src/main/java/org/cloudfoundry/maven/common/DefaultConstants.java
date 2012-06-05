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


/**
 * Provides Default values for several Maven plugin configuration parameters.
 *
 * For instance if for certain parameters neither system properties nor pom.xml
 * configarion parameters are provided, then the below default values are used.
 *
 * See {@link SystemProperties}
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 */
public final class DefaultConstants {

	public static final String  MAVEN_DEFAULT_SERVER = "cloud-foundry-credentials";
	public static final Integer MEMORY               = 512;
	public static final String  RUNTIME              = "java";
	public static final Boolean NO_START             = Boolean.FALSE;
	public static final Integer DEFAULT_INSTANCE     = 1;

	/**
	 * Prevent instantiation.
	 */
	private DefaultConstants() {
		throw new AssertionError();
	}

}
