package org.cloudfoundry.client.lib.util;

import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudSpace;

import java.util.Map;
import java.util.UUID;

/**
 * @author: Thomas Risberg
 */
public class JsonEntityResourceMapper {
	//TODO: use some more advanced JSON mapping framework?

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
		throw new IllegalArgumentException(
				"Error during JSON mapping - unsupported class for entity mapping " + targetClass.getName());
	}

	private CloudSpace mapSpaceResource(Map<String, Object> resource) {
		return new CloudSpace(getGuid(resource), getEntityAttribute(resource, "name", String.class));
	}

	private CloudService mapServiceInstanceResource(Map<String, Object> resource) {
		CloudService cloudService = new CloudService();
		cloudService.setName(getEntityAttribute(resource, "name", String.class));
		Map<String, Object> servicePlanResource = getEmbeddedResource(resource, "service_plan");
		Map<String, Object> serviceResource = null;
		if (servicePlanResource != null) {
			serviceResource = getEmbeddedResource(servicePlanResource, "service");
		}
		if (servicePlanResource != null && serviceResource != null) {
			cloudService.setType(getEntityAttribute(serviceResource, "label", String.class));
			//TODO: assuming vendor corresponds to the service.provider and not service_instance.vendor_data
			cloudService.setVendor(getEntityAttribute(serviceResource, "provider", String.class));
			cloudService.setVersion(getEntityAttribute(serviceResource, "version", String.class));
		}
		if (servicePlanResource != null) {
			cloudService.setTier(getEntityAttribute(servicePlanResource, "name", String.class));
		}
		return cloudService;
	}

	@SuppressWarnings("unchecked")
	private UUID getGuid(Map<String, Object> entity) {
		Map<String, Object> meta = (Map<String, Object>) entity.get("metadata");
		return UUID.fromString(meta.get("guid").toString());
	}

	@SuppressWarnings("unchecked")
	private <T> T getEntityAttribute(Map<String, Object> resource, String attributeName, Class<T> targetClass) {
		Map<String, Object> entity = (Map<String, Object>) resource.get("entity");
		if (targetClass == String.class) {
			return (T) String.valueOf(entity.get(attributeName));
		}
		throw new IllegalArgumentException(
				"Error during JSON mapping - unsupported class for attribute mapping " + targetClass.getName());
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getEmbeddedResource(Map<String, Object> resource, String embeddedResourceName) {
		Map<String, Object> entity = (Map<String, Object>) resource.get("entity");
		return (Map<String, Object>) entity.get(embeddedResourceName);
	}
}
