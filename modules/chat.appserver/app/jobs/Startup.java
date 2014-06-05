package jobs;

import models.chat.ChatPort;
import play.Logger;
import play.Play;
import play.jobs.Job;

public class Startup extends Job {

	@Override
	public void doJob() throws Exception {
		String port = Play.configuration.getProperty("http.port");
		Logger.debug("Chat server is starting on port %s", port);
		ChatPort cp = ChatPort.findByPort(port);
		if(cp != null){
			cp.rooms = 0;
		} else {
			cp = new ChatPort();
			cp.port = Long.parseLong(port);
		}
		cp.save();
		Logger.debug("Chat server started on port %s", port);
	}
}
