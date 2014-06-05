package jobs;

import models.chat.ChatPort;
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
			cp.delete();
		}
		Logger.debug("Chat server stopped on port %s", port);
	}
}
