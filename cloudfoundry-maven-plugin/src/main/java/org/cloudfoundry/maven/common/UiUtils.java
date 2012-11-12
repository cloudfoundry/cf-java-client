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
package org.cloudfoundry.maven.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServicePlan;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;

/**
 * Contains utility methods for rendering data to a formatted console output.
 * E.g. it provides helper methods for rendering ASCII-based data tables.
 *
 * @author Gunnar Hillert
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
	 * @param table List of {@CloudApplication}
	 * @return The rendered table representation as String
	 *
	 */
	public static String renderCloudApplicationDataAsTable(List<CloudApplication> applications) {

		Table table = new Table();

		table.getHeaders().put(COLUMN_1, new TableHeader("Application"));
		table.getHeaders().put(COLUMN_2, new TableHeader("#"));
		table.getHeaders().put(COLUMN_3, new TableHeader("Health"));
		table.getHeaders().put(COLUMN_4, new TableHeader("Memory"));
		table.getHeaders().put(COLUMN_5, new TableHeader("URLS"));
		table.getHeaders().put(COLUMN_6, new TableHeader("Services"));

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

			table.getHeaders().get(COLUMN_2).updateWidth(String.valueOf(application.getInstances()).length());
			tableRow.addValue(COLUMN_2, String.valueOf(application.getInstances()));

			table.getHeaders().get(COLUMN_3).updateWidth(application.getState().toString().length());
			tableRow.addValue(COLUMN_3, application.getState().toString());

			table.getHeaders().get(COLUMN_4).updateWidth(String.valueOf(application.getMemory()).length());
			tableRow.addValue(COLUMN_4, String.valueOf(application.getMemory()));

			String uris = CommonUtils.collectionToCommaDelimitedString(application.getUris());
			String services = CommonUtils.collectionToCommaDelimitedString(application.getServices());

			table.getHeaders().get(COLUMN_5).updateWidth(uris.length());
			tableRow.addValue(COLUMN_5, uris);

			tableRow.addValue(COLUMN_6, services);

			table.getRows().add(tableRow);
		}

		return renderTextTable(table);
	}

	/**
	 * Renders a sorted textual representation of the list of provided {@link CloudFoundryClient, @link ServiceConfigurations}
	 *
	 * The following information is shown:
	 *
	 * For CC V1
	 * <ul>
	 *	<li>Service Vendor</li>
	 *	<li>Service Version</li>
	 *	<li>Service Description</li>
	 * <ul>
	 *
	 *For CC V2
	 * <ul>
	 *	<li>Service Label</li>
	 *	<li>Service Version</li>
	 *	<li>Service Provider</li>
	 *	<li>Service Plans</li>
	 *	<li>Service Description</li>
	 * <ul>
	 * @param client
	 * @param serviceConfigurations
	 * @return The rendered table representation as String
	 *
	 */
	public static String renderServiceConfigurationDataAsTable(final CloudFoundryClient client, List<ServiceConfiguration> serviceConfigurations) {
		Comparator<ServiceConfiguration> labelComparator = new Comparator<ServiceConfiguration>() {
			public int compare(ServiceConfiguration a, ServiceConfiguration b) {
				if (client.getCloudInfo().getCloudControllerMajorVersion() == CloudInfo.CC_MAJOR_VERSION.V2) {
					return a.getCloudServiceOffering().getLabel().compareTo(b.getCloudServiceOffering().getLabel());
				} else {
					return a.getVendor().compareTo(b.getVendor());
				}
			}
		};
		Collections.sort(serviceConfigurations, labelComparator);

		Table table = new Table();
		table.getHeaders().put(COLUMN_1, new TableHeader("Service"));
		table.getHeaders().put(COLUMN_2, new TableHeader("Version"));

		if (client.getCloudInfo().getCloudControllerMajorVersion() == CloudInfo.CC_MAJOR_VERSION.V2) {
			table.getHeaders().put(COLUMN_3, new TableHeader("Provider"));
			table.getHeaders().put(COLUMN_4, new TableHeader("Plans"));
			table.getHeaders().put(COLUMN_5, new TableHeader("Description"));
			List<String> CloudServicePlanNames;

			for (ServiceConfiguration serviceConfiguration : serviceConfigurations) {
				TableRow tableRow = new TableRow();

				table.getHeaders().get(COLUMN_1).updateWidth(serviceConfiguration.getCloudServiceOffering().getLabel().length());
				tableRow.addValue(COLUMN_1, serviceConfiguration.getCloudServiceOffering().getLabel());

				table.getHeaders().get(COLUMN_2).updateWidth(serviceConfiguration.getCloudServiceOffering().getVersion().length());
				tableRow.addValue(COLUMN_2, serviceConfiguration.getCloudServiceOffering().getVersion());

				table.getHeaders().get(COLUMN_3).updateWidth(serviceConfiguration.getCloudServiceOffering().getProvider().length());
				tableRow.addValue(COLUMN_3, serviceConfiguration.getCloudServiceOffering().getProvider());

				CloudServicePlanNames = new ArrayList<String>();
				for (CloudServicePlan servicePlan : serviceConfiguration.getCloudServiceOffering().getCloudServicePlans()) {
					CloudServicePlanNames.add(servicePlan.getName());
				}
				table.getHeaders().get(COLUMN_4).updateWidth(CloudServicePlanNames.toString().length() - 1);
				tableRow.addValue(COLUMN_4, CloudServicePlanNames.toString().substring(1, CloudServicePlanNames.toString().length() - 1));

				table.getHeaders().get(COLUMN_5).updateWidth(serviceConfiguration.getCloudServiceOffering().getDescription().length());
				tableRow.addValue(COLUMN_5, serviceConfiguration.getCloudServiceOffering().getDescription());

				table.getRows().add(tableRow);
			}
		} else {
			table.getHeaders().put(COLUMN_3, new TableHeader("Description"));

			for (ServiceConfiguration serviceConfiguration : serviceConfigurations) {
				TableRow tableRow = new TableRow();

				table.getHeaders().get(COLUMN_1).updateWidth(serviceConfiguration.getVendor().length());
				tableRow.addValue(COLUMN_1, serviceConfiguration.getVendor());

				table.getHeaders().get(COLUMN_2).updateWidth(serviceConfiguration.getVersion().length());
				tableRow.addValue(COLUMN_2, serviceConfiguration.getVersion());

				table.getHeaders().get(COLUMN_3).updateWidth(serviceConfiguration.getDescription().length());
				tableRow.addValue(COLUMN_3, serviceConfiguration.getDescription());

				table.getRows().add(tableRow);
			}
		}

		return renderTextTable(table);
	}

	/**
	 * Renders a sorted textual representation of the list of provided {@link CloudService}
	 *
	 * The following information is shown:
	 *
	 *For CC V1
	 * <ul>
	 *	<li>Service Name</li>
	 *	<li>Service Vendor</li>
	 * <ul>
	 *
	 *For CC V2
	 * <ul>
	 *	<li>Service Name</li>
	 *	<li>Service Label</li>
	 *	<li>Service Version</li>
	 *	<li>Service Plan</li>
	 * <ul>
	 *
	 * @param client
	 * @param services
	 * @return The rendered table representation as String
	 *
	 */
	public static String renderServiceDataAsTable(CloudFoundryClient client, List<CloudService> services) {
		Comparator<CloudService> nameComparator = new Comparator<CloudService>() {
			public int compare(CloudService a, CloudService b) {
				return a.getName().compareTo(b.getName());
			}
		};
		Collections.sort(services, nameComparator);

		Table table = new Table();
		table.getHeaders().put(COLUMN_1, new TableHeader("Name"));
		table.getHeaders().put(COLUMN_2, new TableHeader("Service"));

		if (client.getCloudInfo().getCloudControllerMajorVersion() == CloudInfo.CC_MAJOR_VERSION.V2) {
			table.getHeaders().put(COLUMN_3, new TableHeader("Version"));
			table.getHeaders().put(COLUMN_4, new TableHeader("Plan"));

			for (CloudService service : services) {
				TableRow tableRow = new TableRow();

				table.getHeaders().get(COLUMN_1).updateWidth(service.getName().length());
				tableRow.addValue(COLUMN_1, service.getName());

				table.getHeaders().get(COLUMN_2).updateWidth(service.getLabel().length());
				tableRow.addValue(COLUMN_2, service.getLabel());

				table.getHeaders().get(COLUMN_3).updateWidth(service.getVersion().length());
				tableRow.addValue(COLUMN_3, service.getVersion());

				table.getHeaders().get(COLUMN_4).updateWidth(service.getPlan().length());
				tableRow.addValue(COLUMN_4, service.getPlan());

				table.getRows().add(tableRow);
			}
		} else {
			for (CloudService service : services) {
				TableRow tableRow = new TableRow();

				table.getHeaders().get(COLUMN_1).updateWidth(service.getName().length());
				tableRow.addValue(COLUMN_1, service.getName());

				table.getHeaders().get(COLUMN_2).updateWidth(service.getVendor().length());
				tableRow.addValue(COLUMN_2, service.getVendor());

				table.getRows().add(tableRow);
			}
		}

		return renderTextTable(table);
	}

	/**
	 * Renders a textual representation of provided parameter map.
	 *
	 * @param table Map of parameters (key, value)
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
		final StringBuilder textTable = new StringBuilder();

		for (TableHeader header : table.getHeaders().values()) {
			textTable.append(padding + CommonUtils.padRight(header.getName(), header.getWidth()));
		}

		textTable.append("\n");

		for (TableRow row : table.getRows()) {
			for (Entry<Integer, TableHeader> entry : table.getHeaders().entrySet()) {
				textTable.append(padding + CommonUtils.padRight(row.getValue(entry.getKey()), entry.getValue().getWidth()));
			}
			textTable.append("\n");
		}

		return textTable.toString();
	}
}