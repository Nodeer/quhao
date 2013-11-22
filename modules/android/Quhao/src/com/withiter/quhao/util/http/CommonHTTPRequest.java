package com.withiter.quhao.util.http;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
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

		HttpPost request = new HttpPost(httpUrl);
		request.setHeader("user-agent", "QuhaoAndroid");

		// if account logged in, add session to header
		if(QHClientApplication.getInstance().isLogined){
			request.setHeader("quhao-android-session", new DesUtils().encrypt(QHClientApplication.getInstance().phone));
		}
		
		// set session id if exist
//		if(StringUtils.isNotNull(CommonHTTPRequest.sessionID)){
//			request.setHeader("Cookie", CommonHTTPRequest.sessionID);
//		}
		
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 10 * 1000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

		// Set the default socket timeout in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10 * 1000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(request);
		
//		Header[] headers = response.getAllHeaders();
//		for(Header h : headers){
//			QuhaoLog.d(TAG, h.getName() + "|||" + h.getValue());
//			QuhaoLog.d(TAG, "decoded: "+URLDecoder.decode(h.getValue(), "UTF-8"));
//			
//			// h.getValue() : PLAY_SESSION=28b7eb241f5b26e778acf4a825f8deddfa123e47-%005289c60bc929b65bbf675278%3A5289c60bc929b65bbf675278%00%00quhao_username%3A5289c60bc929b65bbf675278%00;Path=/
//			if(h.getValue().contains("PLAY_SESSION")){
//				
//			}
//		}
		
		QuhaoLog.i(TAG, "get data from server, the status code is  : " + response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			result = EntityUtils.toString(response.getEntity());
			QuhaoLog.v(TAG, "get data from server : " + result);
			
//			CookieStore mCookieStore = ((AbstractHttpClient) httpClient).getCookieStore();
//            List<Cookie> cookies = mCookieStore.getCookies();
//            for (int i = 0; i < cookies.size(); i++) {
//                //这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
//                if ("PHPSESSID".equals(cookies.get(i).getName())) {
//                	CommonHTTPRequest.sessionID = cookies.get(i).getValue();
//                    break;
//                }
//            }
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

		HttpGet request = new HttpGet(httpUrl);
		request.setHeader("user-agent", "QuhaoAndroid");
		
		// if account logged in, add session to header
		QuhaoLog.d(TAG, "QHClientApplication.getInstance().isLogined: "+QHClientApplication.getInstance().isLogined);
		if(QHClientApplication.getInstance().isLogined){
			request.setHeader("quhao-android-session", new DesUtils().encrypt(QHClientApplication.getInstance().phone));
		}
		
		// set session id if exist
//		if(StringUtils.isNotNull(CommonHTTPRequest.sessionID)){
//			request.setHeader("Cookie", CommonHTTPRequest.sessionID);
//		}

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
//			CookieStore mCookieStore = ((AbstractHttpClient) httpClient).getCookieStore();
//            List<Cookie> cookies = mCookieStore.getCookies();
//            for (int i = 0; i < cookies.size(); i++) {
//                //这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
//                if ("PHPSESSID".equals(cookies.get(i).getName())) {
//                	CommonHTTPRequest.sessionID = cookies.get(i).getValue();
//                    break;
//                }
//            }
		}

		return result;
	}
}