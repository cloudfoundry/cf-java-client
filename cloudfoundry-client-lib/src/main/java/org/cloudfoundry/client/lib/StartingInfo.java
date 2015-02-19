/*
 * Copyright 2013 the original author or authors.
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

/**
 * Starting info contains values from response headers when an application is
 * first started. One of the possible header values may be the location of the
 * staging log when starting an application.
 * 
 * @author Nieraj Singh.
 * 
 */
public class StartingInfo {

	private final String stagingFile;

	public StartingInfo(String stagingFile) {
		this.stagingFile = stagingFile;
	}

	/**
	 * 
	 * @return URL value of the file location for the staging log, or null if not available.
	 */
	public String getStagingFile() {
		return stagingFile;
	}

}
