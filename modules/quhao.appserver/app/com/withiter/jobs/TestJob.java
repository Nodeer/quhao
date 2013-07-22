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
	
	private static void removeCategory(){
		MorphiaQuery q = Category.q();
		q.delete();
	}
	
	private static void createCategory(){
		CateType[] cts = CateType.values();
		Random r = new Random();
		
		Category category = null;
		for(int i=0; i < 100; i++){
			int rn = r.nextInt(cts.length);
			String type = cts[rn].toString();
			category = new Category(type, (int)(Math.random()*100)+1);
			category.create();
		}
	}
	
	private static void removeMerchant(){
		MorphiaQuery q = Merchant.q();
		q.delete();
	}
	
	private static void createMerchant(){
		CateType[] cts = CateType.values();
		Random r = new Random();
		
		Merchant merchant = null;
		for(int i=0; i < 20; i++){
			int rn = r.nextInt(cts.length);
			CateType type = cts[rn];
			merchant = new Merchant();
			
			merchant.name = "merchant"+i;
			merchant.cateType = type.toString();
			
			merchant.create();
		}
	}
}
