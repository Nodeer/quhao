package com.withiter.common.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.withiter.models.merchant.Merchant;

public class ParserTest {

	private static Map<String, Merchant> merchantMap = new HashMap<String, Merchant>();

	static {
		// proxy configuration
		System.getProperties().setProperty("proxySet", "true");
		System.getProperties().setProperty("http.proxyHost",
				"www-proxy.ericsson.se");
		System.getProperties().setProperty("http.proxyPort", "8080");
	}

	private static void spiderInternet(String url) {

		try {

			Connection conn = Jsoup.connect(url);
			conn.timeout(60000);
			Document doc = conn.get();
			Elements cateTypes = doc.select("div[class=repast list_nav]");

			Element e1 = cateTypes.get(0).child(0);
			Element e2 = cateTypes.get(0).child(1);

			System.out.println("title:" + e1.text());

			Elements e22 = e2.children();
			for (Element node : e22) {
				System.out.println(node.text());
				System.out.println(node.select("a[href]").attr("href"));
				String childUrl = node.select("a[href]").attr("href");
				merchantMap.put(node.text(), build(childUrl));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void parserChild(String url) throws IOException {
		
		System.out.println("child url: " + url);
		
		Connection conn = Jsoup.connect(url);
		conn.timeout(0);
		Document doc = conn.get();
		doc.html().replaceAll("\\&lt;", "<");
		doc.html().replaceAll("\\&gt;", ">");
		doc.html().replaceAll("\\&quot;", "\"");
		System.out.println(doc.html());
		
		

		Elements es = doc.select("li[class=clearfix place-item]");
		System.out.println("es size: " + es.size());
		
		for(Element e : es){
			System.out.println();
			System.out.println();
			Merchant m = new Merchant();
			Elements titleDiv = e.select("div[class=clearfix]");
			m.name = titleDiv.text();
			System.out.println("name : " + m.name);
			
			Elements childes = e.select("div[class=more-info clearfix]");
			
			// more info
			Element details = childes.get(0);
			Elements detailsDivs = details.children();
			
			Element placeTagDiv = detailsDivs.get(0);
			Element priceDiv = detailsDivs.get(1);
			Element dpDiv = detailsDivs.get(2);
			
			/* place tag begin */
			Elements pingFenDiv = placeTagDiv.select("div[class=pingfen]");
			m.fuwu = Integer.parseInt(pingFenDiv.get(0).select("em").get(0).text());
			m.kouwei = Integer.parseInt(pingFenDiv.get(0).select("em").get(1).text());
			m.huanjing = Integer.parseInt(pingFenDiv.get(0).select("em").get(2).text());
			m.xingjiabi = Integer.parseInt(pingFenDiv.get(0).select("em").get(3).text());
			
			System.out.println("fuwu :" + m.fuwu);
			System.out.println("kouwei :" + m.kouwei);
			System.out.println("huanjing :" + m.huanjing);
			System.out.println("xingjiabi :" + m.xingjiabi);
			
			Elements placeSpan = placeTagDiv.select("span[class=place]");
			m.address = placeSpan.get(0).parent().text();
			System.out.println("address: " + m.address);
			
			
			Elements tagsDiv = placeTagDiv.select("div[class=tags]");
			List<String> tags = new ArrayList<String>();
			if(tagsDiv !=null && tagsDiv.size() > 0){
				for(Element tag : tagsDiv.get(0).select("a[href]")){
					tags.add(tag.text());
				}
			}
			m.tags = tags;
			System.out.println("tags: " + m.tags);
			/* place tag end */
			
			/* price begin */
			Elements price = priceDiv.select("strong[style]");
			m.averageCost = price.get(0).text();
			System.out.println("averageCost: " + m.averageCost);
			/* price end */
			
			/* haopinglv */
			Elements haopinglv = dpDiv.select("em");
			m.grade = haopinglv.get(0).text();
			System.out.println("haopinglv :" + m.grade);
			/* haopinglv */
			
			
			merchantMap.put(m.name, m);
			
//			System.out.println(childes.get(0).text());
		}
		System.out.println(merchantMap.toString());
	}

	private static Merchant build(String url) throws IOException {
		Merchant m = new Merchant();

		Connection conn = Jsoup.connect(url);
		conn.timeout(0);
		Document doc = conn.get();

		// TODO add build procedure here.
		return m;
	}

	public static void main(String[] args) throws IOException {
		// String baseUrl = "http://dianping.com/search/category/1/10/g101";
		// String baseUrl = "http://www.baidu.com/";
		String baseUrl = "http://bendi.koubei.com/list.htm?spm=0.0.0.0.1NrzyZ&city=310100";
		// spiderInternet(baseUrl);
		String childUrl = "http://bendi.koubei.com/shanghai/list--c1-1000445";
		parserChild(childUrl);
	}
}