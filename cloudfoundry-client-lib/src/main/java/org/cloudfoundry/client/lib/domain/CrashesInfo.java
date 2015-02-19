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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CrashesInfo {

	private final List<CrashInfo> crashes;

	public CrashesInfo(List<Map<String, Object>> attributes) {
		List<CrashInfo> crashes = new ArrayList<CrashInfo>(attributes.size());
		for (Map<String, Object> data : attributes) {
			crashes.add(new CrashInfo(data));
		}
		this.crashes = Collections.unmodifiableList(crashes);
	}

	public List<CrashInfo> getCrashes() {
		return crashes;
	}
}
