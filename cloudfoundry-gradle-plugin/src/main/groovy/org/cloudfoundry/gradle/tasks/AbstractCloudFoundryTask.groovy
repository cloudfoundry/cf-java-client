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
import org.cloudfoundry.client.lib.HttpProxyConfiguration
import org.cloudfoundry.client.lib.RestLogCallback
import org.cloudfoundry.client.lib.domain.CloudSpace
import org.cloudfoundry.client.lib.tokens.TokensFile
import org.gradle.api.DefaultTask
import org.cloudfoundry.gradle.GradlePluginRestLogCallback
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.web.client.ResourceAccessException
import org.gradle.api.GradleException

abstract class AbstractCloudFoundryTask extends DefaultTask {
    protected CloudFoundryOperations client
    protected WarningBypassingResponseErrorHandler errorHandler

    AbstractCloudFoundryTask() {
        super()
        group = 'Cloud Foundry'
    }

    protected void log(msg) {
        logger.quiet msg
    }

    protected boolean isVerboseEnabled() {
        logger.infoEnabled
    }

    protected void logVerbose(msg) {
        logger.info msg
    }

    protected def withCloudFoundryClient(Closure c, Object[] args) {
        validateConfiguration()
        connectToCloudFoundry()
        if (client) {
            setupLogging()

            c.call(args)

            disconnectFromCloudFoundry()
        }
    }

    private void validateConfiguration() {
        if (target == null) {
            throw new GradleException("The Cloud Foundry target must be configured.")
        }

        if (space == null) {
            throw new GradleException("The Cloud Foundry space must be configured.")
        }
    }

    private void connectToCloudFoundry() {
        if (username != null && password != null) {
            client = createClientWithUsernamePassword()
        } else {
            client = createClientWithToken()
        }
        errorHandler = new WarningBypassingResponseErrorHandler()
        client.responseErrorHandler = errorHandler
    }

    private void disconnectFromCloudFoundry() {
        try {
            if (username != null && password != null) {
                client.logout()
            }
        } finally {
            client = null
        }
    }

    protected CloudFoundryClient createClientWithUsernamePassword() {
        try {
            if (logger.infoEnabled) {
                logger.info "Connecting to '${target}' with username '${username}'"
            }

            CloudCredentials credentials = new CloudCredentials(username, password)
            CloudFoundryClient localClient = createClient(credentials)

            login(localClient)

            localClient
        } catch (MalformedURLException e) {
            throw new GradleException("Incorrect Cloud Foundry target URL '${target}'. Make sure the URL contains a scheme, e.g. http://...", e)
        }
    }

    private CloudFoundryClient createClientWithToken() {
        try {
            if (verboseEnabled) {
                logVerbose "Connecting to '${target}' with stored token"
            }

            CloudCredentials credentials = new CloudCredentials(retrieveToken())
            return createClient(credentials)
        } catch (MalformedURLException e) {
            throw new GradleException("Incorrect Cloud Foundry target URL '${target}'. Make sure the URL contains a scheme, e.g. http://...", e)
        }
    }

    private CloudFoundryClient createClient(CloudCredentials credentials) {
        HttpProxyConfiguration proxyConfiguration = getHttpProxyConfiguration()
        URL targetUrl = target.toURL()
        new CloudFoundryClient(credentials, targetUrl, organization, space, proxyConfiguration)
    }

    private HttpProxyConfiguration getHttpProxyConfiguration() {
        if (useSystemProxy) {
            String proxyHost = System.getProperty("http.proxyHost")
            String proxyPort = System.getProperty("http.proxyPort")
            if (proxyHost != null && proxyPort != null) {
                return new HttpProxyConfiguration(proxyHost, Integer.parseInt(proxyPort))
            }
        }
        null
    }

    private OAuth2AccessToken retrieveToken() {
        TokensFile tokensFile = new TokensFile()
        OAuth2AccessToken token = tokensFile.retrieveToken(target.toURI())

        if (token == null) {
            throw new GradleException("Can not authenticate to target ${target}. " +
                    "Configure a username and password, or use the login task.")
        }

        token
    }

    private void login(localClient) {
        try {
            localClient.login()
        } catch (CloudFoundryException e) {
            if (HttpStatus.FORBIDDEN == e.statusCode) {
                throw new GroovyRuntimeException("Login failed to '${target}'. Please verify your login credentials.", e)
            } else if (HttpStatus.NOT_FOUND == e.statusCode) {
                throw new GradleException("The target host '${target}' exists but it does not appear to be a valid Cloud Foundry target url.", e)
            } else {
                throw e
            }
        } catch (ResourceAccessException e) {
            throw new GradleException("Cannot access host at '${target}'.", e)
        }
    }

    private def setupLogging() {
        if (logger.debugEnabled) {
            RestLogCallback callback = new GradlePluginRestLogCallback(logger)
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

    protected CloudSpace getCurrentSpace(CloudFoundryOperations c = client) {
        List<CloudSpace> spaces = c.spaces
        spaces.find { it.name.equals(space) }
    }

    protected def propertyMissing(String name) {
        if (project.hasProperty('cf.' + name)) {
            project.property('cf.' + name)
        } else {
            project.cloudfoundry[name]
        }
    }
}
