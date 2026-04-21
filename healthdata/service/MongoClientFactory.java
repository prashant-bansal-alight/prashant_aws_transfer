package com.alight.enrollnment.healthdata.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.bson.Document;

import com.alight.asg.util.crypto.BlowfishDecrypt;
/*import com.aonhewitt.cts.logging.DebugLog;
import com.aonhewitt.cts.util.CtsException;
import com.aonhewitt.cts.util.CtsExceptionCodeConstants;
import com.aonhewitt.cts.util.crypto.BlowfishDecrypt;
import com.aonhewitt.udp.tba.hm.config.JobProperties;
import com.aonhewitt.udp.tba.hm.config.JobPropertiesConstants;*/
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoClientFactory {

	/**
	 * Singleton instance for MongoClientFactory
	 * 
	 * @since 1.0
	 */
	private static MongoClientFactory instance = null;

	/**
	 * Map to keep track of MongoClient instances
	 * 
	 * @since 1.0
	 */
	private Map<String, MongoClient> mClientMap = null;

	private MongoClientFactory() {
		mClientMap = new HashMap<String, MongoClient>();
	}

	/**
	 * Get instance of Singleton object
	 * 
	 * @return
	 * @throws CtsException
	 */
	public static synchronized MongoClientFactory getInstance() {

		if (instance == null) {
			instance = new MongoClientFactory();
		}

		return instance;
	}

	public void releaseAll() {

		for (MongoClient client : mClientMap.values()) {
			client.close();
		}
	}

	public void releaseConnection(MongoClient aClient) {

		aClient.close();
	}

	public MongoDatabase getDatabase(String instance) {

		String dbName = "udpdev";
		return getConnection(instance, dbName).getDatabase(dbName);
	}

	public MongoClient getConnection(String instance, String dbName) {

		MongoClient client = null;

		synchronized (mClientMap) {
			client = mClientMap.get(dbName);
			if (client == null) {
				client = createConnection(instance, dbName);
				mClientMap.put(dbName, client);
			}
		}

		return client;
	}

	private MongoClient createConnection(String instance, String dbName) {

		MongoClient mongoClient = null;

		// First, create the list of servers to connect to.
		ArrayList<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
		String dbUrl = "udp-mongodb1-dld.iud-dev.aws.alight.com:27040";
		StrTokenizer t = new StrTokenizer(dbUrl, ",");

		while (t.hasNext()) {

			String server = t.next();

			if (server.contains(":")) {

				String host = StringUtils.substringBefore(server, ":");
				String port = StringUtils.substringAfter(server, ":");
				int iPort = NumberUtils.toInt(port);
				serverAddresses.add(new ServerAddress(host, iPort));
			} else {
				serverAddresses.add(new ServerAddress(server));
			}
		}
		long serverSelectionTimeout = 5000;
		MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();
		settingsBuilder.applyToClusterSettings(builder -> {
			builder.hosts(serverAddresses).serverSelectionTimeout(serverSelectionTimeout, TimeUnit.MILLISECONDS);
		});

		// Next, configure the connection pool
		long maxConnectionIdleTime = NumberUtils.toLong("300000");

		long maxWaitTime = NumberUtils.toLong("5000");
		int minPoolSize = NumberUtils.toInt("1");
		int maxPoolSize = NumberUtils.toInt("20");

		settingsBuilder.applyToConnectionPoolSettings(builder -> {
			builder.maxConnectionIdleTime(maxConnectionIdleTime, TimeUnit.MILLISECONDS);
			builder.minSize(minPoolSize);
			builder.maxSize(maxPoolSize);
			builder.maxWaitTime(maxWaitTime, TimeUnit.MILLISECONDS);
		});

		int connectTimeout = NumberUtils.toInt("5000");
		settingsBuilder.applyToSocketSettings(builder -> {
			builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
		});

		boolean sslEnabled = true;
		settingsBuilder.applyToSslSettings(builder -> {
			builder.enabled(sslEnabled);
			builder.invalidHostNameAllowed(true);
		});

		// Finally, create the credentials object to authenticate and set the read
		// preference and write concern
		
			String encryptedUser = "w+3DgbFCf8i=";
			String encryptedPwd = "qqS2DFQLXCMUHiFvqtDf7w==";
			/*
			 * System.out.println(BlowfishDecrypt.decrypt(encryptedUser));
			 * System.out.println(BlowfishDecrypt.decrypt(encryptedPwd));
			 */
			if (encryptedUser != null && encryptedPwd != null) {
				MongoCredential creds = MongoCredential.createCredential
						("udprw", dbName,
						"udprw".toCharArray());
				settingsBuilder.credential(creds);
			}
		

		String readPreference ="primaryPreferred";
		if (StringUtils.isNotBlank(readPreference)) {
			settingsBuilder.readPreference(ReadPreference.valueOf(readPreference));
		}

		String writeConcernProp = "w: majority";
		String acknowledgementTimeout = "5000";
		WriteConcern writeConcern = null;
		if (StringUtils.isNotBlank(writeConcernProp)) {
			writeConcern = WriteConcern.valueOf(writeConcernProp);
		}
		if (StringUtils.isNotBlank(acknowledgementTimeout)) {
			if (writeConcern == null) {
				writeConcern = WriteConcern.ACKNOWLEDGED;
			}
			writeConcern = writeConcern.withWTimeout(Integer.parseInt(acknowledgementTimeout), TimeUnit.MILLISECONDS);
		}
		if (writeConcern != null) {
			settingsBuilder.writeConcern(writeConcern);
		}

		MongoClientSettings settings = settingsBuilder.build();
		try {
			mongoClient = MongoClients.create(settings);

			// Force Mongo to ping the database to make sure everything is ok with
			// the connection.
			/*
			 * DebugLog.logDebugEvent(this.getClass().getName(), "", "createConnection",
			 * "Connected to MongoDb. Attempting to ping " + dbName +
			 * " to verify connection.");
			 */ mongoClient.getDatabase(dbName).runCommand(new Document("ping", 1));

		} catch (MongoException me) {

		}

		return mongoClient;
	}

	/*
	 * private String getConfigurationProperty(String instance, String propertyName)
	 * {
	 * 
	 * 
	 * // MongoDB properties will be prefixed with "mongo.". If the properties //
	 * need to differ by instance, the prefix will be "<instance>.mongo." String
	 * propVal = null; // JobProperties props = JobProperties.getInstance();
	 * 
	 * StringBuilder lookupName = new StringBuilder();
	 * lookupName.append(instance).append('.').append(propertyName); propVal =
	 * props.get(lookupName.toString()); if (StringUtils.isBlank(propVal)) {
	 * 
	 * propVal = props.get(propertyName); }
	 * 
	 * return propVal; }
	 */
}