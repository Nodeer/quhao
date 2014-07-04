package com.withiter.quhao.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

import com.withiter.quhao.util.tool.QuhaoConstant;

public class CommantUtil {
	
	
	/**
	 * 提交参数里有文件的数据
	 * @param url 服务器地址
	 * @param param 参数
	 * @return 服务器返回结果
	 * @throws Exception
	 */
	//发送个人头像
	public static String uploadSubmit(String url, Map<String, String> param,File file,String uploadName)
	{
		String httpUrl = QuhaoConstant.HTTP_URL + url;
//		String httpUrl = "http://192.168.1.100:9081/" + url;
		
		StringBuffer sb = null;
		InputStream is = null;
		BufferedReader br = null;
		try
		{
			HttpPost post = new HttpPost(httpUrl);  
			
			post.setHeader("user-agent", "QuhaoAndroid");
			
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 60 * 1000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

			// Set the default socket timeout in milliseconds which is the timeout
			// for waiting for data.
			int timeoutSocket = 60 * 1000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			HttpClient httpClient=new DefaultHttpClient(httpParameters);
			MultipartEntity entity = new MultipartEntity();
			if (param != null && !param.isEmpty()) {
				for (Map.Entry<String, String> entry : param.entrySet()) {     
					if (entry.getValue() != null
							&& entry.getValue().trim().length() > 0) {
						entity.addPart(entry.getKey(),new StringBody(entry.getValue(),
								Charset.forName(org.apache.http.protocol.HTTP.UTF_8)));
					}
				}
			}
			// 添加文件参数
			if (file != null && file.exists()) {
				entity.addPart(uploadName, new FileBody(file));
			}
			post.setEntity(entity);  
			HttpResponse response = httpClient.execute(post);
			int stateCode = response.getStatusLine().getStatusCode();
			sb = new StringBuffer();
			if (stateCode == HttpStatus.SC_OK) {
				HttpEntity result = response.getEntity();
				if (result != null) {
					is = result.getContent();
					br = new BufferedReader(new InputStreamReader(is));
					String tempLine;
					while ((tempLine = br.readLine()) != null) {
						sb.append(tempLine);
					}
				}
			}
			post.abort();
			return sb.toString();
		}catch(Exception e)
		{
			Log.e("", e.getMessage());
			return "error";
		}
		finally
		{
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
