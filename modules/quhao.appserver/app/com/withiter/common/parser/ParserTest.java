package com.withiter.common.parser;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ParserTest {

	private static Set<String> urlSet = new HashSet<String>();

	private static Pattern p = Pattern
			.compile(
					"^(((http|https)://" +
					"(www.|([1-9]|[1-9]\\d|1\\d{2}|2[0-1]\\d|25[0-5])" +
					"(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}:[0-9]+/)?)" +
					"{1}.+){1}quot;",
					Pattern.CASE_INSENSITIVE);

	public static void main(String[] args) {
//		String baseUrl = "http://dianping.com/search/category/1/10/g101";
//		String baseUrl = "http://www.baidu.com/";
		String baseUrl = "http://bendi.koubei.com/list.htm?spm=0.0.0.0.1NrzyZ&city=310100";
		spiderInternet(baseUrl);
	}

	private static void spiderInternet(String baseUrl) {
		String new_url = baseUrl;
		if (urlSet.contains(new_url)) {
			return;
		}
		System.out.println(new_url);
		try {
			
			Connection conn  = Jsoup.connect(new_url);
			conn.timeout(0);
			Document doc = conn.get();
			System.out.println(doc.html());
//			Elements links = doc.select("a[href]");
//			for (Element link : links) {
//				String linkHref = link.attr("href");
//				if (linkHref.equals("#")) {
//					return;
//				}
//				Matcher matcher = p.matcher(linkHref);
//				if (matcher.matches()) {
//					spiderInternet(linkHref);
//				} else {
//					spiderInternet(baseUrl, linkHref);
//				}
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}