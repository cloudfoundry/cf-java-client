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

package org.cloudfoundry.client.lib.util;

import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudServicePlan;
import org.cloudfoundry.client.lib.domain.CloudSpace;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class handling the mapping of the cloud domain objects
 *
 * @author: Thomas Risberg
 */
//TODO: use some more advanced JSON mapping framework?
public class CloudEntityResourceMapper {

	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

	@SuppressWarnings("unchecked")
	public String getNameOfJsonResource(Map<String, Object> resource) {
		return getEntityAttribute(resource, "name", String.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T mapJsonResource(Map<String, Object> resource, Class<T> targetClass) {
		if (targetClass == CloudSpace.class) {
			return (T) mapSpaceResource(resource);
		}
		if (targetClass == CloudService.class) {
			return (T) mapServiceInstanceResource(resource);
		}
		if (targetClass == CloudServiceOffering.class) {
			return (T) mapServiceResource(resource);
		}
		throw new IllegalArgumentException(
				"Error during mapping - unsupported class for entity mapping " + targetClass.getName());
	}

	private CloudSpace mapSpaceResource(Map<String, Object> resource) {
		Map<String, Object> organizationMap = getEmbeddedResource(resource, "organization");
		CloudOrganization organization = null;
		if (organizationMap != null) {
			organization = mapOrganizationResource(organizationMap);
		}
		CloudSpace space =
				new CloudSpace(getMeta(resource), getEntityAttribute(resource, "name", String.class), organization);
		return space;
	}

	private CloudOrganization mapOrganizationResource(Map<String, Object> resource) {
		CloudOrganization org = new CloudOrganization(getMeta(resource), getEntityAttribute(resource, "name", String.class));
		return org;
	}

	private CloudService mapServiceInstanceResource(Map<String, Object> resource) {
		CloudService cloudService = new CloudService(
				getMeta(resource),
				getEntityAttribute(resource, "name", String.class));
		Map<String, Object> servicePlanResource = getEmbeddedResource(resource, "service_plan");
		Map<String, Object> serviceResource = null;
		if (servicePlanResource != null) {
			serviceResource = getEmbeddedResource(servicePlanResource, "service");
		}
		if (servicePlanResource != null && serviceResource != null) {
			//TODO: assuming vendor corresponds to the service.provider and not service_instance.vendor_data
			cloudService.setLabel(getEntityAttribute(serviceResource, "label", String.class));
			cloudService.setProvider(getEntityAttribute(serviceResource, "provider", String.class));
			cloudService.setVersion(getEntityAttribute(serviceResource, "version", String.class));
		}
		if (servicePlanResource != null) {
			cloudService.setPlan(getEntityAttribute(servicePlanResource, "name", String.class));
		}
		return cloudService;
	}

	private CloudServiceOffering mapServiceResource(Map<String, Object> resource) {
		CloudServiceOffering cloudServiceOffering = new CloudServiceOffering(
				getMeta(resource),
				getEntityAttribute(resource, "label", String.class),
				getEntityAttribute(resource, "provider", String.class),
				getEntityAttribute(resource, "version", String.class));
		cloudServiceOffering.setDescription(getEntityAttribute(resource, "description", String.class));
		List<Map<String, Object>> servicePlanList = getEmbeddedResourceList(getEntity(resource), "service_plans");
		if (servicePlanList != null) {
			for (Map<String, Object> servicePlanResource : servicePlanList) {
				CloudServicePlan servicePlan =
						new CloudServicePlan(
								getMeta(servicePlanResource),
								getEntityAttribute(servicePlanResource, "name", String.class),
								cloudServiceOffering);
				cloudServiceOffering.addCloudServicePlan(servicePlan);
			}
		}
		return cloudServiceOffering;
	}

	@SuppressWarnings("unchecked")
	private CloudEntity.Meta getMeta(Map<String, Object> entity) {
		Map<String, Object> metadata = (Map<String, Object>) entity.get("metadata");
		UUID guid = UUID.fromString(String.valueOf(metadata.get("guid")));
		Date createdDate = null;
		String created = String.valueOf(metadata.get("created_at"));
		if (created != null) {
			try {
				createdDate = dateFormatter.parse(created);
			} catch (ParseException ignore) {}
		}
		Date updatedDate = null;
		String updated = String.valueOf(metadata.get("updated_at"));
		if (updated != null) {
			try {
				updatedDate = dateFormatter.parse(updated);
			} catch (ParseException ignore) {}
		}
		int version = 2; // this is always 2 for v2
		CloudEntity.Meta meta = new CloudEntity.Meta(guid, createdDate, updatedDate, version);
		return meta;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getEntity(Map<String, Object> resource) {
		return (Map<String, Object>) resource.get("entity");
	}

	@SuppressWarnings("unchecked")
	private <T> T getEntityAttribute(Map<String, Object> resource, String attributeName, Class<T> targetClass) {
		Map<String, Object> entity = (Map<String, Object>) resource.get("entity");
		if (targetClass == String.class) {
			return (T) String.valueOf(entity.get(attributeName));
		}
		throw new IllegalArgumentException(
				"Error during mapping - unsupported class for attribute mapping " + targetClass.getName());
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getEmbeddedResource(Map<String, Object> resource, String embeddedResourceName) {
		Map<String, Object> entity = (Map<String, Object>) resource.get("entity");
		return (Map<String, Object>) entity.get(embeddedResourceName);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getEmbeddedResourceList(Map<String, Object> resource, String embeddedResourceName) {
		return (List<Map<String, Object>>) resource.get(embeddedResourceName);
	}
}
