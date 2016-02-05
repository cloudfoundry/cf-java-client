/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.client.v2.featureflags;

import org.cloudfoundry.client.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public class GetFeatureFlagRequestTest {

    @Test
    public void isValid() throws Exception {
        ValidationResult result = GetFeatureFlagRequest.builder()
            .name("test_feature_flag_name")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }
    
    @Test
    public void isValidNoName() throws Exception {
        ValidationResult result = GetFeatureFlagRequest.builder()
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must be specified", result.getMessages().get(0));
    }
    
    @Test
    public void isValidBadName1() throws Exception {
        ValidationResult result = GetFeatureFlagRequest.builder()
            .name("mustn't have spaces or / chars (or quotes, or parentheses, or commas)")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must consist only of alphabetic characters and underscores", result.getMessages().get(0));
    }

    @Test
    public void isValidBadName2() throws Exception {
        ValidationResult result = GetFeatureFlagRequest.builder()
            .name("good_name_with_bad_at_end ")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must consist only of alphabetic characters and underscores", result.getMessages().get(0));
    }

    @Test
    public void isValidBadName3() throws Exception {
        ValidationResult result = GetFeatureFlagRequest.builder()
            .name("good_name_with_bad_at_end.")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must consist only of alphabetic characters and underscores", result.getMessages().get(0));
    }
}
