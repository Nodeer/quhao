package notifiers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.jamonapi.utils.Logger;
import com.withiter.common.httprequest.CommonHTTPRequest;

import cn.bran.play.JapidMailer;

public class MailsController extends JapidMailer {

	public static final String FROM = "Quhaola<noreply@quhao.la>";
	public static final String SUBJECT_SIGNUP = "【取号啦】账号激活通知";
	public static final String CONTENT_SIGNUP_TMP = "感谢您注册取号啦，您只需要点击下面链接，激活您的帐户，您便可以享受取号啦各项服务。";

	public static void sendTo(String mailFrom, String mailsTo, String subject,
			String content) {
		setFrom(mailFrom);
		setCharset("UTF-8");
		setSubject(subject);
		addRecipient(mailsTo);
		send(content);
	}

	public static void sendTo(String mailsTo) {
		setFrom(FROM);
		setCharset("UTF-8");
		setSubject(SUBJECT_SIGNUP);
		addRecipient(mailsTo);
		send(CONTENT_SIGNUP_TMP);
	}

	public static void sendTo(String mailsTo, String url) {
		setFrom(FROM);
		setCharset("UTF-8");
		setSubject(SUBJECT_SIGNUP);
		addRecipient(mailsTo);
		String content= "感谢您注册取号APP，您只需要点击下面链接，激活您的帐户，您便可以享受取号APP各项服务<br/><br/>"
		+ "<a href='"+url+"'>"+url+"</a><br/>"+"如无法点击，请将链接拷贝到浏览器地址栏中直接访问.";
		send(content);
	}

	public static void sendBySignUp(String mailsTo) {
		setFrom(FROM);
		setCharset("UTF-8");
		setSubject(SUBJECT_SIGNUP);
		addRecipient(mailsTo);
		send(CONTENT_SIGNUP_TMP);
	}
}