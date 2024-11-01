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

package org.cloudfoundry.client.v3.stacks;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

/**
 * Base class for responses that are stacks
 */
public abstract class Stack extends Resource {

    /**
     * The name
     */
    @JsonProperty("name")
    public abstract String getName();

    /**
     * The description
     */
    @JsonProperty("description")
    @Nullable
    public abstract String getDescription();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * Build RootFS Image
     */
    @JsonProperty("build_rootfs_image")
    @Nullable
    abstract String getBuildRootfsImage();

    /**
     * Run RootFS Image
     */
    @JsonProperty("run_rootfs_image")
    @Nullable
    abstract String getRunRootfsImage();

    /**
     * Whether the stack is configured to be the default stack for new applications.
     */
    @JsonProperty("default")
    @Nullable
    abstract Boolean geDefault();
}
