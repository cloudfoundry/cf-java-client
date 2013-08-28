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
import org.apache.maven.plugin.MojoFailureException;
import org.cloudfoundry.client.lib.tokens.TokensFile;

/**
 * Performs logout and removes the target info from ~/.cf/tokens.yml.
 *
 * @author Ali Moghadam
 * @author Scott Frederick
 * @since 1.0.0
 *
 * @goal logout
 * @requiresProject false
 */
public class Logout extends AbstractCloudFoundryMojo {
	public Logout() {
	}

	public Logout(TokensFile tokensFile) {
		super(tokensFile);
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getClient() != null) {
			getClient().logout();
		}
		doExecute();
	}

	@Override
	protected void doExecute() throws MojoExecutionException {
		tokensFile.removeToken(getTarget());
	}
}