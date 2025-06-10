package com.lawsuittracker.dao;

import java.util.Arrays;

import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

	public static MongoDatabase getMongoDB() {
		MongoDatabase database = null;
		MongoClient mongo = null;
		try {
			MongoCredential credential = MongoCredential.createCredential(LawSuitTrackerConstants.MongoDBUserName,
					LawSuitTrackerConstants.MongoAuthDBName, LawSuitTrackerConstants.MongoDBPassword.toCharArray());
			mongo = new MongoClient(
					new ServerAddress(LawSuitTrackerConstants.MongoDBHost, LawSuitTrackerConstants.MongoDBPort),
					Arrays.asList(credential));
			// , MongoClientOptions.builder().serverSelectionTimeout(3000).build());

			database = mongo.getDatabase(LawSuitTrackerConstants.MongoDBName);

		} catch (Exception e) {
			e.printStackTrace();
		}
//		finally {
//			mongo.close();
//		}
		return database;
	}
}