package com.withiter.common.httprequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonHTTPRequest {

	private static Logger logger =  LoggerFactory.getLogger(CommonHTTPRequest.class);
	private static boolean useProxy = false;
	
	public static boolean isUseProxy() {
		return useProxy;
	}

	public static void setUseProxy(boolean useProxy) {
		CommonHTTPRequest.useProxy = useProxy;
	}

	private static void initProxy(){
		System.getProperties().setProperty("proxySet", "true");
		System.getProperties().setProperty("http.proxyHost", "www-proxy.ericsson.se");
		System.getProperties().setProperty("http.proxyPort", "8080");
	}
	
	/**
	 * a http request with given url
	 * @param strUrl the url you want to request
	 * @return
	 */
	public static String request(String strUrl){

		logger.debug(CommonHTTPRequest.class.getName() + ", request url is : " + strUrl);
		
		String userHome = System.getProperty("user.home");
		if(userHome.contains("eacfgjl")){
			useProxy = true;
		}
		
		if(useProxy){
			initProxy();
		}
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
			while((readerLine=br.readLine())!=null){
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
}
