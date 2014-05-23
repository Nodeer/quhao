package com.withiter.jobs;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.withiter.models.account.Account;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.TopMerchant;

import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;

// the job will fire at 3AM everyday. reference: http://www.playframework.com/documentation/1.2.5/jobs
@On("0 0 3 * * ?")

public class CleanAccountSignInJob extends Job {

	private static Logger logger = LoggerFactory.getLogger(CleanAccountSignInJob.class);

	@Override
	public void doJob() throws Exception {

		long start = System.currentTimeMillis();
		logger.info(CleanAccountSignInJob.class.getName() + " started.");
		Account.cleanSignIn();
		logger.info(CleanAccountSignInJob.class.getName() + " finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
		
		// 检查置顶商家
		long start1 = System.currentTimeMillis();
		logger.info(CleanAccountSignInJob.class.getName() + " top merchant update started.");
		TopMerchant.verifyAndupdateTops();
		logger.info(CleanAccountSignInJob.class.getName() + " top merchant update finished, elapsed time " + (System.currentTimeMillis() - start1) + "ms.");
		
		

	}
}
