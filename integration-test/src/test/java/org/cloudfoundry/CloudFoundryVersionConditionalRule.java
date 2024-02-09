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
import java.util.Optional;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.annotation.AnnotationUtils;

final class CloudFoundryVersionConditionalRule implements ExecutionCondition {

    private final Version server;

    CloudFoundryVersionConditionalRule(Version server) {
        this.server = server;
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        AnnotatedElement element = context.getElement().orElse(null);

        IfCloudFoundryVersion annotation =
                AnnotationUtils.findAnnotation(element, IfCloudFoundryVersion.class);

        boolean enabled =
                Optional.ofNullable(annotation)
                        .map(c -> isTestEnabled(c, CloudFoundryVersionConditionalRule.this.server))
                        .orElse(true);

        return enabled
                ? ConditionEvaluationResult.enabled("Test enabled")
                : ConditionEvaluationResult.disabled(
                        String.format(
                                "Cloud Foundry version required by @IfCloudFoundryVersion is not"
                                        + " valid for test method [%s].",
                                element));
    }

    private static boolean isTestEnabled(IfCloudFoundryVersion condition, Version server) {
        boolean enabled = true;

        if (condition.lessThan() != UNSPECIFIED) {
            enabled = enabled && server.lessThan(condition.lessThan().getVersion());
        }

        if (condition.lessThanOrEqualTo() != UNSPECIFIED) {
            enabled =
                    enabled && server.lessThanOrEqualTo(condition.lessThanOrEqualTo().getVersion());
        }

        if (condition.equalTo() != UNSPECIFIED) {
            enabled = enabled && server.equals(condition.equalTo().getVersion());
        }

        if (condition.greaterThanOrEqualTo() != UNSPECIFIED) {
            enabled =
                    enabled
                            && server.greaterThanOrEqualTo(
                                    condition.greaterThanOrEqualTo().getVersion());
        }

        if (condition.greaterThan() != UNSPECIFIED) {
            enabled = enabled && server.greaterThan(condition.greaterThan().getVersion());
        }

        return enabled;
    }
}
