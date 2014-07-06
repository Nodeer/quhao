package com.withiter.models.patch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import play.Logger;
import play.Play;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants;
import com.withiter.common.Constants.CateType;
import com.withiter.models.merchant.Merchant;

import controllers.Patches;

@Entity("OnetimePatch")
public class ImportChongqingAmapDatas extends OnetimePatch {

	private static final String MERCHANT_TXT_FOLDER = Play.configuration
			.getProperty("merchants.path");

	@Override
	public void run() throws Exception {
		long start = System.currentTimeMillis();
		String dir = MERCHANT_TXT_FOLDER;
		File f = new File(dir);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				
				if(!files[i].getName().contains("023")){
					continue;
				}
				
				Logger.info("Start to import from file %s", files[i].getName());
				importMerchantFromTXT(files[i]);
				Logger.info("End to import from file %s", files[i].getName());
			}
		}
		Logger.info("Import merchants finished, eslaped time is %s ms.", (System.currentTimeMillis() - start));
		MorphiaQuery q = Merchant.q();
		q.filter("cityCode", "023");
		Logger.info("%s merchants imported.", q.count());
	}

	private static void importMerchantFromTXT(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file), 1024 * 4);
		String line = null;
		Merchant m = null;
		MorphiaQuery q = Merchant.q();
		while ((line = br.readLine()) != null) {
			// line: citycode:021|poiid:B0015581X1|name:小江西快餐店|address:青云路472|location:121.470596,31.262354|tel:021-66283877|type:餐饮服务;中餐厅;中餐厅
			String[] s = line.split("\\|");
			String poiid = s[1].split(":")[1];
			if(q.filter("poiId", poiid).first() != null){
				continue;
			}
			
			m = new Merchant();
			m.poiId = poiid;
			m.cityCode = s[0].split(":")[1];
			m.postcode = s[0].split(":")[1];
			m.name = s[2].split(":")[1];
			
			if(s[3].split(":").length == 2){
				m.address = s[3].split(":")[1];
			} else {
				m.address = "";
			}
			
			if(s[4].split(":").length == 2){
				m.y = s[4].split(":")[1].split(",")[0];
				m.x = s[4].split(":")[1].split(",")[1];
			} else {
				m.y = "0";
				m.x = "0";
			}
			
			// 生成坐标，距离查询使用
			m.loc[0] = Double.parseDouble(m.y);
			m.loc[1] = Double.parseDouble(m.x);
			
			if(s[5].split(":").length == 1){	// 没有电话号码
				m.telephone = new String[] {""};
			} else {
				m.telephone = s[5].split(":")[1].split(";");
			}
//						m.telephone = (s[5].split(":").length > 1) ? s[5].split(":")[1].split(";") : new String[] { "" };

					
			// 餐厅分类
			// 中餐厅		综合酒楼-, 四川菜(川菜)-, 广东菜(粤菜)-, 山东菜(鲁菜)-, 江苏菜-, 浙江菜-, 上海菜-, 湖南菜(湘菜)-, 安徽菜(徽菜)-, 
			//				福建菜-, 北京菜-, 湖北菜(鄂菜)-, 东北菜-, 云贵菜-, 西北菜-, 老字号-, 火锅店-, 特色/地方风味餐厅-, 海鲜酒楼-, 中式素菜馆-, 
			//				清真菜馆-, 台湾菜-, 潮州菜-
			// 外国餐厅		西餐厅(综合风味)-, 日本料理-, 韩国料理-, 法式菜品餐厅-, 意式菜品餐厅-, 泰国/越南菜品餐厅-, 地中海风格菜品-, 美式风味-, 
			//				印度风味-, 英国式菜品餐厅-, 牛扒店(扒房)-, 俄国菜-, 葡国菜-, 德国菜-, 巴西菜-, 墨西哥菜-, 其它亚洲菜- 
			// 快餐厅-		肯德基, 麦当劳, 必胜客, 永和豆浆, 茶餐厅, 大家乐, 大快活, 美心, 吉野家, 仙跡岩
			// 休闲餐饮场所-	
			// 咖啡厅-		星巴克咖啡, 上岛咖啡, Pacific Coffee Company, 巴黎咖啡店
			// 茶艺馆-		
			// 冷饮店-
			// 糕饼店-
			// 甜品店-
			
			String type = "";
			String key = "";
			String value = "";
			if(s[6].split(":").length == 2){
				type = s[6].split(":")[1];
				if (type.split(";").length == 2) {
					key = type.split(";")[1];
				}
				if (type.split(";").length == 3) {
					key = type.split(";")[1];
					value = type.split(";")[2];
				}
			}
			
			if(m.name.contains("自助")){
				m.cateType = CateType.zizhucan.toString();
				m.cateName = Constants.categorys.get(CateType.zizhucan);
				m.save();
				continue;
			}
			
			if(value.contains("上海菜")){
				m.cateType = CateType.benbangcai.toString();
				m.cateName = Constants.categorys.get(CateType.benbangcai);
				m.save();
				continue;
			}
			if(value.contains("四川菜")){
				m.cateType = CateType.chuancai.toString();
				m.cateName = Constants.categorys.get(CateType.chuancai);
				m.save();
				continue;
			}
			if(value.contains("其它亚洲菜") || value.contains("泰国")){
				m.cateType = CateType.dongnanyacai.toString();
				m.cateName = Constants.categorys.get(CateType.dongnanyacai);
				m.save();
				continue;
			}
			if(value.contains("海鲜酒楼")){
				m.cateType = CateType.haixian.toString();
				m.cateName = Constants.categorys.get(CateType.haixian);
				m.save();
				continue;
			}
			if(value.contains("火锅店")){
				m.cateType = CateType.huoguo.toString();
				m.cateName = Constants.categorys.get(CateType.huoguo);
				m.save();
				continue;
			}
			if(value.contains("日本料理") || value.contains("韩国料理")){
				m.cateType = CateType.liaoli.toString();
				m.cateName = Constants.categorys.get(CateType.liaoli);
				m.save();
				continue;
			}
			
			if(value.contains("甜品店") || value.contains("冷饮店") || key.contains("咖啡厅") || key.contains("茶艺馆")){
				m.cateType = CateType.tianpinyinpin.toString();
				m.cateName = Constants.categorys.get(CateType.tianpinyinpin);
				m.save();
				continue;
			}
			if(value.contains("湘菜")){
				m.cateType = CateType.xiangcai.toString();
				m.cateName = Constants.categorys.get(CateType.xiangcai);
				m.save();
				continue;
			}
			if(key.contains("快餐厅")){
				m.cateType = CateType.xiaochikuaican.toString();
				m.cateName = Constants.categorys.get(CateType.xiaochikuaican);
				m.save();
				continue;
			}
			
			if(value.contains("西餐厅") || value.contains("法式菜品") || value.contains("意式菜品")
					|| value.contains("美式风味")|| value.contains("印度风味")|| value.contains("英国式菜品餐厅")
					|| value.contains("牛扒店")|| value.contains("俄国菜")|| value.contains("葡国菜")
					|| value.contains("德国菜")|| value.contains("巴西菜")|| value.contains("墨西哥菜")){
				m.cateType = CateType.xican.toString();
				m.cateName = Constants.categorys.get(CateType.xican);
				m.save();
				continue;
			}
			if(value.contains("清真菜馆")){
				m.cateType = CateType.xinjiangqingzhen.toString();
				m.cateName = Constants.categorys.get(CateType.xinjiangqingzhen);
				m.save();
				continue;
			}
			if(value.contains("粤菜")){
				m.cateType = CateType.yuecai.toString();
				m.cateName = Constants.categorys.get(CateType.yuecai);
				m.save();
				continue;
			}
			
			// 老字号, 特色/地方风味餐厅, 中式素菜馆, 
			if(value.contains("综合酒楼") 
					|| value.contains("老字号")|| value.contains("地方风味餐厅")|| value.contains("中式素菜馆")
					){
				m.cateType = CateType.zhongcancaixi.toString();
				m.cateName = Constants.categorys.get(CateType.zhongcancaixi);
				m.save();
				continue;
			}
			if(value.contains("山东菜")){
				m.cateType = CateType.shandongcai.toString();
				m.cateName = Constants.categorys.get(CateType.shandongcai);
				m.save();
				continue;
			}
			if(value.contains("江苏菜")){
				m.cateType = CateType.jiangsucai.toString();
				m.cateName = Constants.categorys.get(CateType.jiangsucai);
				m.save();
				continue;
			}
			if(value.contains("浙江菜")){
				m.cateType = CateType.zhejiangcai.toString();
				m.cateName = Constants.categorys.get(CateType.zhejiangcai);
				m.save();
				continue;
			}
			if(value.contains("安徽菜")){
				m.cateType = CateType.anhuicai.toString();
				m.cateName = Constants.categorys.get(CateType.anhuicai);
				m.save();
				continue;
			}
			if(value.contains("福建菜")){
				m.cateType = CateType.fujiancai.toString();
				m.cateName = Constants.categorys.get(CateType.fujiancai);
				m.save();
				continue;
			}
			if(value.contains("东北菜")){
				m.cateType = CateType.dongbeicai.toString();
				m.cateName = Constants.categorys.get(CateType.dongbeicai);
				m.save();
				continue;
			}
			if(value.contains("西北菜")){
				m.cateType = CateType.xibeicai.toString();
				m.cateName = Constants.categorys.get(CateType.xibeicai);
				m.save();
				continue;
			}
			if(value.contains("北京菜")){
				m.cateType = CateType.beijingcai.toString();
				m.cateName = Constants.categorys.get(CateType.beijingcai);
				m.save();
				continue;
			}
			if(value.contains("湖北菜")){
				m.cateType = CateType.hubeicai.toString();
				m.cateName = Constants.categorys.get(CateType.hubeicai);
				m.save();
				continue;
			}
			if(value.contains("云贵菜")){
				m.cateType = CateType.yunguicai.toString();
				m.cateName = Constants.categorys.get(CateType.yunguicai);
				m.save();
				continue;
			}
			if(value.contains("台湾菜")){
				m.cateType = CateType.taiwancai.toString();
				m.cateName = Constants.categorys.get(CateType.taiwancai);
				m.save();
				continue;
			}
		}
		br.close();
	}
}
