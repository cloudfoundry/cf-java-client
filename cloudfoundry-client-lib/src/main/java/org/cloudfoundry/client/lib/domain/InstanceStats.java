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

import static org.cloudfoundry.client.lib.util.CloudUtil.parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InstanceStats {

	public static class Usage {

		private double cpu;
		private int disk;
		private int mem;
		private Date time;

		public Usage(Map<String, Object> attributes) {
			this.time = parseDate(parse(String.class, attributes.get("time")));
			this.cpu = parse(Double.class, attributes.get("cpu"));
			this.disk = parse(Integer.class, attributes.get("disk"));
			this.mem = parse(Integer.class, attributes.get("mem"));
		}

		public double getCpu() {
			return cpu;
		}

		public int getDisk() {
			return disk;
		}

		public int getMem() {
			return mem;
		}

		public Date getTime() {
			return time;
		}
	}

	private int cores;
	private long diskQuota;
	private int fdsQuota;
	private String host;
	private String id;
	private long memQuota;
	private String name;
	private int port;
	private InstanceState state;
	private double uptime;
	private List<String> uris;
	private Usage usage;

	@SuppressWarnings("unchecked")
	public InstanceStats(String id, Map<String, Object> attributes) {
		this.id = id;
		String instanceState = parse(String.class, attributes.get("state"));
		this.state = InstanceState.valueOfWithDefault(instanceState);
		Map<String, Object> stats = parse(Map.class, attributes.get("stats"));
		if (stats != null) {
			this.cores = parse(Integer.class, stats.get("cores"));
			this.name = parse(String.class, stats.get("name"));
			Map<String, Object> usageValue = parse(Map.class,
					stats.get("usage"));
			if (usageValue != null) {
				this.usage = new Usage(usageValue);
			}
			this.diskQuota = parse(Long.class, stats.get("disk_quota"));
			this.port = parse(Integer.class, stats.get("port"));
			this.memQuota = parse(Long.class, stats.get("mem_quota"));
			List<String> statsValue = parse(List.class, stats.get("uris"));
			if (statsValue != null) {
				this.uris = Collections.unmodifiableList(statsValue);
			}
			this.fdsQuota = parse(Integer.class, stats.get("fds_quota"));
			this.host = parse(String.class, stats.get("host"));
			this.uptime = parse(Double.class, stats.get("uptime"));
		}
	}

	private static Date parseDate(String date) {
		// dates will be of the form 2011-04-07 09:11:50 +0000
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ZZZZZ").parse(date);
		}
		catch (ParseException e) {
			// TODO - not sure how best to handle this error
			return null;
		}
	}

	public int getCores() {
		return cores;
	}

	public long getDiskQuota() {
		return diskQuota;
	}

	public int getFdsQuota() {
		return fdsQuota;
	}

	public String getHost() {
		return host;
	}

	public String getId() {
		return id;
	}

	public long getMemQuota() {
		return memQuota;
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}

	public InstanceState getState() {
		return state;
	}

	public double getUptime() {
		return uptime;
	}

	public List<String> getUris() {
		return uris;
	}

	public Usage getUsage() {
		return usage;
	}
}
