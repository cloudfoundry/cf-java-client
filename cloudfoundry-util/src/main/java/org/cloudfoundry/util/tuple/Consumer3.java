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

package org.cloudfoundry.util.tuple;

/**
 * An operation that accepts three input arguments and returns no result
 *
 * @param <T1> The type of the first input to the operation
 * @param <T2> The type of the second input to the operation
 * @param <T3> The type of the third input to the operation
 */
@FunctionalInterface
public interface Consumer3<T1, T2, T3> {

    /**
     * Performs this operation on the given arguments
     *
     * @param t1 the first input argument
     * @param t2 the second input argument
     * @param t3 the third input argument
     */
    void accept(T1 t1, T2 t2, T3 t3);
}
