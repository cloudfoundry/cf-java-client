/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.operations;

/**
 * An exception indicating an unexpected response has been returned from Cloud Foundry
 */
public class UnexpectedResponseException extends RuntimeException {

    private static final long serialVersionUID = 4252724548345624556L;

    /**
     * Creates a new instance
     * 
     * @param message an explanation of the problem
     */
    public UnexpectedResponseException(String message) {
        super(message);
    }
}
