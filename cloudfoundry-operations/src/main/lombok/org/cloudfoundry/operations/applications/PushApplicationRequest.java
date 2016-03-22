/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.operations.applications;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.io.InputStream;
import java.time.Duration;

/**
 * The request options for the push application operation
 */
@Data
public final class PushApplicationRequest implements Validatable {

    /**
     * The bits for the application
     *
     * @param application the bits for the application
     * @return the bits for the application
     */
    private final InputStream application;

    /**
     * The buildpack for the application
     *
     * @param buildpack the buildpack for the application
     * @return the buildpack for the application
     */
    private final String buildpack;

    /**
     * The custom start command for the application
     *
     * @param command the start command for the application
     * @return the start command for the application
     */
    private final String command;

    /**
     * The disk quota for the application
     *
     * @param diskQuota the disk quota for the application
     * @return the disk quota for the application
     */
    private final Integer diskQuota;

    /**
     * The Docker image for the application
     *
     * @param dockerImage the Docker image for the application
     * @return the Docker image for the application
     */
    private final String dockerImage;

    /**
     * The domain for the application
     *
     * @param domain the domain for the application
     * @return the domain for the application
     */
    private final String domain;

    /**
     * The health check type for the application e.g. 'port' or 'none'
     *
     * @param healthCheckType the health check type for the application
     * @return the health check type for the application
     */
    private final String healthCheckType;

    /**
     * The host for the application
     *
     * @param host the host for the application
     * @return the host for the application
     */
    private final String host;

    /**
     * The number of instances for the application
     *
     * @param instances the number of instances for the application
     * @return the number of instances for the application
     */
    private final Integer instances;

    /**
     * The memory in MB for the application
     *
     * @param memory the memory in MB for the application
     * @return the memory in MB for the application
     */
    private final Integer memory;

    /**
     * The name for the application
     *
     * @param name the name for the application
     * @return the name for the application
     */
    private final String name;

    /**
     * Map the root domain to the application
     *
     * @param noHostname whether to map the root domain to the application
     * @return whether to map the root domain to the application
     */
    private final Boolean noHostname;

    /**
     * Do not create a route for the application
     *
     * @param noRoute whether to create a route for the application
     * @return whether to create a route for the application
     */
    private final Boolean noRoute;

    /**
     * Do not start the application after pushing
     *
     * @param noStart whether to start the application
     * @return whether to start the application
     */
    private final Boolean noStart;

    /**
     * The path for the application
     *
     * @param path the path for the application
     * @return the path for the application
     */
    private final String path;

    /**
     * Use a random route for the application
     *
     * @param randomRoute whether to use a random route for the application
     * @return whether to use a random route for the application
     */
    private final Boolean randomRoute;

    /**
     * The route path for the application
     *
     * @param routePath the route path for the application
     * @return the route path for the application
     */
    private final String routePath;

    /**
     * The stack for the application
     *
     * @param stack the stack for the application
     * @return the stack for the application
     */
    private final String stack;

    /**
     * How long to wait for staging
     *
     * @param stagingTimeout how long to wait for staging
     * @return how long to wait for staging
     */
    private final Duration stagingTimeout;

    /**
     * How long to wait for startup
     *
     * @param startupTimeout how long to wait for startup
     * @return how long to wait for startup
     */
    private final Duration startupTimeout;

    /**
     * The health check timeout
     *
     * @param timeout the health check timeout
     * @return the health check timeout
     */
    private final Integer timeout;

    @Builder
    PushApplicationRequest(InputStream application,
                           String buildpack,
                           String command,
                           Integer diskQuota,
                           String dockerImage,
                           String domain,
                           String healthCheckType,
                           String host,
                           Integer instances,
                           Integer memory,
                           String name,
                           Boolean noHostname,
                           Boolean noRoute,
                           Boolean noStart,
                           String path,
                           Boolean randomRoute,
                           String routePath,
                           String stack,
                           Duration stagingTimeout,
                           Duration startupTimeout,
                           Integer timeout) {
        this.application = application;
        this.buildpack = buildpack;
        this.command = command;
        this.diskQuota = diskQuota;
        this.dockerImage = dockerImage;
        this.domain = domain;
        this.healthCheckType = healthCheckType;
        this.host = host;
        this.instances = instances;
        this.memory = memory;
        this.name = name;
        this.noHostname = noHostname;
        this.noRoute = noRoute;
        this.noStart = noStart;
        this.path = path;
        this.randomRoute = randomRoute;
        this.routePath = routePath;
        this.stack = stack;
        this.stagingTimeout = stagingTimeout;
        this.startupTimeout = startupTimeout;
        this.timeout = timeout;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.application == null) {
            builder.message("application bits must be specified");
        }

        return builder.build();
    }

}
