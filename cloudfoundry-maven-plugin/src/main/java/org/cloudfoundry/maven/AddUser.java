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

import java.net.MalformedURLException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.CommonUtils;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 * Allows for registering a new user with Cloud Foundry
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 * @goal add-user
 * @phase process-sources
 */
public class AddUser extends AbstractCloudFoundryMojo {

    /**
     * Base execute method. Will perform the login and logout into Cloud Foundry.
     * Delegates to doExecute() for the actual business logic.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Assert.configurationNotNull(getUsername(), "username", SystemProperties.USERNAME);
        Assert.configurationNotNull(getPassword(), "password", SystemProperties.PASSWORD);
        Assert.configurationNotNull(getTarget(),   "target",   SystemProperties.TARGET);

        if(!CommonUtils.isValidEmail(getUsername())) {
            throw new MojoExecutionException(getUsername() + " is not a valid email address.");
        }

        try {
            client = new CloudFoundryClient(getTarget().toString());
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(
                    String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://... ", getTarget()), e);
        }

        try {
            doExecute();
            client.logout();

        } catch (RuntimeException e) {
            throw new MojoExecutionException("An exception was caught while executing Mojo.", e);
        }

    }

    @Override
    protected void doExecute() throws MojoExecutionException {
        super.getLog().info(String.format("Registering user...'%s'", this.getUsername()));
        this.getClient().register(this.getUsername(), this.getPassword());
    }

}
