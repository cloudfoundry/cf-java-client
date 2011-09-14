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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudfoundry.client.lib.CloudApplication;

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
			
			String uris     = CommonUtils.collectionToCommaDelimitedString(application.getUris());
			String services = CommonUtils.collectionToCommaDelimitedString(application.getServices());

			table.getHeaders().get(COLUMN_5).updateWidth(uris.length());
			tableRow.addValue(COLUMN_5, uris);
			
			table.getHeaders().get(COLUMN_6).updateWidth(services.length());
			tableRow.addValue(COLUMN_6, services);

			table.getRows().add(tableRow);
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

		final String padding = " ";

		final String headerBorder = getHeaderBorder(table.getHeaders());

		final StringBuilder textTable = new StringBuilder(headerBorder);

		for (TableHeader header : table.getHeaders().values()) {
			textTable.append("|" + padding + CommonUtils.padRight(header.getName(), header.getWidth()) + padding);
		}

		textTable.append("|\n");

		textTable.append(headerBorder);

		for (TableRow row : table.getRows()) {
			for (Entry<Integer, TableHeader> entry : table.getHeaders().entrySet()) {
				textTable.append("|" + padding + CommonUtils.padRight(row.getValue(entry.getKey()), entry.getValue().getWidth()) + padding );
			}
			textTable.append("|\n");	
		}

		textTable.append(headerBorder);

		return textTable.toString();
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
			headerBorder.append("+" + CommonUtils.padRight("", header.getWidth() + 2, '-'));
		}
		headerBorder.append("+\n");

		return headerBorder.toString();
	}

}
