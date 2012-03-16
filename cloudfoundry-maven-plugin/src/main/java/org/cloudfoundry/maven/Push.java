/*
 * Copyright 2009-2011 the original author or authors.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudInfo;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.CommonUtils;
import org.cloudfoundry.maven.common.SystemProperties;
import org.springframework.http.HttpStatus;

/**
 * Push and optionally start an application.
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 * @goal push
 * @execute phase="package"
 */
public class Push extends AbstractApplicationAwareCloudFoundryMojo {

    @Override
    protected void doExecute() throws MojoExecutionException {

        Assert.configurationNotNull(this.getUrl(), "url", SystemProperties.URL);

        final java.util.List<String> uris = new ArrayList<String>(1);
        uris.add(this.getUrl());

        final String appname        = this.getAppname();
        final Integer instances     = this.getInstances();
        final File warfile          = this.getWarfile();
        final Integer memory        = this.getMemory();
        final List<String> services = this.getServices();
        final String framework      = this.getFramework();
        final Map<String,String> env= this.getEnv();

        super.getLog().debug(String.format("Pushing App - Appname: %s, War: %s, Memory: %s, Uris: %s, Services: %s.",
                appname, warfile, memory, uris, services));

        super.getLog().debug("Create Application...");

        validateMemoryChoice(this.getClient(), memory);
        validateFrameworkChoice(this.getClient().getCloudInfo().getFrameworks(), framework);

        boolean found = true;

        try {
            this.getClient().getApplication(appname);
        } catch (CloudFoundryException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                found = false;
            } else {
                throw new MojoExecutionException(String.format("Error while checking for existing application '%s'. Error message: '%s'. Description: '%s'",
                        appname, e.getMessage(), e.getDescription()), e);
            }

        }

        if (found) {
            throw new MojoExecutionException(
                    String.format("The application '%s' is already deployed.", appname));
        }

        try {
            this.getClient().createApplication(appname, framework, memory, uris, services);
        } catch (CloudFoundryException e) {
            throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
                    this.getAppname(), e.getMessage(), e.getDescription()), e);
        }

        super.getLog().debug("Updating Application env...");

        try {
            this.getClient().updateApplicationEnv(appname, env);
        } catch (CloudFoundryException e) {
            throw new MojoExecutionException(String.format("Error while updating application env '%s'. Error message: '%s'. Description: '%s'",
                    this.getAppname(), e.getMessage(), e.getDescription()), e);
        }

        super.getLog().debug("Deploy Application...");

        validateWarFile(warfile);

        try {
            deployWar(this.getClient(), warfile, appname);
        } catch (CloudFoundryException e) {
            throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
                    this.getAppname(), e.getMessage(), e.getDescription()), e);
        }

        if (instances != null) {
            super.getLog().debug("Set the number of instances to " + instances);

            try {
                this.getClient().updateApplicationInstances(appname, instances);
            } catch (CloudFoundryException e) {
                throw new MojoExecutionException(String.format("Error while setting number of instances for application '%s'. Error message: '%s'. Description: '%s'",
                        this.getAppname(), e.getMessage(), e.getDescription()), e);
            }
        }

        if (!isNoStart()) {

            super.getLog().debug("Start Application..." + appname);

            try {
                this.getClient().startApplication(appname);
            } catch (CloudFoundryException e) {
                throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
                        this.getAppname(), e.getMessage(), e.getDescription()), e);
            }

        } else {
            super.getLog().debug("Not Starting Application.");
        }

        super.getLog().info(String.format("'%s' was successfully deployed to: '%s'.", appname, this.getUrl()));
    }

    /**
     * Helper method that validates that the memory size selected is valid and available.
     *
     * @param cloudFoundryClient
     * @param desiredMemory
     *
     * @throws IllegalStateException if memory constraints are violated.
     */
    protected void validateMemoryChoice(CloudFoundryClient cloudFoundryClient, Integer desiredMemory) {
        int[] memoryChoices = cloudFoundryClient.getApplicationMemoryChoices();
        validateMemoryChoice(memoryChoices, desiredMemory);
    }

    /**
     * Helper method that validates that the memory size selected is valid and available.
     *
     * @param desiredMemory
     * @throws IllegalStateException if memory constraints are violated.
     */
    protected void validateMemoryChoice(int[] availableMemoryChoices, Integer desiredMemory) {

        boolean match = false;
        List<String> memoryChoicesAsString = new ArrayList<String>();
        for (int i : availableMemoryChoices) {
            if (Integer.valueOf(i).equals(desiredMemory)) {
                match = true;
            }
            memoryChoicesAsString.add(String.valueOf(i));
        }

        if (!match) {
            throw new IllegalStateException("Memory must be one of the following values: " +
                      CommonUtils.collectionToCommaDelimitedString(memoryChoicesAsString));
        }

    }

    /**
     *
     * @param frameworks
     * @param desiredFramework
     * @return true if valid
     */
    protected boolean validateFrameworkChoice(Collection<CloudInfo.Framework> frameworks, String desiredFramework) {
        if( frameworks != null && !frameworks.isEmpty() && desiredFramework != null ) {
            for(CloudInfo.Framework f : frameworks ) {
                if(f.getName().equals(desiredFramework)) {
                    return true;
                }
            }
        }
        throw new IllegalStateException("Framework must be one of the following values: " +
                      CommonUtils.frameworksToCommaDelimitedString(frameworks));
    }


    /**
     * Executes the actual war deployment to Cloud Foundry.
     *
     * @param client The Cloud Foundry client to use
     * @param warFile The warfile to upload
     * @param appName The name of the application this warfile upload is for
     */
    protected void deployWar(CloudFoundryClient client, File warFile, String appName) {

        getLog().info(String.format("Deploying %s to %s.", warFile.getAbsolutePath(), appName));

        try {
            client.uploadApplication(appName, warFile);
        } catch (IOException e) {
            throw new IllegalStateException("Error while uploading application.", e);
        }

    }

}
