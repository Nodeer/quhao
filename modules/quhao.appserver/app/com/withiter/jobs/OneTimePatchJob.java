package com.withiter.jobs;

import play.jobs.Job;
import play.jobs.OnApplicationStart;

import com.withiter.models.patch.OnetimePatch;

@OnApplicationStart
public class OneTimePatchJob extends Job {

	@Override
	public void doJob() throws Exception {
		OnetimePatch.registerAllOnetimePatch();
		OnetimePatch.executeAll();
	}
	
}
