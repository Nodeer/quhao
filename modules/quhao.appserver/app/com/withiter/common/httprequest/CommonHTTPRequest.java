package com.withiter.common.httprequest;

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
        try {
			url = new URL(strUrl);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
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
		}
        
        return result;
	}
}
