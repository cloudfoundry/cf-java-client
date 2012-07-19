package org.cloudfoundry.client.lib.cloud;

import org.cloudfoundry.client.lib.cloud.SpaceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author: Thomas Risberg
 */
public class JsonEntityInfoMapper {

	public static List<SpaceInfo> toSpaceInfoList(List<Map<String, Object>> entityList) {
		List<SpaceInfo> result = new ArrayList<SpaceInfo>();
		if (entityList != null) {
			for (Map<String, Object> entity : entityList) {
				Map<String, Object> entityValues = (Map<String, Object>) entity.get("entity");
				Map<String, Object> metaValues = (Map<String, Object>) entity.get("metadata");
				SpaceInfo spaceInfo = new SpaceInfo();
				setBaseInfoValues(entityValues, metaValues, spaceInfo);
				result.add(spaceInfo);
			}

		}
		return result;
	}

	public static List<ServiceInstanceInfo> toServiceInstanceInfoList(List<Map<String, Object>> entityList) {
		List<ServiceInstanceInfo> result = new ArrayList<ServiceInstanceInfo>();
		if (entityList != null) {
			for (Map<String, Object> entity : entityList) {
				Map<String, Object> entityValues = (Map<String, Object>) entity.get("entity");
				Map<String, Object> metaValues = (Map<String, Object>) entity.get("metadata");
				ServiceInstanceInfo serviceInstanceInfo = new ServiceInstanceInfo();
				setBaseInfoValues(entityValues, metaValues, serviceInstanceInfo);
				serviceInstanceInfo.setVendor(String.valueOf(entityValues.get("vendor_data")));
				Map<String, Object> servicePlanEntity = (Map<String, Object>) entityValues.get("service_plan");
				if (servicePlanEntity != null) {
					ServicePlanInfo planInfo = toServicePlanInfo(servicePlanEntity);
					serviceInstanceInfo.setServicePlanInfo(planInfo);
				}
				result.add(serviceInstanceInfo);
			}

		}
		return result;
	}

	public static ServicePlanInfo toServicePlanInfo(Map<String, Object> entity) {
		Map<String, Object> entityValues = (Map<String, Object>) entity.get("entity");
		Map<String, Object> metaValues = (Map<String, Object>) entity.get("metadata");
		ServicePlanInfo planInfo = new ServicePlanInfo();
		setBaseInfoValues(entityValues, metaValues, planInfo);
		Map<String, Object> serviceEntity = (Map<String, Object>) entityValues.get("service");
		if (serviceEntity != null) {
			ServiceInfo serviceInfo = toServiceInfo(serviceEntity);
			planInfo.setServiceInfo(serviceInfo);
		}
		return planInfo;
	}

	public static ServiceInfo toServiceInfo(Map<String, Object> entity) {
		System.out.println(entity);
		Map<String, Object> entityValues = (Map<String, Object>) entity.get("entity");
		Map<String, Object> metaValues = (Map<String, Object>) entity.get("metadata");
		ServiceInfo serviceInfo = new ServiceInfo();
		// service doesn't have a name - let's add label as name
		entityValues.put("name", entityValues.get("label"));
		setBaseInfoValues(entityValues, metaValues, serviceInfo);
		serviceInfo.setVersion(entityValues.get("version").toString());
		serviceInfo.setLabel(entityValues.get("label").toString());
		serviceInfo.setProvider(entityValues.get("provider").toString());
		return serviceInfo;
	}

	private static void setBaseInfoValues(Map<String, Object> entityValues, Map<String, Object> metaValues, BaseInfo info){
		info.setName(entityValues.get("name").toString());
		info.setGuid(UUID.fromString(metaValues.get("guid").toString()));
	}

}
