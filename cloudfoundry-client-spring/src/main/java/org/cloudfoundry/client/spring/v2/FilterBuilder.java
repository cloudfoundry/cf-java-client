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

package org.cloudfoundry.client.spring.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A builder for Cloud Foundry V2 filters
 *
 * <p><b>This class is NOT threadsafe</b>
 */
public final class FilterBuilder implements GreaterThanOrEqualToFilterBuilder, GreaterThanFilterBuilder,
        InFilterBuilder, IsFilterBuilder, LessThanFilterBuilder, LessThanOrEqualToFilterBuilder {

    private final String key;

    private volatile Integer greaterThan;

    private volatile Integer greaterThanOrEqualTo;

    private final List<String> in = new ArrayList<>();

    private volatile String is;

    private volatile Integer lessThan;

    private volatile Integer lessThanOrEqualTo;

    /**
     * Creates a new instance
     *
     * @param key the key of the filter
     */
    public FilterBuilder(String key) {
        this.key = key;
    }

    /**
     * Configure the greater than filter
     *
     * @param greaterThan the greater than filter
     * @return {@code this}
     */
    public GreaterThanFilterBuilder greaterThan(Integer greaterThan) {
        this.greaterThan = greaterThan;
        return this;
    }

    /**
     * Configure the greater than or equal to filter
     *
     * @param greaterThanOrEqualTo the greater than or equal to filter
     * @return {@code this}
     */
    public GreaterThanOrEqualToFilterBuilder greaterThanOrEqualTo(Integer greaterThanOrEqualTo) {
        this.greaterThanOrEqualTo = greaterThanOrEqualTo;
        return this;
    }

    @Override
    public InFilterBuilder in(String in) {
        this.in.add(in);
        return this;
    }

    @Override
    public InFilterBuilder in(List<String> ins) {
        this.in.addAll(ins);
        return this;
    }

    /**
     * Configure the is filter
     *
     * @param is the is filter
     * @return {@code this}
     */
    public IsFilterBuilder is(String is) {
        this.is = is;
        return this;
    }

    /**
     * Configure the less than filter
     *
     * @param lessThan the less than filter
     * @return {@code this}
     */
    public LessThanFilterBuilder lessThan(Integer lessThan) {
        this.lessThan = lessThan;
        return this;
    }

    /**
     * Configure the less than or equal to filter
     *
     * @param lessThanOrEqualTo the less than or equal to filter
     * @return {@code this}
     */
    public LessThanOrEqualToFilterBuilder lessThanOrEqualTo(Integer lessThanOrEqualTo) {
        this.lessThanOrEqualTo = lessThanOrEqualTo;
        return this;
    }

    @Override
    public String build() {
        StringBuilder sb = new StringBuilder(this.key);

        if (this.greaterThan != null) {
            sb.append(">").append(this.greaterThan);
        } else if (this.greaterThanOrEqualTo != null) {
            sb.append(">=").append(this.greaterThanOrEqualTo);
        } else if (!this.in.isEmpty()) {
            sb.append(" IN ").append(this.in.stream().collect(Collectors.joining(",")));
        } else if (this.is != null) {
            sb.append(":").append(this.is);
        } else if (this.lessThan != null) {
            sb.append("<").append(this.lessThan);
        } else if (this.lessThanOrEqualTo != null) {
            sb.append("<=").append(this.lessThanOrEqualTo);
        }

        return sb.toString();
    }
}
