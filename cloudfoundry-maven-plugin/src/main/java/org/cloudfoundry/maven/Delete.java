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

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.springframework.http.HttpStatus;

/**
 * Deletes an application.
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 * @goal delete
 * @phase process-sources
 */
public class Delete extends AbstractApplicationAwareCloudFoundryMojo {


    @Override
    protected void doExecute() throws MojoExecutionException {
        super.getLog().info("Deleting application..." + this.getAppname());

        try {
            this.getClient().deleteApplication(this.getAppname());
        } catch (CloudFoundryException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new MojoExecutionException(String.format("The Application '%s' does not exist.",
                        this.getAppname()), e);
            }
        }
    }

}
