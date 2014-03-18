package controllers;

import vo.HaomaVO;

import com.withiter.models.merchant.Haoma;

public class ReservationController extends BaseController{

	public static void refreshPaidui(String aid, String mid, int seatNumber){
		
	}
	
	public static void paiduiStatusForApp(){
		String mid = params.get("mid");
		Haoma haoma = Haoma.findByMerchantId(mid);

		//haoma.updateSelf();

		HaomaVO haomaVO = HaomaVO.build(haoma);
		renderJSON(haomaVO);
	}
}
