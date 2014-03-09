package com.withiter.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.withiter.models.account.Account;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.TopMerchant;

import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
@Every("1min")
public class RefreshAccountDianPingJob extends Job {

	private static Logger logger = LoggerFactory
			.getLogger(RefreshAccountDianPingJob.class);

	@Override
	public void doJob() throws Exception {

		Thread t = new Thread(new Runnable() {
			public void run() {
				long start = System.currentTimeMillis();
				logger.info(RefreshAccountDianPingJob.class.getName() + " started.");
				
				List<Account> accounts = Account.findAll();
				
				for (Account account : accounts) {
					int commentCount = Comment.findbyAccountId(account.getId());
					account.dianping = commentCount;
					account.save();
				}
				
				logger.info(RefreshAccountDianPingJob.class.getName() + " finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");

			}
		});
		t.start();
	}
}
