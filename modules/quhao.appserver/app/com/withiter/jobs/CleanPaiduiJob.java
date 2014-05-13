package com.withiter.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;

import com.withiter.models.merchant.Haoma;

//@OnApplicationStart(async=true)

// 每天1:00AM开始job
@On("0 0 3 * * ?")

public class CleanPaiduiJob extends Job {
	
	private static Logger logger = LoggerFactory.getLogger(CleanPaiduiJob.class);

	@Override
	public void doJob() throws Exception {
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				long start = System.currentTimeMillis();
				logger.info(CleanPaiduiJob.class.getName() + " started.");
				Haoma.clearPaidui();
				logger.info(CleanPaiduiJob.class.getName() + " finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
			}
		});
		
		t.start();
	}
}
