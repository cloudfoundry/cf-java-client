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

package org.cloudfoundry.reactor.util;

import io.netty.buffer.ByteBufAllocator;

public interface ByteBufAllocatorMetricProviderMXBean {

    /**
     * Returns the number of bytes of direct memory used by a {@link ByteBufAllocator} or {@code -1} if unknown.
     */
    long getUsedDirectMemory();

    /**
     * Returns the number of bytes of heap memory used by a {@link ByteBufAllocator} or {@code -1} if unknown.
     */
    long getUsedHeapMemory();

}
