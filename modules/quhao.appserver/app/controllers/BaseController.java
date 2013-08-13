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

	protected static Location getLocation() throws IOException {
		String clientIp = JapidWebUtil.getRemoteIp();
		return GeoBusiness.getLocationByIp(clientIp);
	}

	/**
	 * Get current user location by IP
	 * @return
	 */
	protected static String getLocationStr() {
		String city = "";
		String location = "";
		try {
			Location loc = getLocation();
			if (loc != null) {
				city = loc.getCity();
			}
			
			if (!StringUtils.isEmpty(city)){
				location = city;
			}
		} catch (IOException e) {
			location = "";
			e.printStackTrace();
		}
		return location;
	}
}
