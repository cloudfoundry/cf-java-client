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

package org.cloudfoundry.client.v3.stacks;

import org.cloudfoundry.client.v3.Metadata;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateStackRequestTest {

    @Test(expected = IllegalStateException.class)
    public void invalidNameLengthTest() {
        String invalidName = Stream.generate(() -> "aaa")
            .limit(251)
            .collect(Collectors.joining());

        CreateStackRequest.builder()
            .name(invalidName)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidDescriptionLengthTest() {
        String invalidDescription = Stream.generate(() -> "aaa")
            .limit(251)
            .collect(Collectors.joining());

        CreateStackRequest.builder()
            .name("valid name")
            .description(invalidDescription)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidNameAndDescriptionLengthTest() {
        String invalidName = Stream.generate(() -> "aaa")
            .limit(251)
            .collect(Collectors.joining());
        String invalidDescription = Stream.generate(() -> "aaa")
            .limit(251)
            .collect(Collectors.joining());

        CreateStackRequest.builder()
            .name(invalidName)
            .description(invalidDescription)
            .build();
    }

    @Test
    public void validRequestTest() {
        CreateStackRequest.builder()
            .name("valid name")
            .build();
    }

    @Test
    public void validRequestWithDescriptionTest() {
        CreateStackRequest.builder()
            .name("valid name")
            .description("valid description")
            .build();
    }

    @Test
    public void validRequestWithMetadataTest() {
        CreateStackRequest.builder()
            .name("valid name")
            .metadata(Metadata.builder()
                .label("label-key", "label-value")
                .build())
            .build();
    }

    @Test
    public void validRequestWithAllFieldsTest() {
        CreateStackRequest.builder()
            .name("valid name")
            .description("valid description")
            .metadata(Metadata.builder()
                .label("label-key", "label-value")
                .build())
            .build();
    }

}
