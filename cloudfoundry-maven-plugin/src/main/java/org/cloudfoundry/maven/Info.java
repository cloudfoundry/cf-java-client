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
import java.net.URI;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudInfo;
import org.cloudfoundry.client.lib.ServiceConfiguration;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.CommonUtils;
import org.cloudfoundry.maven.common.SystemProperties;
import org.cloudfoundry.maven.common.UiUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;

/**
 * Provide general usage information about the used Cloud Foundry environment.
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 *
 * @since 1.0.0
 *
 * @goal info
 * @requiresProject false
 */
public class Info extends AbstractCloudFoundryMojo {

    /**
     * 	@FIXME Not sure whether one should be able to overwrite execute()
     *
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (this.getPassword() == null && this.getUsername() == null) {


            final URI target = this.getTarget();

            Assert.configurationNotNull(target,   "target",   SystemProperties.TARGET);

            try {
                client = new CloudFoundryClient(target.toString());
            } catch (MalformedURLException e) {
                throw new MojoExecutionException(
                        String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://... ", target), e);
            }

            try {

                super.getLog().warn("You did not provide a username and password. "
                                  + "Showing basic information only.");

                doExecute();
                client.logout();

            } catch (RuntimeException e) {
                throw new MojoExecutionException("An exception was caught while executing Mojo.", e);
            }

        } else {
            super.execute();
        }

    }

    @Override
    protected void doExecute() throws MojoExecutionException {

        final CloudInfo cloudinfo;
        final List<ServiceConfiguration> serviceConfigurations;
        final String localTarget =  getTarget().toString();

        try {
            cloudinfo = client.getCloudInfo();
            serviceConfigurations = client.getServiceConfigurations();
        } catch (CloudFoundryException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new MojoExecutionException(
                        String.format("The target host '%s' exists but it does not appear to be a valid Cloud Foundry target url.", localTarget), e);
            } else {
                throw e;
            }

        } catch (ResourceAccessException e) {
            throw new MojoExecutionException(
                    String.format("Cannot access host at '%s'.", localTarget), e);
        }

        super.getLog().info(getCloudInfoFormattedAsString(cloudinfo, serviceConfigurations, localTarget));
    }

    /**
     * Renders the help text. If the callers is logged in successfully the full
     * information is rendered if not only basic Cloud Foundry information is
     * rendered and returned as String.
     *
     * @param cloudinfo Contains the information about the Cloud Foundry environment
     * @param target The target Url from which the information was obtained
     *
     * @return Returns a formatted String for console output
     */
    private String getCloudInfoFormattedAsString(CloudInfo cloudinfo, List<ServiceConfiguration> serviceConfigurations, String target) {

        StringBuilder sb = new StringBuilder("\n");

        sb.append(UiUtils.HORIZONTAL_LINE);
        sb.append(String.format("%s (v%s build %s)\n", cloudinfo.getDescription(), cloudinfo.getVersion(), cloudinfo.getBuild()));
        sb.append(String.format("For support visit %s\n\n", cloudinfo.getSupport()));

        sb.append(String.format("Target:          %s  \n"   , target));
        sb.append(String.format("Frameworks:      %s\n"     , CommonUtils.frameworksToCommaDelimitedString(cloudinfo.getFrameworks())));
        sb.append(String.format("System Services: %s\n\n"   , CommonUtils.serviceConfigurationsToCommaDelimitedString(serviceConfigurations)));

        if (cloudinfo.getUser() != null) {
            sb.append(String.format("User:        %s\n", cloudinfo.getUser()));

            sb.append("Usage: "       + "\n");
            sb.append(String.format("    Memory:       %sM of %sM total \n", cloudinfo.getUsage().getTotalMemory()
                                                                           , cloudinfo.getLimits().getMaxTotalMemory()));
            sb.append(String.format("    Services:     %s of %s total \n"  , cloudinfo.getUsage().getServices()
                                                                           , cloudinfo.getLimits().getMaxServices()));
            sb.append(String.format("    Apps:         %s of %s total \n"  , cloudinfo.getUsage().getApps()
                                                                           , cloudinfo.getLimits().getMaxApps()));
            sb.append(String.format("    Uris Per App: %s of %s total \n"  , cloudinfo.getUsage().getUrisPerApp()
                                                                           , cloudinfo.getLimits().getMaxUrisPerApp()));
        }

        sb.append(UiUtils.HORIZONTAL_LINE);
        return sb.toString();
    }
}
