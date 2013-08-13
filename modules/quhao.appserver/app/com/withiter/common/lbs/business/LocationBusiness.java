package com.withiter.common.lbs.business;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import play.Play;

import cn.bran.japid.util.StringUtils;

import com.withiter.common.httprequest.CommonHTTPRequest;
import com.withiter.common.lbs.bean.Location;

public class LocationBusiness {

	private static final String geocodingKey = Play.configuration
			.getProperty("develop.geocoding.app.key");
	private static final String serviceUrl = Play.configuration
			.getProperty("service.geocoding.baseurl");

	/**
	 * Get the coordinate of one address
	 * @param city the city name
	 * @param address the detail address
	 * @return Location object witch has x,y coordinate
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 */
	public static Location getLocationByAddress(String city, String address)
			throws UnsupportedEncodingException, JSONException {
		Location location = new Location();
		if(StringUtils.isEmpty(address)){
			return location;
		}
		String cityEncode = URLEncoder.encode(city, "UTF-8");
		String addEncode = URLEncoder.encode(address, "UTF-8");
		String urlStr = serviceUrl + addEncode + "&output=json&key="
				+ geocodingKey + "&city=" + cityEncode;
		String jsonStr = CommonHTTPRequest.request(urlStr);
		JSONObject json = new JSONObject(jsonStr);
		String status = json.get("status").toString();
		if("OK".equalsIgnoreCase(status) && !json.get("result").toString().startsWith("[")) {
			JSONObject xyJSON = new JSONObject(new JSONObject(json.get("result").toString()).get("location").toString());
			String x = xyJSON.get("lng").toString();
			String y = xyJSON.get("lat").toString();
			location.x = x;
			location.y = y;
		}
		return location;
	}
}
