package com.withiter.common.sms.business;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;

public class SMSBusiness {

	private static boolean useProxy = false;
	private static Logger logger = LoggerFactory.getLogger(SMSBusiness.class);
	private static final String UID = Play.configuration.getProperty("service.sms.uid");
	private static final String KEY = Play.configuration.getProperty("service.sms.key");
	
	private static final String AUTH_CODE_FOR_SIGNUP = Play.configuration.getProperty("service.sms.authCodeForSignup");
	
	public static boolean isUseProxy() {
		return useProxy;
	}

	public static void setUseProxy(boolean useProxy) {
		SMSBusiness.useProxy = useProxy;
	}

	private static void initProxy() {
		System.getProperties().setProperty("proxySet", "true");
		System.getProperties().setProperty("http.proxyHost",
				"www-proxy.ericsson.se");
		System.getProperties().setProperty("http.proxyPort", "8080");
	}

	/**
	 * Send SMS.
	 * @param mobileNumber the mobile number you want to send SMS
	 * @param content the content of SMS
	 * @return the CODE of status.<br>
	 * 			-1 		没有该用户账户<br>
	 *			-2 		密钥不正确 [查看密钥]<br>
	 *			-3 		短信数量不足<br>
	 *			-11 	该用户被禁用<br>
	 *			-14 	短信内容出现非法字符<br>
	 *			-4 		手机号格式不正确<br>
	 *			-41 	手机号码为空<br>
	 *			-42 	短信内容为空<br>
	 *			大于0 	短信发送数量<br>
	 * @throws HttpException
	 * @throws IOException
	 */
	public static int sendSMS(String mobileNumber, String content) throws HttpException, IOException{
		logger.info(SMSBusiness.class.getName() + "sendSMS, mobileNumber is : " + mobileNumber + ", content is : " + content);
		String userHome = System.getProperty("user.home");
		if (userHome.contains("eacfgjl")) {
			useProxy = true;
		}

		if (useProxy) {
			initProxy();
		}
		
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		PostMethod post = new PostMethod("http://utf8.sms.webchinese.cn");
		post.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=utf8");// 在头文件中设置转码
		NameValuePair[] data = { new NameValuePair("Uid", UID),
				new NameValuePair("Key", KEY),
				new NameValuePair("smsMob", mobileNumber),
				new NameValuePair("smsText", content) };
		post.setRequestBody(data);
		client.executeMethod(post);
		int statusCode = post.getStatusCode();
		post.releaseConnection();
		return statusCode;
	}
	
	/**
	 * Send SMS.
	 * @param mobileNumbers a list of mobile numbers you want to send SMS
	 * @param content the content of SMS
	 * @return the CODE of status.<br>
	 * 			-1 		没有该用户账户<br>
	 *			-2 		密钥不正确 [查看密钥]<br>
	 *			-3 		短信数量不足<br>
	 *			-11 	该用户被禁用<br>
	 *			-14 	短信内容出现非法字符<br>
	 *			-4 		手机号格式不正确<br>
	 *			-41 	手机号码为空<br>
	 *			-42 	短信内容为空<br>
	 *			大于0 	短信发送数量<br>
	 * @throws HttpException
	 * @throws IOException
	 */
	public static int sendSMS(List<String> mobileNumbers, String content) throws HttpException, IOException{
		logger.info(SMSBusiness.class.getName() + "sendSMS, mobileNumber is : " + mobileNumbers + ", content is : " + content);
		String userHome = System.getProperty("user.home");
		if (userHome.contains("eacfgjl")) {
			useProxy = true;
		}

		if (useProxy) {
			initProxy();
		}
		
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://utf8.sms.webchinese.cn");
		post.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=utf8");// 在头文件中设置转码
		
		StringBuilder sb = new StringBuilder();
		for(String s : mobileNumbers){
			sb.append(s).append(",");
		}
		sb.substring(0, sb.length()-1);
		
		NameValuePair[] data = { new NameValuePair("Uid", UID),
				new NameValuePair("Key", KEY),
				new NameValuePair("smsMob", sb.toString()),
				new NameValuePair("smsText", content) };
		post.setRequestBody(data);
		client.executeMethod(post);
		int statusCode = post.getStatusCode();
		post.releaseConnection();
		return statusCode;
	}
	
	/**
	 * Send SMS.
	 * @param mobileNumbers a list of mobile numbers you want to send SMS
	 * @return the CODE of status.<br>
	 * 			100000 ~ 999999		随即验证码
	 *			0 					短信发送失败<br>
	 * @throws HttpException
	 * @throws IOException
	 */
	public static int sendAuthCodeForSignup(String mobileNumber) throws HttpException, IOException{
		logger.info(SMSBusiness.class.getName() + "sendSMS, mobileNumber is : " + mobileNumber);
		String userHome = System.getProperty("user.home");
		if (userHome.contains("eacfgjl")) {
			useProxy = true;
		}

		if (useProxy) {
			initProxy();
		}
		Random r = new Random();
		int x = r.nextInt(999999);
		while(x < 100000){
			x = r.nextInt(999999);
		}
		String message=AUTH_CODE_FOR_SIGNUP+String.valueOf(x);
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://utf8.sms.webchinese.cn");
		post.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=utf8");// 在头文件中设置转码
		NameValuePair[] data = { new NameValuePair("Uid", UID),
				new NameValuePair("Key", KEY),
				new NameValuePair("smsMob", mobileNumber),
				new NameValuePair("smsText", message) };
		post.setRequestBody(data);
		client.executeMethod(post);
		int statusCode = post.getStatusCode();
		post.releaseConnection();
		if(statusCode > 0){
			return x;
		}else{
			return 0;
		}
	}
	
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws HttpException
	 */
	public static void main(String[] args) throws HttpException, IOException {

		String userHome = System.getProperty("user.home");
		if (userHome.contains("eacfgjl")) {
			useProxy = true;
		}

		if (useProxy) {
			initProxy();
		}

		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn");
		post.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码
		NameValuePair[] data = { new NameValuePair("Uid", "withiter"),
				new NameValuePair("Key", "65a4d62f45a07fd1d206"),
				new NameValuePair("smsMob", "18817261072"),
				new NameValuePair("smsText", "您在quhao.com注册的网站动态密码是：123456") };
		post.setRequestBody(data);
		client.executeMethod(post);
		Header[] headers = post.getResponseHeaders();
		int statusCode = post.getStatusCode();
		System.out.println("statusCode:" + statusCode);
		for (Header h : headers) {
			System.out.println(h.toString());
		}
		String result = new String(post.getResponseBodyAsString().getBytes(
				"gbk"));
		System.out.println(result);

		post.releaseConnection();
	}
}
