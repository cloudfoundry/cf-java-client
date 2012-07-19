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
				SpaceInfo si = new SpaceInfo();
				setBaseInfoValues(entityValues, metaValues, si);
				result.add(si);
			}

		}
		return result;
	}

	public static List<ServiceInfo> toServiceInfoList(List<Map<String, Object>> entityList) {
		List<ServiceInfo> result = new ArrayList<ServiceInfo>();
		if (entityList != null) {
			for (Map<String, Object> entity : entityList) {
				Map<String, Object> entityValues = (Map<String, Object>) entity.get("entity");
				Map<String, Object> metaValues = (Map<String, Object>) entity.get("metadata");
				ServiceInfo si = new ServiceInfo();
				setBaseInfoValues(entityValues, metaValues, si);
				si.setType("unknown");
				si.setVendor(String.valueOf(entityValues.get("vendor_data")));
				si.setVersion("unknown");
				Map<String, Object> servicePlanEntity = (Map<String, Object>) entityValues.get("service_plan");
				if (servicePlanEntity != null) {
					ServicePlanInfo planInfo = toServicePlanInfo(servicePlanEntity);
					si.setServicePlanInfo(planInfo);
				}
				result.add(si);
			}

		}
		return result;
	}

	public static ServicePlanInfo toServicePlanInfo(Map<String, Object> entity) {
		Map<String, Object> entityValues = (Map<String, Object>) entity.get("entity");
		Map<String, Object> metaValues = (Map<String, Object>) entity.get("metadata");
		ServicePlanInfo planInfo = new ServicePlanInfo();
		setBaseInfoValues(entityValues, metaValues, planInfo);
		return planInfo;
	}

	private static void setBaseInfoValues(Map<String, Object> entityValues, Map<String, Object> metaValues, BaseInfo info){
		info.setName(entityValues.get("name").toString());
		info.setGuid(UUID.fromString(metaValues.get("guid").toString()));
	}

}
