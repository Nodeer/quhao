package com.withiter.jobs;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.Codec;
import play.modules.morphia.Model.MorphiaQuery;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;

@OnApplicationStart
public class TestJob extends Job {

	private static Logger logger = LoggerFactory.getLogger(TestJob.class);
	
	@Override
	public void doJob() throws Exception {
		addTestAccount();
	}
	
	
	private static void addTestAccount(){
		MorphiaQuery account = Account.q();
		if(account.count() == 0){
			logger.info(TestJob.class.getName() + " : start to add test accounts");
			for(int i=0; i < 10; i++){
				Account a = new Account();
				a.birthDay = new Date().toString();
				a.email = "111111"+i+"@126.com";
				a.enable = true;
				a.lastLogin = new Date();
				a.mobileOS = Constants.MobileOSType.IOS;
				a.nickname = "Cross"+i;
				a.password = Codec.hexSHA1("111111");
				a.phone = "1868888888"+i;
				a.save();
			}
		}
	}
}
