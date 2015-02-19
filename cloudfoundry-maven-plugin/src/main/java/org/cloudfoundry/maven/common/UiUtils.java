/*
 * Copyright 2009-2013 the original author or authors.
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
package org.cloudfoundry.maven.common;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.ApplicationLog;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudServicePlan;
import org.cloudfoundry.client.lib.domain.InstanceStats;

/**
 * Contains utility methods for rendering data to a formatted console output.
 * E.g. it provides helper methods for rendering ASCII-based data tables.
 *
 * @author Gunnar Hillert
 * @author Scott Frederick
 * @since 1.0.0
 *
 */
public final class UiUtils {
	public static final String HORIZONTAL_LINE = "-------------------------------------------------------------------------------\n";

	public static final int COLUMN_1 = 1;
	public static final int COLUMN_2 = 2;
	public static final int COLUMN_3 = 3;
	public static final int COLUMN_4 = 4;
	public static final int COLUMN_5 = 5;
	public static final int COLUMN_6 = 6;

	/**
	 * Prevent instantiation.
	 *
	 */
	private UiUtils() {
		throw new AssertionError();
	}

	/**
	 * Renders a textual representation of a Application {@link CloudApplication}
	 *
	 * <ul>
	 *     <li>Names of the Applications</li>
	 *     <li>Number of Instances</li>
	 *     <li>Current State (Health)</li>
	 *     <li>Used Memory</li>
	 *     <li>The comma-separated list of Uris</li>
	 *     <li>The comma-separated list of Services</li>
	 * <ul>
	 */
	public static String renderCloudApplicationDataAsTable(CloudApplication application, ApplicationStats stats) {
		StringBuilder sb = new StringBuilder("\n");

		sb.append(String.format("application: %s\n", application.getName()));
		sb.append(String.format("state: %s\n", application.getState()));
		sb.append(String.format("instances: %d/%d\n", application.getRunningInstances(), application.getInstances()));
		sb.append(String.format("usage: %s x %s instance\n", formatMBytes(application.getMemory()), application.getInstances()));
		sb.append(String.format("urls: %s\n", CommonUtils.collectionToCommaDelimitedString(application.getUris())));
		sb.append(String.format("services: %s\n", CommonUtils.collectionToCommaDelimitedString(application.getServices())));

		Table table = new Table();

		table.getHeaders().put(COLUMN_1, new TableHeader("instance"));
		table.getHeaders().put(COLUMN_2, new TableHeader("state"));
		table.getHeaders().put(COLUMN_3, new TableHeader("cpu"));
		table.getHeaders().put(COLUMN_4, new TableHeader("memory"));
		table.getHeaders().put(COLUMN_5, new TableHeader("disk"));

		for (InstanceStats instance : stats.getRecords()) {
			TableRow tableRow = new TableRow();

			String index = instance.getId();
			table.getHeaders().get(COLUMN_1).updateWidth(String.valueOf(index).length());
			tableRow.addValue(COLUMN_1, String.valueOf(index));

			String state = instance.getState().toString().toLowerCase();
			table.getHeaders().get(COLUMN_2).updateWidth(String.valueOf(state).length());
			tableRow.addValue(COLUMN_2, String.valueOf(state));

			String cpu = String.format("%.2f%%", instance.getUsage().getCpu() * 100);
			table.getHeaders().get(COLUMN_3).updateWidth(String.valueOf(cpu).length());
			tableRow.addValue(COLUMN_3, String.valueOf(cpu));

			String memory = String.format("%s of %s",
					formatBytes(instance.getUsage().getMem()),
					formatBytes(instance.getMemQuota()));
			table.getHeaders().get(COLUMN_4).updateWidth(String.valueOf(memory).length());
			tableRow.addValue(COLUMN_4, String.valueOf(memory));

			String disk = String.format("%s of %s",
					formatBytes(instance.getUsage().getDisk()),
					formatBytes(instance.getDiskQuota()));
			table.getHeaders().get(COLUMN_5).updateWidth(String.valueOf(disk).length());
			tableRow.addValue(COLUMN_5, String.valueOf(disk));

			table.getRows().add(tableRow);
		}

		sb.append("\n").append(renderTextTable(table));

		return sb.toString();
	}

	/**
	 * Renders a textual representation of the list of provided {@link CloudApplication}
	 *
	 * The following information is shown:
	 *
	 * <ul>
	 *     <li>Names of the Applications</li>
	 *     <li>Number of Instances</li>
	 *     <li>Current State (Health)</li>
	 *     <li>Used Memory</li>
	 *     <li>The comma-separated list of Uris</li>
	 *     <li>The comma-separated list of Services</li>
	 * <ul>
	 *
	 * @param applications List of {@CloudApplication}
	 * @return The rendered table representation as String
	 *
	 */
	public static String renderCloudApplicationsDataAsTable(List<CloudApplication> applications) {

		Table table = new Table();

		table.getHeaders().put(COLUMN_1, new TableHeader("name"));
		table.getHeaders().put(COLUMN_2, new TableHeader("status"));
		table.getHeaders().put(COLUMN_3, new TableHeader("instances"));
		table.getHeaders().put(COLUMN_4, new TableHeader("memory"));
		table.getHeaders().put(COLUMN_5, new TableHeader("disk"));
		table.getHeaders().put(COLUMN_6, new TableHeader("url"));

		Comparator<CloudApplication> nameComparator = new Comparator<CloudApplication>() {
			public int compare(CloudApplication a, CloudApplication b) {
				return a.getName().compareTo(b.getName());
			}
		};

		Collections.sort(applications, nameComparator);

		for (CloudApplication application : applications) {

			TableRow tableRow = new TableRow();

			table.getHeaders().get(COLUMN_1).updateWidth(application.getName().length());
			tableRow.addValue(COLUMN_1, application.getName());

			String status = renderHealthStatus(application);
			table.getHeaders().get(COLUMN_2).updateWidth(status.length());
			tableRow.addValue(COLUMN_2, status);

			String instances = String.format("%d/%d", application.getRunningInstances(), application.getInstances());
			table.getHeaders().get(COLUMN_3).updateWidth(String.valueOf(instances).length());
			tableRow.addValue(COLUMN_3, String.valueOf(instances));

			String memory = formatMBytes(application.getMemory());
			table.getHeaders().get(COLUMN_4).updateWidth(String.valueOf(memory).length());
			tableRow.addValue(COLUMN_4, String.valueOf(memory));

			String disk = formatMBytes(application.getDiskQuota());
			table.getHeaders().get(COLUMN_5).updateWidth(String.valueOf(disk).length());
			tableRow.addValue(COLUMN_5, String.valueOf(disk));

			String uris = CommonUtils.collectionToCommaDelimitedString(application.getUris());

			table.getHeaders().get(COLUMN_6).updateWidth(uris.length());
			tableRow.addValue(COLUMN_6, uris);

			table.getRows().add(tableRow);
		}

		return renderTextTable(table);
	}

	private static String renderHealthStatus(CloudApplication app) {
		String state = app.getState().toString();

		if (state.equals("STARTED")) {
			int running_instances = app.getRunningInstances();
			int expected_instances = app.getInstances();

			if (expected_instances > 0) {
				float ratio = running_instances / expected_instances;
				if (ratio == 1.0)
					return "running";
				else
					return new Float((ratio * 100)).intValue() + "%";
			} else {
				return "n/a";
			}
		} else {
			return state.toLowerCase();
		}
	}

	/**
	 * Renders a textual representation of a application's environment variables
	 */
	public static String renderEnvVarDataAsTable(CloudApplication application) {
		StringBuilder sb = new StringBuilder("\n");

		sb.append(String.format("Environment for application '%s'\n", application.getName()));
		final List<String> envVars = application.getEnv();
		for (String envVar : envVars) {
			sb.append(envVar).append("\n");
		}

		return sb.toString();
	}

	/**
	 * Renders a sorted textual representation of the list of provided {@link CloudFoundryClient, @link CloudServiceOffering}
	 *
	 * @param serviceOfferings
	 * @return The rendered table representation as String
	 *
	 */
	public static String renderServiceOfferingDataAsTable(List<CloudServiceOffering> serviceOfferings) {
		Comparator<CloudServiceOffering> labelComparator = new Comparator<CloudServiceOffering>() {
			public int compare(CloudServiceOffering a, CloudServiceOffering b) {
				return a.getLabel().compareTo(b.getLabel());
			}
		};
		Collections.sort(serviceOfferings, labelComparator);

		Table table = new Table();
		table.getHeaders().put(COLUMN_1, new TableHeader("service"));
		table.getHeaders().put(COLUMN_2, new TableHeader("plans"));
		table.getHeaders().put(COLUMN_3, new TableHeader("description"));

		List<String> CloudServicePlanNames;

		for (CloudServiceOffering serviceOffering : serviceOfferings) {
			TableRow tableRow = new TableRow();

			table.getHeaders().get(COLUMN_1).updateWidth(serviceOffering.getLabel().length());
			tableRow.addValue(COLUMN_1, serviceOffering.getLabel());

			CloudServicePlanNames = new ArrayList<String>();
			for (CloudServicePlan servicePlan : serviceOffering.getCloudServicePlans()) {
				CloudServicePlanNames.add(servicePlan.getName());
			}
			table.getHeaders().get(COLUMN_2).updateWidth(CloudServicePlanNames.toString().length() - 1);
			tableRow.addValue(COLUMN_2, CloudServicePlanNames.toString().substring(1, CloudServicePlanNames.toString().length() - 1));

			table.getHeaders().get(COLUMN_3).updateWidth(serviceOffering.getDescription().length());
			tableRow.addValue(COLUMN_3, serviceOffering.getDescription());

			table.getRows().add(tableRow);
		}

		return renderTextTable(table);
	}

	/**
	 * Renders a sorted textual representation of the list of provided {@link CloudService}
	 *
	 * The following information is shown:
	 *
	 *
	 * @param services
	 * @param servicesToApps
	 * @return The rendered table representation as String
	 *
	 */
	public static String renderServiceDataAsTable(List<CloudService> services, Map<String, List<String>> servicesToApps) {
		Comparator<CloudService> nameComparator = new Comparator<CloudService>() {
			public int compare(CloudService a, CloudService b) {
				return a.getName().compareTo(b.getName());
			}
		};
		Collections.sort(services, nameComparator);

		Table table = new Table();
		table.getHeaders().put(COLUMN_1, new TableHeader("name"));
		table.getHeaders().put(COLUMN_2, new TableHeader("service"));
		table.getHeaders().put(COLUMN_3, new TableHeader("plan"));
		table.getHeaders().put(COLUMN_4, new TableHeader("bound apps"));

		for (CloudService service : services) {
			TableRow tableRow = new TableRow();

			String name = service.getName();

			String label;
			String plan;
			if (service.isUserProvided()) {
				label = "user-provided";
				plan = "";
			} else {
				label = service.getLabel();
				plan = service.getPlan();
			}

			table.getHeaders().get(COLUMN_1).updateWidth(name.length());
			tableRow.addValue(COLUMN_1, name);

			table.getHeaders().get(COLUMN_2).updateWidth(label.length());
			tableRow.addValue(COLUMN_2, label);

			table.getHeaders().get(COLUMN_3).updateWidth(plan.length());
			tableRow.addValue(COLUMN_3, plan);

			final List<String> appNames = servicesToApps.get(name);
			final String appNamesString = CommonUtils.collectionToCommaDelimitedString(appNames);
			table.getHeaders().get(COLUMN_4).updateWidth(appNamesString.length());
			tableRow.addValue(COLUMN_4, appNamesString);

			table.getRows().add(tableRow);
		}
		return renderTextTable(table);
	}

	/**
	 * Renders a textual representation of provided parameter map.
	 *
	 * @param parameters Map of parameters (key, value)
	 * @return The rendered table representation as String
	 *
	 */
	public static String renderParameterInfoDataAsTable(Map<String, String> parameters) {
		final Table table = new Table();

		table.getHeaders().put(COLUMN_1, new TableHeader("Parameter"));
		table.getHeaders().put(COLUMN_2, new TableHeader("Value (Configured or Default)"));

		for (Entry<String, String> entry : parameters.entrySet()) {

			final TableRow tableRow = new TableRow();

			table.getHeaders().get(COLUMN_1).updateWidth(entry.getKey().length());
			tableRow.addValue(COLUMN_1, entry.getKey());

			table.getHeaders().get(COLUMN_2).updateWidth(entry.getValue() != null ? entry.getValue().length() : 0);
			tableRow.addValue(COLUMN_2, entry.getValue());

			table.getRows().add(tableRow);
		}

		return renderTextTable(table);
	}

	/**
	 * Renders a textual representation of the provided {@link Table}
	 *
	 * @param table Table data {@link Table}
	 * @return The rendered table representation as String
	 */
	public static String renderTextTable(Table table) {
		final String padding = "  ";
		final String headerBorder = getHeaderBorder(table.getHeaders());
		final StringBuilder textTable = new StringBuilder();

		for (TableHeader header : table.getHeaders().values()) {
			textTable.append(padding + CommonUtils.padRight(header.getName(), header.getWidth()));
		}

		textTable.append("\n");

		textTable.append(headerBorder);

		for (TableRow row : table.getRows()) {
			for (Entry<Integer, TableHeader> entry : table.getHeaders().entrySet()) {
				textTable.append(padding + CommonUtils.padRight(row.getValue(entry.getKey()), entry.getValue().getWidth()));
			}
			textTable.append("\n");
		}

		return textTable.toString();
	}

	/**
	 * Renders the help text. If the callers is logged in successfully the full
	 * information is rendered if not only basic Cloud Foundry information is
	 * rendered and returned as String.
	 *
	 *
	 *
	 * @param cloudInfo Contains the information about the Cloud Foundry environment
	 * @param target The target Url from which the information was obtained
	 * @param org
	 * @param space
	 *  @return Returns a formatted String for console output
	 */
	public static String renderCloudInfoFormattedAsString(CloudInfo cloudInfo, String target, String org, String space) {

		final String cloudInfoMessage = "\n" +
		UiUtils.HORIZONTAL_LINE +
		String.format("API endpoint: %s (API version: %s) \n", target, cloudInfo.getVersion()) +
		String.format("user:         %s\n", cloudInfo.getUser()) +
		String.format("org:          %s\n", org) +
		String.format("space:        %s\n", space) +
		UiUtils.HORIZONTAL_LINE;

		return cloudInfoMessage;
	}

	/**
	 * Renders a line of application logging output.
	 */
	public static String renderApplicationLogEntry(ApplicationLog logEntry) {
		StringBuilder logLine = new StringBuilder();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		logLine.append(dateFormat.format(logEntry.getTimestamp())).append(" ");

		String source;
		if (logEntry.getSourceName().equals("App")) {
			source = String.format("[%s/%s]", logEntry.getSourceName(), logEntry.getSourceId());
		} else {
			source = String.format("[%s]", logEntry.getSourceName());
		}
		logLine.append(String.format("%-10s", source));

		logLine.append(logEntry.getMessageType().name().substring("STD".length())).append(" ");

		logLine.append(logEntry.getMessage());

		return logLine.toString();
	}

	/**
	 * Renders the Table header border, based on the map of provided headers.
	 *
	 * @param headers Map of headers containing meta information e.g. name+width of header
	 * @return Returns the rendered header border as String
	 */
	public static String getHeaderBorder(Map<Integer, TableHeader> headers) {

		final StringBuilder headerBorder = new StringBuilder();

		for (TableHeader header : headers.values()) {
			headerBorder.append(CommonUtils.padRight("  ", header.getWidth() + 2, '-'));
		}
		headerBorder.append("\n");

		return headerBorder.toString();
	}

	public static String formatMBytes(int size) {
		int g = size / 1024;

		DecimalFormat dec = new DecimalFormat("0");

		if (g > 1) {
			return dec.format(g).concat("G");
		} else {
			return dec.format(size).concat("M");
		}
	}

	public static String formatBytes(double size) {
		double k = size / 1024.0;
		double m = k / 1024.0;
		double g = m / 1024.0;

		DecimalFormat dec = new DecimalFormat("0");

		if (g > 1) {
			return dec.format(g).concat("G");
		} else if (m > 1) {
			return dec.format(m).concat("M");
		} else if (k > 1) {
			return dec.format(k).concat("K");
		} else {
			return dec.format(size).concat("B");
		}
	}
}