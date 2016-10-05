/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.gradle;

/**
 * Property name related constants
 *
 * @author Biju Kunjummen
 */
public interface PropertyNameConstants {
	String CF_APPLICATION_NAME = "cf.name";
	String CF_APPLICATION_HOST_NAME = "cf.hostName";
	String CF_APPLICATION_DOMAIN = "cf.domain";
	String CF_FILE_PATH = "cf.filePath";
	String CC_HOST = "cf.ccHost";
	String CC_USER = "cf.ccUser";
	String CC_PASSWORD = "cf.ccPassword";
	String CF_ORG = "cf.org";
	String CF_SPACE = "cf.space";
	String CF_BUILDPACK = "cf.buildpack";
	String CF_MEMORY = "cf.memory";
	String CF_INSTANCES = "cf.instances";
	String CF_APPLICATION_NEW_NAME = "cf.newName";
	String CF_HEALTH_CHECK_TIMEOUT = "cf.timeout";
	String CF_DISK_QUOTA = "cf.diskQuota";
	String CF_PATH = "cf.path";

	String CF_STAGING_TIMEOUT = "cf.stagingTimeout" ;
	String CF_STARTUP_TIMEOUT = "cf.stagingTimeout" ;
}
