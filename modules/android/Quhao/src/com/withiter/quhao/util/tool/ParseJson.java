package com.withiter.quhao.util.tool;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import com.withiter.quhao.vo.Comment;
import com.withiter.quhao.vo.Credit;
import com.withiter.quhao.vo.Haoma;
import com.withiter.quhao.vo.LoginInfo;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.MerchantLocation;
import com.withiter.quhao.vo.Paidui;
import com.withiter.quhao.vo.ReservationVO;
import com.withiter.quhao.vo.SignupVO;
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
						catTypeToString = "中餐菜系";
					if (categoryType.equals("zizhucan"))
						catTypeToString = "自助餐";
					if (categoryType.equals("xiaochikuaican"))
						catTypeToString = "小吃快餐";
				}

				url = obj.optString("url");
				Category category = new Category(count, categoryType, catTypeToString, url);
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
					// TODO test here
					if (QuhaoConstant.test) {
						imgUrl = obj.getString("merchantImage").replace("localhost", "10.0.2.2");
					} else {
						imgUrl = obj.getString("merchantImage");
					}

					try {
						if (!imgUrl.contains("=")) {
							imgUrl = URLDecoder.decode(obj.getString("merchantImage"), "UTF-8");
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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

	public static List<Merchant> getMerchants(String buf) {
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

		double lat = obj.optDouble("x");
		double lng = obj.optDouble("y");

		merchant = new Merchant(id, imgUrl, name, address, phone, cateType, grade, averageCost, tags, kouwei, huanjing, fuwu, xingjiabi, teses, nickName, description, openTime, closeTime,
				marketCount, enable, joinedDate, lat, lng);

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
			String accountId = obj.optString("accountId");
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

			loginInfo = new LoginInfo(msg, accountId, phone, jifen, email, password, nickName, birthday, userImage, enable, mobileOS, lastLogin, signIn, isSignIn, dianping, zhaopian);

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
		// if (null == result || "".equals(result)) {
		// return haoma;
		// }

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
		paidu = new Paidui(key, currentNumber, canceled, expired, finished, enable);

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

	private static ReservationVO coventReservationVO(JSONObject obj) throws JSONException {
		ReservationVO rvo;
		String rId = obj.optString("id");
		String accountId = obj.optString("accountId");
		String merchantId = obj.optString("merchantId");
		String seatNumber = obj.optString("seatNumber");
		String myNumber = obj.optString("myNumber");
		String beforeYou = obj.optString("beforeYou");
		String currentNumber = obj.optString("currentNumber");
		boolean valid = obj.optBoolean("valid");
		boolean tipKey = obj.optBoolean("tipKey");
		String tipValue = obj.optString("tipValue");
		String merchantName = obj.optString("merchantName");
		String merchantAddress = obj.optString("merchantAddress");

		rvo = new ReservationVO(rId,accountId, merchantId, seatNumber, myNumber, beforeYou, currentNumber, valid, tipKey, tipValue, merchantName, merchantAddress);
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

	private static MerchantLocation coventMerchantLocation(JSONObject obj) throws JSONException {
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
	public static List<Comment> getComments(String buf) {

		List<Comment> comments = new ArrayList<Comment>();
		if (StringUtils.isNull(buf)) {
			return comments;
		}

		try {
			JSONArray array = new JSONArray(buf);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Comment comment = coventComment(obj);
				comments.add(comment);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return comments;
	}

	private static Comment coventComment(JSONObject obj) throws JSONException {

		Comment comment;
		String uid = obj.optString("uid");
		String accountId = obj.optString("accountId");
		String nickName = obj.optString("nickName");
		String mid = obj.optString("mid");
		String merchantName = obj.optString("merchantName");
		String merchantAddress = obj.optString("merchantAddress");
		String rId = obj.optString("rid");
		String averageCost = obj.optString("averageCost");
		float xingjiabi = Float.valueOf(obj.optString("xingjiabi"));
		float kouwei = Float.valueOf(obj.optString("kouwei"));
		float huanjing = Float.valueOf(obj.optString("huanjing"));
		float fuwu = Float.valueOf(obj.optString("fuwu"));
		String content = obj.optString("content");
		String created = obj.optString("created");
		String modified = obj.optString("modified");

		comment = new Comment(uid, accountId, nickName, mid,merchantName,merchantAddress, rId, averageCost, xingjiabi, kouwei, huanjing, fuwu, content, created, modified);
		return comment;
	}

	public static List<Credit> getCredits(String buf) {

		List<Credit> credits = new ArrayList<Credit>();
		if (StringUtils.isNull(buf)) {
			return credits;
		}

		try {
			JSONArray array = new JSONArray(buf);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Credit credit = coventCredit(obj);
				if (null != credit) {
					credits.add(credit);
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return credits;
	}

	private static Credit coventCredit(JSONObject obj) {
		String accountId = obj.optString("accountId");
		String merchantId = obj.optString("merchantId");
		String merchantName = obj.optString("merchantName");
		String merchantAddress = obj.optString("merchantAddress");
		String reservationId = obj.optString("reservationId");

		int seatNumber = obj.optInt("seatNumber");
		int myNumber = obj.optInt("myNumber");
		boolean cost = obj.optBoolean("cost");
		String status = obj.optString("status");

		Credit credit = new Credit(accountId, merchantId, merchantName, merchantAddress, reservationId, seatNumber, myNumber, cost, status);
		return credit;
	}

	/**
	 * parse json string to signup
	 * 
	 * @param buf
	 * @return
	 */
	public static SignupVO getSignup(String buf) {
		SignupVO signup = null;
		if (null == buf || "".equals(buf)) {
			return signup;
		}

		try {
			JSONObject json = new JSONObject(buf);
			String errorKey = json.optString("errorKey");
			String errorText = json.optString("errorText");
			signup = new SignupVO(errorKey, errorText);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return signup;
	}
}
