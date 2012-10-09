/*
 * Copyright 2009-2012 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RestLogCallback implementation that logs to application log
 *
 * @author: Thomas Risberg
 */
public class RestLogger implements RestLogCallback {

	private final Log logger;

	public RestLogger(String category) {
		logger = LogFactory.getLog(category == null ? getClass().getName() : category);
	}

	public void onNewLogEntry(RestLogEntry logEntry) {
		if (logger.isDebugEnabled()) {
			logger.debug(formatLogMessage(logEntry));
		}
	}

	private String formatLogMessage(RestLogEntry restLogEntry) {
		return restLogEntry.getStatus() +
				" :: HTTP STATUS: " + restLogEntry.getHttpStatus() +
				" :: REQUEST: " + restLogEntry.getMethod() + " " + restLogEntry.getUri() +
				" :: " + restLogEntry.getMessage();
	}

}
