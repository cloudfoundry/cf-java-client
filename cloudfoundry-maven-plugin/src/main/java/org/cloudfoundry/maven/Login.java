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
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.tokens.TokensFile;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.SystemProperties;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.List;

/**
 * Writes the user's token into ~/.cf/tokens.yml file.
 *
 * @author Ali Moghadam
 * @author Scott Frederick
 * @goal login
 * @requiresProject false
 * @since 1.0.0
 */
public class Login extends AbstractCloudFoundryMojo {

    public Login() {
    }

    public Login(TokensFile tokensFile) {
        this.tokensFile = tokensFile;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Assert.configurationNotNull(getUsername(), "username", SystemProperties.USERNAME);
        Assert.configurationNotNull(getPassword(), "password", SystemProperties.PASSWORD);
        Assert.configurationNotNull(getTarget(), "target", SystemProperties.TARGET);

        super.execute();
    }

    @Override
    protected void doExecute() throws MojoExecutionException {
        final OAuth2AccessToken token = getClient().login();
        final CloudInfo cloudInfo = getClient().getCloudInfo();
        final CloudSpace space = getCurrentSpace();

        tokensFile.saveToken(getTarget(), token, cloudInfo, space);

        getLog().info("Authentication successful");
    }

    protected CloudSpace getCurrentSpace() {
        List<CloudSpace> spaces = client.getSpaces();
        for (CloudSpace space : spaces) {
            if (space.getName().equals(getSpace())) {
                return space;
            }
        }
        return null;
    }
}
