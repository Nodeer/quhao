package com.withiter.jobs;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.withiter.models.account.Account;
import com.withiter.models.activity.Activity;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.TopMerchant;

import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;

// the job will fire at 3AM everyday. reference: http://www.playframework.com/documentation/1.2.5/jobs
@On("0 0 3 * * ?")

public class ThreeAmJob extends Job {

	private static Logger logger = LoggerFactory.getLogger(ThreeAmJob.class);

	@Override
	public void doJob() throws Exception {

		long start = System.currentTimeMillis();
		logger.info(ThreeAmJob.class.getName() + " started.");
		Account.cleanSignIn();
		logger.info(ThreeAmJob.class.getName() + " finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
		
		// 检查置顶商家
		long start1 = System.currentTimeMillis();
		logger.info(ThreeAmJob.class.getName() + " top merchant update started.");
		TopMerchant.verifyAndupdateTops();
		logger.info(ThreeAmJob.class.getName() + " top merchant update finished, elapsed time " + (System.currentTimeMillis() - start1) + "ms.");
		
		// 检查活动
		long start2 = System.currentTimeMillis();
		logger.info(ThreeAmJob.class.getName() + " top merchant update started.");
		Activity.verifyAndupdateActivity();
		logger.info(ThreeAmJob.class.getName() + " top merchant update finished, elapsed time " + (System.currentTimeMillis() - start2) + "ms.");
	}
}
