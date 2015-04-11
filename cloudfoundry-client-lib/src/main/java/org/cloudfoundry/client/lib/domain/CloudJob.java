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

package org.cloudfoundry.client.lib.domain;

/**
 * @author Scott Frederick
 */
public class CloudJob extends CloudEntity {
	public enum Status {
		FAILED("failed"), FINISHED("finished"), QUEUED("queued"), RUNNING("running");

		private final String status;

		Status(String status) {
			this.status = status;
		}

		@Override
		public String toString() {
			return status;
		}

		public static Status getEnum(String status) {
			for (Status value : Status.values()) {
				if (value.status.equals(status)) {
					return value;
				}
			}
			throw new IllegalArgumentException("Invalid Status value: " + status);
		}
	}

	public static class ErrorDetails {
		private long code;
		private String description;
		private String errorCode;

		public ErrorDetails(long code, String description, String errorCode) {
			this.code = code;
			this.description = description;
			this.errorCode = errorCode;
		}

		public long getCode() {
			return code;
		}

		public String getDescription() {
			return description;
		}

		public String getErrorCode() {
			return errorCode;
		}
	}

	private final Status status;
	private final ErrorDetails errorDetails;

	public CloudJob(Meta meta, Status status) {
		this(meta, status, null);
	}

	public CloudJob(Meta meta, Status status, ErrorDetails errorDetails) {
		super(meta);
		this.status = status;
		this.errorDetails = errorDetails;
	}

	public Status getStatus() {
		return status;
	}

	public ErrorDetails getErrorDetails() {
		return errorDetails;
	}
}
