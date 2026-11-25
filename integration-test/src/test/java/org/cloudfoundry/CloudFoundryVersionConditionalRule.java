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

package org.cloudfoundry;

import static org.cloudfoundry.CloudFoundryVersion.UNSPECIFIED;

import com.github.zafarkhaja.semver.Version;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Evaluates {@link IfCloudFoundryVersion}. The V2 and V3 API have separate version numbers, so
 * every {@link CloudFoundryVersion} is compared with the server version of its own API: a 2.x
 * version with the V2 API version from {@code /v2/info}, a 3.x version with the V3 API version
 * from the root endpoint.
 */
final class CloudFoundryVersionConditionalRule implements ExecutionCondition {

    /**
     * Stands in for the V2 API version of a server that has the V2 API disabled. Only CAPI
     * releases newer than every V2 version referenced by a {@link CloudFoundryVersion} can disable
     * the V2 API, so such a server counts as newer than all of them.
     */
    static final Version V2_DISABLED = Version.of(2, Integer.MAX_VALUE, 0);

    private final Version serverV2;

    private final Version serverV3;

    /**
     * @param serverV2 the V2 API version of the server, empty if the server has the V2 API disabled
     * @param serverV3 the V3 API version of the server
     */
    CloudFoundryVersionConditionalRule(Optional<Version> serverV2, Version serverV3) {
        this.serverV2 = serverV2.orElse(V2_DISABLED);
        this.serverV3 = serverV3;
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        AnnotatedElement element = context.getElement().orElse(null);

        // This rule is registered on a test instance field, so JUnit only evaluates it for test
        // methods, and a method lookup doesn't see the annotations of its class. A class-level
        // @IfCloudFoundryVersion is therefore looked up on the test class as well.
        return Stream.of(element, context.getTestClass().orElse(null))
                .filter(Objects::nonNull)
                .filter(e -> !isTestEnabled(e))
                .findFirst()
                .map(
                        e ->
                                ConditionEvaluationResult.disabled(
                                        String.format(
                                                "Cloud Foundry version required by"
                                                        + " @IfCloudFoundryVersion on [%s] is not"
                                                        + " met (server: V2 API %s, V3 API %s).",
                                                e,
                                                this.serverV2 == V2_DISABLED
                                                        ? "disabled"
                                                        : this.serverV2,
                                                this.serverV3)))
                .orElseGet(() -> ConditionEvaluationResult.enabled("Test enabled"));
    }

    private boolean isTestEnabled(AnnotatedElement element) {
        IfCloudFoundryVersion condition =
                AnnotationUtils.findAnnotation(element, IfCloudFoundryVersion.class);
        return condition == null || isTestEnabled(condition);
    }

    private boolean isTestEnabled(IfCloudFoundryVersion condition) {
        boolean enabled = true;

        if (condition.lessThan() != UNSPECIFIED) {
            enabled =
                    enabled
                            && server(condition.lessThan())
                                    .isLowerThan(version(condition.lessThan()));
        }

        if (condition.lessThanOrEqualTo() != UNSPECIFIED) {
            enabled =
                    enabled
                            && server(condition.lessThanOrEqualTo())
                                    .isLowerThanOrEquivalentTo(
                                            version(condition.lessThanOrEqualTo()));
        }

        if (condition.equalTo() != UNSPECIFIED) {
            enabled = enabled && server(condition.equalTo()).equals(version(condition.equalTo()));
        }

        if (condition.greaterThanOrEqualTo() != UNSPECIFIED) {
            enabled =
                    enabled
                            && server(condition.greaterThanOrEqualTo())
                                    .isHigherThanOrEquivalentTo(
                                            version(condition.greaterThanOrEqualTo()));
        }

        if (condition.greaterThan() != UNSPECIFIED) {
            enabled =
                    enabled
                            && server(condition.greaterThan())
                                    .isHigherThan(version(condition.greaterThan()));
        }

        return enabled;
    }

    private Version server(CloudFoundryVersion version) {
        long major = version(version).majorVersion();
        if (major == 2) {
            return this.serverV2;
        }
        if (major == 3) {
            return this.serverV3;
        }
        throw new IllegalArgumentException(
                String.format(
                        "%s is neither a V2 (2.x) nor a V3 (3.x) API version: %s",
                        version, version(version)));
    }

    private static Version version(CloudFoundryVersion version) {
        return version.getVersion();
    }
}
