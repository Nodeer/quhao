/**
 * 
 */
package com.withiter.quhao.test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.withiter.quhao.util.http.CommonHTTPRequest;

/**
 * @author eacfgjl
 * 
 */
public class TestCommonHTTPRequest {
	
	public static void main(String[] args) throws ClientProtocolException, IOException{
		String url = "";
		CommonHTTPRequest.get(url);
		
	}
}
