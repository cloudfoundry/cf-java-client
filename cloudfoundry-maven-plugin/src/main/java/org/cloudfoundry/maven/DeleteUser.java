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

import org.apache.maven.plugin.MojoExecutionException;


/**
 * Deletes the user with the provided login credentials.
 *
 * @author Gunnar Hillert
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal delete-user
 * @phase process-sources
 */
public class DeleteUser extends AbstractCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {
		Logout logout = new Logout();

		getLog().debug("Unregistering the user");
		getClient().unregister();

		getLog().debug("Removing the token in the token file");
		logout.doExecute();
		getLog().info("User has been deleted successfully.");
	}
}