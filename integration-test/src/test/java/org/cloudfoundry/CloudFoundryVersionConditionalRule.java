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

package org.cloudfoundry;

import com.github.zafarkhaja.semver.Version;
import org.junit.Assume;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Optional;

import static org.cloudfoundry.CloudFoundryVersion.UNSPECIFIED;

final class CloudFoundryVersionConditionalRule implements MethodRule {

    private final Version server;

    CloudFoundryVersionConditionalRule(Version server) {
        this.server = server;
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                IfCloudFoundryVersion annotation = Optional.ofNullable(AnnotationUtils.findAnnotation(method.getMethod(), IfCloudFoundryVersion.class))
                    .orElse(AnnotationUtils.findAnnotation(method.getDeclaringClass(), IfCloudFoundryVersion.class));

                boolean enabled = Optional.ofNullable(annotation)
                    .map(c -> isTestEnabled(c, CloudFoundryVersionConditionalRule.this.server))
                    .orElse(true);

                Assume.assumeTrue(String.format("Cloud Foundry version required by @IfCloudFoundryVersion is not valid for test method [%s].", method.getMethod()), enabled);

                base.evaluate();
            }
        };
    }

    private static boolean isTestEnabled(IfCloudFoundryVersion condition, Version server) {
        boolean enabled = true;

        if (condition.lessThan() != UNSPECIFIED) {
            enabled = enabled && server.lessThan(condition.lessThan().getVersion());
        }

        if (condition.lessThanOrEqualTo() != UNSPECIFIED) {
            enabled = enabled && server.lessThanOrEqualTo(condition.lessThanOrEqualTo().getVersion());
        }

        if (condition.equalTo() != UNSPECIFIED) {
            enabled = enabled && server.equals(condition.equalTo().getVersion());
        }

        if (condition.greaterThanOrEqualTo() != UNSPECIFIED) {
            enabled = enabled && server.greaterThanOrEqualTo(condition.greaterThanOrEqualTo().getVersion());
        }

        if (condition.greaterThan() != UNSPECIFIED) {
            enabled = enabled && server.greaterThan(condition.greaterThan().getVersion());
        }

        return enabled;
    }

}
