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

import java.util.Date;
import java.util.UUID;

/**
 * @author Thomas Risberg
 */
public class CloudEntity {

	private Meta meta;

	private String name;

	public CloudEntity() {
	}

	public CloudEntity(String name) {
		this.name = name;
	}

	public CloudEntity(Meta meta, String name) {
		this.meta = meta;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": (" + meta.getGuid() + ") " + getName();
	}

	public static class Meta {

		private UUID guid;
		private Date created;
		private Date updated;
		private int version;

		public UUID getGuid() {
			return guid;
		}

		public void setGuid(UUID guid) {
			this.guid = guid;
		}

		public Date getCreated() {
			return created;
		}

		public void setCreated(Date created) {
			this.created = created;
		}

		public Date getUpdated() {
			return updated;
		}

		public void setUpdated(Date updated) {
			this.updated = updated;
		}

		public int getVersion() {
			return version;
		}

		public void setVersion(int version) {
			this.version = version;
		}
	}
}
