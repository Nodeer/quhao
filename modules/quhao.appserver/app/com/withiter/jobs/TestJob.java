package com.withiter.jobs;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.morphia.Model.MorphiaQuery;

import com.withiter.common.Constants.CateType;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.TopMerchant;

@OnApplicationStart
public class TestJob extends Job {

	private static Logger logger = LoggerFactory.getLogger(TestJob.class);
	
	@Override
	public void doJob() throws Exception {
//		Thread t1 = new Thread(new Runnable() {
//			public void run() {
//				long start = System.currentTimeMillis();
//				logger.info(TestJob.class.getName() + ": category job started.");
//				removeCategory();
//				createCategory();
//				logger.info(TestJob.class.getName() + ": category job finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
//			}
//		});
//		Thread t2 = new Thread(new Runnable() {
//			public void run() {
//				long start = System.currentTimeMillis();
//				logger.info(TestJob.class.getName() + ": merchant job started.");
//				removeMerchant();
//				createMerchant();
//				logger.info(TestJob.class.getName() + ": merchant job finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
//			}
//		});
//		t1.start();
//		t2.start();
	}
	
}
