package org.cloudfoundry.client.lib.domain;

/**
 * @author Olivier Orand
 */
public class CloudUser extends CloudEntity {

    private boolean active;

    private boolean admin;

    private String defaultSpaceGuid;

    private CloudOrganization organization;

    private String username;

    public CloudUser(Meta meta, String username, boolean admin, boolean active, String defaultSpaceGuid) {
        super(meta, username);
        this.username = username;
        this.admin = admin;
        this.active = active;

        this.defaultSpaceGuid = defaultSpaceGuid;
        //this.organization = organization;
    }

    public String getDefaultSpaceGuid() {
        return defaultSpaceGuid;
    }

    public CloudOrganization getOrganization() {
        return organization;
    }

    public String getUsername() {
        return username;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAdmin() {
        return admin;
    }

}
