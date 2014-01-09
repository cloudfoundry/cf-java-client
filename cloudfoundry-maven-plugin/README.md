# Cloud Foundry Maven Plugin

## Introduction

Since Maven is one of the dominant build and deployment tools in the Java world, we made the core functionality of the [Cloud Foundry cf command line tool](http://docs.cloudfoundry.com/docs/using/managing-apps/cf/) also available to Maven users.

## Basic Configuration

In order to get started you must as a minimum add the **cf-maven-plugin** to your project's pom.xml:

~~~xml
    <plugin>
        <groupId>org.cloudfoundry</groupId>
        <artifactId>cf-maven-plugin</artifactId>
        <version>1.0.1</version>
    </plugin>
~~~

This minimal configuration will be sufficient to execute many of the plugin's Maven Goals.

> All configuration options that can be specified either through configuration parameters in the pom.xml, or via command-line-provided system properties (e.g. *mvn cf:push \-Dcf.appname=greenhouse*). Please read on for further details.

## Advanced Configuration

As mentioned above, the Cloud Foundry Maven Plugin can be configured either by providing relevant information in the `pom.xml` file and/or via *command line* parameters (system properties). This allows users to chose the configuration path most appropriate to their business needs. In most cases though, we expect users to configure the static and non-security-sensitive parameters in the `pom.xml` file.

### Security and Storing of Cloud Foundry Credentials

While it is possible to configure Cloud Foundry security credentials within the pom.xml file (discouraged), they can also be configured via system properties. However, even better, the security credentials for your Cloud Foundry instance can also be configured using the standard "server" XML configuration element ([http://maven.apache.org/settings.html#Servers]). This allows for keeping out security-sensitive information from the `pom.xml` file, yet eliminating the need to provide the security credential every time you interact with Cloud Foundry. In that case, the username and password information is stored in the `settings.xml` file, which is usually placed under `~/.m2/settings.xml` (home directory). The following example illustrated the necessary configuration:

Plugin configuration in `pom.xml`:

~~~xml
    <plugin>
      <groupId>org.cloudfoundry</groupId>
      <artifactId>cf-maven-plugin</artifactId>
      <version>1.0.1</version>
      <configuration>
          <server>mycloudfoundry-instance</server>
          <target>http://api.cloudfoundry.com</target>
      </configuration>
    </plugin>
~~~

> The `server` configuration element is actually optional. If not explicitly set, its value will default to `cloud-foundry-credentials`:

Configuration of `mycloudfoundry-instance` in `settings.xml`:

~~~xml
    <settings>
      ...
      <servers>
        ...
        <server>
          <id>mycloudfoundry-instance</id>
          <username>myname@email.com</username>
          <password>s3cr3t</password>
        </server>
        ...
      </servers>
      ...
    </settings>
~~~

As mentioned previously, you can also provide the user credentials through the following command line parameters instead:

* cf.username
* cf.password

e.g. by using:

    $ mvn cf:info -Dcf.username=myusername -Dcf.password=s3cr3t -Dtarget=http://api.cloudfoundry.com

> If the credentials are defined via the server element (in `settings.xml`) AND through the command line, then the command line parameter takes the precedence.

Another alternative is to use the `login` and `logout` goals to authenticate with a user name and password, and allow all other goals to use access tokens saved by `login`.

    $ mvn cf:login -Dcf.username=myusername -Dcf.password=s3cr3t -Dtarget=http://api.cloudfoundry.com

After the `login` goal is executed in this way, it is not necessary to have a user name or password configured.

Finally, describing probably a rather rare use-case: If you have multiple Cloud Foundry specific `server` elements defined in your `settings.xml`, you can address those through command line parameters as well using:

* cf.server (e.g. *mvn push -Dcf.server=mycloudfoundry-instance*)

### A complete configuration example for a web application

Following, a typical (expected) configuration example is shown, which uses several of the available configuration parameters. However for a complete listing of proposed configuration options, please have a look under section *Command Line Usage*".

~~~xml
    <project>
      ...
    <build>
        <plugins>
            <plugin>
                <groupId>org.cloudfoundry</groupId>
                <artifactId>maven-cf-plugin</artifactId>
                <version>1.0.1</version>
                <configuration>
                    <server>mycloudfoundry-instance</server>
                    <target>http://api.run.pivotal.io</target>
                    <org>mycloudfoundry-org</org>
                    <space>development</space>
                    <appname>my-app</appname>
                    <url>my-app.cfapps.io</url>
                    <memory>512</memory>
                    <env>
                        <ENV-VAR-NAME>env-var-value</ENV-VAR-NAME>
                    </env>
                    <services>
                        <service>
                            <name>postgres-test</name>
                            <label>elephantsql</label>
                            <provider>elephantsql</provider>
                            <version>n/a</version>
                            <plan>turtle</plan>
                        </service>
                        <service>
                            <name>mongodb-test</name>
                            <label>mongolab</label>
                            <provider>mongolab</provider>
                            <version>n/a</version>
                            <plan>sandbox</plan>
                        </service>
                    </services>
                </configuration>
            </plugin>
        </plugins>
    </build>
      ...
    </project>
~~~

in **settings.xml**:

~~~xml
    <settings>
      <servers>
        <server>
          <id>mycloudfoundry-instance</id>
          <username>myname@email.com</username>
          <password>s3cr3t</password>
        </server>
      </servers>
      ...
    </settings>
~~~

## Command Line Usage

### Overview

The following Maven *goals* are available for the Cloud Foundry Maven Plugin:

<table>
    <tr><th align="left">cf:apps</th>             <td>List deployed applications.</td></tr>
    <tr><th align="left">cf:app</th>              <td>Show details of an application.</td></tr>
    <tr><th align="left">cf:delete</th>           <td>Delete an application.</td></tr>
    <tr><th align="left">cf:env</th>              <td>Show an application's environment variables.</td></tr>
    <tr><th align="left">cf:help</th>             <td>Documentation for all available commands.</td></tr>
    <tr><th align="left">cf:push</th>             <td>Push and optionally start an application.</td></tr>
    <tr><th align="left">cf:push-only</th>        <td>Push and optionally start an application, without packaging.</td></tr>
    <tr><th align="left">cf:restart</th>          <td>Restart an application.</td></tr>
    <tr><th align="left">cf:start</th>            <td>Start an application.</td></tr>
    <tr><th align="left">cf:stop</th>             <td>Stop an application.</td></tr>
    <tr><th align="left">cf:target</th>           <td>Show information about the target Cloud Foundry service.</td></tr>
    <tr><th align="left">cf:logs</th>             <td>Show log files (stdout and stderr).</td></tr>
    <tr><th align="left">cf:scale</th>            <td>Scale the application instances up or down.</td></tr>
    <tr><th align="left">cf:services</th>         <td>Show a list of provisioned services.</td></tr>
    <tr><th align="left">cf:service-plans</th>    <td>Show a list of available service plans.</td></tr>
    <tr><th align="left">cf:create-services</th>  <td>Create services defined in the pom.</td></tr>
    <tr><th align="left">cf:delete-services</th>  <td>Delete services defined in the pom.</td></tr>
    <tr><th align="left">cf:bind-services</th>    <td>Bind services to an application.</td></tr>
    <tr><th align="left">cf:unbind-services</th>  <td>Unbind services from an application.</td></tr>
    <tr><th align="left">cf:login</th>            <td>Log in to the target Cloud Foundry service and saves access tokens.</td></tr>
    <tr><th align="left">cf:logout</th>           <td>Log out of the target Cloud Foundry service and removes access tokens.</td></tr>
</table>

### Usage Examples

**List deployed applications**

    $ mvn cf:apps

**Delete an application**

    $ mvn cf:delete [-Dcf.appname] [-Dcf.force]

**Documentation for all available commands**

    $ mvn cf:help

**Show target service information**

    $ mvn cf:target

**Scale the application instances up or down**

    $ mvn cf:scale [-Dcf.appname] [-Dcf.instances]

**Push and optionally start an application**

    $ mvn cf:push [-Dcf.appname] [-Dcf.path] [-Dcf.url] [-Dcf.instances] [-Dcf.memory] [-Dcf.no-start]

**Restart the application**

    $ mvn cf:restart [-Dcf.appname]

**Start the application**

    $ mvn cf:start [-Dcf.appname]

**Stop the application**

    $ mvn cf:stop [-Dcf.appname]

## Sensible Defaults

### Precedence

As the same configuration parameters can be provided either through system properties,
configuration elements in the the pom.xml and through the settings.xml file (username and
password information only), the following precedence rules apply (starting with the highest precedence):

1. **System Properties** e.g. mvn cf:start -Dcf.appname
2. **Setting.xml parameters** for username and password
3. **Pom.xml** Configuration parameters e.g. <configuration><appname>myApp</appname></configuration>
4. **Pom.xml** Properties e.g. <properties><cf.appname>myApp</cf.appname><properties>

> **INFO** The implemented behavior deviates slightly from standard Maven behavior.
  Usually Maven configuration parameters in the pom take precedence over system
  properties passed in via the command line. Within the scope of the Cloud Foundry
  Maven Plugin, however, system properties passed in via e.g. -Dcf.appname take precedence
  over pom configuration parameters.

Additional certain configuration parameter will fall back to using default values in case **no configuration value was provided**:

### Defaults

+ **appname**: If no app name is specified, the Maven artifact id is being used
+ **instances**: Defaults to *1*
+ **no-start**: Defaults to *false*
+ **memory**: Defaults to *512* (MB)
+ **path**: Defaults to *${project.build.directory}/${project.build.finalName}.war*
+ **server**: Special parameter to tell **Maven** which server element in `settings.xml`
  holds the credentials for Cloud Foundry. Defaults to *cloud-foundry-credentials*

> The parameters **username**, **password**, **target**, and **space** don't have default values and you are required to provide them.

# History

## Changes from version 1.0.0.M4 to 1.0.0

* Upgraded to cloudfoundry-client-lib 1.0.0
* Removed v1 support and all v1 concepts (update goal, runtime and framework parameters)
* Added support for buildpacks
* Renamed goals and parameters for consistency with 'cf' and Cloud Foundry Gradle Plugin
* Changed login and logout goals to save tokens to the file `~/.cf/tokens.yml` instead of `~/.mvn-cf.xml`, for compatibility with the cf CLI

## Changes from version 1.0.0.M3 to 1.0.0.M4

* Upgraded to cloudfoundry-client-lib 0.8.2
* Modified the output from older grid style to cleaner column style
* Added Cloud Controller v1 / v2 (cloud\_controller\_ng) detection
* Added support for org and space in v2 (cloud\_controller\_ng)
* Added support for push and delete app to v2 (cloud\_controller\_ng)
* Added show log for v2 (cloud\_controller\_ng)
* Added support for create-services for v2 (cloud\_controller\_ng)

## Changes from version **1.0.0.M2** to **1.0.0.M3**

* Added support for one or more **service** child elements for the <service> element. This allows to create and delete services. The required config options for a service are *name* and *vendor* with *version* and *tier* being optional.
* Modified the **cf:push** goal to take the services configuration and create the services, if they don't exist, and bind them to the application.
* Added **cf:logs** goal which shows the log files of the application specified in either the configuration parameter or in the pom file.
* Added **cf:services** goal which shows the list of available services along with provisioned ones.
* Added **cf:create-services** goal which creates services specified in the configuration parameter or in the pom file.
* Added **cf:delete-services** goal which deletes the services created using the services configuration in the pom file.


## Changes from version **1.0.0.M1** to **1.0.0.M2**

* Added **Framework** configuration parameter (-Dcf.framework) which allows to set the framework for the application. It defaults to *spring*.
* Added ability to deploy not only war-files but also point to directories and deploy those
* Added support for deployments of stand-alone applications by specifying **standalone** as the *framework*
* Deprecated **warfile** as now you can deploy stand-alone applications as well. Behavior is equal to the **path** property.
* Added **path** configuration property (-Dcf.path).
* Added **Runtime** property (-Dcf.runtime). It defaults to 'java' but technically you could also use the Maven Plugin to deploy e.g. Node and Ruby applications.
* Improved **cf:info** Maven goal. It will now show a list of available **frameworks** and **system services**, as well as a list of available **runtimes***
* **instances** property now defaults to *1*.

