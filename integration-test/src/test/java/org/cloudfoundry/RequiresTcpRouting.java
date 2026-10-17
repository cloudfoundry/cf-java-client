/*
 * Copyright 2026 the original author or authors.
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Annotation to mark tests that require TCP routing to be configured.
 *
 * <p>Tests annotated with this (or test classes containing this annotation)
 * will be skipped if the {@code SKIP_TCP_ROUTING_TESTS} environment variable
 * is set to "true".
 *
 * <p>Use this when your Cloud Foundry instance does not have TCP routing
 * configured (i.e., when the info payload does not contain a 'routing_endpoint' key).
 *
 * <p>Example usage:
 * <pre>
 * &#64;RequiresTcpRouting
 * public class TcpRoutesTest extends AbstractIntegrationTest {
 *     // tests that require TCP routing
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(RequiresTcpRouting.SkipTcpRoutingCondition.class)
public @interface RequiresTcpRouting {

    /**
     * JUnit 5 ExecutionCondition that checks if tcp routing tests should be skipped.
     */
    class SkipTcpRoutingCondition implements ExecutionCondition {

        private static final String ENV_VAR = "SKIP_TCP_ROUTING_TESTS";

        @Override
        public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
            String envValue = System.getenv(ENV_VAR);

            if ("true".equalsIgnoreCase(envValue)) {
                return ConditionEvaluationResult.disabled(
                        "TCP routing tests are disabled via "
                                + ENV_VAR
                                + " environment variable"
                                + ". TCP routing may not be configured on the target CF instance.");
            }

            return ConditionEvaluationResult.enabled("TCP routing tests are enabled");
        }
    }
}
