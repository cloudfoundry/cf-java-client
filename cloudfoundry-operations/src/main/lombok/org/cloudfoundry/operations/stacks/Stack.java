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

package org.cloudfoundry.operations.stacks;

import lombok.Builder;
import lombok.Data;

/**
 * A Stack.
 */
@Data
public final class Stack {

    /**
     * The description of this stack
     *
     * @param description the description
     * @return the description
     */
    private final String description;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The name of this stack
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    @Builder
    Stack(String description, String id, String name) {
        this.description = description;
        this.id = id;
        this.name = name;
    }

}
