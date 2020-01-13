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

package org.cloudfoundry.uaa.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;

import java.util.List;

/**
 * The entity response payload for User
 */
public abstract class AbstractUser extends AbstractUserSummary {

    /**
     * The approvals for the user
     */
    @JsonProperty("approvals")
    public abstract List<Approval> getApprovals();

    /**
     * The external id
     */
    @JsonProperty("externalId")
    @Nullable
    public abstract String getExternalId();

    /**
     * The groups for the user
     */
    @JsonProperty("groups")
    public abstract List<org.cloudfoundry.uaa.users.Group> getGroups();

}
