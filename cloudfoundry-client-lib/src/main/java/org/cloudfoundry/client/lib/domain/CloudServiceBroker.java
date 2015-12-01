package org.cloudfoundry.client.lib.domain;

public class CloudServiceBroker extends CloudEntity {

    private String password;

    private String url;

    private String username;

    public CloudServiceBroker(String url, String username) {
        this.url = url;
        this.username = username;
    }

    public CloudServiceBroker(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public CloudServiceBroker(Meta meta, String name, String url, String username) {
        super(meta, name);
        this.url = url;
        this.username = username;
    }

    public CloudServiceBroker(Meta meta, String name, String url, String username, String password) {
        super(meta, name);
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }
}
