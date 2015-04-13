Cloud Foundry Client Library
============================

Changes in version 1.1.2
------------------------

* Changed `deleteService()` method to delete the service asynchronously and poll for completion on the client. 
* Added forms of `getSpaceRole()` methods and `associateRoleWithSpace()` methods that take an organization parameter.
* Added populating `detected_buildpack` field of app metadata when retrieving applications.
* Added `getApplicationEnvironment()` methods for retrieving user and system environment variables.
* Added `getEvents()` and `getApplicationEvents()` methods.
* Added support for providing implicit-grant tokens in credentials.
* Fixed parsing of UTC Z dates.

Changes in version 1.1.1
------------------------

* Upgraded the Apache HttpClient library to version 4.3.6 to address a security vulnerability in HttpClient (http://www.openwall.com/lists/oss-security/2014/08/18/8)
* Added operations for managing security groups
* Added the ability to assign space roles to a user
* Added setting the space when retrieving and application 
* Fixed bugs in parsing application environment variables 

Changes in version 1.1.0
------------------------

* Updated Spring Framework, Apache HttpClient, and Jackson dependendencies to Spring 4.x, HttpComponents 4.3.x, and Jackson 2.x 

Changes in version 1.0.6
------------------------

* Added the ability to specify a syslog drain URL with a user-provided service
* Added space management operations
* Added the ability to upload an application from an InputStream

Changes in version 1.0.5
------------------------

* Changed retrieval of “recent logs” from Loggregator to use the HTTP endpoint instead of the deprecated WebSockets endpoint (tailing logs from Loggregator continues to use WebSockets).
* Updated tomcat-embed-websockets library to address a problem tailing logs from Loggregator on some platforms
* Added support for authenticated HTTP proxies
* Changed uploading of application bits to always use UTC time for “last modified” timestamp of files to match CLI behavior

Changes in version 1.0.4
------------------------

* Added service broker management operations
* Added quota management operations 
* Added method for removal of orphaned application routes

Changes in version 1.0.3
------------------------

* `streamLogs()` and `getRecentLogs()` methods were added to retrieve logs from Loggregator 
* An `openFile()` method was added to stream the contents of a file
* A `getServiceBrokers()` method was added to retrieve a list of available service brokers

------------------------

Changes in version 1.0.2
------------------------

* Added support for user-provided servicess.
* Added support for "healthCheckTimeout" and "diskQuota" parameters on application creation in the Client Library.
* Removed validation of application memory settings, since memory values are no longer constrained to a fixed set of options.
* Improved support for async file uploads.
* Added support for accepting self-signed SSL certificates from the Cloud Foundry target endpoint.

API Changes for version 0.8.6:
------------------------------

### Removed V1 concepts

* Frameworks
* Runtimes
* ServiceConfiguration (replaced by CloudServiceOffering)
* Application plans
* CloudFoundryClient constructors and parameters modified accordingly

### Added V2 concepts

* Buildpack and command fields were added to the Staging object used for createApplication
* Space name and Org name added as a CloudFoundryClient constructor parameters

API Changes for version 0.8.0
-----------------------------

### Package changes:

The following classes have moved to the _org.cloudfoundry.client.lib.domain_ package

  * ApplicationStats.java
  * CloudApplication.java
  * CloudEntity.java
  * CloudInfo.java
  * CloudResource.java
  * CloudResources.java
  * CloudService.java
  * CrashInfo.java
  * CrashesInfo.java
  * DeploymentInfo.java
  * InstanceInfo.java
  * InstanceStats.java
  * InstancesInfo.java
  * ServiceConfiguration.java
  * Staging.java
  * UploadApplicationPayload.java

The following classes have moved to the _org.cloudfoundry.client.lib.util_ package

  * CloudUtil.java
  * StringHttpMessageConverterWithoutMediaType.java
  * UploadApplicationPayloadHttpMessageConverter.java

You will need to adjust the import statements for these classes


### Implementation changes:

The _org.cloudfoundry.client.lib.CloudFoundryOperations_ interface defines the public API. You can use this interface or continue using the _org.cloudfoundry.client.lib.CloudFoundryClient_ class.

All entity classes in the _org.cloudfoundry.client.lib.domain_ package have a CloudEntity.Meta property that hold the following attributes:

  * UUID guid;
  * Date created;
  * Date updated;
  * int version;

This allows you to determine the version of any entity using getMeta().getVersion() which should return 1 or 2 depending on the cloud controller version currently connected to.

The following method has been removed from _CloudFoundryClient_:

    public <T> T getFile(String appName, int instanceIndex, String filePath, RequestCallback requestCallback, ResponseExtractor<T> responseHandler)

The following methods have been removed from _CloudApplication_:

    public Map<String, Object> getMeta()
    public void setMeta(Map<String, Object> meta)

They are replaced with the following methods that are now available for all cloud entity classes:

    public CloudEntity.Meta getMeta()
    public void setMeta(CloudEntity.Meta meta)


### Deprecations:

The _org.cloudfoundry.client.lib.domain.CloudApplication_ class has been deprecated since it's not used anywhere in the library. You will have to replace this with your own implementation.
