package org.cloudfoundry.client.lib.domain;

import java.util.UUID;

public class RawServiceInstance extends CloudEntity {

    private UUID spaceGuid;

    public RawServiceInstance(Meta meta, String serviceInstanceName) {
        super(meta, serviceInstanceName);
    }

    public UUID getSpaceGuid() {
        return spaceGuid;
    }

    public void setSpaceGuid(UUID spaceGuid) {
        this.spaceGuid = spaceGuid;
    }
}
