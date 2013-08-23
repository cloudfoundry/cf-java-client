/*
 * Copyright 2012 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.gradle.tasks

import org.cloudfoundry.client.lib.CloudCredentials
import org.cloudfoundry.client.lib.CloudFoundryClient
import org.cloudfoundry.client.lib.CloudFoundryException
import org.cloudfoundry.client.lib.CloudFoundryOperations
import org.cloudfoundry.client.lib.RestLogCallback
import org.cloudfoundry.client.lib.domain.CloudSpace
import org.gradle.api.DefaultTask
import org.cloudfoundry.gradle.GradlePluginRestLogCallback
import org.springframework.http.HttpStatus
import org.springframework.web.client.ResourceAccessException
import org.gradle.api.GradleException

abstract class AbstractCloudFoundryTask extends DefaultTask {
    protected CloudFoundryOperations client

    AbstractCloudFoundryTask() {
        super()
        group = 'Cloud Foundry'
    }

    protected void log(msg) {
        println "Cloud Foundry - $msg"
    }

    protected def withCloudFoundryClient(Closure c, Object[] args) {
        validateConfiguration()
        connectToCloudFoundry()
        if (client) {
            setupLogging()

            c.call(args)

            logout()
        }
    }

    private void validateConfiguration() {
        if (target == null) {
            throw new GradleException("The Cloud Foundry target must be configured.")
        }

        if (username == null || password == null) {
            throw new GradleException("The Cloud Foundry username and password must be configured.")
        }
    }

    private void connectToCloudFoundry() {
        if (verbose) {
            log "Connecting to '${target}' with user '${username}'"
        }

        CloudFoundryClient localClient = getClient()

        login(localClient)

        client = localClient
    }

    private CloudFoundryClient getClient() {
        try {
            CloudCredentials credentials = new CloudCredentials(username, password)
            return new CloudFoundryClient(credentials, target.toURL(), organization, space)
        } catch (MalformedURLException e) {
            throw new GradleException("Incorrect Cloud Foundry target url, are you sure '${target}' is correct? Make sure the url contains a scheme, e.g. http://...", e)
        }
    }

    private void login(localClient) {
        try {
            localClient.login()
        } catch (CloudFoundryException e) {
            if (HttpStatus.FORBIDDEN == e.statusCode) {
                throw new GroovyRuntimeException("Login failed to '${target}' using username '${username}'. Please verify your login credentials.", e)
            } else if (HttpStatus.NOT_FOUND == e.statusCode) {
                throw new GradleException("The target host '${target}' exists but it does not appear to be a valid Cloud Foundry target url.", e)
            } else {
                throw e
            }
        } catch (ResourceAccessException e) {
            throw new GradleException("Cannot access host at '${target}'.", e)
        }
    }

    protected void logout() {
        try {
            client.logout()
        } finally {
            client = null
        }
    }

    protected def setupLogging() {
        if (debugTrace) {
            RestLogCallback callback = new GradlePluginRestLogCallback()
            client.registerRestLogListener(callback)
        }
    }

    protected def withApplication(Closure c, Object[] args) {
        try {
            c.call(args)
        } catch (CloudFoundryException e) {
            if (e.statusCode == HttpStatus.NOT_FOUND) {
                throw new GradleException("Application ${application} not found")
            } else {
                throw e
            }
        }
    }

    protected def withApplicationIfExists(Closure c, Object[] args) {
        if (application) {
            try {
                c.call(args)
            } catch (CloudFoundryException e) {
                if (e.statusCode == HttpStatus.NOT_FOUND) {
                    // do nothing, continue execution after closure
                } else {
                    throw e
                }
            }
        }
    }

    protected CloudSpace getCurrentSpace() {
        List<CloudSpace> spaces = client.spaces
        spaces.find { it.name.equals(space) }
    }

    protected def getServiceInfos() {
        project.serviceInfos
    }

    protected def propertyMissing(String name) {
        project.cloudfoundry[name]
    }
}
