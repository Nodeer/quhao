package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.modules.morphia.Model.MorphiaQuery;

import com.withiter.jobs.CategoryJob;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Tese;

public class Patches extends BaseController {
	
	private static final String MERCHANT_CSV_FOLDER = Play.configuration.getProperty("merchants.path");
	private static Logger logger = LoggerFactory.getLogger(Patches.class);
	
	public static void index(){
		renderJapid();
	}
	
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
	
	private static void importMerchantFromCSV(File file) throws IOException{
		System.out.println(file.getAbsolutePath());
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
		m.averageCost = (s[3].split(":").length == 1) ? "" : s[3].split(":")[1].trim();
		
		m.openTime = (s[4].split(":").length == 1) ? "" : s[4].split(":")[1].trim();
		m.closeTime = (s[5].split(":").length == 1) ? "" : s[5].split(":")[1].trim();
		m.description = (s[6].split(":").length == 1) ? "" : s[6].split(":")[1].trim();
		m.fuwu = (s[7].split(":").length == 1) ? 0 : Integer.parseInt(s[7].split(":")[1].trim());
		m.huanjing = (s[8].split(":").length == 1) ? 0 : Integer.parseInt(s[8].split(":")[1].trim());
		m.kouwei = (s[9].split(":").length == 1) ? 0 : Integer.parseInt(s[9].split(":")[1].trim());
		m.xingjiabi = (s[10].split(":").length == 1) ? 0 : Integer.parseInt(s[10].split(":")[1].trim());
		
		m.grade = (s[11].split(":").length == 1) ? "" : s[11].split(":")[1].trim();
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
	
	public static void main(String[] args) {
		String aa = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
		System.out.println(aa);
	}
	
}
