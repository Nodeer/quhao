package controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Before;
import vo.ShareVO;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.common.Constants;
import com.withiter.models.social.Share;

public class ShareController extends BaseController {
	
	public static final int  NUMBER_PER_PAGE = 10;
	private static Logger logger = LoggerFactory.getLogger(ShareController.class);

	/**
	 * Interception any caller on this controller, will first invoke this method
	 */
	@Before
	static void checkAuthentification() {
		if(session.contains(Constants.SESSION_USERNAME)){
			return;
		}
		
		Map headers = request.headers;
		Iterator it = headers.keySet().iterator();
		while(it.hasNext()){
			String key = (String) it.next();
			logger.debug(key+", " +headers.get(key));
		}
		
		if(headers.containsKey("user-agent")){
			if(!(request.headers.get("user-agent").values.contains("QuhaoAndroid") || request.headers.get("user-agent").values.contains("QuhaoIOS"))){
				renderJSON("请使用Android/iOS APP访问。");
			}
		} else {
			renderJSON("请使用Android/iOS APP访问。");
		}
	}
	
	/**
	 * 用户未开启定位，获取分享列表
	 * @param page 第page页
	 * @param cityCode 城市代码
	 */
	public static void getShare(int page, String cityCode) {
		page = (page == 0) ? 1 : page;
		int num = (page - 1) * NUMBER_PER_PAGE;
		List<Share> list = Share.nextPage(page, cityCode);
		List<ShareVO> voList = new ArrayList<ShareVO>();
		ShareVO svo = null;
		for(Share s : list){
			svo = new ShareVO();
			svo.build(s);
			voList.add(svo);
		}
		renderJSON(svo);
	}
	
	/**
	 * 获取周边分享
	 * @param page 第page页
	 * @param userX x坐标
	 * @param userY y坐标
	 * @param maxDis 最远距离
	 * @param cityCode 城市代码
	 */
	public static void getNearShare(int page, double userX, double userY, double maxDis, String cityCode) {
		page = (page == 0) ? 1 : page;
		int num = (page - 1) * NUMBER_PER_PAGE;
		BasicDBObject cmdBody = new BasicDBObject("aggregate", "Share");
		List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();

		BasicDBObject geoNearParams = new BasicDBObject();
		geoNearParams.put("near", new double[] { userX, userY });
		if (maxDis >= 0) {
			geoNearParams.put("maxDistance", maxDis / 6371);
		}
		geoNearParams.put("distanceField", "dis");
		geoNearParams.put("distanceMultiplier", 6371000);
		geoNearParams.put("spherical", true);
		geoNearParams.put("num", num + NUMBER_PER_PAGE);
		
		// filter
		BasicDBObject filterParams = new BasicDBObject();
		filterParams.put("deleted", false);
		filterParams.put("cityCode", cityCode);
		geoNearParams.put("query", filterParams);

		pipeline.add(new BasicDBObject("$geoNear", geoNearParams));
		if (num != 0) {
			pipeline.add(new BasicDBObject("$skip", num));
		}

		BasicDBObject projectParams = new BasicDBObject();
		projectParams.put("_id", 1);
		projectParams.put("content", 1);
		projectParams.put("address", 1);
		projectParams.put("dis", 1);
		projectParams.put("aid", 1);
		projectParams.put("image", 1);
		projectParams.put("created", 1);
		
		pipeline.add(new BasicDBObject("$project", projectParams));
		cmdBody.put("pipeline", pipeline);

		if (!MorphiaQuery.ds().getDB().command(cmdBody).ok()) {
			logger.warn("geoNear查询出错: " + MorphiaQuery.ds().getDB().command(cmdBody).getErrorMessage());
		}

		CommandResult myResult = MorphiaQuery.ds().getDB().command(cmdBody);
		if (myResult.containsField("result")) {
			List<ShareVO> shareVOList = analyzeResults(myResult);
			renderJSON(shareVOList);
		} else {
			renderJSON("");
		}
	}
	
	/**
	 * 解析geoNearShare返回的数据
	 * 
	 * @param commandResult
	 * @return
	 */
	private static List<ShareVO> analyzeResults(CommandResult commandResult) {
		List<ShareVO> lists = new ArrayList<ShareVO>();
		BasicDBList resultList = (BasicDBList) commandResult.get("result");
		Iterator<Object> it = resultList.iterator();
		BasicDBObject resultContainer = null;
		ObjectId resultId = null;
		ShareVO s = null;
		while (it.hasNext()) {
			s = new ShareVO();
			resultContainer = (BasicDBObject) it.next();
			resultId = (ObjectId) resultContainer.get("_id");

			s.id = resultId.toString();
			s.aid = resultContainer.getString("aid");
			s.address = resultContainer.getString("address");
			s.content = resultContainer.getString("content");
			s.x = resultContainer.getString("x");
			s.y = resultContainer.getString("y");
			s.dis = resultContainer.getDouble("dis");
			s.image = resultContainer.getString("image");
			s.date = (Date) resultContainer.get("created");

			lists.add(s);
		}
		
		// 对list按照时间排序
		Collections.sort(lists, new Comparator(){
			@Override
			public int compare(Object arg0, Object arg1) {
				if(((ShareVO)arg0).date.before(((ShareVO)arg1).date)){
					return 1;
				} else {
					return -1;
				}
			}
			
		});
		return lists;
	}
	
	/**
	 * 添加分享
	 * @param content 分享内容
	 * @param x x坐标
	 * @param y y坐标
	 * @param address 地址信息
	 * @param aid Account id
	 * @param image 图片
	 * @param cityCode 城市代码
	 * 
	 */
	public static void add(){
		String content = params.get("content");
		String x = params.get("x");
		String y = params.get("y");
		String address = params.get("address");
		String aid = params.get("aid");
		String image = params.get("image");
		String cityCode = params.get("cityCode");
		
		Share s = new Share();
		s.content = content;
		s.cityCode = cityCode;
		if(!StringUtils.isEmpty(x)){
			s.x = x;
			s.y = y;
			s.loc[0] = Float.parseFloat(y);
			s.loc[1] = Float.parseFloat(x);
			s.address = address;
		}
		s.aid = aid;
		s.save();
		
		if (!StringUtils.isEmpty(image)) {
			GridFSInputFile file = uploadFirst(image, aid);
			if (file != null) {
				if (!StringUtils.isEmpty(aid)) {
					try {
						String imageStorePath = Play.configuration.getProperty("imageShare.store.path");
						String imageUrl = URLEncoder.encode(imageStorePath + file.getFilename(), "UTF-8");
						s.image = imageUrl;
						s.save();
						renderJSON(true);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						renderJSON(false);
					}
				}
			}
		}
	}
	
	private static GridFSInputFile uploadFirst(String param, String aid) {
		GridFSInputFile gfsFile = null;
		File[] files = params.get(param, File[].class);
		for (File file : files) {
			try {
				gfsFile = UploadController.saveBinaryForShare(file, aid);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (gfsFile == null) {
			return null;
		} else {
			return gfsFile;
		}
	}
}
