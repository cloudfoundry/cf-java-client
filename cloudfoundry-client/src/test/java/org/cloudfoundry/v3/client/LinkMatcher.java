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

package org.cloudfoundry.v3.client;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class LinkMatcher {

    public static Matcher<Link> linkMatches(Link expectedValue) {
        return new TypeSafeMatcher<Link>() {

            @Override
            public void describeTo(Description description) {
                description.appendValue(expectedValue);
            }

            @Override
            protected boolean matchesSafely(Link item) {
                return expectedValue.getHref().equals(item.getHref()) &&
                        expectedValue.getMethod().equals(item.getMethod());
            }

        };
    }

}
