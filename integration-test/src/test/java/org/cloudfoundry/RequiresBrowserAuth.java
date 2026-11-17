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
 * Annotation to mark tests that require browser-based authentication (e.g., OAuth
 * authorization code grants, implicit grants). Tests annotated with this will be skipped
 * if the environment variable {@code SKIP_BROWSER_AUTH_TESTS} is set to "true".
 *
 * <p>Usage:
 * <pre>
 * &#64;RequiresBrowserAuth
 * public class MyAuthTest extends AbstractIntegrationTest {
 *     // ...
 * }
 * </pre>
 *
 * <p>To skip browser auth tests, set the environment variable:
 * <pre>
 * export SKIP_BROWSER_AUTH_TESTS=true
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(RequiresBrowserAuth.BrowserAuthCondition.class)
public @interface RequiresBrowserAuth {

    /**
     * JUnit 5 ExecutionCondition that checks if Browser Auth tests should be skipped.
     */
    class BrowserAuthCondition implements ExecutionCondition {

        private static final String SKIP_BROWSER_AUTH_ENV = "SKIP_BROWSER_AUTH_TESTS";

        @Override
        public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
            if ("true".equalsIgnoreCase(System.getenv(SKIP_BROWSER_AUTH_ENV))) {
                return ConditionEvaluationResult.disabled(
                        "Tests requiring Browser Authentication are disabled via "
                                + SKIP_BROWSER_AUTH_ENV
                                + " environment variable");
            }

            return ConditionEvaluationResult.enabled("Browser authentication tests are enabled");
        }
    }
}
