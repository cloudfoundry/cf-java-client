package org.cloudfoundry.client.lib;

import org.junit.Ignore;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * Rule for conditional disabling of test classes annotated with @CloudVersions
 * when a specific version (V1 or V2) is requested with -Dcloud.version system property
 *
 * @author: trisberg
 */
public class CloudVersionRule implements TestRule {

	public Statement apply(Statement statement, Description description) {
		CloudVersions annotation = description.getTestClass().getAnnotation(CloudVersions.class);
		if(annotation == null) {
			return statement;
		}
		String testVersionProperty = System.getProperty("cloud.version");
		if(testVersionProperty == null) {
			return statement;
		}
		String testVersion = testVersionProperty.toUpperCase();
		if (!(testVersion.equals("V1") || testVersion.equals("V2"))) {
			throw new IllegalArgumentException("The 'cloud.version' system property can only be V1 or V2 -- '" +
					testVersionProperty + "' is not a valid value.");
		}
		List<String> allowedVersions = Arrays.asList(annotation.value());
		if (allowedVersions.contains(testVersion)) {
			return statement;
		}
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				// Return an empty Statement object for those tests
				// that shouldn't run for the specified version.
			}
		};
	}

}
