package com.withiter.jobs;

import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class OneTimePatchJob extends Job {

	@Override
	public void doJob() throws Exception {
//		SchoolIdentityService.getInstance().createWriter();
//		try {
//			HighSchoolImporter.importHighSchools();
//		} catch (Exception e) {
//			Logger.error(e, "Import high school data failed");
//		}
//		try {
//			USCityImporter.importUSCitys();
//		} catch (Exception e) {
//			Logger.error(e, "Import US City data failed");
//		}
//		try {
//			TestScoresImporter.importTestScores();
//		} catch (Exception e) {
//			Logger.error(e, "Import test scores data failed");
//		}
//
//		try {
//			SchoolDeadlineImporter.importSchoolDeadlines();
//		} catch (Exception e) {
//			Logger.error(e, "Import School Deadline data failed");
//		}
//		try {
//			SchoolRelatedImporter.importSchoolRelateds();
//		} catch (Exception e) {
//			Logger.error(e, "Import School Related data failed");
//		}
//
//		try {
//			SchoolLogosImporter.importSchoolRelateds();
//		} catch (Exception e) {
//			Logger.error(e, "Import School Related data failed");
//		}
//
//		try {
//			SchoolPropertyImporter.importSchoolProperty();
//		} catch (Exception e) {
//			Logger.error(e, "Import School Property data failed");
//		}
//
//		try {
//			DefaultCollectionsImporter.importDefaultCollections();
//		} catch (Exception e) {
//			Logger.error(e, "Import Default Collections Error");
//		}
//
//		try {
//			MorphiaPlugin.ds().getMongo().getDB("applyful-dev-db").getCollection("School").ensureIndex(new BasicDBObject("latLng", "2d"));
//		} catch (Exception e) {
//			Logger.error(e, "Init MongoDB Error");
//		}
//
//		OnetimePatch.registerAllOnetimePatch();
//		OnetimePatch.executeAll();
	}
	
}
