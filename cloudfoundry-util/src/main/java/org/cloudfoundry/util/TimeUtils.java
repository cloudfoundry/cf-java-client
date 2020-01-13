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

package org.cloudfoundry.util;

/**
 * Utilities for dealing with time
 */
public final class TimeUtils {

    private static final double MILLISECOND = 1;

    private static final double SECOND = 1000 * MILLISECOND;

    private static final double MINUTE = 60 * SECOND;

    private static final double HOUR = 60 * MINUTE;

    private TimeUtils() {
    }

    /**
     * Renders a time period in human readable form
     *
     * @param time the time in milliseconds
     * @return the time in human readable form
     */
    public static String asTime(long time) {
        if (time > HOUR) {
            return String.format("%.1f h", (time / HOUR));
        } else if (time > MINUTE) {
            return String.format("%.1f m", (time / MINUTE));
        } else if (time > SECOND) {
            return String.format("%.1f s", (time / SECOND));
        } else {
            return String.format("%d ms", time);
        }
    }

}
