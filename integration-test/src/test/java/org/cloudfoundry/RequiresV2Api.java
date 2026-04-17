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
 * Annotation to mark tests that require the V2 API. Tests annotated with this
 * will be skipped if the environment variable {@code SKIP_V2_TESTS} is set to "true".
 *
 * <p>Usage:
 * <pre>
 * &#64;RequiresV2Api
 * public class MyV2Test extends AbstractIntegrationTest {
 *     // ...
 * }
 * </pre>
 *
 * <p>To skip V2 tests, set the environment variable:
 * <pre>
 * export SKIP_V2_TESTS=true
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(RequiresV2Api.V2ApiCondition.class)
public @interface RequiresV2Api {

    /** Environment variable name used to skip V2 API tests. */
    String SKIP_V2_TESTS_ENV = "SKIP_V2_TESTS";

    /**
     * JUnit 5 ExecutionCondition that checks if V2 tests should be skipped.
     */
    class V2ApiCondition implements ExecutionCondition {

        @Override
        public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
            if ("true".equalsIgnoreCase(System.getenv(SKIP_V2_TESTS_ENV))) {
                return ConditionEvaluationResult.disabled(
                        "V2 API tests are disabled via "
                                + SKIP_V2_TESTS_ENV
                                + " environment variable");
            }
            return ConditionEvaluationResult.enabled("V2 API tests are enabled");
        }
    }
}
