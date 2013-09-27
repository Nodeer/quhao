package com.withiter.common.geoip.business;

import java.io.IOException;

import play.Play;

import com.withiter.common.geoip.bean.Location;
import com.withiter.common.geoip.bean.LookupService;

public class GeoBusiness {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(getLocationByIp("220.181.111.147").getCity());
	}

	public static Location getLocationByIp(String ipaddr) throws IOException {
		String sep = System.getProperty("file.separator");
		String dir = Play.configuration.getProperty("geoip.datdir");
		String dbfile = dir + sep + "GeoLiteCity.dat";
		LookupService cl = new LookupService(dbfile,
				LookupService.GEOIP_MEMORY_CACHE);
		Location location = cl.getLocation(ipaddr);

		cl.close();
		return location;
	}
}
