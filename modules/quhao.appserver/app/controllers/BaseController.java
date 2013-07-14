package controllers;

import japidviews._javatags.JapidWebUtil;

import java.io.IOException;

import play.mvc.results.RenderJson;
import cn.bran.play.JapidController;

import com.withiter.common.Constants;
import com.withiter.common.geoip.bean.Location;
import com.withiter.common.geoip.bean.Region;
import com.withiter.common.geoip.business.GeoBusiness;
import com.withiter.utils.StringUtils;
import com.google.gson.Gson;

public class BaseController extends JapidController {
	protected static void renderJSONP(Object o) {
		String callback = params.get("callback");
		if (org.apache.commons.lang.StringUtils.isEmpty(callback)) {
			renderJSON(o);
		} else {
			throw new RenderJson(callback + "(" + new Gson().toJson(o) + ")");
		}
	}

	protected static Location getLocation() throws IOException {
		String clientIp = JapidWebUtil.getRemoteIp();
		return GeoBusiness.getLocationByIp(clientIp);
	}

	protected static String getLocationStr() {
		String city = "";
		String regionCode = "";
		String countryCode = "";
		String region = "";
		String country = "";
		
		String location = "";
		try {
			Location loc = getLocation();
			if (loc != null) {
				city = loc.getCity();
				regionCode = loc.getRegion();
				countryCode = loc.getCountryCode();
				country = loc.getCountryName();
			}
			
			if (!StringUtils.isEmpty(city)){
				location = city;
			}
			
			// Comment below, just remain city
			/* 
			if(!StringUtils.isEmpty(regionCode) && !StringUtils.isEmpty(countryCode)){
				region = Region.regionMap.get(countryCode+""+regionCode);
			}
			
			if (!StringUtils.isEmpty(city) && !StringUtils.isEmpty(region)) {
				location = city + ", " + region;
			} else if(!StringUtils.isEmpty(city) && !StringUtils.isEmpty(country)){
				location = city + ", " + country;
			}
			*/
		} catch (IOException e) {
			location = "";
			e.printStackTrace();
		}
		return location;
	}
}
