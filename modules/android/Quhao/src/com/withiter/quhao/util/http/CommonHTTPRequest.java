package com.withiter.quhao.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CommonHTTPRequest {

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
			urlConn.setConnectTimeout(1000*30);
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
