package com.withiter.common.sms.business;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;

public class SMSBusiness {

	private static Logger logger = LoggerFactory.getLogger(SMSBusiness.class);

	private static final String YUNPIAN_KEY = Play.configuration.getProperty("service.sms.yupian.key");

	/**
	 * 服务http地址
	 */
	private static String BASE_URI = "http://yunpian.com";
	/**
	 * 服务版本号
	 */
	private static String VERSION = "v1";
	/**
	 * 编码格式
	 */
	private static String ENCODING = "UTF-8";
	/**
	 * 查账户信息的http地址
	 */
	private static String URI_GET_USER_INFO = BASE_URI + "/" + VERSION + "/user/get.json";
	/**
	 * 通用发送接口的http地址
	 */
	private static String URI_SEND_SMS = BASE_URI + "/" + VERSION + "/sms/send.json";
	/**
	 * 模板发送接口的http地址
	 */
	private static String URI_TPL_SEND_SMS = BASE_URI + "/" + VERSION + "/sms/tpl_send.json";
	

	/**
	 * Send SMS.
	 * 
	 * @param mobileNumber
	 *            the mobile number you want to send SMS
	 * @param content
	 *            the content of SMS
	 * @return the CODE of status.<br>
	 *         -1 没有该用户账户<br>
	 *         -2 密钥不正确 [查看密钥]<br>
	 *         -3 短信数量不足<br>
	 *         -11 该用户被禁用<br>
	 *         -14 短信内容出现非法字符<br>
	 *         -4 手机号格式不正确<br>
	 *         -41 手机号码为空<br>
	 *         -42 短信内容为空<br>
	 *         大于0 短信发送数量<br>
	 * @throws HttpException
	 * @throws IOException
	 */
//	public static int sendSMS(String mobileNumber, String content) throws HttpException, IOException {
//		logger.info(SMSBusiness.class.getName() + "sendSMS, mobileNumber is : " + mobileNumber + ", content is : " + content);
//		HttpClient client = new HttpClient();
//		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
//		PostMethod post = new PostMethod("http://utf8.sms.webchinese.cn");
//		post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf8");// 在头文件中设置转码
//		NameValuePair[] data = { new NameValuePair("Uid", UID), new NameValuePair("Key", KEY), new NameValuePair("smsMob", mobileNumber), new NameValuePair("smsText", content) };
//		post.setRequestBody(data);
//		client.executeMethod(post);
//		int statusCode = post.getStatusCode();
//		post.releaseConnection();
//		return statusCode;
//	}

	/**
	 * Send SMS.
	 * 
	 * @param mobileNumbers
	 *            a list of mobile numbers you want to send SMS
	 * @param content
	 *            the content of SMS
	 * @return the CODE of status.<br>
	 *         -1 没有该用户账户<br>
	 *         -2 密钥不正确 [查看密钥]<br>
	 *         -3 短信数量不足<br>
	 *         -11 该用户被禁用<br>
	 *         -14 短信内容出现非法字符<br>
	 *         -4 手机号格式不正确<br>
	 *         -41 手机号码为空<br>
	 *         -42 短信内容为空<br>
	 *         大于0 短信发送数量<br>
	 * @throws HttpException
	 * @throws IOException
	 */
//	public static int sendSMS(List<String> mobileNumbers, String content) throws HttpException, IOException {
//		logger.info(SMSBusiness.class.getName() + "sendSMS, mobileNumber is : " + mobileNumbers + ", content is : " + content);
//		HttpClient client = new HttpClient();
//		PostMethod post = new PostMethod("http://utf8.sms.webchinese.cn");
//		post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf8");// 在头文件中设置转码
//
//		StringBuilder sb = new StringBuilder();
//		for (String s : mobileNumbers) {
//			sb.append(s).append(",");
//		}
//		sb.substring(0, sb.length() - 1);
//
//		NameValuePair[] data = { new NameValuePair("Uid", UID), new NameValuePair("Key", KEY), new NameValuePair("smsMob", sb.toString()), new NameValuePair("smsText", content) };
//		post.setRequestBody(data);
//		client.executeMethod(post);
//		int statusCode = post.getStatusCode();
//		post.releaseConnection();
//		return statusCode;
//	}

	/**
	 * Send SMS.
	 * 
	 * @param mobileNumbers
	 *            a list of mobile numbers you want to send SMS
	 * @return the CODE of status.<br>
	 *         100000 ~ 999999 随即验证码 0 短信发送失败<br>
	 * @throws HttpException
	 * @throws IOException
	 */
	public static int sendAuthCodeForSignup(String mobileNumber) throws HttpException, IOException {
		logger.info(SMSBusiness.class.getName() + "sendSMS, mobileNumber is : " + mobileNumber);
		Random r = new Random();
		int x = r.nextInt(999999);
		while (x < 100000) {
			x = r.nextInt(999999);
		}
		/**************** 使用模板接口发短信 *****************/
		//401089	您在「取号啦」注册的动态密码是：#code#。如非本人操作，请忽略本短信【取号啦】
		long tpl_id = 401089l;
		//设置对应的模板变量值
		String tpl_value ="#code#="+x;
		int code = tplSendSms(tpl_id,tpl_value, mobileNumber);
		if (code == 0) {
			return x;
		} else {
			return 0;
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws HttpException
	 */
	public static void main(String[] args) throws HttpException, IOException {
		//修改为您的apikey
		String apikey = "9deb30e3c0712b9de88ca922dc7a68f7";
		//修改为您要发送的手机号
		String mobile = "18817261072";
		
		/**************** 查账户信息调用示例 *****************/
		System.out.println(getUserInfo());
		
		/**************** 使用通用接口发短信 *****************/
		//设置您要发送的内容
		String text = "您的排队号码是8号，很快就要到你了，请抓紧时间前往商家。【取号啦】";
		//发短信调用示例
//		System.out.println(JavaSmsApi.sendSms(apikey, text, mobile));
		
		/**************** 使用模板接口发短信 *****************/
		//设置模板ID，如使用1号模板:您的验证码是#code#【#company#】
		long tpl_id=401095;
		//设置对应的模板变量值
		String tpl_value="#code1#=12&#code2#=4";
		//模板发送的调用示例
		tplSendSms(tpl_id, tpl_value, mobile);
	}
	
	
	
	
	//////////////////////////////////// 云片网络短信服务 http://www.yunpian.com/
	/**
	 * 取账户信息
	 * @return json格式字符串
	 * @throws IOException 
	 */
	public static String getUserInfo() throws IOException{
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(URI_GET_USER_INFO+"?apikey="+YUNPIAN_KEY);
		HttpMethodParams param = method.getParams();
		param.setContentCharset(ENCODING);
		client.executeMethod(method);
		return method.getResponseBodyAsString();
	}
	/**
	 * 发短信
	 * @param apikey apikey
	 * @param text　短信内容　
	 * @param mobile　接受的手机号
	 * @return json格式字符串
	 * @throws IOException 
	 */
	public static String sendSms(String text, String mobile) throws IOException{
		HttpClient client = new HttpClient();
		NameValuePair[] nameValuePairs = new NameValuePair[3];
		nameValuePairs[0] = new NameValuePair("apikey", YUNPIAN_KEY);
		nameValuePairs[1] = new NameValuePair("text", text);
		nameValuePairs[2] = new NameValuePair("mobile", mobile);
		PostMethod method = new PostMethod(URI_SEND_SMS);
		method.setRequestBody(nameValuePairs);
		HttpMethodParams param = method.getParams();
		param.setContentCharset(ENCODING);
		client.executeMethod(method);
		return method.getResponseBodyAsString();
	}
	
	/**
	 * 通过模板发送短信
	 * @param apikey apikey
	 * @param tpl_id　模板id
	 * @param tpl_value　模板变量值　
	 * @param mobile　接受的手机号
	 * @return json格式字符串
	 * @throws IOException 
	 */
	public static int tplSendSms(long tpl_id, String tpl_value, String mobile){
		try {
			HttpClient client = new HttpClient();
			NameValuePair[] nameValuePairs = new NameValuePair[4];
			nameValuePairs[0] = new NameValuePair("apikey", YUNPIAN_KEY);
			nameValuePairs[1] = new NameValuePair("tpl_id", String.valueOf(tpl_id));
			nameValuePairs[2] = new NameValuePair("tpl_value", tpl_value);
			nameValuePairs[3] = new NameValuePair("mobile", mobile);
			PostMethod method = new PostMethod(URI_TPL_SEND_SMS);
			method.setRequestBody(nameValuePairs);
			HttpMethodParams param = method.getParams();
			param.setContentCharset(ENCODING);
			client.executeMethod(method);
			String response = method.getResponseBodyAsString();
			logger.debug(response);
			JSONObject json = new JSONObject(response);
			return json.getInt("code");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return -1;
		} catch (HttpException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
