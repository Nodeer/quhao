package com.withiter.common.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Merchant;
import com.withiter.utils.StringUtils;

public class ParserTest {

	private static Logger logger = LoggerFactory.getLogger(ParserTest.class);
	
	private static Map<String, Merchant> merchantMap = new HashMap<String, Merchant>();
	private static Set<Category> categorySet = new HashSet<Category>();
	private static List<String> menuList = new ArrayList<String>();

	private static Pattern cateAndNumberPattern = Pattern.compile("(.*)\\(([\\d]*)\\)");
	private static Pattern openAndCloseTimePattern = Pattern.compile("([^-]*)[-]+(.*)");
	
	public static String categoryName = "";

	// proxy configuration
	static {
		System.getProperties().setProperty("proxySet", "true");
		System.getProperties().setProperty("http.proxyHost", "www-proxy.ericsson.se");
		System.getProperties().setProperty("http.proxyPort", "8080");
	}

	private static void mainListPage(String url) throws IOException, InterruptedException{
		logger.info(ParserTest.class.getName()+": mainListPage start");
		Connection conn = Jsoup.connect(url);
		conn.timeout(0);
		Document doc = conn.get();
		
		// get list of all categories
		Elements allCategories = doc.select("div[class=repast list_nav]");
		if(checkElementsSize(allCategories)){
			Elements list = allCategories.get(0).getElementsByAttributeValue("class", "list");
			if(checkElementsSize(allCategories)){
				Elements allList = list.first().children();
				logger.info(ParserTest.class.getName()+": All categories url start");
				for(Element e : allList){
					String menuUrl = e.children().first().attr("href");
					menuList.add(menuUrl);
					logger.info(menuUrl);
				}
				logger.info(ParserTest.class.getName()+": All categories url end");
			}
		}
		
		Element e2 = allCategories.get(0).child(1);
		categoryPage(menuList.get(0), e2);
		logger.info(ParserTest.class.getName()+": mainListPage end");
	}
	
	private static void categoryPage(String url, Element e) throws IOException, InterruptedException {
		Element e2 = e;
		Elements e22 = e2.children();
		
		for (Element node : e22) {
			String cateAndNumberStr = node.text();
			Matcher cateAndNumberMatcher = cateAndNumberPattern.matcher(cateAndNumberStr);
			Category category = null;

			String cate = null; // caixi
			int number = 0; // shangjia counts
			while (cateAndNumberMatcher.find()) {
				cate = cateAndNumberMatcher.group(1);
				number = Integer.parseInt(cateAndNumberMatcher.group(2));
				category = new Category(cate, number);
				categorySet.add(category);
				System.out.println(cate);
				categoryName = cate;
				System.out.println(number);
			}

			if(cate.equalsIgnoreCase("本帮菜")||cate.equalsIgnoreCase("中餐菜系")||cate.equalsIgnoreCase("火锅")
					||cate.equalsIgnoreCase("西餐")||cate.equalsIgnoreCase("韩国料理")||cate.equalsIgnoreCase("粤菜馆")
					||cate.equalsIgnoreCase("日本料理")||cate.equalsIgnoreCase("面包蛋糕")||cate.equalsIgnoreCase("甜品饮品")
					||cate.equalsIgnoreCase("湘菜")||cate.equalsIgnoreCase("小吃快餐")){
				continue;
			}
			
			int pages = (number % 10) != 0 ? (number / 10) + 1 : (number / 10);
			System.out.println("pages : " + pages);
			pages = (pages > 100) ? 100 : pages;
			System.out.println("pages : " + pages);

			// write into file
//			File f = new File("/Users/user/c/quhao/data/shangjia/"+cate + ".csv");
////			File f = new File("c:/cross/shangjia/"+cate + ".csv");
//			BufferedWriter output = new BufferedWriter(new FileWriter(f));
//			if(!f.exists()){
//				f.createNewFile();
//			}

			logger.info("start to write to file /Users/user/c/quhao/data/shangjia/"+cate + ".csv");
			for (int i = 1; i <= pages; i++) {
				String childUrl = formatUrlWithPage(node.select("a[href]").attr("href"), i);
				parseListChild(childUrl);
				for(String s : merchantMap.keySet()){
					Merchant m = merchantMap.get(s);
//					output.write(m.toString());
//					output.newLine();
				}
				logger.info("writing to file /Users/user/c/quhao/data/shangjia/"+cate + ".csv");
				merchantMap.clear();
				Thread.currentThread().sleep(1000);
			}
//			output.flush();
//			output.close();
			logger.info("end to write to file /Users/user/c/quhao/data/shangjia/"+cate + ".csv");
		}
	}

	private static String formatUrlWithPage(String url, int page) {
		String formatedUrl = url + "--page-" + page;
		return formatedUrl;
	}

	private static boolean checkElementsSize(Elements es){
		return es != null && es.size() > 0 ? true : false; 
	}
	
	private static void parseDetails(String url, Merchant m) throws IOException{
		if(StringUtils.isEmpty(url)){
			return;
		}
		Connection conn = Jsoup.connect(url);
		conn.timeout(0);
		Document doc = conn.get();
		if(url.startsWith("http://dd.taobao.com/")){
			Elements shopName = doc.getElementsByAttributeValue("class", "shop-name");
			Elements shopPos = doc.getElementsByAttributeValue("class", "shop-pos");
			Elements shopAddress = doc.getElementsByAttributeValue("class", "shop-addr");
			Elements tel = doc.getElementsByAttributeValue("class", "shop-tel");
			Elements openAndCloseTime = doc.getElementsByAttributeValue("class", "time-num");
			
			if(checkElementsSize(shopName)){
				String name = shopName.first().text().trim();
				m.name = name;
			}
			if(checkElementsSize(shopPos)){
				String nickName = shopPos.first().text().trim();
				m.nickName = nickName;
			}
			if(checkElementsSize(shopAddress)){
				String address = shopAddress.first().text().trim();
				m.address = address;
			}
			if(checkElementsSize(tel)){
				String telephone = tel.first().text().trim();
				m.telephone = new String[]{telephone};
			}
			if(checkElementsSize(openAndCloseTime)){
				String openCloeseTime = openAndCloseTime.first().text().trim();
				if(!StringUtils.isEmpty(openCloeseTime)){
					Matcher timeMatcher = openAndCloseTimePattern.matcher(openCloeseTime);
					while(timeMatcher.find()){
						m.openTime = timeMatcher.group(1);
						m.closeTime = timeMatcher.group(2);
					}
				}
			}
			
		}
		if(url.startsWith("http://detail.koubei.com/")){
			Elements shopName = doc.getElementsByAttributeValue("class", "shop-name");
			Elements shopPos = doc.getElementsByAttributeValue("class", "shop-pos");
			Elements tel = doc.getElementsByAttributeValue("class", "strong-tel");
			Elements openAndCloseTime = doc.getElementsByAttributeValue("class", "time-num");
			Elements shopAddress = doc.select("dd[title]");
			
			if(checkElementsSize(shopName)){
				String name = shopName.first().text().trim();
				m.name = name;
			}
			if(checkElementsSize(shopPos)){
				String nickName = shopPos.first().text().trim();
				m.nickName = nickName;
			}
			if(checkElementsSize(shopAddress)){
				if(shopAddress.size() > 1){
					String address = shopAddress.get(1).text().trim();
					m.address = address;
				}
			}
			if(checkElementsSize(tel)){
				String telephone = tel.first().text().trim();
				m.telephone = new String[]{telephone};
			}
			if(checkElementsSize(openAndCloseTime)){
				String openCloeseTime = openAndCloseTime.first().text().trim();
				if(!StringUtils.isEmpty(openCloeseTime)){
					Matcher timeMatcher = openAndCloseTimePattern.matcher(openCloeseTime);
					while(timeMatcher.find()){
						m.openTime = timeMatcher.group(1);
						m.closeTime = timeMatcher.group(2);
					}
				}
			}
		}
	}
	
	/**
	 * parse the list items on the page
	 * @param url
	 * @throws IOException
	 */
	private static void parseListChild(String url) throws IOException {
		System.out.println("child url: " + url);
		Connection conn = Jsoup.connect(url);
		conn.timeout(0);
		Document doc = conn.get();
		String docHtml = doc.html()
				.replaceAll("<textarea class=\"ks-datalazyload hidden\">", "")
				.replaceAll("</textarea>", "").replaceAll("&lt;", "<")
				.replaceAll("&gt;", ">").replaceAll("&quot;", "\"")
				.replaceAll("</span>", "</span").replaceAll("</a>", "</a")
				.replaceAll("</div>", "</div").replaceAll("</em>", "</em")
				.replaceAll("</p>", "</p").replaceAll("</span", "</span>")
				.replaceAll("</a", "</a>").replaceAll("</div", "</div>")
				.replaceAll("</em", "</em>").replaceAll("</p", "</p>");
		doc = Jsoup.parse(docHtml);

		Elements es = doc.select("li[class=clearfix place-item]");
		System.out.println("es size: " + es.size());

		for (Element e : es) {
			Merchant m = new Merchant();
			
			// image element
			Elements imageDiv = e.select("div[class=photo]");
			Elements imageNode = imageDiv.select("img");
			String src = imageNode.attr("src");
			System.out.println("src:"+src);
			
			
			Elements titleDiv = e.select("div[class=clearfix]");
			m.name = titleDiv.text();
			
			saveImageFromURL(m.name, src);
//			
//			Elements childes = e.select("div[class=more-info clearfix]");
//
//			// more info
//			if (childes != null && childes.size() > 0) {
//				Element details = childes.get(0);
//				Elements detailsDivs = details.children();
//				if (detailsDivs != null && detailsDivs.size() > 0) {
//					Element placeTagDiv = detailsDivs.get(0);
//					if (detailsDivs.size() == 2) {
//						Element priceDiv = detailsDivs.get(1);
//						
//						/* price begin */
//						Elements price = priceDiv.select("strong[style]");
//						if (price != null && price.size() > 0) {
//							m.averageCost = price.get(0).text();
//						}
//						/* price end */
//					}
//					if (detailsDivs.size() == 3) {
//						Element priceDiv = detailsDivs.get(1);
//						
//						/* price begin */
//						Elements price = priceDiv.select("strong[style]");
//						if (price != null && price.size() > 0) {
//							m.averageCost = price.get(0).text();
//						}
//						/* price end */
//						Element dpDiv = detailsDivs.get(2);
//
//						/* haopinglv */
//						Elements haopinglv = dpDiv.select("em");
//						if (haopinglv != null && haopinglv.size() > 0) {
//							m.grade = haopinglv.get(0).text();
//						}
//						/* haopinglv */
//					}
//					
//					/* place tag begin */
//					Elements pingFenDiv = placeTagDiv.select("div[class=pingfen]");
//					if (pingFenDiv != null && pingFenDiv.size() > 1) {
//						m.fuwu = Integer.parseInt(pingFenDiv.get(0)
//								.select("em").get(0).text());
//						m.kouwei = Integer.parseInt(pingFenDiv.get(0)
//								.select("em").get(1).text());
//						m.huanjing = Integer.parseInt(pingFenDiv.get(0)
//								.select("em").get(2).text());
//						m.xingjiabi = Integer.parseInt(pingFenDiv.get(0)
//								.select("em").get(3).text());
//					}
//
//					Elements placeSpan = placeTagDiv.select("span[class=place]");
//					m.address = placeSpan.get(0).parent().text();
//
//					Elements tagsDiv = placeTagDiv.select("div[class=tags]");
//					List<String> tags = new ArrayList<String>();
//					if (tagsDiv != null && tagsDiv.size() > 0) {
//						for (Element tag : tagsDiv.get(0).select("a[href]")) {
//							tags.add(tag.text());
//						}
//					}
//					m.tags = tags;
//					/* place tag end */
//
//				}
//			}
			
			// parse details of thie element
//			if(titleDiv != null && titleDiv.size() > 0){
//				Elements aLink = titleDiv.get(0).select("a[class=name]");
//				if(aLink != null && aLink.size() > 0){
//					Element nameLink = aLink.get(0);
//					String linkUrl = nameLink.attr("href");
//					parseDetails(linkUrl, m);
//				}
//			}
//			System.out.println(m.toString());
//			merchantMap.put(m.name, m);
		}
	}

	public static void saveImageFromURL(String merchantName, String url){

		try {
			URL picUrl;
			HttpURLConnection conn = null;
			InputStream is = null;
				picUrl = new URL(url);
				conn = (HttpURLConnection) picUrl.openConnection();
				conn.setConnectTimeout(20000);
				conn.setReadTimeout(20000);
				conn.connect();
				// 获取图片大小
				int picSize = conn.getContentLength();
				is = conn.getInputStream();
					String fileName  = url.substring(url.lastIndexOf("/")+1);
					File folder = new File("c:/testimage/");
					if(!folder.exists()){
						folder.mkdir();
					}
					File file = new File("c:/testimage/"+categoryName+"_"+merchantName+"_"+fileName);
					OutputStream os = new FileOutputStream(file);

					final int buffer_size = 1024;
					byte[] bytes = new byte[buffer_size];
					for (;;) {
						int count = is.read(bytes, 0, buffer_size);
						if (count == -1)
							break;
						os.write(bytes, 0, count);
					}
					os.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
//		String baseUrl = "http://bendi.koubei.com/list.htm?spm=0.0.0.0.1NrzyZ&city=310100";
		String baseUrl = "http://bendi.koubei.com/shanghai/list--c1-1000243?spm=5026.1000614.0.0.jySz57";
		mainListPage(baseUrl);
		
//		System.getProperties().setProperty("proxySet", "true");
//		System.getProperties().setProperty("http.proxyHost", "www-proxy.ericsson.se");
//		System.getProperties().setProperty("http.proxyPort", "8080");
//		
//		Connection conn = Jsoup.connect(baseUrl);
//		conn.timeout(60000);
//		Document doc = conn.get();
//		System.out.println(doc.html());
		
	}
}