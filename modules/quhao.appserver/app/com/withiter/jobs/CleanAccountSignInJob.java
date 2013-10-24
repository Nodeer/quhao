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
import play.jobs.OnApplicationStart;

@OnApplicationStart
@Every("5min")
public class CleanAccountSignInJob extends Job {

	private static Logger logger = LoggerFactory
			.getLogger(CleanAccountSignInJob.class);

	@Override
	public void doJob() throws Exception {

		Thread t = new Thread(new Runnable() {
			public void run() {
				long start = System.currentTimeMillis();
				logger.info(CleanAccountSignInJob.class.getName() + " started.");
				Calendar calendar = Calendar.getInstance();
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);

				if (hour == 23) {
					if (51 < minute && minute < 60) {
						Account.cleanSignUp();
					}
				}
				logger.info(CleanAccountSignInJob.class.getName() + " finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");

			}
		});
		t.start();
	}
}
