package controllers;

import java.util.Date;

import com.withiter.models.merchant.GaodeToMerchant;
import com.withiter.models.merchant.Open;

public class GaodeToMerchantController extends BaseController{
	
	/**
	 *  更新GaodeToMerchant，如果存在就在原来的count上+1，如果不存在就创建一个新的对象。
	 * 
	 * @param poiId POI ID
	 * @return  String
	 */
	public static void updateGaodeToMerchant(String poiId) {
		if(null!=poiId&&!"".equals(poiId)){
			GaodeToMerchant gaodeToMerchant = GaodeToMerchant.queryByPoiId(poiId);
			if(null == gaodeToMerchant)
			{
				gaodeToMerchant = new GaodeToMerchant();
				gaodeToMerchant.poiId = poiId;
				gaodeToMerchant.status = "false";
				gaodeToMerchant.count = 1;
				gaodeToMerchant.created = new Date();
				gaodeToMerchant.modified = new Date();
				gaodeToMerchant.create();
			}
			else
			{
				gaodeToMerchant.count = gaodeToMerchant.count+1;
				gaodeToMerchant.modified = new Date();
				gaodeToMerchant.save();
			}
			renderText("success");
		}else{
			renderText("error");
		}
	}
}
