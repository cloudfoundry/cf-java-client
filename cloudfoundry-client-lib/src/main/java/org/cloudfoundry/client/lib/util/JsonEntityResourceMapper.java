package org.cloudfoundry.client.lib.util;

import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudSpace;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;

/**
 * @author: Thomas Risberg
 */
//TODO: use some more advanced JSON mapping framework?
public class JsonEntityResourceMapper {

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
		throw new IllegalArgumentException(
				"Error during JSON mapping - unsupported class for entity mapping " + targetClass.getName());
	}

	private CloudSpace mapSpaceResource(Map<String, Object> resource) {
		return new CloudSpace(getMeta(resource), getEntityAttribute(resource, "name", String.class));
	}

	private CloudService mapServiceInstanceResource(Map<String, Object> resource) {
		CloudService cloudService = new CloudService();
		cloudService.setMeta(getMeta(resource));
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
	private CloudEntity.Meta getMeta(Map<String, Object> entity) {
		Map<String, Object> metadata = (Map<String, Object>) entity.get("metadata");
		CloudEntity.Meta meta = new CloudEntity.Meta();
		meta.setGuid(UUID.fromString(String.valueOf(metadata.get("guid"))));
		String created = String.valueOf(metadata.get("created_at"));
		if (created != null) {
			try {
				meta.setCreated(dateFormatter.parse(created));
			} catch (ParseException ignore) {}
		}
		String updated = String.valueOf(metadata.get("updated_at"));
		if (updated != null) {
			try {
				meta.setUpdated(dateFormatter.parse(updated));
			} catch (ParseException ignore) {}
		}
		meta.setVersion(2); // this is always 2 for v2
		return meta;
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
