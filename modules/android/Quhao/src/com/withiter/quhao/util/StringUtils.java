package com.withiter.quhao.util;

public class StringUtils {

	public static boolean isNull(String str) {
		if (null == str || "".equals(str)) {
			return true;
		}
		return false;
	}

	public static boolean isNotNull(String str) {
		return !isNull(str);
	}
}
