package com.withiter.models.merchant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants;
import com.withiter.common.Constants.CateType;

@Entity
public class Category extends CategoryEntityDef {

	public static Map<String, String> categorys = new HashMap<String, String>();
	
	static{
		categorys.put(CateType.benbangcai.toString(), "本帮菜");
		categorys.put(CateType.chuancai.toString(), "川菜");
		categorys.put(CateType.dongnanyacai.toString(), "东南亚菜");
		categorys.put(CateType.haixian.toString(), "海鲜");
		categorys.put(CateType.hanguoliaoli.toString(), "火锅料理");
		categorys.put(CateType.huoguo.toString(), "火锅");
		categorys.put(CateType.mianbaodangao.toString(), "面包蛋糕");
		categorys.put(CateType.ribenliaoli.toString(), "日本料理");
		categorys.put(CateType.shaokao.toString(), "烧烤");
		categorys.put(CateType.tianpinyinpin.toString(), "甜品饮品");
		categorys.put(CateType.xiangcai.toString(), "湘菜");
		categorys.put(CateType.xiaochikuaican.toString(), "小吃快餐");
		categorys.put(CateType.xican.toString(), "西餐");
		categorys.put(CateType.xinjiangqingzhen.toString(), "新疆清真");
		categorys.put(CateType.yuecaiguan.toString(), "粤菜馆");
		categorys.put(CateType.zhongcancaixi.toString(), "中餐菜系");
		categorys.put(CateType.zizhucan.toString(), "自助餐");
	}
	
	/**
	 * update category counts for CategoryJob
	 */
	public static void updateCounts() {
		CateType[] categories = Constants.CateType.values();
		Map<String, String> cates = Constants.categorys;
		for(CateType cate : categories){
			MorphiaQuery q = Category.q();
			q.filter("cateType", cate.toString());
			Category c = null;
			if(q.first() != null){
				c = q.first();
			}else{
				c = new Category();
			}
			c.cateType = cate.toString();
			c.cateName = cates.get(cate.toString());
			c.count = count(cate.toString());
			if(cate == CateType.mianbaodangao || cate == CateType.tianpinyinpin || cate == CateType.xiaochikuaican){
				c.enable = false;
			}
			c.save();
		}
	}

	// 系统第一次启动时，初始化Category
	public static void init(){
		CateType[] categories = Constants.CateType.values();
		Map<String, String> cates = Constants.categorys;
		Category c = null;
		for(CateType cate : categories){
			c = new Category();
			c.cateType = cate.toString();
			c.cateName = cates.get(cate.toString());
			if(cate == CateType.mianbaodangao || cate == CateType.tianpinyinpin || cate == CateType.xiaochikuaican){
				c.enable = false;
			}
			c.save();
		}
	}
	
	private static long count(String cateType){
		MorphiaQuery q = Merchant.q();
		q.filter("cateType", cateType.toLowerCase());
		return q.count();
	}
	
	/**
	 * Get all categories
	 * @return the list of all category
	 */
	public static List<Category> getAll() {
		MorphiaQuery q = Category.q();
		q.filter("enable", true);
		return q.asList();
	}
	
	public Category(String cateType, int count){
		this.cateType = cateType;
		this.count = count;
	}
	
	public Category(){
		
	}
}
