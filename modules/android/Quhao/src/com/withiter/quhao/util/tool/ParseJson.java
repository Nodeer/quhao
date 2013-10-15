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
				if (obj.has("merchantImage")) {
					if(QuhaoConstant.test){
						imgUrl = obj.getString("merchantImage").replace("localhost", "10.0.2.2");
					}else{
						imgUrl = obj.getString("merchantImage");
					}
				}

				String name = "";
				if (obj.has("name")) {
					name = obj.getString("name");
				}

				String id = "";
				if (obj.has("id")) {
					id = obj.getString("id");
				}
				
				String mid = "";
				if(obj.has("mid")){
					mid = obj.getString("mid");
				}

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
		String id = "";

		if (obj.has("id")) {
			id = obj.getString("id");
		}
		String imgUrl = "http://tuan.cs090.com/static/team/2013/0924/13799987621497_200x120.jpg";

		if (obj.has("merchantImage")) {
			imgUrl = obj.getString("merchantImage");
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

			JSONArray array = obj.getJSONArray("telephone");
			for (int i = 0; i < array.length(); i++) {
				if (i == array.length() - 1) {
					phone = phone + array.get(i).toString();
					break;
				}
				phone = phone + array.get(i).toString() + ",";
			}

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
			JSONArray array = obj.getJSONArray("tags");
			for (int i = 0; i < array.length(); i++) {
				if (i == array.length() - 1) {
					tags = tags + array.get(i).toString();
					break;
				}
				tags = tags + array.get(i).toString() + ",";
			}

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
			if (StringUtils.isNull(nickName) || "null".equals(nickName)) {
				nickName = "";
			}
		}

		String description = "";
		if (obj.has("description")) {
			description = obj.getString("description");
			if (StringUtils.isNull(description) || "null".equals(description)) {
				description = "";
			}
		}

		String openTime = "";
		if (obj.has("openTime")) {
			openTime = obj.getString("openTime");
			if (StringUtils.isNull(openTime) || "null".equals(openTime)) {
				openTime = "";
			}
		}

		String closeTime = "";
		if (obj.has("closeTime")) {
			closeTime = obj.getString("closeTime");
			if (StringUtils.isNull(closeTime) || "null".equals(closeTime)) {
				closeTime = "";
			}
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

	public static LoginInfo getLoginInfo(String result) {
		LoginInfo loginInfo = new LoginInfo();
		if (null == result || "".equals(result)) {
			return loginInfo;
		}

		try {
			JSONObject obj = new JSONObject(result);

			String msg = "";
			if (obj.has("msg")) {
				msg = obj.getString("msg");
			}

			String phone = "";

			if (obj.has("phone")) {
				phone = obj.getString("phone");
			}
			String jifen = "";

			if (obj.has("jifen")) {
				jifen = obj.getString("jifen");
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

			loginInfo = new LoginInfo(msg, phone, jifen, email, password,
					nickName, birthday, userImage, enable, mobileOS, lastLogin);

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

				while (keyIter.hasNext()) {
					String key = keyIter.next();
					JSONObject obj1 = jsonMaps.getJSONObject(key);
					Paidui paidui = coventPaidui(key, obj1);
					if (paidui.enable == true) {
						paiduiList.add(paidui);
					}

				}

				Collections.sort(paiduiList);
				haoma.paiduiList = paiduiList;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return haoma;
	}

	private static Paidui coventPaidui(String key, JSONObject obj) {
		Paidui paidu = null;

		try {

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

			paidu = new Paidui(key, currentNumber, canceled, expired, finished,
					enable);

		} catch (JSONException e) {
			e.printStackTrace();
		}
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
		String accountId = "";

		if (obj.has("accountId")) {
			accountId = obj.getString("accountId");
		}
		String merchantId = "";

		if (obj.has("merchantId")) {
			merchantId = obj.getString("merchantId");
		}

		String seatNumber = "";

		if (obj.has("seatNumber")) {
			seatNumber = obj.getString("seatNumber");
		}

		String myNumber = "";

		if (obj.has("myNumber")) {
			myNumber = obj.getString("myNumber");
		}

		String beforeYou = "";
		if (obj.has("beforeYou")) {
			beforeYou = obj.getString("beforeYou");
		}

		String currentNumber = "";
		if (obj.has("currentNumber")) {
			currentNumber = obj.getString("currentNumber");
		}

		boolean valid = false;
		if (obj.has("valid")) {
			valid = obj.getBoolean("valid");
		}

		boolean tipKey = false;
		if (obj.has("tipKey")) {
			tipKey = obj.getBoolean("tipKey");
		}

		String tipValue = "";
		if (obj.has("tipValue")) {
			tipValue = obj.getString("tipValue");
		}

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

		String id = "";
		if (obj.has("id")) {
			id = obj.getString("id");
		}
		String name = "";
		if (obj.has("name")) {
			name = obj.getString("name");
		}
		double lat = 0L;
		if (obj.has("lat")) {
			lat = obj.getDouble("lat");
		}
		double lng = 0L;
		if (obj.has("lng")) {
			lng = obj.getDouble("lng");
		}
		String address = "";
		if (obj.has("address")) {
			address = obj.getString("address");
		}

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

		String accountId = "";
		if (obj.has("accountId")) {
			accountId = obj.getString("accountId");
		}
		String nickName = "";
		if (obj.has("nickName")) {
			nickName = obj.getString("nickName");
		}

		int level = 0;
		if (obj.has("level")) {
			level = obj.getInt("level");
		}

		int star = 0;
		if (obj.has("star")) {
			star = obj.getInt("star");
		}

		double average = 0L;
		if (obj.has("average")) {
			average = obj.getDouble("average");
		}

		String desc = "";
		if (obj.has("desc")) {
			desc = obj.getString("desc");
		}

		String updateDate = "";
		if (obj.has("updateDate")) {
			updateDate = obj.getString("updateDate");
		}

		critique = new Critique(accountId, nickName, level, star, average,
				desc, updateDate);
		return critique;
	}
}
