/*
 * Copyright 2009-2011 the original author or authors.
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

package org.cloudfoundry.client.lib;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class CloudFoundryException extends HttpClientErrorException {

	private static final long serialVersionUID = 3744107230930564876L;

	private String description;

	public CloudFoundryException(HttpStatus statusCode) {
		super(statusCode);
	}

	public CloudFoundryException(HttpStatus statusCode, String statusText) {
		super(statusCode, statusText);
	}

	/**
	 * Construct a new instance of {@code CloudFoundryException} based on a {@link HttpStatus}, status text and description.
	 * @param statusCode the status code
	 * @param statusText the status text
	 * @param description the description
	 */
	public CloudFoundryException(HttpStatus statusCode, String statusText, String description) {
		super(statusCode, statusText);
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		if (description != null) {
			return super.toString() + " (" + description + ")";
		}
		return super.toString();
	}
}
