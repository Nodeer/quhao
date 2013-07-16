package com.withiter.common.geoip.bean;

// generated automatically from admin/generate_regionName.pl
public class regionName {
	static public String regionNameByCode(String country_code,
			String region_code) {
		String name = null;
//		int region_code2 = -1;
		if (region_code == null) {
			return null;
		}
		if (region_code.equals("")) {
			return null;
		}

		if (((region_code.charAt(0) >= 48) && (region_code.charAt(0) < (48 + 10)))
				&& ((region_code.charAt(1) >= 48) && (region_code.charAt(1) < (48 + 10)))) {
			// only numbers, that shorten the large switch statements
//			region_code2 = (region_code.charAt(0) - 48) * 10
//					+ region_code.charAt(1) - 48;
		} else if ((((region_code.charAt(0) >= 65) && (region_code.charAt(0) < (65 + 26))) || ((region_code
				.charAt(0) >= 48) && (region_code.charAt(0) < (48 + 10))))
				&& (((region_code.charAt(1) >= 65) && (region_code.charAt(1) < (65 + 26))) || ((region_code
						.charAt(1) >= 48) && (region_code.charAt(1) < (48 + 10))))) {

//			region_code2 = (region_code.charAt(0) - 48) * (65 + 26 - 48)
//					+ region_code.charAt(1) - 48 + 100;
		}

		return name;
	}
}
