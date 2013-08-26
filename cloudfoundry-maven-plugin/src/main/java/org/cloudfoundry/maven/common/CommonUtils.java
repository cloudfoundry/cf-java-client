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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.springframework.util.StringUtils;

/**
 * Contains common non-ui related helper methods for the Cloud Foundry Maven
 * Plugin.
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 * @author Scott Frederick
 *
 * @since 1.0.0
 */
public final class CommonUtils {

	/**
	 * Prevent instantiation.
	 */
	private CommonUtils() {
		throw new AssertionError();
	}

	/**
	 * Right-pad a String with a configurable padding character.
	 *
	 * @param string      The String to pad
	 * @param size        Pad String by the number of characters.
	 * @param paddingChar The character to pad the String with.
	 * @return The padded String. If the provided String is null, an empty String is returned.
	 */
	public static String padRight(String string, int size, char paddingChar) {

		if (string == null) {
			return "";
		}

		StringBuilder padded = new StringBuilder(string);
		while (padded.length() < size) {
			padded.append(paddingChar);
		}
		return padded.toString();
	}

	/**
	 * Right-pad the provided String with empty spaces.
	 *
	 * @param string The String to pad
	 * @param size   Pad String by the number of characters.
	 * @return The padded String. If the provided String is null, an empty String is returned.
	 */
	public static String padRight(String string, int size) {
		return padRight(string, size, ' ');
	}

	/**
	 * Convert a List of Strings to a comma delimited String.
	 *
	 * @param list
	 * @return Returns the List as a comma delimited String. Returns an empty
	 *         String for a Null or empty list.
	 */
	public static String collectionToCommaDelimitedString(Collection<String> list) {
		return StringUtils.collectionToCommaDelimitedString(list);
	}

	/**
	 * Convert a List of Strings to a comma delimited String.
	 *
	 * @param list
	 * @return Returns the List as a comma delimited String. Returns an empty
	 *         String for a Null or empty list.
	 */
	public static String collectionToCommaDelimitedString(Collection<String> list, String prefix) {
		return StringUtils.collectionToDelimitedString(list, ",", prefix, "");
	}

	/**
	 * Convert a List of CloudServices to a comma delimited String using their names.
	 *
	 * @param list
	 * @return Returns the List as a comma delimited String. Returns an empty
	 *         String for a Null or empty list.
	 */
	public static String collectionServicesToCommaDelimitedString(Collection<CloudService> list) {
		if (list == null || list.isEmpty()) {
			return "";
		}

		List<String> sb = new ArrayList<String>();

		for (CloudService service : list) {
			sb.add(service.getName());
		}

		return sb.toString();
	}

	/**
	 * @param reader
	 */
	public static void closeReader(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				throw new IllegalStateException("Encountered problem closing Reader.", e);
			}
		}
	}

	/**
	 * @param emailAddress
	 * @return
	 */
	public static boolean isValidEmail(String emailAddress) {
		final String regex = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-+]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		return emailAddress.matches(regex);
	}

	/**
	 * Simple method to replace characters in a String with Stars to mask the
	 * password.
	 *
	 * @param password The password to mask
	 */
	public static String maskPassword(String password) {
		int lengthOfPassword = password.length();
		StringBuilder stringBuilder = new StringBuilder(lengthOfPassword);

		for (int i = 0; i < lengthOfPassword; i++) {
			stringBuilder.append('*');
		}

		return stringBuilder.toString();
	}

	/**
	 * Formats the supported frameworks as a command separated list.
	 *
	 * @param offerings List of services
	 * @return a String but never null
	 */
	public static String serviceOfferingsToCommaDelimitedString(final Collection<CloudServiceOffering> offerings) {

		if (offerings == null || offerings.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		final Iterator<CloudServiceOffering> it = offerings.iterator();

		while (it.hasNext()) {
			CloudServiceOffering offering = it.next();
			sb.append(offering.getLabel());

			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}