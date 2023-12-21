/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.roles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type of a {@link Role}
 */
public enum RoleType {

    /**
     * organization_auditor role type
     */
    ORGANIZATION_AUDITOR,

    /**
     * organization_billing_manager role type
     */
    ORGANIZATION_BILLING_MANAGER,

    /**
     * organization_manager role type
     */
    ORGANIZATION_MANAGER,

    /**
     * organization_user role type
     */
    ORGANIZATION_USER,

    /**
     * space_auditor role type
     */
    SPACE_AUDITOR,

    /**
     * space_developer role type
     */
    SPACE_DEVELOPER,

    /**
     * space_manager role type
     */
    SPACE_MANAGER;

    public static final Set<RoleType> ORGANIZATION_ROLE_TYPES =
            EnumSet.of(
                    ORGANIZATION_AUDITOR,
                    ORGANIZATION_BILLING_MANAGER,
                    ORGANIZATION_MANAGER,
                    ORGANIZATION_USER);

    public static final Set<RoleType> SPACE_ROLE_TYPES =
            EnumSet.of(SPACE_AUDITOR, SPACE_DEVELOPER, SPACE_MANAGER);

    private static final Map<String, RoleType> NAMES_TO_VALUES =
            Arrays.stream(RoleType.values())
                    .collect(Collectors.toMap(RoleType::getValue, roleType -> roleType));

    @JsonCreator
    public static RoleType from(String s) {
        RoleType roleType = NAMES_TO_VALUES.get(s);
        if (roleType == null) {
            throw new IllegalArgumentException("Unknown role type: " + s);
        }
        return roleType;
    }

    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }

    @Override
    public String toString() {
        return getValue();
    }
}
