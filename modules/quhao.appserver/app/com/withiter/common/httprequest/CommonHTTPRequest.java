package com.withiter.common.httprequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;

import com.withiter.utils.ExceptionUtil;

public class CommonHTTPRequest {

	private static Logger logger = LoggerFactory.getLogger(CommonHTTPRequest.class);
	private static String HTTP_URL = Play.configuration.getProperty("application.domain");

	/**
	 * a HTTP request with given URL
	 * 
	 * @param strUrl
	 *            the URL you want to request
	 * @return
	 */
	public static String request(String strUrl) {

		logger.debug(CommonHTTPRequest.class.getName() + ", request url is : " + strUrl);

		URL url = null;
		String result = "";
		HttpURLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			url = new URL(strUrl);
			urlConn = (HttpURLConnection) url.openConnection();
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
			logger.error(ExceptionUtil.getTrace(e));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(ExceptionUtil.getTrace(e));
			}
			urlConn.disconnect();
		}

		return result;
	}

	/**
	 * A HTTP request(POST) with given URL
	 * 
	 * @param strUrl
	 *            the URL you want to request
	 * @return
	 */
	public static String post(String url) {
		String result = "";
		String httpUrl = HTTP_URL + url;
		try {
			httpUrl = encodeURL(httpUrl);
			HttpPost request = new HttpPost(httpUrl);
			request.setHeader("user-agent", "QuhaoAndroid");
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 10 * 1000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

			// Set the default socket timeout in milliseconds which is the
			// timeout
			// for waiting for data.
			int timeoutSocket = 10 * 1000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpClient.execute(request);

			logger.debug("get data from server, the status code is  : " + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
				logger.debug("get data from server : " + result);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
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
	public static String get(String url) {
		String result = "";
		String httpUrl = HTTP_URL + url;
		logger.debug("HTTP REQUEST POST, URL: " + httpUrl);
		try {
			httpUrl = encodeURL(httpUrl);
			logger.debug("HTTP REQUEST POST, URL after encode: " + httpUrl);
			HttpGet request = new HttpGet(httpUrl);
			request.setHeader("user-agent", "QuhaoAndroid");

			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 10 * 1000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

			// Set the default socket timeout in milliseconds which is the
			// timeout
			// for waiting for data.
			int timeoutSocket = 10 * 1000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpClient.execute(request);
			logger.debug("get data from server, the status code is  : " + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
				logger.debug("get data from server : " + result);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		}

		return result;
	}

	private static String encodeURL(String httpUrl) throws UnsupportedEncodingException {
		String[] strs = httpUrl.split("\\?");

		if (strs.length > 1) {
			httpUrl = strs[0] + "?";
			String[] paramStr = strs[1].split("&");
			for (int i = 0; i < paramStr.length; i++) {
				String params[] = paramStr[i].split("=");

				if (params.length > 1) {
					if (i == 0) {
						httpUrl = httpUrl + params[0] + "=" + URLEncoder.encode(params[1], "UTF-8");
						continue;
					}
					httpUrl = httpUrl + "&" + params[0] + "=" + URLEncoder.encode(params[1], "UTF-8");
				} else {
					if (i == 0) {
						httpUrl = httpUrl + params[0] + "=" + URLEncoder.encode("", "UTF-8");
						continue;
					}
					httpUrl = httpUrl + "&" + params[0] + "=" + URLEncoder.encode("", "UTF-8");
				}
			}
		}
		return httpUrl;
	}
}
