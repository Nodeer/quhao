package com.withiter.common.sms.business;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.withiter.common.httprequest.CommonHTTPRequest;

public class SMSBusiness {

	private static boolean useProxy = false;
	private static Logger logger = LoggerFactory.getLogger(SMSBusiness.class);

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
		client.setConnectionTimeout(1000*60);
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
