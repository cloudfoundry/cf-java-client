/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.client.v3.deployments;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.Resource;
import org.cloudfoundry.client.v3.ToOneRelationship;

import java.util.List;

/**
 * Base class for responses that are deployments
 */
public abstract class Deployment extends Resource {

    /**
     * The application the deployment is updating
     */
    @JsonProperty("app")
    @Nullable
    public abstract ToOneRelationship getApplication();

    /**
     * The droplet the deployment is transitioning the app to
     */
    @JsonProperty("droplet")
    @Nullable
    public abstract Relationship getDroplet();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * The revision the deployment is transitioning the app to
     */
    @JsonProperty("new_processes")
    @Nullable
    public abstract List<Process> getNewProcesses();

    /**
     * The app’s current droplet before the deployment was created
     */
    @JsonProperty("previous_droplet")
    @Nullable
    public abstract Relationship getPreviousDroplet();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    @Nullable
    public abstract DeploymentRelationships getRelationships();

    /**
     * The revision the deployment is transitioning the app to
     */
    @JsonProperty("revision")
    @Nullable
    public abstract Revision getRevision();

    /**
     * The state of the deployment
     */
    @Deprecated
    @JsonProperty("state")
    public abstract DeploymentState getState();

    /**
     * The status of the deployment
     */
    @JsonProperty("status")
    @Nullable
    public abstract Status getStatus();

    /**
     * The strategy of the deployment
     */
    @JsonProperty("strategy")
    @Nullable
    public abstract DeploymentStrategy getStrategy();

}
