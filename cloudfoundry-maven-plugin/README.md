# Cloud Foundry Maven Plugin
Version 1.0.0.M4, November 16, 2012

* Project website: [https://github.com/cloudfoundry/vcap-java-client/tree/master/cloudfoundry-maven-plugin](https://github.com/cloudfoundry/vcap-java-client/tree/master/cloudfoundry-maven-plugin)
* Source code:     [git://github.com/cloudfoundry/vcap-java-client.git](git://github.com/cloudfoundry/vcap-java-client.git)
* Build Server:    [https://build.springsource.org/browse/VCAPJAVA](https://build.springsource.org/browse/VCAPJAVA)
* Sonar:           [https://sonar.springsource.org/dashboard/index/org.cloudfoundry:cf-maven-plugin](https://sonar.springsource.org/dashboard/index/org.cloudfoundry:cf-maven-plugin)
* Issue Tracker:   [https://cloudfoundry.atlassian.net/](https://cloudfoundry.atlassian.net/) - Component: *Frameworks and Runtime*

## Introduction

Since Maven is one of the dominant build and deployment tools in the Java world, we made the core functionality of the [Cloud Foundry VMC command line tool](http://support.cloudfoundry.com/entries/20012337-getting-started-guide-command-line-vmc-users) also available to Maven users.

As an immediate step, we have implemented the following functionality as a Maven Plugin:

* Deploy (Push) Maven based projects to Cloud Foundry
* Undeploy Maven based projects from Cloud Foundry
* Redeploy (Update) Maven based Cloud Foundry projects

The Maven plugin also closely mimics the feature set provided by the [Grails Cloud Foundry Plugin](http://grails-plugins.github.com/grails-cloud-foundry/docs/manual/index.html).

## Basic Configuration

In order to get started you must as a minimum add the **cf-maven-plugin** to your project's pom.xml:

    <plugin>
        <groupId>org.cloudfoundry</groupId>
        <artifactId>cf-maven-plugin</artifactId>
        <version>1.0.0.M1-SNAPSHOT</version>
    </plugin>

This minimal configuration will be sufficient to execute many of the plugin's Maven Goals.

> All configuration options that can be specified either through configuration parameters in the pom.xml, or via command-line-provided system properties (e.g. *mvn cf:push \-Dcf.appname=greenhouse*). Please read on for further details.

## Advanced Configuration

As mentioned above, the Cloud Foundry Maven Plugin can be configured either by providing relevant information in the *pom.xml* file and/or via *command line* parameters (system properties). This allows users to chose the configuration path most appropriate to their business needs. In most cases though, we expect users to configure the static and non-security-sensitive parameters in the pom.xml file.

### Security and Storing of Cloud Foundry Credentials

While it is possible to configure Cloud Foundry security credentials within the pom.xml file (discouraged), they can also be configured via system properties. However, even better, the security credentials for your Cloud Foundry instance can also be configured using the standard "server" Xml configuration element ([http://maven.apache.org/settings.html#Servers]). This allows for keeping out security-sensitive information from the *pom.xml* file, yet eliminating the need to provide the security credential every time you interact with Cloud Foundry. In that case, the username and password information is stored in the **settings.xml** file, which is usually placed under  *~/.m2/settings.xml* (home directory). The following example illustrated the necessary configuration:

Plugin configuration in **pom.xml**:

    <plugin>
      <groupId>org.cloudfoundry</groupId>
      <artifactId>cf-maven-plugin</artifactId>
      <version>1.0.0.BUILD-SNAPSHOT</version>
      <configuration>
          <server>mycloudfoundry-instance</server>
          <target>http://api.cloudfoundry.com</target>
      </configuration>
    </plugin>

> The **server** configuration element is actually optional. If not explicitly set, its value will default to **cloud-foundry-credentials**:

Configuration of *mycloudfoundry-instance* in **settings.xml**:

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

As mentioned previously, you can also provide the user credentials through the following command line parameters instead:

* cf.username
* cf.password

e.g. by using:

    $ mvn cf:info -Dcf.username=myusername -Dcf.password=s3cr3t -Dtarget=http://api.cloudfoundry.com


> If the credentials are defined via the server element (in settings.xml) AND through the command line, then the command line parameter takes the precedence.

Finally, describing probably a rather rare use-case: If you have multiple Cloud Foundry specific **server** elements defined in your **settings.xml**, you can address those through command line parameters as well using:

* cf.server (e.g. *mvn push -Dcf.server=mycloudfoundry-instance*)

### A complete configuration example for a web application

Following, a typical (expected) configuration example is shown, which uses several of the available configuration parameters. However for a complete listing of proposed configuration options, please have a look under section *Command Line Usage*".

    <project>
      ...
    <build>
        <plugins>
            <plugin>
                <groupId>org.cloudfoundry</groupId>
                <artifactId>maven-cf-plugin</artifactId>
                <version>1.0.0.M1-SNAPSHOT</version>
                <configuration>
                    <server>mycloudfoundry-instance</server>
                    <target>http://api.cloudfoundry.com</target>
                    <appname>spring-integration-rocks</appname>
                    <url>spring-integration-rocks.cloudfoundry.com</url>
                    <memory>1024</memory>
                    <services>
                        <service>
                            <name>mysql-test</name>
                            <vendor>mysql</vendor>
                        </service>
                        <service>
                            <name>mongodb-test</name>
                            <vendor>mongodb</vendor>
                        </service>
                    </services>
                </configuration>
            </plugin>
        </plugins>
    </build>
      ...
    </project>

> The **url** element is optional. If not specified, it defaults to *appname*.<main *target* domain> (using the *appname* element)
> e.g. if your **appname** is *spring-integration-rocks* and the **target** is defined as *api.cloudfoundry.com* then the url will default to: **spring-integration-rocks.cloudfoundry.com**

in **settings.xml**:

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

## Command Line Usage

### Overview

The following Maven *goals* are available for the Cloud Foundry Maven Plugin:

<table>
    <tr><th align="left">cf:add-user</th>   <td>Register a new user.</td></tr>
    <tr><th align="left">cf:apps</th>       <td>List deployed applications.</td></tr>
    <tr><th align="left">cf:delete</th>     <td>Deletes an application.</td></tr>
    <tr><th align="left">cf:delete-user</th><td>Delete a user and all apps and services.</td></tr>
    <tr><th align="left">cf:help</th>       <td>Documentation for all available commands.</td></tr>
    <tr><th align="left">cf:info</th>       <td>Shows usage information.</td></tr>
    <tr><th align="left">cf:instances</th>  <td>Scale the application instances up or down.</td></tr>
    <tr><th align="left">cf:push</th>       <td>Push and optionally start an application.</td></tr>
    <tr><th align="left">cf:restart</th>    <td>Restarts an application.</td></tr>
    <tr><th align="left">cf:start</th>      <td>Starts an application.</td></tr>
    <tr><th align="left">cf:stop</th>       <td>Stops an application.</td></tr>
    <tr><th align="left">cf:update</th>     <td>Updates an application.</td></tr>
    <tr><th align="left">cf:logs</th>     <td>Shows log files (stdout and stderr).</td></tr>
    <tr><th align="left">cf:services</th>     <td>Shows a list of available services along with provisioned.</td></tr>
    <tr><th align="left">cf:create-services</th>     <td>Creates services defined in the pom.</td></tr>
    <tr><th align="left">cf:delete-services</th>     <td>Deletes services defined in the pom.</td></tr>
</table>

### Usage Examples

**Add a User**

    $ mvn cf:add-user [-Dcf.username] [-Dcf.password]

**List deployed applications**

    $ mvn cf:apps

**Delete an application**

    $ mvn cf:delete [-Dcf.appname] [-Dcf.force]

**Delete a User**

    $ mvn cf:delete-user [-Dcf.username] [-Dcf.password]

**Documentation for all available commands**

    $ mvn cf:help

    Cloud Foundry Maven Plugin detected Parameters and/or default values:

    +-----------+-----------------------------------------------------------------------------------+
    | Parameter | Value (Configured or Default)                                                     |
    +-----------+-----------------------------------------------------------------------------------+
    | Appname   | hello-java                                                                        |
    | Instances | 1                                                                               |
    | Memory    | 512                                                                               |
    | No-start  | false                                                                             |
    | Password  | *****                                                                             |
    | Server    | cloud-foundry-credentials                                                         |
    | Services  |                                                                                   |
    | Target    | N/A                                                                               |
    | Url       | hello-java.cloudfoundry.com                                                       |
    | Username  | demouser@cloudfoundry.com                                                         |
    | Warfile   | /Users/demouser/dev/git/cloudfoundry-samples/hello-java/target/hello-java-1.0.war |
    +-----------+-----------------------------------------------------------------------------------+
    Usage: mvn cf:command [command_options]

    Currently available Cloud Foundry Maven Plugin Goals are:

      Getting Started
        info -Dcf.username -Dcf.password                                        System and account information

      Applications
        apps -Dcf.username -Dcf.password                                        List deployed applications

      Application Creation
        push -Dcf.username -Dcf.password [-Dcf.appname]                         Create, push, map, and start a new application
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.warfile]          Push application from specified path
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.url]              Set the url for the application
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.instances]        Set the expected number of instances
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.memory]           Set the memory reservation for the application
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.no-start]         Do not auto-start the application
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.framework]        Set the framework for the application

      Application Operations
        start   -Dcf.username -Dcf.password [-Dcf.appname]                      Start the application
        stop    -Dcf.username -Dcf.password [-Dcf.appname]                      Stop the application
        restart -Dcf.username -Dcf.password [-Dcf.appname]                      Restart the application
        delete  -Dcf.username -Dcf.password [-Dcf.appname]                      Delete the application

      Application Updates
        update    -Dcf.username -Dcf.password [-Dcf.warfile]                    Update the application bits
        instances -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.instances]   Scale the application instances up or down

      Administration
        add-user    -Dcf.username -Dcf.password                                 Register a new user
        register    -Dcf.username -Dcf.password                                 Register a new user (Alias for 'add-user')
        delete-user -Dcf.username -Dcf.password                                 Delete a user and all apps and services

      Application Information
        instances -Dcf.username -Dcf.password [-Dcf.appname]                    List application instances

      Services
        services                                                                List the available services along with provisioned.
        create-services                                                         Create services defined in the pom file.
        delete-services                                                         Delete services defined in the pom file.

      Help
        help                                                                    Get general help

      Logs
        logs                                                                    Get the log files (stdout and stderr)

**Show usage information**

    $ mvn cf:info

    --------------------------------------------------------
    VMware's Cloud Application Platform (v0.999 build 2222)
    For support visit http://support.cloudfoundry.com

    Target:          http://api.cloudfoundry.com
    Frameworks:      sinatra, spring, grails, standalone, node, java_web, rails3, rack, lift, play
    Runtimes:        node, ruby19, java, ruby18, node06
    System Services: mysql, postgresql, redis, mongodb, rabbitmq

    User:        yep-it-rocks@cloudfoundry.com
    Usage:
        Memory:       1536M of 2048M total
        Services:     4 of 16 total
        Apps:         3 of 20 total
        Uris Per App: 0 of 4 total
    --------------------------------------------------------

**Scale the application instances up or down**

    $ mvn cf:instances [-Dcf.appname] [-Dcf.instances]

**Push and optionally start an application**

    $ mvn cf:push [-Dcf.appname] [-Dcf.warfile] [-Dcf.url] [-Dcf.instances] [-Dcf.memory] [-Dcf.no-start]

**Restart the application**

    $ mvn cf:restart [-Dcf.appname]

**Start the application**

    $ mvn cf:start [-Dcf.appname]

**Stop the application**

    $ mvn cf:stop [-Dcf.appname]

**Update the application**

    $ mvn cf:update [-Dcf.appname] [-Dcf.warfile]

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
+ **path**: If not provided and if **framework** is not set to **standalone**, then this property defaults to: *${project.build.directory}/${project.build.finalName}.war*.
+ **runtime**: Defaults to *java*
+ **server**: Special parameter to tell **Maven** which server element in *settings.xml*
  holds the credentials for Cloud Foundry. Defaults to *cloud-foundry-credentials*
+ **url**: If no Url is specified, then the *appname* and the main domain from the *target* parameter are used to dynamically form the url
+ **warfile** (**deprecated** use **path** instead): Same functionality as **path**
+ **framework**: Defaults to *spring*

> The parameters **username**, **password** and **target** don't have default values and you are required to provide them.

# Samples

## Deploying a Stand-alone Spring Application

The following sample is based on the blog posting:

* [http://blog.cloudfoundry.com/2012/05/09/running-workers-on-cloud-foundry-with-spring/](http://blog.cloudfoundry.com/2012/05/09/running-workers-on-cloud-foundry-with-spring/)

We will adapt the used sample to work with the *Cloud Foundry Maven Plugin*

1. Checkout the sample using [GIT](http://git-scm.com/)

		$ git clone git://github.com/ghillert/twitter-rabbit-socks-sample.git

2. Go to the `twitter2rabbit` directory

		$ cd twitter-rabbit-socks-sample/twitter2rabbit

3. Add the [Maven Application Assembler Plugin](http://mojo.codehaus.org/appassembler/appassembler-maven-plugin) to the **pom.xml** file:

		<build>
			...
		    <plugins>
				...
		        <plugin>
		            <groupId>org.codehaus.mojo</groupId>
		            <artifactId>appassembler-maven-plugin</artifactId>
		            <version>1.2.2</version>
		            <executions>
		                <execution>
		                    <phase>package</phase>
		                    <goals>
		                        <goal>assemble</goal>
		                    </goals>
		                    <configuration>
		                        <assembledirectory>target</assembledirectory>
		                        <programs>
		                            <program>
		                                <mainClass>org.springsource.samples.twitter.Demo</mainClass>
		                            </program>
		                        </programs>
		                    </configuration>
		                </execution>
		            </executions>
		        </plugin>
				...
		    </plugins>
			...
		</build>

4. Add the Cloud Foundry Maven Plugin:

		<build>
			...
		    <plugins>
				...
		        <plugin>
					<groupId>org.cloudfoundry</groupId>
					<artifactId>cf-maven-plugin</artifactId>
					<version>1.0.0.M2</version>
					<configuration>
						<command>bin/demo</command>
						<framework>standalone</framework>
						<memory>256</memory>
						<path>target/appassembler</path>
						<services>
							<service>
								<name>myRabbitService</name>
								<vendor>rabbitmq</service>
							</service>
						</services>
						<target>http://api.cloudfoundry.com</target>
					</configuration>
		        </plugin>
				...
		    </plugins>
			...
		</build>

5. Deploy the application:

		$ mvn cf:push -Dcf.username=your_username -Dcf.password=yu0r p455w0rd

## Deploying a Web Application

1. Checkout the sample using [GIT](http://git-scm.com/), IF you have not checked it out per the previous example:

		$ git clone git://github.com/ghillert/twitter-rabbit-socks-sample.git

2. Go to the `twitter2rabbit` directory

		$ cd twitter-rabbit-socks-sample/rabbit2spring

3. Add the Cloud Foundry Maven Plugin:

		<build>
			...
		    <plugins>
				...
		        <plugin>
					<groupId>org.cloudfoundry</groupId>
					<artifactId>cf-maven-plugin</artifactId>
					<version>1.0.0.M2</version>
					<configuration>
						<target>http://api.cloudfoundry.com</target>
						<url>spring-integration-twitter.cloudfoundry.com</url>
						<memory>256</memory>
						<services>
							<service>
								<name>myRabbitService</name>
								<vendor>rabbitmq</vendor>
							</service>
						</services>
					</configuration>
		        </plugin>
				...
		    </plugins>
			...
		</build>

5. Deploy the application:

		$ mvn cf:push -Dcf.username=your_username -Dcf.password=yu0r p455w0rd

# History

## Changes from version 1.0.0.M3 to 1.0.0.M4

* Upgraded to cloudfoundry-client-lib 0.8.2
* Modified the output from older grid style to cleaner column style
* Add Cloud Controller v1 / v2 (cloud\_controller\_ng) detection
* Add support for org & space in v2 (cloud\_controller\_ng)
* Add support for push & delete app to v2 (cloud\_controller\_ng)
* Add show log for v2 (cloud\_controller\_ng)
* Add support for create-services for v2 (cloud\_controller\_ng)

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
* Add support for deployments of stand-alone applications by specifying **standalone** as the *framework*
* Deprecated **warfile** as now you can deploy stand-alone applications as well. Behavior is equal to the **path** property.
* Added **path** configuration property (-Dcf.path).
* Added **Runtime** property (-Dcf.runtime). It defaults to 'java' but technically you could also use the Maven Plugin to deploy e.g. Node and Ruby applications.
* Improved **cf:info** Maven goal. It will now show a list of available **frameworks** and **system services**, as well as a list of available **runtimes***
* **instances** property now defaults to *1*.

# Resources

http://blog.springsource.com/2011/09/22/rapid-cloud-foundry-deployments-with-maven/
