package com.withiter.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.morphia.Model.MorphiaQuery;

import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.Merchant;

@OnApplicationStart(async=true)
@Every("1h")
public class UpdateMerchantEvaluate extends Job {
	private static Logger logger = LoggerFactory.getLogger(UpdateMerchantEvaluate.class);

	@Override
	public void doJob() throws Exception {

		Thread t = new Thread(new Runnable() {
			public void run() {
				long start = System.currentTimeMillis();
				logger.info("job started.");
				
				// TODO optimize business handler
				MorphiaQuery q = Merchant.q();
				q.filter("enable", true);
				List<Merchant> mList = q.asList();
				
				String mid = ""; 
				for(Merchant m : mList){
					mid = m.id();
					MorphiaQuery commentQ = Comment.q();
					commentQ.filter("mid", mid);
					if(commentQ.count() == 0){
						continue;
					}
					long xingjiabi =  commentQ.average("xingjiabi");
					long kouwei =  commentQ.average("kouwei");
					long huanjing =  commentQ.average("huanjing");
					long fuwu =  commentQ.average("fuwu");
					long grade =  commentQ.average("grade");
					long averageCost =  commentQ.average("averageCost");
					
					logger.debug("merchant " + m.name + ", xingjiabi :" + xingjiabi);
					logger.debug("merchant " + m.name + ", kouwei :" + kouwei);
					logger.debug("merchant " + m.name + ", huanjing :" + huanjing);
					logger.debug("merchant " + m.name + ", fuwu :" + fuwu);
					logger.debug("merchant " + m.name + ", grade :" + grade);
					logger.debug("merchant " + m.name + ", averageCost :" + averageCost);
					
					m.xingjiabi = xingjiabi;
					m.kouwei = kouwei;
					m.huanjing = huanjing;
					m.fuwu = fuwu;
					m.grade = grade;
					m.averageCost = averageCost;
					
					m.save();
				}
				
				Merchant.updateMerchantEvaluate();
				logger.info("job finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
			}
		});

		t.start();
	}
}
