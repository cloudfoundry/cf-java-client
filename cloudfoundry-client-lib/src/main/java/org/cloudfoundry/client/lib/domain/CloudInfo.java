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

import org.cloudfoundry.client.lib.util.CloudUtil;

import java.util.Map;

/**
 * @author Ramnivas Laddad
 * @author Dave Syer
 * @author Thomas Risberg
 */
@SuppressWarnings("unused")
public class CloudInfo {

	private Limits limits;
	private Usage usage;
	private String name;
	private String support;
	private String build;
	private String version;
	private String user;
	private String description;
	private String authorizationEndpoint;
	private boolean allowDebug;
    private String loggregatorEndpoint;

	@SuppressWarnings("unchecked")
	public CloudInfo(Map<String, Object> infoMap) {
		name = CloudUtil.parse(String.class, infoMap.get("name"));
		support = CloudUtil.parse(String.class, infoMap.get("support"));
		build = CloudUtil.parse(String.class, infoMap.get("build"));
		version = CloudUtil.parse(String.class, infoMap.get("version"));
		if (version == null) {
			Number iVersion = CloudUtil.parse(Number.class, infoMap.get("version"));
			if (iVersion != null) {
				version = iVersion.toString();
			}
		}
		user = CloudUtil.parse(String.class, infoMap.get("user"));
		description = CloudUtil.parse(String.class, infoMap.get("description"));
		authorizationEndpoint = CloudUtil.parse(String.class, infoMap.get("authorization_endpoint"));
		loggregatorEndpoint = CloudUtil.parse(String.class, infoMap.get("logging_endpoint"));

		Object allowDebugValue = infoMap.get("allow_debug");
		if (allowDebugValue != null) {
			allowDebug = CloudUtil.parse(Boolean.class, allowDebugValue);
		} else {
			allowDebug = false; // default to false
		}

		Map<String, Object> limitsMap = CloudUtil.parse(Map.class, infoMap.get("limits"));
		if (limitsMap != null) {
			limits = new Limits(limitsMap);
		} else {
			limits = new Limits();
		}

		Map<String, Object> usageMap = CloudUtil.parse(Map.class, infoMap.get("usage"));
		if (usageMap != null) {
			usage = new Usage(usageMap);
		} else {
			usage = new Usage();
		}
	}

	public CloudInfo(String name, String support, String authorizationEndpoint, String build, String version,
			String user, String description, Limits limits, Usage usage, boolean allowDebug, String loggregatorEndpoint) {
		this.name = name;
		this.support = support;
		this.authorizationEndpoint = authorizationEndpoint;
        this.loggregatorEndpoint = loggregatorEndpoint;
		this.build = build;
		this.version = version;
		this.user = user;
		this.description = description;
		this.limits = limits;
		this.usage = usage;
		this.allowDebug = allowDebug;
	}

	public Limits getLimits() {
		return limits;
	}

	public Usage getUsage() {
		return usage;
	}

	public String getName() {
		return name;
	}

	public String getSupport() {
		return support;
	}

	public String getAuthorizationEndpoint() {
		return authorizationEndpoint;
	}
	
	public String getLoggregatorEndpoint() {
	    return loggregatorEndpoint;
	}

	public String getBuild() {
		return build;
	}

	public String getDescription() {
		return description;
	}

	public String getUser() {
		return user;
	}

	public String getVersion() {
		return version;
	}

	public boolean getAllowDebug() {
		return allowDebug;
	}
	public static class Limits {
		private int maxApps;
		private int maxTotalMemory;
		private int maxUrisPerApp;
		private int maxServices;

		public Limits(Map<String, Object> limitMap) {
			maxApps = CloudUtil.parse(Integer.class, limitMap.get("apps"));
			maxTotalMemory = CloudUtil.parse(Integer.class, limitMap.get("memory"));
			maxUrisPerApp = CloudUtil.parse(Integer.class, limitMap.get("app_uris"));
			maxServices = CloudUtil.parse(Integer.class, limitMap.get("services"));
		}

		Limits() {
			maxApps = Integer.MAX_VALUE;
			maxTotalMemory = Integer.MAX_VALUE;
			maxUrisPerApp = Integer.MAX_VALUE;
			maxServices = Integer.MAX_VALUE;
		}

		public int getMaxApps() {
			return maxApps;
		}

		public int getMaxTotalMemory() {
			return maxTotalMemory;
		}

		public int getMaxUrisPerApp() {
			return maxUrisPerApp;
		}

		public int getMaxServices() {
			return maxServices;
		}
	}

	public static class Usage {
		private int apps;
		private int totalMemory;
		private int urisPerApp;
		private int services;

		public Usage(Map<String, Object> data) {
			if (data != null && !data.isEmpty()) {
				apps = CloudUtil.parse(Integer.class, data.get("apps"));
				totalMemory = CloudUtil.parse(Integer.class, data.get("memory"));
				urisPerApp = CloudUtil.parse(Integer.class, data.get("app_uris"));
				services = CloudUtil.parse(Integer.class, data.get("services"));
			}
		}

		Usage() {
			apps = Integer.MAX_VALUE;
			totalMemory = Integer.MAX_VALUE;
			urisPerApp = Integer.MAX_VALUE;
			services = Integer.MAX_VALUE;
		}

		public int getApps() {
			return apps;
		}

		public int getTotalMemory() {
			return totalMemory;
		}

		public int getUrisPerApp() {
			return urisPerApp;
		}

		public int getServices() {
			return services;
		}
	}
}
