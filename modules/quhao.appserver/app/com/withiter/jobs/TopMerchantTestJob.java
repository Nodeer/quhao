package com.withiter.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.TopMerchant;

import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
@Every("1min")
public class TopMerchantTestJob extends Job {

	private static Logger logger = LoggerFactory.getLogger(TopMerchantTestJob.class);
	
	@Override
	public void doJob() throws Exception {
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				long start = System.currentTimeMillis();
				logger.info(TopMerchantTestJob.class.getName() + " started.");
				TopMerchant.updateTopMerchantForTest();
				logger.info(TopMerchantTestJob.class.getName() + " finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
			}
		});
		
		t.start();
	}
}
