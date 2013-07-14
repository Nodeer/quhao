package notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import cn.bran.play.JapidMailer;

public class MailsController extends JapidMailer {

	public static final String	FROM				= "Quhao<quhaonoreply@gmail.com>";
	public static final String	SUBJECT_SIGNUP		= "Thanks for signing up to quhao";
	public static final String	CONTENT_SIGNUP_TMP	= "Thanks for signing up for quhao";

	public static void sendTo(String mailFrom, String mailsTo, String subject, String content) {
		setFrom(mailFrom);
		setSubject(subject);
		addRecipient(mailsTo);
		send(content);
	}

}