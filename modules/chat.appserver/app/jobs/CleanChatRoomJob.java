package jobs;


import java.util.Enumeration;
import java.util.Iterator;

import models.ChatRoomFactory;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

//每天2:00AM开始job
//@On("0 0 2 * * ?")
@OnApplicationStart
public class CleanChatRoomJob extends Job {

	@Override
	public void doJob() throws Exception {
		
		Logger.debug("%s", play.Play.configuration.get("http.port"));
		
		Logger.info("start to clean chat rooms map");
		Logger.info("current chat rooms size is : " + ChatRoomFactory.rooms().size());
		long start = System.currentTimeMillis();
		ChatRoomFactory.rooms().clear();
		Logger.info("after job, chat rooms size is : " + ChatRoomFactory.rooms().size());
		Logger.info("finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
	}
}
