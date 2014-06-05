package jobs;

import models.chat.ChatPort;
import models.chat.MerchantPort;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStop;

@OnApplicationStop
public class Shutdown extends Job {
	@Override
	public void doJob() throws Exception {
		String port = Play.configuration.getProperty("http.port");
		Logger.debug("Chat server is stopping on port %s", port);
		ChatPort cp = ChatPort.findByPort(port);
		if(cp != null){
			Logger.info("Start to delete all ChatPort(port:%d)", cp.port);
			cp.delete();
			Logger.info("Finished deleting all ChatPort(port:%d)", cp.port);
		}
		
		Logger.info("Start to delete all MerchantPort(port:%d)", port);
		MerchantPort.deleteAllMerhcnatPortByPort(port);
		Logger.info("Finished deleting all MerchantPort(port:%d)", port);
		Logger.debug("Chat server stopped on port %s", port);
	}
}
