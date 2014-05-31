package jobs;


import models.ChatRoomFactory;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;

//每天2:00AM开始job
@On("0 31 16 * * ?")
public class CleanChatRoomJob extends Job {

	@Override
	public void doJob() throws Exception {
		Logger.info("start to clean chat rooms map");
		Logger.info("current chat rooms size is : " + ChatRoomFactory.rooms().size());
		long start = System.currentTimeMillis();
		ChatRoomFactory.rooms().clear();
		Logger.info("after job, chat rooms size is : " + ChatRoomFactory.rooms().size());
		Logger.info("finished, elapsed time " + (System.currentTimeMillis() - start) + "ms.");
	}
}
