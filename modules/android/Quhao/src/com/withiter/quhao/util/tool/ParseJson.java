package com.withiter.quhao.util.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.Critique;
import com.withiter.quhao.vo.Haoma;
import com.withiter.quhao.vo.LoginInfo;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.MerchantLocation;
import com.withiter.quhao.vo.Paidui;
import com.withiter.quhao.vo.ReservationVO;
import com.withiter.quhao.vo.TopMerchant;

public class ParseJson {

	// TODO need to optimize these methods

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

				url = obj.optString("url");
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
				if (obj.has("merchantImage")) {
					if (QuhaoConstant.test) {
						imgUrl = obj.getString("merchantImage").replace(
								"localhost", "10.0.2.2");
					} else {
						imgUrl = obj.getString("merchantImage");
					}
				}
				String name = obj.optString("name");
				String id = obj.optString("id");
				String mid = obj.optString("mid");
				TopMerchant topMerchant = new TopMerchant(id, mid, imgUrl, name);
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
		String id = obj.optString("id");
		String imgUrl = obj.optString("merchantImage");
		String name = obj.optString("name");
		String address = obj.optString("address");
		String phone = "";
		if (obj.has("telephone")) {
			JSONArray array = obj.getJSONArray("telephone");
			for (int i = 0; i < array.length(); i++) {
				if (i == array.length() - 1) {
					phone = phone + array.get(i).toString();
					break;
				}
				phone = phone + array.get(i).toString() + ",";
			}
		}

		String cateType = obj.optString("cateType");
		String grade = obj.optString("grade");
		String averageCost = obj.optString("averageCost");

		String tags = "";
		if (obj.has("tags")) {
			JSONArray array = obj.getJSONArray("tags");
			for (int i = 0; i < array.length(); i++) {
				if (i == array.length() - 1) {
					tags = tags + array.get(i).toString();
					break;
				}
				tags = tags + array.get(i).toString() + ",";
			}
		}

		Integer kouwei = obj.optInt("kouwei");
		Integer huanjing = obj.optInt("huanjing");
		Integer fuwu = obj.optInt("fuwu");
		Integer xingjiabi = obj.optInt("xingjiabi");

		String teses = obj.optString("teses");
		String nickName = obj.optString("nickName");
		String description = obj.optString("description");
		String openTime = obj.optString("openTime");
		String closeTime = obj.optString("closeTime");

		Integer marketCount = obj.optInt("marketCount");
		boolean enable = obj.optBoolean("enable");
		String joinedDate = obj.optString("joinedDate");

		long lat = obj.optLong("lat");
		long lng = obj.optLong("lng");

		merchant = new Merchant(id, imgUrl, name, address, phone, cateType,
				grade, averageCost, tags, kouwei, huanjing, fuwu, xingjiabi,
				teses, nickName, description, openTime, closeTime, marketCount,
				enable, joinedDate, lat, lng);

		String commentAverageCost = obj.optString("commentAverageCost");
		int commentXingjiabi = obj.optInt("commentXingjiabi");
		int commentKouwei = obj.optInt("commentHuanjing");
		int commentFuwu = obj.optInt("commentFuwu");
		int commentHuanjing = obj.optInt("commentHuanjing");
		String commentContent = obj.optString("commentContent");
		String commentDate = obj.optString("commentDate");

		merchant.commentAverageCost = commentAverageCost;
		merchant.commentXingjiabi = commentXingjiabi;
		merchant.commentKouwei = commentKouwei;
		merchant.commentHuanjing = commentHuanjing;
		merchant.commentFuwu = commentFuwu;
		merchant.commentContent = commentContent;
		merchant.commentDate = commentDate;

		return merchant;
	}

	public static LoginInfo getLoginInfo(String result) {
		LoginInfo loginInfo = new LoginInfo();
		if (null == result || "".equals(result)) {
			return loginInfo;
		}

		try {
			JSONObject obj = new JSONObject(result);

			String msg = obj.optString("msg");
			String phone = obj.optString("phone");
			String jifen = obj.optString("jifen");
			String email = obj.optString("email");
			String password = obj.optString("password");
			String nickName = obj.optString("nickname");
			String birthday = obj.optString("birthDay");
			String userImage = obj.optString("userImage");
			String enable = obj.optString("enable");
			String mobileOS = obj.optString("mobileOS");
			String lastLogin = obj.optString("lastLogin");
			String signIn = obj.optString("signIn");
			String isSignIn = obj.optString("isSignIn");
			String dianping = obj.optString("dianping");
			String zhaopian = obj.optString("zhaopian");

			loginInfo = new LoginInfo(msg, phone, jifen, email, password,
					nickName, birthday, userImage, enable, mobileOS, lastLogin,signIn,isSignIn,dianping,zhaopian);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return loginInfo;
	}

	/**
	 * parse string to haoma object
	 * 
	 * @param buf
	 *            the string from server
	 * @return haoma object
	 */
	public static Haoma getHaoma(String result) {
		Haoma haoma = new Haoma();
//		if (null == result || "".equals(result)) {
//			return haoma;
//		}

		try {
			JSONObject obj = new JSONObject(result);

			String merchantId = obj.optString("merchantId");
			haoma.merchantId = merchantId;

			JSONObject jsonMaps = null;
			List<Paidui> paiduiList = null;
			if (obj.has("haomaVOMap")) {
				paiduiList = new ArrayList<Paidui>();
				jsonMaps = obj.getJSONObject("haomaVOMap");

				Iterator<String> keyIter = jsonMaps.keys();

				while (keyIter.hasNext()) {
					String key = keyIter.next();
					JSONObject obj1 = jsonMaps.getJSONObject(key);
					Paidui paidui = coventPaidui(key, obj1);
					if (paidui.enable) {
						paiduiList.add(paidui);
					}

				}

				Collections.sort(paiduiList);
				haoma.paiduiList.addAll(paiduiList);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return haoma;
	}

	private static Paidui coventPaidui(String key, JSONObject obj) {
		Paidui paidu = null;

		Integer currentNumber = obj.optInt("currentNumber");
		Integer canceled = obj.optInt("canceled");
		Integer expired = obj.optInt("expired");
		Integer finished = obj.optInt("finished");
		boolean enable = obj.optBoolean("enable");
		paidu = new Paidui(key, currentNumber, canceled, expired, finished,
				enable);
		
		return paidu;
	}

	public static List<ReservationVO> getReservations(String buf) {
		List<ReservationVO> rvos = new ArrayList<ReservationVO>();

		if (null == buf || "".equals(buf)) {
			return rvos;
		}

		try {
			JSONArray jsonArrays = new JSONArray(buf);
			for (int i = 0; i < jsonArrays.length(); i++) {
				JSONObject obj = jsonArrays.getJSONObject(i);
				ReservationVO rvo = coventReservationVO(obj);
				rvos.add(rvo);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return rvos;
	}

	public static ReservationVO getReservation(String buf) {
		ReservationVO rvo = null;
		if (null == buf || "".equals(buf)) {
			return rvo;
		}

		try {
			JSONObject json = new JSONObject(buf);
			rvo = coventReservationVO(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return rvo;
	}

	private static ReservationVO coventReservationVO(JSONObject obj)
			throws JSONException {
		ReservationVO rvo;
		String accountId = obj.optString("accountId");
		String merchantId = obj.optString("merchantId");
		String seatNumber = obj.optString("seatNumber");
		String myNumber = obj.optString("myNumber");
		String beforeYou = obj.optString("beforeYou");
		String currentNumber = obj.optString("currentNumber");
		boolean valid = obj.optBoolean("valid");
		boolean tipKey = obj.optBoolean("tipKey");
		String tipValue = obj.optString("tipValue");

		rvo = new ReservationVO(accountId, merchantId, seatNumber, myNumber,
				beforeYou, currentNumber, valid, tipKey, tipValue);
		return rvo;
	}

	public static List<MerchantLocation> getMerchantLocations(String buf) {
		List<MerchantLocation> locations = new ArrayList<MerchantLocation>();

		if (null == buf || "".equals(buf)) {
			return locations;
		}

		try {
			JSONArray jsonArrays = new JSONArray(buf);
			for (int i = 0; i < jsonArrays.length(); i++) {
				JSONObject obj = jsonArrays.getJSONObject(i);
				MerchantLocation location = coventMerchantLocation(obj);
				locations.add(location);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return locations;
	}

	private static MerchantLocation coventMerchantLocation(JSONObject obj)
			throws JSONException {
		MerchantLocation location;

		String id = obj.optString("id");
		String name = obj.optString("name");
		double lat = obj.optDouble("lat");
		double lng = obj.optDouble("lng");
		String address = obj.optString("address");

		location = new MerchantLocation(id, name, lat, lng, address);
		return location;
	}

	/**
	 * 
	 * parse json string to critiques
	 * 
	 * @param buf
	 *            json string
	 * @return critiques
	 */
	public static List<Critique> getCritiques(String buf) {

		List<Critique> critiques = new ArrayList<Critique>();
		if (StringUtils.isNull(buf)) {
			return critiques;
		}

		try {
			JSONArray array = new JSONArray(buf);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Critique critique = coventCritique(obj);
				critiques.add(critique);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return critiques;
	}

	private static Critique coventCritique(JSONObject obj) throws JSONException {

		Critique critique;

		String accountId = obj.optString("accountId");
		String nickName = obj.optString("nickName");
		int level = obj.optInt("level");
		int star = obj.optInt("star");
		double average = obj.optDouble("average");
		String desc = obj.optString("desc");
		String updateDate = obj.optString("updateDate");

		critique = new Critique(accountId, nickName, level, star, average,
				desc, updateDate);
		return critique;
	}
}
