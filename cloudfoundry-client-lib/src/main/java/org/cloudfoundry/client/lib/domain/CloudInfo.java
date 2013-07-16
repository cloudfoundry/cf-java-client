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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ramnivas Laddad
 * @author Dave Syer
 * @author Thomas Risberg
 */
@SuppressWarnings("unused")
public class CloudInfo {

	public enum CC_MAJOR_VERSION {
		UNKNOWN,
		V1,
		V2
	}

	private Limits limits;
	private Usage usage;
	private String name;
	private String support;
	private Integer build;
	private String version;
	private String user;
	private String description;
	private String authorizationEndpoint;
	private boolean allowDebug;
	private Collection<Framework> frameworks = new ArrayList<Framework>();
	private Map<String, Runtime> runtimes = new HashMap<String, CloudInfo.Runtime>();

	@SuppressWarnings("unchecked")
	public CloudInfo(Map<String, Object> infoMap) {
		name = CloudUtil.parse(String.class, infoMap.get("name"));
		support = CloudUtil.parse(String.class, infoMap.get("support"));
		build = CloudUtil.parse(Integer.class, infoMap.get("build"));
		version = CloudUtil.parse(String.class, infoMap.get("version"));
		if (version == null) {
			// could this be V2?
			Number iVersion = CloudUtil.parse(Number.class, infoMap.get("version"));
			if (iVersion != null) {
				version = iVersion.toString();
			}
		}
		user = CloudUtil.parse(String.class, infoMap.get("user"));
		description = CloudUtil.parse(String.class, infoMap.get("description"));
		authorizationEndpoint = CloudUtil.parse(String.class, infoMap.get("authorization_endpoint"));

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

		Map<String, Object> frameworksMap = CloudUtil.parse(Map.class, infoMap.get("frameworks"));
		if (frameworksMap != null) {
			for (Map.Entry<String, Object> entry : frameworksMap.entrySet()) {
				Framework framework = new Framework((Map<String, Object>)entry.getValue());
				frameworks.add(framework);
				for (Runtime runtime : framework.runtimes) {
					if (!runtimes.containsKey(runtime.getName())) {
						runtimes.put(runtime.getName(), runtime);
					}
				}
			}
		}
	}

	public CloudInfo(String name, String support, String authorizationEndpoint, int build, String version,
			String user, String description, Limits limits, Usage usage, boolean allowDebug) {
		this(name, support, authorizationEndpoint, build, version,
				user, description, limits, usage, allowDebug, null, null);
	}

	public CloudInfo(String name, String support, String authorizationEndpoint, int build, String version,
			String user, String description, Limits limits, Usage usage, boolean allowDebug,
			Collection<Framework> frameworks, Map<String, Runtime> runtimes) {
		this.name = name;
		this.support = support;
		this.authorizationEndpoint = authorizationEndpoint;
		this.build = build;
		this.version = version;
		this.user = user;
		this.description = description;
		this.limits = limits;
		this.usage = usage;
		this.allowDebug = allowDebug;
		if(frameworks != null) {
			this.frameworks.addAll(frameworks);
		}
		if (runtimes != null) {
			this.runtimes.putAll(runtimes);
		}
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

	public Integer getBuild() {
		return build;
	}

	public String getDescription() {
		return description;
	}

	public String getUser() {
		return user;
	}

	/**
	 * Get Cloud Controller major version (V1, V2) for the current cloud.
	 *
	 * @return CloudInfo.CC_MAJOR_VERSION generation enum
	 */
	public CloudInfo.CC_MAJOR_VERSION getCloudControllerMajorVersion() {
		int majorVersion = 0;
		try {
			Number decVersion = new BigDecimal(getVersion());
			if (decVersion.doubleValue() > 0 && decVersion.doubleValue() < 2) {
				majorVersion = 1;
			}
			else {
				majorVersion = decVersion.intValue();
			}
		} catch (NumberFormatException ignore) {}
		if (majorVersion == 0) {
			return CC_MAJOR_VERSION.UNKNOWN;
		}
		if (majorVersion < 2) {
			return CC_MAJOR_VERSION.V1;
		}
		return CC_MAJOR_VERSION.V2;
	}

	public String getVersion() {
		return version;
	}

	public boolean getAllowDebug() {
		return allowDebug;
	}

	public Collection<Framework> getFrameworks() {
		return Collections.unmodifiableCollection(frameworks);
	}

	public Collection<Runtime> getRuntimes() {
		return Collections.unmodifiableCollection(runtimes.values());
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
            if(data != null && !data.isEmpty()) {
                apps = CloudUtil.parse(Integer.class, data.get("apps"));
                totalMemory = CloudUtil.parse(Integer.class,  data.get("memory"));
                urisPerApp = CloudUtil.parse(Integer.class,  data.get("app_uris"));
                services = CloudUtil.parse(Integer.class,  data.get("services"));
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

	public static class Runtime {
		private String name;
		private String version;
		private String description;

		public Runtime(Map<String, Object> data) {
			name = CloudUtil.parse(String.class,  data.get("name"));
			description = CloudUtil.parse(String.class,  data.get("description"));
			// The way Jackson will parse, the version will be a Double with simpler versions like "1.6" with one . but String otherwise
			if (data.containsKey("version")) {
				version = CloudUtil.parse(Object.class,  data.get("version")).toString();
			}
			else {
				version = "?";
			}
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getVersion() {
			return version;
		}
	}

	public static class Framework {
		private String name;
		private List<Runtime> runtimes = new ArrayList<Runtime>();

		@SuppressWarnings("unchecked")
		public Framework(Map<String, Object> data) {
			name = CloudUtil.parse(String.class, data.get("name"));
			List<Map<String, Object>> runtimeData = CloudUtil.parse(List.class, data.get("runtimes"));
			if (runtimeData != null) {
				for (Map<String, Object> runtime : runtimeData) {
					runtimes.add(new Runtime(runtime));
				}
			}
		}

		public Framework(Map<String, Object> data, List<Runtime> runtimes) {
			name = CloudUtil.parse(String.class, data.get("name"));
			if (runtimes != null) {
				this.runtimes.addAll(runtimes);
			}
		}

		public String getName() {
			return name;
		}

		public List<Runtime> getRuntimes() {
			return Collections.unmodifiableList(runtimes);
		}
	}
}
