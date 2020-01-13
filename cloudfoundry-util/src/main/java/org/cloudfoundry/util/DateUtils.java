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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Utilities for dealing with {@link Date}s
 */
public final class DateUtils {

    private static final SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    private static final Object MONITOR = new Object();

    private DateUtils() {
    }

    /**
     * Formats a {@link Date} into a String in {@code ISO8601} format
     *
     * @param d the date to formatToIso8601
     * @return the formatted date
     */
    public static String formatToIso8601(Date d) {
        synchronized (MONITOR) {
            return ISO8601.format(d);
        }
    }

    /**
     * Parses a string in {@code ISO8601} format to a {@link Date} object
     *
     * @param s the string to parse
     * @return the parsed {@link Date}
     */
    public static Date parseFromIso8601(String s) {
        synchronized (MONITOR) {
            try {
                return ISO8601.parse(s);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Unable to parse date", e);
            }
        }
    }

    /**
     * Parses a double representing seconds from the epoch to a {@link Date} object
     *
     * @param d the double to parse
     * @return the parsed {@link Date}
     */
    public static Date parseSecondsFromEpoch(Double d) {
        synchronized (MONITOR) {
            return new Date(SECONDS.toMillis(d.longValue()));
        }
    }

}
