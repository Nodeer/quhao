package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.modules.morphia.Model.MorphiaQuery;
import vo.CategoryVO;

import com.withiter.common.httprequest.CommonHTTPRequest;
import com.withiter.common.lbs.bean.Location;
import com.withiter.common.lbs.business.LocationBusiness;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Tese;
import com.withiter.models.merchant.TopMerchant;

public class Patches extends BaseController {
	
	private static final String MERCHANT_CSV_FOLDER = Play.configuration.getProperty("merchants.path");
	private static final String TOP_MERCHANT_CSV_FOLDER = Play.configuration.getProperty("topMerchants.path");
	private static Logger logger = LoggerFactory.getLogger(Patches.class);
	
	public static void index(){
		renderJapid();
	}
	
	/**
	 * 从data/merchants导入商家信息
	 * @throws IOException
	 */
	public static void importMerchants() throws IOException{
		logger.info(Patches.class.getName()+" start to importMerchants.");
		long start = System.currentTimeMillis();
		String dir = MERCHANT_CSV_FOLDER;
		File f = new File(dir);
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for(int i = 0; i < files.length; i++){
				importMerchantFromCSV(files[i]);
			}
		}
		logger.info(Patches.class.getName()+" importMerchants finished, "+ (System.currentTimeMillis() - start) + "ms.");
		
		MorphiaQuery q = Merchant.q();
		renderJSON(q.count());
	}
	
	/**
	 * 从data/merchants导入商家信息
	 * @throws IOException
	 */
	public static void importMerchants1() throws IOException{
		logger.info(Patches.class.getName()+" start to importMerchants.");
		long start = System.currentTimeMillis();
		String dir = MERCHANT_CSV_FOLDER;
		File f = new File(dir);
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for(int i = 0; i < files.length; i++){
				if(files[i].getName().indexOf(".txt")>0)
				{
					BufferedReader br = new BufferedReader(new FileReader(files[i]));
					String line = null;
					while((line=br.readLine())!=null){
						String[] s = line.replaceAll("：", ":").split("\\|");
						

						Merchant m = new Merchant();
						for(String ss : s){
							System.out.println(ss);
						}
						
						
						m.cityCode = s[1].split(":")[1].trim();
						m.email = (s[4].split(":").length == 1) ? "" : s[4].split(":")[1].trim();
						m.postcode = s[6].split(":")[1].trim();
						m.name = s[9].split(":")[1].trim();
						m.address = (s[7].split(":").length == 1) ? "" : s[7].split(":")[1].trim();
						
						m.telephone = (s[8].split(":").length == 1) ? new String[]{""} :s[8].split(":")[1].split(";"); 
						m.website = (s[11].split(":").length == 1) ? "" : s[11].split(":")[1].trim();
						m.x = s[12].split(":")[1].trim();
						m.y = s[13].split(":")[1].trim();
						
						String[] typeDescs = s[10].split(":")[1].split(";");
						
						if(typeDescs[2].contains("中餐厅"))
						{
							m.cateType = "zhongcancaixi";
						}else if(typeDescs[2].contains("中式素菜"))
						{
							m.cateType = "zhongcancaixi";
						}
						else if(typeDescs[2].contains("四川"))
						{
							m.cateType = "chuancai";
						}
						else if(typeDescs[2].contains("火锅"))
						{
							m.cateType = "huoguo";
						}
						else if(typeDescs[2].contains("湖南"))
						{
							m.cateType = "xiangcai";
						}
						else if(typeDescs[2].contains("广东"))
						{
							m.cateType = "yuecaiguan";
						}
						else if(typeDescs[2].contains("上海"))
						{
							m.cateType = "benbangcai";
						}
						else if(typeDescs[2].contains("综合"))
						{
							m.cateType = "zhongcancaixi";
						}
						else if(typeDescs[2].contains("海鲜"))
						{
							m.cateType = "haixian";
						}
						else if(typeDescs[2].contains("江苏"))
						{
							m.cateType = "jiangsucai";
						}
						else if(typeDescs[2].contains("台湾"))
						{
							m.cateType = "taiwan";
						}
						else if(typeDescs[2].contains("东北"))
						{
							m.cateType = "dongbei";
						}
						else if(typeDescs[2].contains("潮州"))
						{
							m.cateType = "yuecaiguan";
						}
						else if(typeDescs[2].contains("浙江"))
						{
							m.cateType = "zhejiangcai";
						}
						else if(typeDescs[2].contains("清真"))
						{
							m.cateType = "xinjiangqingzhen";
						}
						else if(typeDescs[2].contains("特色"))
						{
							m.cateType = "zhongcancaixi";
						}
						else if(typeDescs[2].contains("清真"))
						{
							m.cateType = "xinjiangqingzhen";
						}
						else if(typeDescs[2].contains("云贵"))
						{
							m.cateType = "chuancai";
						}
						else if(typeDescs[2].contains("快餐"))
						{
							m.cateType = "xiaochikuaican";
						}
						else if(typeDescs[2].contains("茶餐"))
						{
							m.cateType = "xiaochikuaican";
						}
						else if(typeDescs[2].contains("大家"))
						{
							m.cateType = "xiaochikuaican";
						}
						else if(typeDescs[2].contains("大家"))
						{
							m.cateType = "xiaochikuaican";
						}
						else if(typeDescs[2].contains("大家"))
						{
							m.cateType = "xiaochikuaican";
						}
						else if(typeDescs[2].contains("大家"))
						{
							m.cateType = "xiaochikuaican";
						}
						else if(typeDescs[2].contains("大家"))
						{
							m.cateType = "xiaochikuaican";
						}
						else
						{
							m.cateType = "zhongcancaixi";
						}
							
						
						m.averageCost = 0;
						
						m.openTime = "";
						m.closeTime = "";
						m.description = "";
						m.fuwu = 0;
						m.huanjing = 0;
						m.kouwei = 0;
						m.xingjiabi = 0;
						
						m.grade = 0;
						m.markedCount = 0;
						
						m.nickName = "";
						//m.cateType = fileName;
						m.enable = false;
						m.joinedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
						
						m.save();
					
					}
					br.close();
				}
				
			}
		}
		logger.info(Patches.class.getName()+" importMerchants finished, "+ (System.currentTimeMillis() - start) + "ms.");
		
		MorphiaQuery q = Merchant.q();
		renderJSON(q.count());
	}
	
	/**
	 * 从data/topmerchants导入topX商家信息
	 * @throws IOException
	 */
	public static void importTopMerchants() throws IOException{
		logger.info(Patches.class.getName()+" start to importMerchants.");
		TopMerchant.deleteAll();
		long start = System.currentTimeMillis();
		String dir = TOP_MERCHANT_CSV_FOLDER;
		File f = new File(dir);
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for(int i = 0; i < files.length; i++){
				importTopMerchantFromCSV(files[i]);
			}
		}
		logger.info(Patches.class.getName()+" importMerchants finished, "+ (System.currentTimeMillis() - start) + "ms.");
		
		MorphiaQuery q = TopMerchant.q();
		renderJSON(q.count());
	}
	
	/**
	 * Import the coordinate info for all merchants
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 */
	public static void importMerchantCoordinate() throws UnsupportedEncodingException, JSONException{
		MorphiaQuery q = Merchant.q();
		List<Merchant> mList = q.asList();
		int i = 0;
		for(Merchant m : mList){
			if(StringUtils.isEmpty(m.x)){
				Location location = LocationBusiness.getLocationByAddress("上海", m.address);
				if(StringUtils.isEmpty(location.x)){
					continue;
				}
				m.x = location.x;
				m.y = location.y;
				m.save();
				i++;
			}
		}
		
		renderJSON(i);
	}
	
	public static void updateCounts(){
		Category.updateCounts();
		List<Category> categories = Category.getAll();
		List<CategoryVO> categoriesVO = new ArrayList<CategoryVO>();
		for (Category c : categories) {
			categoriesVO.add(CategoryVO.build(c));
		}
		renderJSON(categoriesVO);
	}
	
	private static void importTopMerchantFromCSV(File file) throws IOException {
		String fileName = file.getName().replaceAll(".csv", "");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while((line=br.readLine())!=null){
			String[] s = line.replaceAll("：", ":").split("\\],\\[");
			buildTopMerchant(s, fileName);
		}
		br.close();
	}

	private static void importMerchantFromCSV(File file) throws IOException{
		String fileName = file.getName().replaceAll(".csv", "");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while((line=br.readLine())!=null){
			String[] s = line.replaceAll("：", ":").split("\\],\\[");
			build(s, fileName);
		}
		br.close();
	}
	
	private static void build(String[] s, String fileName){
		Merchant m = new Merchant();
		for(String ss : s){
			System.out.println(ss);
		}
		m.name = s[0].split(":")[1].trim();
		m.address = (s[1].split(":").length == 1) ? "" : s[1].split(":")[1].trim();
		
		m.telephone = (s[2].split(":").length == 1) ? new String[]{""} : new String[]{s[2].split(":")[1].trim()};
		m.averageCost = Float.parseFloat((s[3].split(":").length == 1) ? "" : s[3].split(":")[1].trim());
		
		m.openTime = (s[4].split(":").length == 1) ? "" : s[4].split(":")[1].trim();
		m.closeTime = (s[5].split(":").length == 1) ? "" : s[5].split(":")[1].trim();
		m.description = (s[6].split(":").length == 1) ? "" : s[6].split(":")[1].trim();
		m.fuwu = (s[7].split(":").length == 1) ? 0 : Integer.parseInt(s[7].split(":")[1].trim());
		m.huanjing = (s[8].split(":").length == 1) ? 0 : Integer.parseInt(s[8].split(":")[1].trim());
		m.kouwei = (s[9].split(":").length == 1) ? 0 : Integer.parseInt(s[9].split(":")[1].trim());
		m.xingjiabi = (s[10].split(":").length == 1) ? 0 : Integer.parseInt(s[10].split(":")[1].trim());
		
		m.grade = Float.parseFloat((s[11].split(":").length == 1) ? "" : s[11].split(":")[1].trim());
		m.markedCount = (s[12].split(":").length == 1) ? 0 : Integer.parseInt(s[12].split(":")[1].trim());
		
		m.nickName = (s[13].split(":").length == 1) ? "" : s[13].split(":")[1].trim();
		m.cateType = fileName;
		m.enable = (s[15].split(":").length == 1) ? false : Boolean.parseBoolean(s[15].split(":")[1].trim());
		m.joinedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
		
		List<String> tags = new ArrayList<String>();
		if(s[17].split(":")[1].equalsIgnoreCase("\\[\\]")){
			m.tags = tags;
		}else{
			String[] tagsArray = s[17].split(":")[1].split(",");
			for(int i=0; i < tagsArray.length; i++){
				tags.add(tagsArray[i].trim());
			}
			m.tags = tags;
		}
		
		if(s[18].split(":")[1].trim().equalsIgnoreCase("null")){
			m.teses = new ArrayList<Tese>();
		}
		m.save();
	}
	
	private static void buildTopMerchant(String[] s, String fileName){
		TopMerchant m = new TopMerchant();
		for(String ss : s){
			System.out.println(ss);
		}
		m.name = s[0].split(":")[1].trim();
		m.address = (s[1].split(":").length == 1) ? "" : s[1].split(":")[1].trim();
		
		m.telephone = (s[2].split(":").length == 1) ? new String[]{""} : new String[]{s[2].split(":")[1].trim()};
		m.averageCost = Float.parseFloat((s[3].split(":").length == 1) ? "" : s[3].split(":")[1].trim());
		
		m.openTime = (s[4].split(":").length == 1) ? "" : s[4].split(":")[1].trim();
		m.closeTime = (s[5].split(":").length == 1) ? "" : s[5].split(":")[1].trim();
		m.description = (s[6].split(":").length == 1) ? "" : s[6].split(":")[1].trim();
		m.fuwu = (s[7].split(":").length == 1) ? 0 : Integer.parseInt(s[7].split(":")[1].trim());
		m.huanjing = (s[8].split(":").length == 1) ? 0 : Integer.parseInt(s[8].split(":")[1].trim());
		m.kouwei = (s[9].split(":").length == 1) ? 0 : Integer.parseInt(s[9].split(":")[1].trim());
		m.xingjiabi = (s[10].split(":").length == 1) ? 0 : Integer.parseInt(s[10].split(":")[1].trim());
		
		m.grade = Float.parseFloat((s[11].split(":").length == 1) ? "" : s[11].split(":")[1].trim());
		m.markedCount = (s[12].split(":").length == 1) ? 0 : Integer.parseInt(s[12].split(":")[1].trim());
		
		m.nickName = (s[13].split(":").length == 1) ? "" : s[13].split(":")[1].trim();
		m.cateType = fileName;
		m.enable = (s[15].split(":").length == 1) ? false : Boolean.parseBoolean(s[15].split(":")[1].trim());
		m.joinedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
		
		List<String> tags = new ArrayList<String>();
		if(s[17].split(":")[1].equalsIgnoreCase("\\[\\]")){
			m.tags = tags;
		}else{
			String[] tagsArray = s[17].split(":")[1].split(",");
			for(int i=0; i < tagsArray.length; i++){
				tags.add(tagsArray[i].trim());
			}
			m.tags = tags;
		}
		
		if(s[18].split(":")[1].trim().equalsIgnoreCase("null")){
			m.teses = new ArrayList<Tese>();
		}
		m.save();
	}
}
