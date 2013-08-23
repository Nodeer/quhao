package com.withiter.quhao.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.withiter.quhao.util.tool.QuhaoConstant;

public class CommonHTTPRequest {

	private static boolean useProxy = false;
	private static String TAG = CommonHTTPRequest.class.getName();

	public static boolean isUseProxy() {
		return useProxy;
	}

	public static void setUseProxy(boolean useProxy) {
		CommonHTTPRequest.useProxy = useProxy;
	}

	private static void initProxy() {
		System.getProperties().setProperty("proxySet", "true");
		System.getProperties().setProperty("http.proxyHost",
				"www-proxy.ericsson.se");
		System.getProperties().setProperty("http.proxyPort", "8080");
	}

	/**
	 * a http request with given url
	 * 
	 * @param strUrl
	 *            the url you want to request
	 * @return
	 */
	public static String request(String strUrl) {
		strUrl = QuhaoConstant.HTTP_URL + strUrl;
		URL url = null;
		String result = "";
		HttpURLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			url = new URL(strUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout(1000 * 20);
			in = new InputStreamReader(urlConn.getInputStream());
			BufferedReader br = new BufferedReader(in);

			String readerLine = null;
			while ((readerLine = br.readLine()) != null) {
				result += readerLine;
			}
			in.close();
			urlConn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			urlConn.disconnect();
		}
		return result;
	}

	public static String get(String url) throws ClientProtocolException, IOException {
		String result = "";
		String httpUrl = QuhaoConstant.HTTP_URL + url;

		HttpGet request = new HttpGet(httpUrl);

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 10 * 1000;
		// Set the default socket timeout (SO_TIMEOUT) in milliseconds which is
		// the timeout for waiting for data.
		int timeoutSocket = 10 * 1000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response;
		response = httpClient.execute(request);
		Log.i(TAG, "get top merchant data form server : "
				+ response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			result = EntityUtils.toString(response.getEntity());
			Log.v(TAG, "get top merchant data form server buf : " + result);
			// 返回HTML页面
//			if (result.indexOf("<html>") != -1
//					|| result.indexOf("<HTML>") != -1) {
//				throw new Exception("session timeout!");
//			}
		}

		return result;
	}
}