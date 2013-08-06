package com.withiter.common.lbs.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import play.Play;
import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants;
import com.withiter.common.httprequest.CommonHTTPRequest;
import com.withiter.common.lbs.bean.Distance;

public class DirectionBusiness {

	private static final String key = Play.configuration
			.getProperty("develop.direction.app.ak");
	private static final String serviceUrl = Play.configuration
			.getProperty("service.direction.baseurl");

	// 	for driving
	//	http://api.map.baidu.com/direction/v1?mode=driving&origin=%E6%B8%85%E5%8D%8E%E5%A4%A7%E5%AD%A6&destination=%E5%8C%97%E4%BA%AC%E5%A4%A7%E5%AD%A6&origin_region=%E5%8C%97%E4%BA%AC&destination_region=%E5%8C%97%E4%BA%AC&output=json&ak=E4805d16520de693a3fe707cdc962045
	
	//	for transit
	//	http://api.map.baidu.com/direction/v1?mode=transit&origin=%E4%B8%8A%E5%9C%B0%E4%BA%94%E8%A1%97&destination=%E5%8C%97%E4%BA%AC%E5%A4%A7%E5%AD%A6&region=%E5%8C%97%E4%BA%AC&output=json&ak=E4805d16520de693a3fe707cdc962045
	
	public static Distance direction(String origin, String destination,
			String mode, String region, String origin_region, String destination_region, String output, String ak) throws JSONException {
		Distance d = new Distance();
		if (StringUtils.isEmpty(origin) || StringUtils.isEmpty(destination)
				|| StringUtils.isEmpty(key)) {
			return null;
		}
		String requestUrl = null;
		StringBuilder sb = new StringBuilder(serviceUrl);

		if (StringUtils.isEmpty(mode)) {
			mode = Constants.DirectionMode.driving.toString();
		}
		sb.append("?mode=").append(mode);

		if (StringUtils.isEmpty(output)) {
			output = "json";
		}

		if (mode.equalsIgnoreCase(Constants.DirectionMode.driving.toString())) {
			sb.append("&origin=").append(origin)
					.append("&destination=").append(destination)
					.append("&origin_region=").append(origin_region)
					.append("&destination_region=").append(destination_region)
					.append("&output=").append(output).append("&ak=").append(key);
			requestUrl = sb.toString();
		}

		if (!mode.equalsIgnoreCase(Constants.DirectionMode.driving.toString())) {
			sb.append("&origin=").append(origin).append("&destination=")
					.append("&region=").append(region).append(destination)
					.append("&output=").append(output).append("&ak=")
					.append(key);
			requestUrl = sb.toString();
		}
		
		String jsonResponse = CommonHTTPRequest.request(requestUrl);
		d = buildDistanceObj(jsonResponse, d);
		return d;
	}
	
	private static Distance buildDistanceObj(String jsonStr, Distance d) throws JSONException{
		JSONObject json = new JSONObject(jsonStr);
		String status = json.getString("status");
		String result = json.getString("result");
		
		if (!StringUtils.isEmpty(status) && status.equalsIgnoreCase("0")) {
			if(!StringUtils.isEmpty(result)){
				JSONObject resultJSON = new JSONObject(result);
				JSONArray schemeArray = resultJSON.getJSONArray("scheme");
				if(schemeArray != null && schemeArray.length() > 0){
					String scheme = schemeArray.getString(0);
					if(!StringUtils.isEmpty(scheme)){
						JSONObject distanceJSON = new JSONObject(scheme);
						String distanceStr = distanceJSON.getString("distance");
						d.distance = distanceStr;
					}
				}
			}
		}
		return d;
	}
}
