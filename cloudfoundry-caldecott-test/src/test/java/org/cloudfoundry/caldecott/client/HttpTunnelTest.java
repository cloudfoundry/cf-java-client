package org.cloudfoundry.caldecott.client;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.caldecott.TunnelException;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.codehaus.jackson.map.ObjectMapper;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.System;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Integration tests for testing each data store supported.
 * Starts a tunnel and tests the tunneling functionality.
 *
 * @author Thomas Risberg
 */
public class HttpTunnelTest {

	protected final Log logger = LogFactory.getLog(getClass());

	private static final String VCAP_TARGET = System.getProperty("vcap.target", "https://api.cloudfoundry.com");
	private static final String VCAP_EMAIL = System.getProperty("vcap.email", "cloud@springdeveloper.com");
	private static final String VCAP_PASSWD = System.getProperty("vcap.passwd");
	private static final String VCAP_ORG = System.getProperty("vcap.org", "springdeveloper.com");
	private static final String VCAP_SPACE = System.getProperty("vcap.space", "test");
	private static final String VCAP_MYSQL_SERVICE = "mysql-caldecott-test";
	private static final String VCAP_POSTGRES_SERVICE = "postgres-caldecott-test";
	private static final String VCAP_MONGO_SERVICE = "mongo-caldecott-test";
	private static final String VCAP_REDIS_SERVICE = "redis-caldecott-test";
	private static final String VCAP_RABBIT_SERVICE = "rabbit-caldecott-test";
	public static final int LOCAL_PORT = 10000;
	public static final String LOCAL_HOST = "127.0.0.1";

	private CloudFoundryClient client;
	private String svc_username;
	private String svc_passwd;
	private String svc_dbname;
	private String svc_vhost;
	private TunnelServer tunnelServer;

	private DataSource ds;

	@BeforeClass
	public static void printTargetInfo() {
		System.out.println("Running tests on " + VCAP_TARGET + " on behalf of " + VCAP_EMAIL);
		if (VCAP_PASSWD == null) {
			Assert.fail("System property vcap.passwd must be specified, supply -Dvcap.passwd=<password>");
		}
	}

	@Before
	public void setUp() throws Exception {
		client = clientInit();
		checkForCaldecottServerApp();
	}

	@Test
	public void testTunnel() {
		logger.info("Tunnel using " + VCAP_TARGET + " for user " + VCAP_EMAIL);
		logger.info("Auth token is  " + TunnelHelper.getTunnelAuth(client));
	}

	@Test
	public void testMysql() {
		long start = System.currentTimeMillis();
		logger.info("MySQL:");
		createService(VCAP_MYSQL_SERVICE, "mysql", "5.1");
		int tunnelPort = LOCAL_PORT;
		createTunnelServer(VCAP_MYSQL_SERVICE, tunnelPort);
		ds = new SimpleDriverDataSource();
		String url = "jdbc:mysql://"+ LOCAL_HOST + ":" + tunnelPort + "/" + svc_dbname + "?rewriteBatchedStatements=true";
		((SimpleDriverDataSource)ds).setDriverClass(com.mysql.jdbc.Driver.class);
		((SimpleDriverDataSource)ds).setUrl(url);
		((SimpleDriverDataSource)ds).setUsername(svc_username);
		((SimpleDriverDataSource)ds).setPassword(svc_passwd);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
		jdbcTemplate.execute("create table records (id smallint, " +
				"name varchar(100), address varchar(100), city varchar(100), country varchar(100), " +
				"age smallint)");
		Connection con = DataSourceUtils.getConnection(ds);
		IDatabaseConnection dbUnitCon = null;
		try {
			dbUnitCon = new DatabaseConnection(con);
		} catch (DatabaseUnitException e) {
			System.out.println(e);
		}
		dbUnitCon.getConfig().setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, Boolean.TRUE);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		IDataSet dataSet = null;
		try {
			dataSet = new XmlDataSet(new FileInputStream("data/load.xml"));
		} catch (IOException e) {
			System.out.println(e);
		} catch (DataSetException e) {
			System.out.println(e);
		}
		try {
			try {
				DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, dataSet);
			} catch (DatabaseUnitException e) {
				System.out.println(e);
			} catch (SQLException e) {
				System.out.println(e);
			}
		} finally {
			DataSourceUtils.releaseConnection(con, ds);
		}

		int count = jdbcTemplate.queryForInt("select count(*) from records");
		Assert.assertEquals("Did not load the data correctly", 200, count);

		stopTunnelServer();
		removeService(VCAP_MYSQL_SERVICE);
		logger.info("Time elapsed: " + (System.currentTimeMillis() - start) / 1000.0d + " sec");
	}

	@Test
	public void testPostgreSql() {
		long start = System.currentTimeMillis();
		logger.info("PostgreSQL:");
		createService(VCAP_POSTGRES_SERVICE, "postgresql", "9.0");
		int tunnelPort = LOCAL_PORT + 1;
		createTunnelServer(VCAP_POSTGRES_SERVICE, tunnelPort);
		ds = new SimpleDriverDataSource();
		String url = "jdbc:postgresql://"+ LOCAL_HOST + ":" + tunnelPort + "/" + svc_dbname;
		((SimpleDriverDataSource)ds).setDriverClass(org.postgresql.Driver.class);
		((SimpleDriverDataSource)ds).setUrl(url);
		((SimpleDriverDataSource)ds).setUsername(svc_username);
		((SimpleDriverDataSource)ds).setPassword(svc_passwd);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
		jdbcTemplate.execute("create table records (id smallint, " +
				"name varchar(100), address varchar(100), city varchar(100), country varchar(100), " +
				"age smallint)");
		// Test data
		Connection con = DataSourceUtils.getConnection(ds);
		IDatabaseConnection dbUnitCon = null;
		try {
			dbUnitCon = new DatabaseConnection(con);
		} catch (DatabaseUnitException e) {
			System.out.println(e);
		}
		dbUnitCon.getConfig().setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, Boolean.TRUE);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
		IDataSet dataSet = null;
		try {
			dataSet = new XmlDataSet(new FileInputStream("data/load.xml"));
		} catch (IOException e) {
			System.out.println(e);
		} catch (DataSetException e) {
			System.out.println(e);
		}
		try {
			try {
				DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, dataSet);
			} catch (DatabaseUnitException e) {
				System.out.println(e);
			} catch (SQLException e) {
				System.out.println(e);
			}
		} finally {
			DataSourceUtils.releaseConnection(con, ds);
		}

		int count = jdbcTemplate.queryForInt("select count(*) from records");
		Assert.assertEquals("Did not load the data correctly", 200, count);

		stopTunnelServer();
		removeService(VCAP_POSTGRES_SERVICE);
		logger.info("Time elapsed: " + (System.currentTimeMillis() - start) / 1000.0d + " sec");
	}

	@Test
	public void testMongoDb() throws UnknownHostException {
		long start = System.currentTimeMillis();
		logger.info("MongoDB:");
		createService(VCAP_MONGO_SERVICE, "mongodb", "1.8");
		int tunnelPort = LOCAL_PORT + 2;
		createTunnelServer(VCAP_MONGO_SERVICE, tunnelPort);
		Mongo mongo = new Mongo(LOCAL_HOST, tunnelPort);
		MongoDbFactory mdbf = new SimpleMongoDbFactory(mongo, svc_dbname, new UserCredentials(svc_username, svc_passwd));
		MongoTemplate mongoTemplate = new MongoTemplate(mdbf);
		// Test data
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,Object> dataMap = null;
		try {
			dataMap = objectMapper.readValue(new File("data/load.json"), Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> l = (List<Map<String, Object>>) dataMap.get("records");
		for (Map<String, Object> m : l) {
			Map<String, Object> rec = (Map<String, Object>) m.get("record");
			final BasicDBObject dbo = new BasicDBObject();
			dbo.putAll(rec);
			mongoTemplate.execute("records", new CollectionCallback<Object>() {
				public Object doInCollection(DBCollection collection) throws MongoException, DataAccessException {
					collection.insert(dbo);
					return null;
				}
			});
		}
		List records = mongoTemplate.findAll(BasicDBObject.class, "records");
		Assert.assertEquals("Did not load the data correctly", 200, records.size());
		mongo.close();

		stopTunnelServer();
		removeService(VCAP_MONGO_SERVICE);
		logger.info("Time elapsed: " + (System.currentTimeMillis() - start) / 1000.0d + " sec");
	}

	@Test
	public void testRedis() {
		long start = System.currentTimeMillis();
		logger.info("Redis:");
		createService(VCAP_REDIS_SERVICE, "redis", "2.2");
		int tunnelPort = LOCAL_PORT + 3;
		createTunnelServer(VCAP_REDIS_SERVICE, tunnelPort);

		JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
		connectionFactory.setHostName(LOCAL_HOST);
		connectionFactory.setPort(tunnelPort);
		connectionFactory.setPassword(svc_passwd);
		connectionFactory.setUsePool(true);
		connectionFactory.afterPropertiesSet();
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.afterPropertiesSet();

		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

		// Test data
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,Object> dataMap = null;
		try {
			dataMap = objectMapper.readValue(new File("data/load.json"), Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> l = (List<Map<String, Object>>) dataMap.get("records");
		Map<String, String> values = new HashMap<String, String>();
		List<String> ids = new ArrayList<String>();
		for (Map<String, Object> m : l) {
			Map<String, Object> rec = (Map<String, Object>) m.get("record");
			String id = "rec-" + rec.get("_id");
			values.put(id, (String) rec.get("name"));
			ids.add(id);
		}
		valueOps.multiSet(values);

		List<String> names = valueOps.multiGet(ids);

		Assert.assertEquals("Did not load the data correctly", 200, names.size());

		connectionFactory.destroy();

		stopTunnelServer();
		removeService(VCAP_REDIS_SERVICE);
		logger.info("Time elapsed: " + (System.currentTimeMillis() - start) / 1000.0d + " sec");
	}

	@Test
	public void testRabbit() {
		long start = System.currentTimeMillis();
		logger.info("RabbitMQ:");
		createService(VCAP_RABBIT_SERVICE, "rabbitmq", "2.4");
		int tunnelPort = LOCAL_PORT + 4;
		createTunnelServer(VCAP_RABBIT_SERVICE, tunnelPort);

		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(LOCAL_HOST, tunnelPort);
		connectionFactory.setUsername(svc_username);
		connectionFactory.setPassword(svc_passwd);
		connectionFactory.setVirtualHost(svc_vhost);

		String queueName = "CLOUD";
		AmqpAdmin amqpAdmin = new RabbitAdmin(connectionFactory);
		Queue cloudQueue = new Queue(queueName);
		amqpAdmin.declareQueue(cloudQueue);

		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setRoutingKey(queueName);
		template.setQueue(queueName);
		template.afterPropertiesSet();

		template.convertAndSend("Hello, CloudFoundry!");
		template.convertAndSend("Hello, Spring!");
		template.convertAndSend("Hello, Java!");
		template.convertAndSend("Hello, Caldecott!");
		template.convertAndSend("Hello, Rabbit!");
		template.convertAndSend("Hello, AMQP!");

		int count = 0;
		while (true) {
			String message = (String) template.receiveAndConvert();
			if (message == null) {
				break;
			}
			else {
				count++;
			}
		}

		Assert.assertEquals("Did not send/receive the messages correctly", 6, count);

		connectionFactory.destroy();

		stopTunnelServer();
		removeService(VCAP_RABBIT_SERVICE);
		logger.info("Time elapsed: " + (System.currentTimeMillis() - start) / 1000.0d + " sec");
	}

	@After
	public void cleanup() {
		deleteCaldecottServerApp();
		finalize(this.client);
	}

	private void deleteCaldecottServerApp() {
		CloudApplication app =  null;
		try {
			app = client.getApplication(TunnelHelper.getTunnelAppName());
		} catch (CloudFoundryException ignore) {}
		if (app != null) {
			client.deleteApplication(TunnelHelper.getTunnelAppName());
		}
	}

	private void checkForCaldecottServerApp() throws Exception {
		CloudApplication app =  null;
		try {
			try {
				app = client.getApplication(TunnelHelper.getTunnelAppName());
			} catch (CloudFoundryException ignore) {}
			if (app == null) {
				TunnelHelper.deployTunnelApp(client);
				app = client.getApplication(TunnelHelper.getTunnelAppName());
			}
		} catch (Exception e) {
			logger.error("Error deploying Caldecott app", e);
			throw new TunnelException("Error deploying Caldecott app", e);
		}
	}

	private void createService(String dbSvcName, String dbType, String dbVersion) {
		try {
			client.getService(dbSvcName);
			client.deleteService(dbSvcName);
		} catch (Exception ignore) {}
		client.stopApplication(TunnelHelper.getTunnelAppName());
		CloudService cloudSvc = new CloudService();
		cloudSvc.setName(dbSvcName);
		cloudSvc.setVersion(dbVersion);
		// for v1
		cloudSvc.setVendor(dbType);
		cloudSvc.setTier("free");
		// for v2
		cloudSvc.setLabel(dbType);
		cloudSvc.setPlan("D100");
		cloudSvc.setProvider("core");
		// create service
		client.createService(cloudSvc);
		client.bindService(TunnelHelper.getTunnelAppName(), dbSvcName);
		client.startApplication(TunnelHelper.getTunnelAppName());
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {}
	}

	private void removeService(String dbSvcName) {
		client.stopApplication(TunnelHelper.getTunnelAppName());
		client.unbindService(TunnelHelper.getTunnelAppName(), dbSvcName);
		client.startApplication(TunnelHelper.getTunnelAppName());
		client.deleteService(dbSvcName);
	}

	private void createTunnelServer(String dbSvcName, int tunnelPort) {
		logger.info("Starting tunnel on " + LOCAL_HOST +" port " + tunnelPort);
		InetSocketAddress local = new InetSocketAddress(LOCAL_HOST, tunnelPort);
		String url = TunnelHelper.getTunnelUri(client);
		Map<String, String> info = TunnelHelper.getTunnelServiceInfo(client, dbSvcName);
		String host = info.get("hostname");
		int port = Integer.valueOf(info.get("port"));
		String auth = TunnelHelper.getTunnelAuth(client);
		svc_username = info.get("username");
		svc_passwd = info.get("password");
		svc_dbname = info.get("db") != null ? info.get("db") : info.get("name");
		svc_vhost = info.get("vhost");
		tunnelServer = new TunnelServer(local, new HttpTunnelFactory(url, host, port, auth));
		tunnelServer.start();
	}

	private void stopTunnelServer() {
		tunnelServer.stop();
	}

	private static CloudFoundryClient clientInit() {
		URL vcapUrl = null;
		try {
			vcapUrl = new URL(VCAP_TARGET);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		CloudFoundryClient client = new CloudFoundryClient(new CloudCredentials(VCAP_EMAIL, VCAP_PASSWD), vcapUrl);
		client.login();
		String version = client.getCloudInfo().getVersion();
		if (Float.valueOf(version) >= 2.0) {
			client = new CloudFoundryClient(new CloudCredentials(VCAP_EMAIL, VCAP_PASSWD), vcapUrl, getSpace(client));
			client.login();
		}
		return client;
	}

	private static CloudSpace getSpace(CloudFoundryClient client) {
		List<CloudSpace> spaces = client.getSpaces();
		CloudSpace useSpace = null;
		for (CloudSpace space : spaces) {
			if (space.getOrganization().getName().equals(VCAP_ORG) && space.getName().equals(VCAP_SPACE)) {
				useSpace = space;
				break;
			}
		}
		return useSpace;
	}

	private static void finalize(CloudFoundryClient client) {
		client.logout();
	}
}
