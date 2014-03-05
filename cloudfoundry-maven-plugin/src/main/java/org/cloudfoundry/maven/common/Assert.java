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

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

import org.cloudfoundry.client.lib.domain.CloudService;

/**
 * Various helper methods that help with the validation of parameters.
 *
 * @author Gunnar Hillert
 * @author Scott Frederick
 * @since 1.0.0
 *
 */
public final class Assert {

	/**
	 * Prevent instantiation.
	 */
	private Assert() {
		throw new AssertionError();
	}

	/**
	 * Assert that an object is not <code>null</code> .
	 * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is <code>null</code>
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param object
	 * @param objectName
	 * @param property
	 */
	public static void configurationNotNull(Object object, String objectName,
			SystemProperties property) throws MojoExecutionException {
		configurationNotNull(object, objectName, property, null);
	}

	/**
	 *
	 * @param object
	 * @param objectName
	 * @param property
	 * @param additionalDescription
	 */
	public static void configurationNotNull(Object object, String objectName,
			SystemProperties property, String additionalDescription) throws MojoExecutionException {

		if (object == null) {

			final StringBuilder message = new StringBuilder("\n\n");

			message.append(UiUtils.HORIZONTAL_LINE);
			message.append(String.format("\nRequired argument '%s' is missing.\n", objectName));
			message.append("========================================================================\n\n");
			message.append(String.format("Did you configure the parameter? You "
					+"can provide the parameter either as:\n\n"
					+ "- System Property using: -D%1$s=<provide value> or \n"
					+ "- Add the parameter to the pom.xml under the plugin's configuration element:\n\n"
					+ "    <configuration>\n"
					+ "        <%2$s>provide value</%2$s>\n"
					+ "    </configuration>\n"
					+ "\n", property.getProperty(), property.getXmlElement()));
			message.append(UiUtils.HORIZONTAL_LINE);

			if (additionalDescription != null) {
				message.append(additionalDescription).append("\n");
				message.append(UiUtils.HORIZONTAL_LINE);
			}

			throw new MojoExecutionException(message.toString());
		}

	}

	/**
	 *
	 * @param cloudService Object
	 * @param additionalDescription
	 */
	public static void configurationServiceNotNull(CloudService cloudService,
												   String additionalDescription) throws MojoExecutionException {

		if (cloudService.getName() == null || cloudService.getLabel() == null) {

			final StringBuilder message = new StringBuilder("\n\n");

			message.append(UiUtils.HORIZONTAL_LINE);
			message.append(String.format("\nRequired arguments for '%s' are missing.\n", cloudService.getName()));
			message.append("========================================================================\n\n");
			message.append("Did you configure the parameter? You ");
			message.append("can provide the parameter in the pom.xml under the plugin's configuration element:\n\n");
			message.append("<configuration>\n");
			message.append("  <services>\n");
			message.append("    <service>\n");
			message.append("      <name>provide value</name>\n");
			message.append("      <label>provide value</label>\n");
			message.append("    <service>\n");
			message.append("  <services>\n");
			message.append("</configuration>\n\n");
			message.append(UiUtils.HORIZONTAL_LINE);

			if (additionalDescription != null) {
				message.append(additionalDescription).append("\n");
				message.append(UiUtils.HORIZONTAL_LINE);
			}

			throw new MojoExecutionException(message.toString());
		}
	}

	/**
	 * Cannot use elements url and urls together
	 */
	public static void configurationUrls(String url, List<String> urls) throws MojoExecutionException {
		if (url != null && !urls.isEmpty()) {
			final String message = "\n\n" +
			"Both url and urls elements are specified at the same level\n" +
			"========================================================================\n\n" +
			"The element <url> should be nested inside a <urls> element or specified alone without a <urls> element present.\n" +
			UiUtils.HORIZONTAL_LINE;

			throw new MojoExecutionException(message);
		}
	}
}