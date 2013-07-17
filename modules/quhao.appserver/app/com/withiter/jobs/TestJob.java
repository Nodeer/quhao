package com.withiter.jobs;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Job;
import play.jobs.OnApplicationStart;

import com.withiter.common.Constants.CateType;
import com.withiter.models.merchant.Category;

@OnApplicationStart
public class TestJob extends Job {

	private static Logger logger = LoggerFactory.getLogger(TestJob.class);
	
	@Override
	public void doJob() throws Exception {
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				long start = System.currentTimeMillis();
				logger.info(TestJob.class.getName() + " started.");
				createCategory();
				logger.info(TestJob.class.getName() + " finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
			}
		});
		
		t.start();
	}
	
	private static void createCategory(){
		
		CateType[] cts = CateType.values();
		Random r = new Random();
		
		Category category = null;
		for(int i=0; i < 100; i++){
			int rn = r.nextInt(cts.length);
			CateType type = cts[rn];
			category = new Category(type, (int)(Math.random()*100)+1);
			category.create();
		}
	}
}
