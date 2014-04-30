package com.withiter.jobs;

import com.withiter.models.merchant.Category;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.morphia.Model.MorphiaQuery;

@OnApplicationStart
public class StartupJob extends Job {
	@Override
	public void doJob() throws Exception {
		MorphiaQuery q = Category.q();
		if(q.count() == 0){
			// 初始化Category
			Logger.debug("start to initial the category");
			Category.init();
		}
	}
}
