package japidviews._javatags;

import java.util.Date;

import play.data.validation.Validation;
import play.mvc.Http.Header;
import play.mvc.Http.Request;

/**
 * a well-know place to add all the static method you want to use in your
 * templates.
 * 
 * All the public static methods will be automatically "import static " to the
 * generated Java classes by the Japid compiler.
 * 
 */
public class JapidWebUtil {

	public static String hi() {
		return "Hi";
	}

	public static String sinceFrom(Date date) {
		long sinceTime = (new Date().getTime() - date.getTime()) / 1000;
		if (sinceTime < 1) {
			return "just now";
		} else if (sinceTime < 60) {
			return String.format("%s seconds ago", sinceTime);
		} else if (sinceTime < 3600) {
			return String.format("%s minutes ago", sinceTime / 60);
		} else if (sinceTime < 24 * 3600) {
			return String.format("%s hours ago", sinceTime / 3600);
		} else if (sinceTime < (24 * 3600 * 30)) {
			long days = sinceTime / (24 * 3600);
			long hours = (sinceTime % (24 * 3600)) / 3600;
			if (hours == 0)
				return String.format("%s days ago", days);
			else
				return String.format("%s days, %s hours ago", days, hours);
		} else {
			long months = sinceTime / (24 * 3600 * 30);
			long days = (sinceTime % (24 * 3600 * 30)) / (24 * 3600);
			if (days == 0)
				return String.format("%s months ago", months);
			else
				return String.format("%s months, %s days ago", months, days);
		}

	}

	public static String getRemoteIp() {
		Header realIp = Request.current().headers.get("x-real-ip");
		if (realIp != null) {
			return realIp.value();
		} else {
			return Request.current().remoteAddress;
		}
	}

	public static String topActive(String value) {
		if (value.equals(Request.current().args.get("topActive"))) {
			return "active";
		} else {
			return "";
		}
	}

	public static String leftActive(String value) {
		if (value.equals(Request.current().args.get("leftActive"))) {
			return "active";
		} else {
			return "";
		}
	}

	public static void putTopActive(String value) {
		Request.current().args.put("topActive", value);
	}

	public static void putLeftActive(String value) {
		Request.current().args.put("leftActive", value);
	}

	public static String isValid(String field) {
		Validation.current();
		if (Validation.hasError(field)) {
			return "error";
		} else {
			return "";
		}
	}

	public static String selected(String value, String current) {
		if (value.equals(current)) {
			return "selected='selected'";
		}
		return "";
	}
}
