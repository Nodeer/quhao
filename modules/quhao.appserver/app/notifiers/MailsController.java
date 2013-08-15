package notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.withiter.common.httprequest.CommonHTTPRequest;

import cn.bran.play.JapidMailer;

public class MailsController extends JapidMailer {

	public static final String	FROM				= "Quhao<quhaonoreply@gmail.com>";
	public static final String	SUBJECT_SIGNUP		= "Thanks for signing up to quhao";
	public static final String	CONTENT_SIGNUP_TMP	= "Thanks for signing up for quhao";
	
	private static boolean useProxy = false;
	
	public static boolean isUseProxy() {
		return useProxy;
	}

	public static void setUseProxy(boolean useProxy) {
		MailsController.useProxy = useProxy;
	}

	private static void initProxy(){
		System.getProperties().setProperty("proxySet", "true");
		System.getProperties().setProperty("http.proxyHost", "www-proxy.ericsson.se");
		System.getProperties().setProperty("http.proxyPort", "8080");
	}

	public static void sendTo(String mailFrom, String mailsTo, String subject, String content) {
		setFrom(mailFrom);
		setSubject(subject);
		addRecipient(mailsTo);
		send(content);
	}
	public static void sendTo(String mailsTo) {
		setFrom(FROM);
		setSubject(SUBJECT_SIGNUP);
		addRecipient(mailsTo);
		send(CONTENT_SIGNUP_TMP);
	}
	
	public static void sendBySignUp(String mailsTo) {
		
		setUseProxy(true);
		
		String userHome = System.getProperty("user.home");
		if(userHome.contains("eacfgjl")){
			useProxy = true;
		}
		
		if(useProxy){
			initProxy();
		}
		setFrom(FROM);
		setSubject(SUBJECT_SIGNUP);
		addRecipient(mailsTo);
		send(CONTENT_SIGNUP_TMP);
	}

}