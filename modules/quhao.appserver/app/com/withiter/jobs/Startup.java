package com.withiter.jobs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
@Every("10min")
public class Startup extends Job {
	private static Logger logger = LoggerFactory.getLogger(Startup.class);
	
	@Override
	public void doJob() {
		logger.info("play job test.");
	}
}
