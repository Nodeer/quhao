package com.withiter.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.withiter.models.merchant.Category;

import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
@Every("1min")
public class CategoryJob extends Job {
	
	// TODO remove this job

//	private static Logger logger = LoggerFactory.getLogger(CategoryJob.class);
//	
//	@Override
//	public void doJob() throws Exception {
//		
//		Thread t = new Thread(new Runnable() {
//			public void run() {
//				long start = System.currentTimeMillis();
//				logger.info(CategoryJob.class.getName() + " started.");
//				Category.updateCounts();
//				logger.info(CategoryJob.class.getName() + " finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
//			}
//		});
//		
//		t.start();
//	}
}
