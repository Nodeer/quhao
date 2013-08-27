package com.withiter.quhao.util.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.Haoma;
import com.withiter.quhao.vo.LoginInfo;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.Paidui;
import com.withiter.quhao.vo.TopMerchant;

public class ParseJson {

	public static Collection<? extends Category> getCategorys(String buf) {
		List<Category> categroys = new ArrayList<Category>();
		if (null == buf || "".equals(buf)) {
			return categroys;
		}

		try {
			JSONArray jsonArrays = new JSONArray(buf);
			for (int i = 0; i < jsonArrays.length(); i++) {
				JSONObject obj = jsonArrays.getJSONObject(i);
				long count = 0L;
				String categoryType = "";
				String catTypeToString = "";
				String url = "";
				if (obj.has("count")) {
					count = Long.valueOf(obj.getString("count"));
				}
				if (obj.has("cateType")) {
					categoryType = obj.getString("cateType");
					if (categoryType.equals("benbangcai"))
						catTypeToString = "本帮菜";
					if (categoryType.equals("chuancai"))
						catTypeToString = "川菜";
					if (categoryType.equals("haixian"))
						catTypeToString = "海鲜";
					if (categoryType.equals("hanguoliaoli"))
						catTypeToString = "火锅料理";
					if (categoryType.equals("mianbaodangao"))
						catTypeToString = "面包蛋糕";
					if (categoryType.equals("dongnanyacai"))
						catTypeToString = "东南亚菜";
					if (categoryType.equals("huoguo"))
						catTypeToString = "火锅";
					if (categoryType.equals("ribenliaoli"))
						catTypeToString = "日本料理";
					if (categoryType.equals("shaokao"))
						catTypeToString = "烧烤";
					if (categoryType.equals("tianpinyinpin"))
						catTypeToString = "甜品饮料";
					if (categoryType.equals("xiangcai"))
						catTypeToString = "湘菜";
					if (categoryType.equals("xican"))
						catTypeToString = "西餐";
					if (categoryType.equals("xinjiangqingzhen"))
						catTypeToString = "新疆清真";
					if (categoryType.equals("yuecaiguan"))
						catTypeToString = "粤菜馆";
					if (categoryType.equals("zhongcancaixi"))
						catTypeToString = "中餐西餐";
					if (categoryType.equals("zizhucan"))
						catTypeToString = "自助餐";
					if (categoryType.equals("xiaochikuaican"))
						catTypeToString = "小吃快餐";
				}

				if (obj.has("url")) {
					url = obj.getString("url");
				}

				Category category = new Category(count, categoryType,
						catTypeToString, url);
				categroys.add(category);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return categroys;
	}

	public static Collection<? extends TopMerchant> getTopMerchants(String buf) {
		List<TopMerchant> topMerchants = new ArrayList<TopMerchant>();

		if (null == buf || "".equals(buf)) {
			return topMerchants;
		}

		try {
			JSONArray jsonArrays = new JSONArray(buf);

			for (int i = 0; i < jsonArrays.length(); i++) {
				JSONObject obj = jsonArrays.getJSONObject(i);
				String imgUrl = "";

				if (obj.has("imgUrl")) {
					imgUrl = obj.getString("imgUrl");
				}

				String name = "";

				if (obj.has("name")) {
					name = obj.getString("name");
				}

				String id = "";

				if (obj.has("id")) {
					id = obj.getString("id");
				}

				TopMerchant topMerchant = new TopMerchant(id, imgUrl, name);
				topMerchants.add(topMerchant);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return topMerchants;
	}

	public static Collection<? extends Merchant> getMerchants(String buf) {
		List<Merchant> merchants = new ArrayList<Merchant>();

		if (null == buf || "".equals(buf)) {
			return merchants;
		}

		try {
			JSONArray jsonArrays = new JSONArray(buf);

			for (int i = 0; i < jsonArrays.length(); i++) {
				JSONObject obj = jsonArrays.getJSONObject(i);

				Merchant merchant = coventMerchant(obj);
				merchants.add(merchant);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return merchants;
	}

	public static Merchant getMerchant(String buf) {
		Merchant merchant = new Merchant();
		if (null == buf || "".equals(buf)) {
			return merchant;
		}

		try {
			JSONObject obj = new JSONObject(buf);

			merchant = coventMerchant(obj);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return merchant;
	}

	private static Merchant coventMerchant(JSONObject obj) throws JSONException {
		Merchant merchant;
		String id = "";

		if (obj.has("id")) {
			id = obj.getString("id");
		}
		String imgUrl = "";

		if (obj.has("imgUrl")) {
			imgUrl = obj.getString("imgUrl");
		}

		String name = "";

		if (obj.has("name")) {
			name = obj.getString("name");
		}

		String address = "";

		if (obj.has("address")) {
			address = obj.getString("address");
		}

		String phone = "";
		if (obj.has("telephone")) {
			phone = obj.getString("telephone");
		}

		String cateType = "";
		if (obj.has("cateType")) {
			cateType = obj.getString("cateType");
		}

		String grade = "";
		if (obj.has("grade")) {
			grade = obj.getString("grade");
		}

		String averageCost = "";
		if (obj.has("averageCost")) {
			averageCost = obj.getString("averageCost");
		}

		String tags = "";
		if (obj.has("tags")) {
			tags = obj.getString("tags");
		}

		Integer kouwei = null;
		if (obj.has("kouwei")) {
			kouwei = obj.getInt("kouwei");
		}

		Integer huanjing = null;
		if (obj.has("huanjing")) {
			huanjing = obj.getInt("huanjing");
		}

		Integer fuwu = null;
		if (obj.has("fuwu")) {
			fuwu = obj.getInt("fuwu");
		}

		Integer xingjiabi = null;
		if (obj.has("xingjiabi")) {
			xingjiabi = obj.getInt("xingjiabi");
		}

		String teses = "";
		if (obj.has("teses")) {
			teses = obj.getString("teses");
		}

		String nickName = "";
		if (obj.has("nickName")) {
			nickName = obj.getString("nickName");
		}

		String description = "";
		if (obj.has("description")) {
			description = obj.getString("description");
		}

		String openTime = "";
		if (obj.has("openTime")) {
			openTime = obj.getString("openTime");
		}

		String closeTime = "";
		if (obj.has("closeTime")) {
			closeTime = obj.getString("closeTime");
		}

		Integer marketCount = null;
		if (obj.has("marketCount")) {
			marketCount = obj.getInt("marketCount");
		}

		boolean enable = false;
		if (obj.has("enable")) {
			enable = obj.getBoolean("enable");
		}

		String joinedDate = "";
		if (obj.has("joinedDate")) {
			joinedDate = obj.getString("joinedDate");
		}

		merchant = new Merchant(id, imgUrl, name, address, phone, cateType,
				grade, averageCost, tags, kouwei, huanjing, fuwu, xingjiabi,
				teses, nickName, description, openTime, closeTime, marketCount,
				enable, joinedDate);
		return merchant;
	}

	public static LoginInfo getLoginInfo(String result)
	{
		LoginInfo loginInfo = new LoginInfo();
		if (null == result || "".equals(result)) {
			return loginInfo;
		}

		try {
			JSONObject obj = new JSONObject(result);

			String phone = "";

			if (obj.has("phone")) {
				phone = obj.getString("phone");
			}
			String email = "";

			if (obj.has("email")) {
				email = obj.getString("email");
			}

			String password = "";

			if (obj.has("password")) {
				password = obj.getString("password");
			}

			String nickName = "";

			if (obj.has("nickname")) {
				nickName = obj.getString("nickname");
			}

			String birthday = "";
			if (obj.has("birthDay")) {
				birthday = obj.getString("birthDay");
			}

			String userImage = "";
			if (obj.has("userImage")) {
				userImage = obj.getString("userImage");
			}

			String enable = "";
			if (obj.has("enable")) {
				enable = obj.getString("enable");
			}

			String mobileOS = "";
			if (obj.has("mobileOS")) {
				mobileOS = obj.getString("mobileOS");
			}

			String lastLogin = "";
			if (obj.has("lastLogin")) {
				lastLogin = obj.getString("lastLogin");
			}

			loginInfo = new LoginInfo(phone, email, password, nickName, birthday, userImage, enable, mobileOS, lastLogin);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return loginInfo;
	}

	/**
	 * parse string to haoma object
	 * 
	 * @param buf the string from server
	 * @return haoma object
	 */
	public static Haoma getHaoma(String result)
	{
		Haoma haoma = new Haoma();
		if (null == result || "".equals(result)) {
			return haoma;
		}

		try {
			JSONObject obj = new JSONObject(result);

			String merchantId = "";

			if (obj.has("merchantId")) {
				merchantId = obj.getString("merchantId");
				haoma.merchantId = merchantId;
			}
			
			JSONObject jsonMaps = null;
			List<Paidui> paiduiList = null;
			if (obj.has("haomaVOMap")) {
				paiduiList = new ArrayList<Paidui>();
				jsonMaps = obj.getJSONObject("haomaVOMap");
				
				Iterator<String> keyIter = jsonMaps.keys();
				
				while(keyIter.hasNext())
				{
					String key = keyIter.next();
					JSONObject obj1 = jsonMaps.getJSONObject(key);
					Paidui paidu = coventPaidui(key,obj1);
					paiduiList.add(paidu);
				}
				
				Collections.sort(paiduiList);
				haoma.paiduiList = paiduiList;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return haoma;
	}

	private static Paidui coventPaidui(String key,JSONObject obj)
	{
		Paidui paidu = null;
		
		try
		{
			
			Integer currentNumber = null;
	
			if (obj.has("currentNumber")) {
				currentNumber = obj.getInt("currentNumber");
			}
			
			Integer canceled = null;
			
			if (obj.has("canceled")) {
				canceled = obj.getInt("canceled");
			}
			
			Integer expired = null;
			
			if (obj.has("expired")) {
				expired = obj.getInt("expired");
			}
			
			Integer finished = null;
			
			if (obj.has("finished")) {
				finished = obj.getInt("finished");
			}
			
			boolean enable = false;
			
			if (obj.has("enable")) {
				enable = obj.getBoolean("enable");
			}

			paidu = new Paidui(key,currentNumber, canceled, expired, finished, enable);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return paidu;
	}
}
