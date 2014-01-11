package com.withiter.quhao.util.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.encrypt.DesUtils;
import com.withiter.quhao.util.tool.QuhaoConstant;

public class CommonHTTPRequest {

	private static boolean useProxy = false;
	private static String TAG = CommonHTTPRequest.class.getName();
	private static String sessionID = "";

	public static boolean isUseProxy() {
		return useProxy;
	}

	public static void setUseProxy(boolean useProxy) {
		CommonHTTPRequest.useProxy = useProxy;
	}

	private static void initProxy() {
		System.getProperties().setProperty("proxySet", "true");
		System.getProperties().setProperty("http.proxyHost", "www-proxy.ericsson.se");
		System.getProperties().setProperty("http.proxyPort", "8080");
	}
	
	/**
	 * A HTTP request(POST) with given URL
	 * 
	 * @param strUrl
	 *            the URL you want to request
	 * @return
	 */
	public static String post(String url) throws ClientProtocolException, IOException {
		String result = "";
		String httpUrl = QuhaoConstant.HTTP_URL + url;
		QuhaoLog.d(TAG, "HTTP REQUEST POST, URL: " + httpUrl);
		httpUrl = encoderURL(httpUrl);
		HttpPost request = new HttpPost(httpUrl);
		request.setHeader("user-agent", "QuhaoAndroid");

		// if account logged in, add session to header
		if(QHClientApplication.getInstance().isLogined){
			request.setHeader("quhao-android-session", new DesUtils().encrypt(QHClientApplication.getInstance().phone));
		}
		
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 10 * 1000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

		// Set the default socket timeout in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10 * 1000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(request);
		
		QuhaoLog.i(TAG, "get data from server, the status code is  : " + response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			result = EntityUtils.toString(response.getEntity());
			QuhaoLog.v(TAG, "get data from server : " + result);
		}

		return result;
	}

	/**
	 * A HTTP request(GET) with given URL
	 * 
	 * @param strUrl
	 *            the URL you want to request
	 * @return
	 */
	public static String get(String url) throws ClientProtocolException, IOException {
		String result = "";
		String httpUrl = QuhaoConstant.HTTP_URL + url;
		QuhaoLog.d(TAG, "HTTP REQUEST POST, URL: " + httpUrl);
		httpUrl = encoderURL(httpUrl);
		QuhaoLog.d(TAG, "HTTP REQUEST POST, URL: " + httpUrl);
		HttpGet request = new HttpGet(httpUrl);
		request.setHeader("user-agent", "QuhaoAndroid");
		
		// if account logged in, add session to header
		QuhaoLog.d(TAG, "QHClientApplication.getInstance().isLogined: "+QHClientApplication.getInstance().isLogined);
		if(QHClientApplication.getInstance().isLogined){
			request.setHeader("quhao-android-session", new DesUtils().encrypt(QHClientApplication.getInstance().phone));
		}
		
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 10 * 1000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

		// Set the default socket timeout in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10 * 1000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(request);
		QuhaoLog.i(TAG, "get data from server, the status code is  : " + response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			result = EntityUtils.toString(response.getEntity());
			QuhaoLog.v(TAG, "get data from server : " + result);
		}
		
		return result;
	}

	private static String encoderURL(String httpUrl)
			throws UnsupportedEncodingException {
		String[] strs = httpUrl.split("\\?");
		
		if(strs.length>1)
		{
			httpUrl = strs[0] + "?";
			String[] paramStr = strs[1].split("&");
			for (int i = 0; i < paramStr.length; i++) {
				String params[] = paramStr[i].split("=");
				
				if(i==0)
				{
					httpUrl = httpUrl + params[0] + "=" + URLEncoder.encode(params[1], "UTF-8");
					continue;
				}
				httpUrl = httpUrl + "&" + params[0] + "=" + URLEncoder.encode(params[1], "UTF-8");
			}
		}
		return httpUrl;
	}
}